package fr.umontpellier.iut.graphes;

import java.util.*;

/**
 * Graphe simple non-orienté pondéré représentant le plateau du jeu.
 * Pour simplifier, on supposera que le graphe sans sommets est le graphe vide.
 * Le poids de chaque sommet correspond au coût de pose d'un rail sur la tuile correspondante.
 * Les sommets sont indexés par des entiers (pas nécessairement consécutifs).
 */

public class Graphe {
    private final Set<Sommet> sommets;
    private boolean check = false;
    private int minEnsCritique = Integer.MAX_VALUE;

    public Graphe(Set<Sommet> sommets) {
        this.sommets = sommets;
    }

    /**
     * Construit un graphe à n sommets 0..n-1 sans arêtes
     */
    public Graphe(int n) {
        sommets = new HashSet<>();
        for (int i = 0; i < n; i++) {
            ajouterSommet(i);
        }
    }

    /**
     * Construit un graphe vide
     */
    public Graphe() {
        this(0);
    }

    /**
     * Construit un sous-graphe induit par un ensemble de sommets
     * sans modifier le graphe donné
     *
     * @param g le graphe à partir duquel on construit le sous-graphe
     * @param X les sommets à considérer (on peut supposer que X est inclus dans l'ensemble des sommets de g,
     *          même si en principe ce n'est pas obligatoire)
     */
    public Graphe(Graphe g, Set<Sommet> X) {
        sommets = new HashSet<>();
        Sommet newS;
        for (Sommet s : X) {
            newS = new Sommet(g.getSommet(s.getIndice()));
            newS.getVoisins().clear();
            ajouterSommet(newS);
        }
        for (Sommet s : sommets) {
            for (Sommet voisin : g.getSommet(s.getIndice()).getVoisins()) {
                if (X.contains(voisin)) {
                    s.getVoisins().add(getSommet(voisin.getIndice()));
                }
            }
        }

    }

    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre
     * correspond à un graphe simple valide dont les degrés correspondent aux éléments de la liste.
     * Pré-requis : on peut supposer que la séquence est triée dans l'ordre croissant.
     */
    public static boolean sequenceEstGraphe(List<Integer> sequence) {
        double somme = 0;
        int max = 0;
        int taille = 0;
        for (int x : sequence) {
            somme = somme + x;
            if (x > max) max = x;
            if (x != 0) taille = taille + 1;
        }
        return (somme / 2 == (double) (int) somme / 2 && !(max >= taille)) || sequence.isEmpty() || taille == 0;
    }

    /**
     * @param g        le graphe source, qui ne doit pas être modifié
     * @param ensemble un ensemble de sommets
     *                 pré-requis : l'ensemble donné est inclus dans l'ensemble des sommets de {@code g}
     * @return un nouveau graph obtenu en fusionnant les sommets de l'ensemble donné.
     * On remplacera l'ensemble de sommets par un seul sommet qui aura comme indice
     * le minimum des indices des sommets de l'ensemble. Le surcout du nouveau sommet sera
     * la somme des surcouts des sommets fusionnés. Le nombre de points de victoire du nouveau sommet
     * sera la somme des nombres de points de victoire des sommets fusionnés.
     * L'ensemble de joueurs du nouveau sommet sera l'union des ensembles de joueurs des sommets fusionnés.
     */
    public static Graphe fusionnerEnsembleSommets(Graphe g, Set<Sommet> ensemble) {
        Graphe res = new Graphe(g, g.getSommets());
        Set<Sommet> voisinsFusion = new HashSet<>();
        if (!ensemble.isEmpty()) {
            int indiceMin = Integer.MAX_VALUE;
            int sommeSurcouts = 0;
            int sommePtsVictoire = 0;
            Set<Integer> unionJoueurs = new HashSet<>();
            for (Sommet s : ensemble) {
                indiceMin = Integer.min(indiceMin, s.getIndice());
                sommeSurcouts += s.getSurcout();
                sommePtsVictoire += s.getNbPointsVictoire();
                unionJoueurs.addAll(s.getJoueurs());
                voisinsFusion.addAll(s.getVoisins());
            }
            res.getSommets().removeAll(ensemble);
            voisinsFusion.removeAll(ensemble);
            Sommet Sfusion = Sommet.sommetBuilder.setIndice(indiceMin).setSurcout(sommeSurcouts).
                    setNbPointsVictoire(sommePtsVictoire).setJoueurs(unionJoueurs).createSommet();
            Sfusion.getVoisins().addAll(voisinsFusion);
            res.ajouterSommet(Sfusion);
        }
        return res;
    }

