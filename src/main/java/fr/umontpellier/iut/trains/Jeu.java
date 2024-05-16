package fr.umontpellier.iut.trains;

import java.util.*;
import java.util.stream.Collectors;

import fr.umontpellier.iut.graphes.Graphe;
import fr.umontpellier.iut.graphes.Sommet;
import fr.umontpellier.iut.trains.cartes.Carte;
import fr.umontpellier.iut.trains.cartes.FabriqueListeDeCartes;
import fr.umontpellier.iut.trains.cartes.ListeDeCartes;
import fr.umontpellier.iut.trains.plateau.Plateau;
import fr.umontpellier.iut.trains.plateau.Tuile;

public class Jeu implements Runnable {
    /**
     * Liste des joueurs
     */
    private ArrayList<Joueur> joueurs;
    /**
     * Joueur qui joue le tour courant
     */
    private Joueur joueurCourant;
    /**
     * Dictionnaire des piles de réserve.
     * 
     * Associe à un nom de carte la liste des cartes correspondantes disponibles
     * dans la réserve
     */
    private Map<String, ListeDeCartes> reserve;
    /**
     * Liste des cartes écartées par les joueurs
     */
    private ListeDeCartes cartesEcartees;
    /**
     * Nom de la ville du plateau (pour afficher le plateau dans l'interface
     * graphique)
     */
    private String nomVille;
    /**
     * Tuiles du plateau de jeu (indexées dans l'ordre de lecture)
     */
    private List<Tuile> tuiles;
    /**
     * Nombre de jetons Gare restant (non placés sur les tuiles)
     */
    private int nbJetonsGare;
    /**
     * Scanner pour lire les entrées clavier
     */
    private Scanner scanner;
    /**
     * Messages d'information du jeu (affichés dans l'interface graphique)
     */
    private final List<String> log;
    /**
     * Instruction affichée au joueur courant
     */
    private String instruction;
    /**
     * Liste des boutons à afficher dans l'interface
     */
    private List<Bouton> boutons;

    /**
     * Constructeur de la classe Jeu
     * 
     * @param nomsJoueurs       noms des joueurs de la partie
     * @param cartesPreparation noms des cartes à utiliser pour créer les piles de
     *                          réserve (autres que les piles de cartes communes)
     * @param plateau           choix du plateau ({@code Plateau.OSAKA} ou
     *                          {@code Plateau.TOKYO})
     */
    public Jeu(String[] nomsJoueurs, String[] cartesPreparation, Plateau plateau) {
        // initialisation des entrées/sorties
        scanner = new Scanner(System.in);
        // inputQueue = new LinkedBlockingQueue<>();
        log = new ArrayList<>();

        // préparation du plateau
        this.nomVille = plateau.getNomVille();
        this.tuiles = plateau.makeTuiles();

        this.nbJetonsGare = 30;
        this.cartesEcartees = new ListeDeCartes();

        // construction des piles de réserve
        this.reserve = new HashMap<>();

        // ajouter les cartes communes et les cartes de préparation
        creerCartesCommunes();
        for (String nomCarte : cartesPreparation) {
            reserve.put(nomCarte, FabriqueListeDeCartes.creerListeDeCartes(nomCarte, 10));
        }

        // trier les noms de cartes pour l'affichage
        // this.nomsCartesCommunes = FabriqueListeDeCartes.getNomsCartesCommunes();
        // this.nomsCartesPreparation = new ArrayList<>(List.of(cartesPreparation));
        // this.nomsCartesPreparation.sort((s1, s2) ->
        // reserve.get(s1).get(0).compareTo(reserve.get(s2).get(0)));

        // création des joueurs
        this.joueurs = new ArrayList<>();
        ArrayList<CouleurJoueur> couleurs = new ArrayList<>(List.of(CouleurJoueur.values()));
        Collections.shuffle(couleurs);
        for (String nomJoueur : nomsJoueurs) {
            this.joueurs.add(new Joueur(this, nomJoueur, couleurs.remove(0)));
        }
        this.joueurCourant = joueurs.get(0);
    }

    public Map<String, ListeDeCartes> getReserve() {
        return reserve;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public List<Tuile> getTuiles() {
        return tuiles;
    }

    public Tuile getTuile(int index) {
        return tuiles.get(index);
    }

    /**
     * Renvoie un ensemble de tous les noms des cartes en jeu.
     * 
     * Cette liste contient les noms des cartes qui étaient disponibles dans les
     * piles la réserve et "Train omnibus" que les joueurs ont en main en début de
     * partie mais ne correspond pas à une pile de la réserve.
     */
    public Set<String> getListeNomsCartes() {
        Set<String> noms = new HashSet<>(reserve.keySet());
        noms.add("Train omnibus");
        return noms;
    }

    /**
     * Renvoie une liste contenant la carte du dessus de chacune des piles non vides
     * de la réserve
     */
    public List<Carte> getCartesDisponiblesEnReserve() {
        List<Carte> cartes = new ArrayList<>();
        for (ListeDeCartes pileReserve : reserve.values()) {
            if (!pileReserve.isEmpty()) {
                cartes.add(pileReserve.get(0));
            }
        }
        return cartes;
    }

    /**
     * Construit les piles de réserve pour les cartes communes
     */
    private void creerCartesCommunes() {
        reserve.put("Train express", FabriqueListeDeCartes.creerListeDeCartes("Train express", 20));
        reserve.put("Train direct", FabriqueListeDeCartes.creerListeDeCartes("Train direct", 10));
        reserve.put("Pose de rails", FabriqueListeDeCartes.creerListeDeCartes("Pose de rails", 20));
        reserve.put("Gare", FabriqueListeDeCartes.creerListeDeCartes("Gare", 20));
        reserve.put("Appartement", FabriqueListeDeCartes.creerListeDeCartes("Appartement", 10));
        reserve.put("Immeuble", FabriqueListeDeCartes.creerListeDeCartes("Immeuble", 10));
        reserve.put("Gratte-ciel", FabriqueListeDeCartes.creerListeDeCartes("Gratte-ciel", 10));
        reserve.put("Ferraille", FabriqueListeDeCartes.creerListeDeCartes("Ferraille", 70));
    }

    /**
     * Renvoie une carte prise dans la réserve
     * 
     * @param nomCarte nom de la carte à prendre
     * @return la carte retirée de la réserve ou `null` si aucune disponible (ou si
     *         le nom de carte n'existe pas dans la réserve)
     */
    public Carte prendreDansLaReserve(String nomCarte) {
        if (!reserve.containsKey(nomCarte)) {
            return null;
        }

        ListeDeCartes pile = reserve.get(nomCarte);
        if (pile.isEmpty()) {
            return null;
        }
        return pile.remove(0);
    }

    /**
     * Modifie l'attribut {@code joueurCourant} pour passer au joueur suivant dans
     * l'ordre du tableau {@code joueurs} (le tableau est considéré circulairement)
     */
    public void passeAuJoueurSuivant() {
        int i = joueurs.indexOf(joueurCourant);
        i = (i + 1) % joueurs.size();
        joueurCourant = joueurs.get(i);
    }

    /**
     * @return {@code true} si la partie est finie, {@code false} sinon
     */
    public boolean estFini() {
        // condition 1: 4 piles de cartes de la réserve sont vides - à l'exception des
        // Ferraille
        int nbPilesVides = 0;
        for (String nomCarte : reserve.keySet()) {
            if (!nomCarte.equals("Ferraille") && reserve.get(nomCarte).isEmpty()) {
                nbPilesVides += 1;
            }
        }
        if (nbPilesVides >= 4) {
            return true;
        }
        // condition 2: un joueur a utilisé tous ses jetons Rails
        for (Joueur joueur : joueurs) {
            if (joueur.getNbJetonsRails() == 0) {
                return true;
            }
        }
        // condition 3: tous les jetons Gare ont été placés sur le plateau
        if (nbJetonsGare == 0) {
            return true;
        }
        return false;
    }

    /**
     * Démarre la partie et exécute les tours des joueurs jusqu'à ce que la partie
     * soit terminée
     */
    public void run() {
        // initialisation (chaque joueur choisit une position de départ)
        for (int i = 0; i < joueurs.size(); i++) {
            joueurCourant.choisirPositionDepart();
            passeAuJoueurSuivant();
        }

        // tours des joueurs jusqu'à une condition de fin
        while (!estFini()) {
            joueurCourant.jouerTour();
            passeAuJoueurSuivant();
        }

        // fin de la partie
        log("<div class=\"tour\">Fin de la partie</div>");
        for (Joueur j : joueurs) {
            log(String.format("%s : %d points", j.toLog(), j.getScoreTotal()));
        }
        prompt("Fin de la partie.", null, true);
    }

    /**
     * Ajoute un message au log du jeu
     */
    public void log(String message) {
        log.add(message);
    }

    /**
     * Envoie l'état de la partie pour affichage aux joueurs avant de faire un choix
     *
     * @param instruction l'instruction qui est donnée au joueur
     * @param boutons     boutons pour les choix qui ne sont pas disponibles
     *                    autrement (ou {@code null})
     * @param peutPasser  indique si le joueur peut passer sans faire de choix
     */
    public void prompt(String instruction, List<Bouton> boutons, boolean peutPasser) {
        if (boutons == null) {
            boutons = new ArrayList<>();
        }

        this.instruction = instruction;
        this.boutons = boutons;

        System.out.println();
        System.out.println(this);
        if (boutons.isEmpty()) {
            System.out.printf(">>> %s: %s <<<\n", joueurCourant.getNom(), instruction);
        } else {
            StringJoiner joiner = new StringJoiner(" / ");
            for (Bouton bouton : boutons) {
                joiner.add(bouton.toString());
            }
            System.out.printf(">>> %s: %s [%s] <<<\n", joueurCourant.getNom(), instruction, joiner);
        }
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");

        StringJoiner reserveJoiner = new StringJoiner(", ");
        for (String nomCarte : reserve.keySet()) {
            reserveJoiner.add(String.format("%s (%d)", nomCarte, reserve.get(nomCarte).size()));
        }
        joiner.add("=== Réserve ===\n" + reserveJoiner + "\n");
        joiner.add(joueurCourant.toString() + "\n");
        return joiner.toString();
    }

    /**
     * Renvoie une représentation du jeu sous la forme d'un dictionnaire de
     * valeurs sérialisables (qui sera converti en JSON pour l'envoyer à l'interface
     * graphique)
     */
    public Map<String, Object> dataMap() {
        // liste des données des piles de réserve
        List<Map<String, Object>> listeReserve = new ArrayList<>();
        List<String> nomsCartesEnReserve = reserve.keySet().stream().sorted().collect(Collectors.toList());
        // piles de cartes communes en premier
        for (String nomCarte : FabriqueListeDeCartes.getNomsCartesCommunes()) {
            if (nomsCartesEnReserve.contains(nomCarte)) {
                listeReserve.add(Map.of("carte", nomCarte, "nombre", reserve.get(nomCarte).size()));
                nomsCartesEnReserve.remove(nomCarte);
            }
        }
        // autres piles de réserve après, par ordre alphabétique
        for (String nomCarte : nomsCartesEnReserve) {
            listeReserve.add(Map.of("carte", nomCarte, "nombre", reserve.get(nomCarte).size()));
        }

        return Map.ofEntries(
                Map.entry("joueurs", joueurs.stream().map(Joueur::dataMap).toList()),
                Map.entry("joueurCourant", joueurs.indexOf(joueurCourant)),
                Map.entry("instruction", instruction),
                Map.entry("boutons", boutons),
                Map.entry("ville", nomVille),
                Map.entry("tuiles", tuiles.stream().map(Tuile::dataMap).toList()),
                Map.entry("log", log),
                Map.entry("reserve", listeReserve));
    }

    /**
     * Lit une ligne de l'entrée standard
     * C'est cette méthode qui doit être appelée à chaque fois qu'on veut lire
     * l'entrée clavier de l'utilisateur (par exemple dans {@code Player.choisir})
     *
     * @return une chaîne de caractères correspondant à l'entrée suivante dans la
     *         file
     */
    public String lireLigne() {
        return scanner.nextLine();
    }

    public void remettreCarteDansLaReserve(Carte c) {
        reserve.get(c.getNom()).add(c);
    }

    public void ecarterCarte(Carte c) {
        cartesEcartees.add(c);
    }

    public List<String> getPositionsDepartDisponibles() {
        List<String> positions = new ArrayList<>();
        for (int i = 0; i < tuiles.size(); i++) {
            if (tuiles.get(i).peutEtrePositionDepart() && tuiles.get(i).estVide()) {
                positions.add(String.valueOf(i));
            }
        }
        return positions;
    }

    public List<String> getPositionsGareDisponibles() {
        List<String> positions = new ArrayList<>();
        if (nbJetonsGare <= 0) {
            // plus aucun jeton Gare disponible
            return positions;
        }

        for (int i = 0; i < tuiles.size(); i++) {
            if (tuiles.get(i).peutPlacerGare()) {
                positions.add(String.valueOf(i));
            }
        }
        return positions;
    }

    public void ajouterGare(int i) {
        tuiles.get(i).ajouterGare();
        nbJetonsGare--;
    }

    public Collection<String> getPositionsRailDisponibles(Joueur joueur) {
        Collection<String> positions = new HashSet<>();
        for (int i = 0; i < tuiles.size(); i++) {
            if (tuiles.get(i).peutPlacerRail(joueur)) {
                positions.add(String.valueOf(i));
            }
        }
        return positions;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    /**
     * @return le graphe des tuiles du jeu (sans les tuiles Mer)
     */
    public Graphe getGraphe() {
        HashMap<Tuile, Sommet> map = new HashMap<>();
        for (Tuile tuile : getTuiles()) {
            map.put(tuile, new Sommet(tuile, this));
        }
        Set<Sommet> sommets = new HashSet<>();
        for (Tuile tuile : getTuiles()) {
            if (!tuile.estMer()) {
                for (Tuile voisin : tuile.getVoisines()){
                    if (!tuile.estMer()) {
                        map.get(tuile).ajouterVoisin(map.get(voisin));
                    }
                }
                sommets.add(map.get(tuile));
            }
        }
        return new Graphe(sommets);
    }

    /**
     * @param joueur
     * @return le sous-graphe des tuiles du jeu sur lesquelles le joueur a posé des
     *         rails
     */
    public Graphe getGraphe(Joueur joueur) {
        throw new RuntimeException("Méthode à implémenter");
    }
}