    /**
     * @param i un entier
     * @return le sommet d'indice {@code i} dans le graphe ou null si le sommet d'indice {@code i} n'existe pas dans this
     */
    public Sommet getSommet(int i) {
        for (Sommet s : sommets) {
            if (s.getIndice() == i) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return l'ensemble des sommets du graphe
     */
    public Set<Sommet> getSommets() {
        return sommets;
    }

    /**
     * @return l'ordre du graphe, c'est-à-dire le nombre de sommets
     */
    public int getNbSommets() {
        return sommets.size();
    }

    /**
     * @return l'ensemble d'arêtes du graphe sous forme d'ensemble de paires de sommets
     */
    public Set<Set<Sommet>> getAretes() {
        Set<Set<Sommet>> aretes = new HashSet<>();

        for (Sommet sommet : sommets) {
            for (Sommet voisin : sommet.getVoisins()) {
                if (sommets.contains(voisin)) {
                    Set<Sommet> paire = new HashSet<>();
                    paire.add(sommet);
                    paire.add(voisin);
                    aretes.add(paire);
                }
            }
        }

        return aretes;
    }

    /**
     * @return le nombre d'arêtes du graphe
     */
    public int getNbAretes() {
        return getAretes().size();
    }

    /**
     * Ajoute un sommet d'indice i au graphe s'il n'est pas déjà présent
     *
     * @param i l'entier correspondant à l'indice du sommet à ajouter dans le graphe
     */
    public boolean ajouterSommet(int i) {
        return sommets.add(Sommet.sommetBuilder.setIndice(i).createSommet());
    }

    /**
     * Ajoute un sommet au graphe s'il n'est pas déjà présent
     *
     * @param s le sommet à ajouter
     * @return true si le sommet a été ajouté, false sinon
     */
    public boolean ajouterSommet(Sommet s) {
        if (s == null) {
            return false;
        }
        return sommets.add(s);
    }

    /**
     * @param s le sommet dont on veut connaître le degré
     *          pré-requis : {@code s} est un sommet de this
     * @return le degré du sommet {@code s}
     */
    public int degre(Sommet s) {
        return s.getVoisins().size();
    }

    /**
     * @return true si et seulement si this est complet.
     */
    public boolean estComplet() {
        int n = getNbSommets();
        return getNbAretes() == n * (n - 1) / 2;
    }

    /**
     * @return true si et seulement si this est une chaîne. On considère que le graphe vide est une chaîne.
     */
    public boolean estChaine() {
        //CAS PARTICULIERS
        if (sommets.size() < 2) {
            return true;
        } else {
            List<Integer> seq = getSequenceDegres();
            ListIterator<Integer> it = seq.listIterator(2);
            if (seq.get(0) != 1 && seq.get(1) != 1) {
                return false;
            }
            while (it.hasNext()) {
                if (it.next() != 2) {
                    return false;
                }
            }
        }
        return estConnexe();
    }

    /**
     * @return true si et seulement si this est un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean estCycle() {
        List<Integer> seq = getSequenceDegres();
        for (Integer i : seq) {
            if (i != 2) {
                return false;
            }
        }
        return !seq.isEmpty() && estConnexe();
    }

    /**
     * @return true si et seulement si this est une forêt. On considère qu'un arbre est une forêt
     * et que le graphe vide est un arbre.
     */
    public boolean estForet() {
        Set<Set<Sommet>> classes = getEnsembleClassesConnexite();
        Graphe composanteConnexe;
        for (Set<Sommet> sommets : classes) {
            composanteConnexe = new Graphe(this, sommets);
            if (!composanteConnexe.estArbre()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true si et seulement si this a au moins un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean possedeUnCycle() {
        return !estForet();
        // arbre <=> sans cycle et connexe
        // pas arbre <=> cycle ou pas connexe (or classe de connexite forcemment connexe)
    }

    /**
     * @return true si et seulement si this a un isthme
     */
    public boolean possedeUnIsthme() {
        Graphe composanteConnexe;
        for (Set<Sommet> classe : getEnsembleClassesConnexite()) {
            composanteConnexe = new Graphe(this, classe);
            if (!composanteConnexe.estCycle() && composanteConnexe.getNbSommets() > 1) {
                if (composanteConnexe.estChaine()) {
                    return true;
                } else {
                    Iterator<Sommet> it;
                    Sommet s1;
                    Sommet s2;
                    for (Set<Sommet> arete : composanteConnexe.getAretes()) {
                        it = arete.iterator();
                        s1 = it.next();
                        s2 = it.next();
                        composanteConnexe.supprimerArete(s1, s2);
                        if (!composanteConnexe.estConnexe()) {
                            composanteConnexe.ajouterArete(s1, s2);
                            return true;
                        }
                        composanteConnexe.ajouterArete(s1, s2);
                    }
                }
            }
        }
        return false;
    }

    public void ajouterArete(Sommet s, Sommet t) {
        try {
            if (!s.equals(t) && sommets.contains(s) && sommets.contains(t)) {
                s.ajouterVoisin(t);
                t.ajouterVoisin(s);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public void supprimerArete(Sommet s, Sommet t) {
        try {
            s.getVoisins().remove(t);
            t.getVoisins().remove(s);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * @return une coloration gloutonne du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * L'ordre de coloration des sommets est suivant l'ordre décroissant des degrés des sommets
     * (si deux sommets ont le même degré, alors on les ordonne par indice croissant).
     */
    public Map<Integer, Set<Sommet>> getColorationGloutonne() {
        //INIT
        Map<Integer, Set<Sommet>> coloration = new HashMap<>();
        PriorityQueue<Sommet> sommetDesc = getSommetsDegresDecroissant();
        Sommet s;
        //BOUCLE
        while (!sommetDesc.isEmpty()) {
            s = sommetDesc.poll();
            int indiceCouleur = calulerCouleur(s, coloration);
            if (coloration.get(indiceCouleur) == null) {
                coloration.put(indiceCouleur, new HashSet<>(Set.of(s)));
            } else {
                coloration.get(indiceCouleur).add(s);
            }
        }
        return coloration;
    }

    /**
     * @param depart  - ensemble non-vide de sommets
     * @param arrivee
     * @return le surcout total minimal du parcours entre l'ensemble de depart et le sommet d'arrivée
     * pré-requis : l'ensemble de départ et le sommet d'arrivée sont inclus dans l'ensemble des sommets de this
     */
    public int getDistance(Set<Sommet> depart, Sommet arrivee) {
        int min = Integer.MAX_VALUE;
        for (Sommet d : depart) {
            min = Math.min(min, getDistance(d, arrivee));
        }
        return min;
    }

    /**
     * @return le surcout total minimal du parcours entre le sommet de depart et le sommet d'arrivée
     */

    public int getDistance(Sommet depart, Sommet arrivee) {
        try {
            Map<Sommet, Integer> sommetParcouru = new HashMap<>();
            PriorityQueue<Sommet> valeurParcouru = new PriorityQueue<>((o1, o2) -> sommetParcouru.get(o1) - sommetParcouru.get(o2));
            Set<Sommet> visiter = new HashSet<>();
            sommetParcouru.put(depart, 0);
            sommetParcouru.put(arrivee, Integer.MAX_VALUE);
            valeurParcouru.add(depart);
            Sommet actuel = depart;
            while (!valeurParcouru.isEmpty() && actuel != arrivee) {
                actuel = valeurParcouru.poll();
                visiter.add(actuel);
                for (Sommet s : actuel.getVoisins()) {
                    if (!visiter.contains(s)) {
                        if (sommetParcouru.containsKey(s)) {
                            if (sommetParcouru.get(actuel) + s.getSurcout() < sommetParcouru.get(s)) {
                                sommetParcouru.put(s, sommetParcouru.get(actuel) + s.getSurcout());
                            }
                        } else {
                            sommetParcouru.put(s, sommetParcouru.get(actuel) + s.getSurcout());
                            valeurParcouru.add(s);
                        }
                    }
                }
            }
            return sommetParcouru.get(arrivee);
        } catch (NullPointerException ignored) {return Integer.MAX_VALUE;}
    }

    /**
     * @return l'ensemble des classes de connexité du graphe sous forme d'un ensemble d'ensembles de sommets.
     */
    public Set<Set<Sommet>> getEnsembleClassesConnexite() {
        Set<Set<Sommet>> ensembleClassesConnexite = new HashSet<>();
        if (sommets.isEmpty())
            return ensembleClassesConnexite;
        Set<Sommet> sommets = new HashSet<>(this.sommets);
        while (!sommets.isEmpty()) {
            Sommet v = sommets.iterator().next();
            Set<Sommet> classe = getClasseConnexite(v);
            sommets.removeAll(classe);
            ensembleClassesConnexite.add(classe);
        }
        return ensembleClassesConnexite;
    }

    /**
     * @param v un sommet du graphe this
     * @return la classe de connexité du sommet {@code v} sous forme d'un ensemble de sommets.
     */
    public Set<Sommet> getClasseConnexite(Sommet v) {
        if (!sommets.contains(v))
            return new HashSet<>();
        Set<Sommet> classe = new HashSet<>();
        calculerClasseConnexite(v, classe);
        return classe;
    }

    private void calculerClasseConnexite(Sommet v, Set<Sommet> dejaVus) {
        dejaVus.add(v);
        Set<Sommet> voisins = v.getVoisins();

        for (Sommet voisin : voisins) {
            if (dejaVus.add(voisin))
                calculerClasseConnexite(voisin, dejaVus);
        }
    }

    /**
     * @return true si et seulement si this est connexe.
     */
    public boolean estConnexe() {
        //on considère que le graphe vide est un arbre donc il est connexe
        return getEnsembleClassesConnexite().size() <= 1;
    }

    /**
     * @return le degré maximum des sommets du graphe
     */
    public int degreMax() {
        int degreMax = 0;
        for (Sommet s : sommets) {
            degreMax = Math.max(degre(s), degreMax);
        }
        return degreMax;
    }

    /**
     * @return une coloration propre optimale du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * Chaque classe de couleur est représentée par un entier (la clé de la Map).
     * Pré-requis : le graphe est issu du plateau du jeu Train (entre autres, il est planaire).
     */
    public Map<Integer, Set<Sommet>> getColorationPropreOptimale() {
        HashMap<Integer, Set<Sommet>> coloration = new HashMap<>();
        //Colorer tuile horizontalement 3 couleurs
        Graphe cc;
        for (Set<Sommet> classe : getEnsembleClassesConnexite()) {
            cc = new Graphe(this, classe); //Composante Connexe

            //INIT
            LinkedHashSet<Sommet> ordreOpti = new LinkedHashSet<>(); //Determination de l'ordre de la coloration
            Sommet curr = cc.getSommetInclusDansUnTriangle();
            //Cas particulier où on n'arrive pas à trouver de triangles (3 tuiles reliées entre eux)
            if (curr == null) {
                cc.getColorationGloutonne();
                for (Map.Entry<Integer, Set<Sommet>> entry : cc.getColorationGloutonne().entrySet()) {
                    int indiceCouleur = entry.getKey();
                    if (coloration.get(indiceCouleur) == null) {
                        coloration.put(indiceCouleur, entry.getValue());
                    } else {
                        coloration.get(indiceCouleur).addAll(entry.getValue());
                    }
                }
            } else {
                ordreOpti.add(curr);
                ordreOpti.addAll(curr.getVoisins());
                Set<Sommet> sommetsDeg1;
                boolean toutEstColore = false;

                while (!toutEstColore) {
                    if (!ordreOpti.addAll(cc.getSommetsAyantNvoisinsDeEnsemble(ordreOpti, 2))) {
                        sommetsDeg1 = cc.getSommetsAyantNvoisinsDeEnsemble(ordreOpti, 1);
                        if (sommetsDeg1.isEmpty()) {
                            toutEstColore = true;
                        } else {
                            ordreOpti.add(sommetsDeg1.iterator().next());
                        }
                    }
                }
                ordreOpti.addAll(cc.getSommetsAyantNvoisinsDeEnsemble(ordreOpti, 1));

                //BOUCLE
                for (Sommet s : ordreOpti) {
                    int indiceCouleur = cc.calulerCouleur(s, coloration);

                    if (coloration.get(indiceCouleur) == null) {
                        coloration.put(indiceCouleur, new HashSet<>(Set.of(s)));
                    } else {
                        coloration.get(indiceCouleur).add(s);
                    }
                }
            }
        }
        return coloration;
    }

    /**
     * @return true si et seulement si this possède un sous-graphe complet d'ordre {@code k}
     */
    public boolean possedeSousGrapheComplet(int k) {
        //Regarder les classes de connexités
        //Pour chaque composante connexe :
        //Trier les sommets en fonction de leur degré
        //prendre les sommets de degré supérieur à k-1
        //verifier pour chaque combinaison de sous graphe d'ordre 3 contenant ces sommets, si estComplet
        //Si oui, alors true, Sinon continuer jusqu'à 0 combinaison
        //Passer à la composante connexe suivante

        check = false; //PRE INIT, variable globale pour stopper la recursivité
        //CAS EXTREME
        if (k > getNbSommets() || k < 0) {
            return false;
        } else if (k <= 1) {
            return true;
        } else if (estComplet()) {
            return true;
        } else if (k > 2 && !possedeUnCycle()) {
            return false;
        }
        //INIT
        Set<Set<Sommet>> classesConnexite = getEnsembleClassesConnexite();
        Graphe g; //pour stocker les composantes connexes
        LinkedList<Sommet> sommetsGraphe;
        LinkedList<Sommet> sommetsSousGraphe;

        //PARCOURIR LES COMPOSANTES CONNEXES
        for (Set<Sommet> classe : classesConnexite) {
            g = new Graphe(this, classe); //composante connexe de this
            if (g.estComplet() && g.getNbSommets() >= k) {
                return true;
            } else if (!((g.estChaine() && k > 2) || (g.estCycle() && k > 3) || (g.estArbre()) && k > 2)) {
                //Conditions pour rendre l'algo plus rapide, car sinon le temps augmente avec l'arborescence du graphe, or si
                //le graphe est un arbre alors, il n'a pas de cycle et connexe (composant connexe toujours connexe),
                //or un graphe complet d'ordre supérieur à 2 a forcément un cycle.
                //INIT
                sommetsGraphe = g.getSommetsDegresDecroissantDegreSuperieurAOrdre(k);
                sommetsSousGraphe = new LinkedList<>();
                //BOUCLE
                if (sousGrapheEstClique(sommetsGraphe, sommetsSousGraphe, 0, k, g)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param g un graphe
     * @return true si et seulement si this possède un sous-graphe isomorphe à {@code g}
     */
    public boolean possedeSousGrapheIsomorphe(Graphe g) {
        throw new RuntimeException("Méthode à implémenter");
    }

    /**
     * @param s
     * @param t
     * @return un ensemble de sommets qui forme un ensemble critique de plus petite taille entre {@code s} et {@code t}
     */
    public Set<Sommet> getEnsembleCritique(Sommet s, Sommet t) {
        minEnsCritique = Integer.MAX_VALUE; //reset
        PriorityQueue<Set<Sommet>> ensDep = new PriorityQueue<>((o1, o2) -> o1.size()-o2.size());
        calculerEnsCritiques(ensDep, new HashSet<>(), s, s, t);
        return ensDep.peek();
    }

    //METHODES AJOUTEES =======================================================================

    /**
     * @return true si et seulement si this est un arbre. On considère que le graphe vide est un arbre.
     */
    public boolean estArbre() {
        if (sommets.isEmpty()) {
            return true;
        }
        return estConnexe() && (getNbAretes() == getNbSommets() - 1);
    }

    /**
     * Fonction recursive por calculer les combinaisons de graphe d'ordre k, avec des sommets contenus
     * dans ensembleInit. Pour chaque combinaison, vérifie si la combinaison ensembleCourrant forme un graphe complet.
     * S'arrete lorsqu'il en trouve un.
     */
    private boolean sousGrapheEstClique(List<Sommet> ensembleInit, List<Sommet> ensembleCourrant,
                                        int indiceCourrant, int k, Graphe sousGraphe) {

        if (ensembleCourrant.size() == k) {
            sousGraphe = new Graphe(this, new HashSet<>(ensembleCourrant));
            if (sousGraphe.estComplet()) {
                check = true;
            }
            return check;
        }
        for (int i = indiceCourrant; i <= ensembleInit.size() - k + ensembleCourrant.size(); i++) {
            ensembleCourrant.add(ensembleInit.get(i));
            if (!check) {
                sousGrapheEstClique(ensembleInit, ensembleCourrant, i + 1, k, sousGraphe);
            }
            ensembleCourrant.remove(ensembleCourrant.size() - 1);
        }
        return check;
    }

    /**
     * Trie les sommets de this en fonction de leur degré (décroissant) et renvoie une nouvelle Queue les contenant.
     */
    public PriorityQueue<Sommet> getSommetsDegresDecroissant() {
        PriorityQueue<Sommet> sommetsDec = new PriorityQueue<>(
                (Sommet s1, Sommet s2) ->
                        degre(s2) == degre(s1) ?
                                Integer.compare(s1.getIndice(), s2.getIndice()) :
                                Integer.compare(degre(s2), degre(s1))
        );
        sommetsDec.addAll(sommets);
        return sommetsDec;
    }

    /**
     * Trie les sommets de this en fonction de leur degré (décroissant) et renvoie une nouvelle Queue les contenant.
     */
    public PriorityQueue<Sommet> getSommetsDegresCroissant() {
        PriorityQueue<Sommet> sommetsAsc = new PriorityQueue<>(
                (Sommet s1, Sommet s2) ->
                        degre(s2) == degre(s1) ?
                                Integer.compare(s1.getIndice(), s2.getIndice()) :
                                Integer.compare(degre(s1), degre(s2))
        );
        sommetsAsc.addAll(sommets);
        return sommetsAsc;
    }

    /**
     * Renvoie la sequence de degré de this
     */
    public List<Integer> getSequenceDegres() {
        List<Integer> sequence = new ArrayList<>();
        PriorityQueue<Sommet> sommetsAsc = getSommetsDegresCroissant();
        while (!sommetsAsc.isEmpty()) {
            sequence.add(degre(sommetsAsc.remove()));
        }
        return sequence;
    }

    /**
     * Trie les sommets de this en fonction de leur degré (décroissant) et renvoie une nouvelle List contenant,
     * seulement les sommets de degré supérieur à ordre-1
     */
    public LinkedList<Sommet> getSommetsDegresDecroissantDegreSuperieurAOrdre(int ordre) {
        PriorityQueue<Sommet> sommetsDesc = getSommetsDegresDecroissant();
        LinkedList<Sommet> res = new LinkedList<>();
        Sommet currSommet;
        while (!sommetsDesc.isEmpty() && degre(currSommet = sommetsDesc.remove()) >= ordre - 1) {
            res.add(currSommet);
        }
        return res;
    }

    /**
     * Trie les sommets de this en fonction de leur degré (Croissant) et renvoie une nouvelle List contenant,
     * seulement les sommets de degré supérieur à ordre-1
     */
    public LinkedList<Sommet> getSommetsDegresCroissantDegreSuperieurAOrdre(int ordre) {
        PriorityQueue<Sommet> sommetsAsc = getSommetsDegresCroissant();
        LinkedList<Sommet> res = new LinkedList<>();
        Sommet currSommet;
        while (!sommetsAsc.isEmpty()) {
            if (degre(currSommet = sommetsAsc.remove()) >= ordre - 1) {
                res.add(currSommet);
            }
        }
        return res;
    }

    /**
     * Renvoie la sequence de degré de this pour seulement les sommets de degré supérieur à ordre-1
     */
    public List<Integer> getSequenceDegres(int ordre) {
        List<Integer> sequence = new ArrayList<>();
        LinkedList<Sommet> sommetsAsc = getSommetsDegresCroissantDegreSuperieurAOrdre(ordre);
        while (!sommetsAsc.isEmpty()) {
            sequence.add(degre(sommetsAsc.remove()));
        }
        return sequence;
    }

    public Set<Sommet> getSommetsAyantNvoisinsDeEnsemble(Set<Sommet> ensemble, int n) {
        Set<Sommet> res = new HashSet<>();
        Set<Sommet> aExplorer = new HashSet<>(sommets);
        aExplorer.removeAll(ensemble);
        int countVoisin;

        for (Sommet s : aExplorer) {
            countVoisin = 0;
            Iterator<Sommet> it = ensemble.iterator();
            while(it.hasNext() && countVoisin < n) {
                if (s.estVoisin(it.next())) {
                    countVoisin++;
                }
            }
            if (countVoisin == n) {
                res.add(s);
            }
        }
        return res;
    }

    public Sommet getSommetInclusDansUnTriangle() {
        for (Sommet s : sommets) {
            if (s.isInTriangle()) {
                return s;
            }
        }
        return null;
    }

    public int calulerCouleur(Sommet s, Map<Integer, Set<Sommet>> coloration) {
        boolean aTrouveCouleur = false;
        int i = 1;
        Iterator<Sommet> it;
        Set<Sommet> current = coloration.get(i);
        while (!aTrouveCouleur && !s.getVoisins().isEmpty()) {
            boolean contientVoisin = false;
            if (current == null) {
                aTrouveCouleur = true;
            } else {
                it = s.getVoisins().iterator();
                while (it.hasNext() && !contientVoisin) {
                    if (current.contains(it.next())) {
                        contientVoisin = true;
                    }
                }
            }

            if (!contientVoisin) {
                aTrouveCouleur = true;
            } else {
                i++;
                current = coloration.get(i);
            }
        }
        return i;
    }

    public void calculerEnsCritiques(Collection<Set<Sommet>> ensDep, Set<Sommet> ensC, Sommet s, Sommet curr, Sommet t) {
        ensC = new HashSet<>(ensC);
        ensC.add(curr);
        if (ensC.size() >= minEnsCritique) {
            return;
        }
        if (curr.equals(t)) {
            minEnsCritique = ensC.size();
            ensC.remove(s);
            ensC.remove(t);
            ensDep.add(ensC);
            return;
        }
        for (Sommet v : curr.getVoisins()) {
            if (!ensC.contains(v)) {
                calculerEnsCritiques(ensDep, ensC, s, v, t);
            }
        }
    }

}