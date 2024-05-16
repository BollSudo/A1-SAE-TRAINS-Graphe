package fr.umontpellier.iut.trains.plateau;

import java.util.*;

import fr.umontpellier.iut.trains.Joueur;
import fr.umontpellier.iut.trains.cartes.EffetTour;

public abstract class Tuile {
    private ArrayList<Tuile> voisines;
    private Set<Joueur> rails;

    public Tuile() {
        this.voisines = new ArrayList<>();
        this.rails = new HashSet<>();
    }

    // Utilisée dans les tests
    public boolean estVide() {
        return rails.isEmpty();
    }

    // Utilisée dans les tests
    public boolean hasRail(Joueur joueur) {
        return rails.contains(joueur);
    }

    public void ajouterRail(Joueur joueur) {
        rails.add(joueur);
    }

    public void ajouterGare() {
        throw new UnsupportedOperationException("Impossible d'ajouter une gare sur cette tuile");
    }

    public boolean peutPlacerGare() {
        return false;
    }

    public void ajouterVoisine(Tuile tuile) {
        voisines.add(tuile);
        tuile.voisines.add(this);
    }

    public void supprimerVoisine(Tuile tuile) {
        voisines.remove(tuile);
        tuile.voisines.remove(this);
    }

    public boolean estReliee(Tuile tuile) {
        return voisines.contains(tuile);
    }

    public int getSurcout(Joueur joueur) {
        if (joueur.hasEffet(EffetTour.VOIE_SOUTERRAINE)) {
            return 0;
        }
        if (joueur.hasEffet(EffetTour.COOPERATION)) {
            return 0;
        }
        return rails.size();
    }
    
    public int getSurcout(){
        return rails.size();
    }

    /**
     * @return le nombre de points de victoire que rapporte la tuile si un joueur a
     *         posé un rail dessus
     */
    public int getNbPointsVictoire() {
        return 0;
    }

    // utilisé par les tests et l'UI
    public int getNbGares() {
        return 0;
    }

    public boolean peutPlacerRail(Joueur joueur) {
        if (rails.contains(joueur))
            return false;
        for (Tuile tuile : voisines) {
            if (tuile.rails.contains(joueur)) {
                return joueur.getArgent() >= getSurcout(joueur);
            }
        }
        return false;
    }

    public boolean peutEtrePositionDepart() {
        return true;
    }

    public Map<String, Object> dataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("rails", rails.stream().map(Joueur::getCouleur).toArray());
        int nbGares = getNbGares();
        if (nbGares > 0) {
            map.put("nbGares", nbGares);
        }
        return map;
    }

    public void onConstruitRail(Joueur joueur) {
        if (!rails.isEmpty() && !joueur.hasEffet(EffetTour.COOPERATION)) {
            joueur.recevoirFerraille();
        }
    }

    public List<Tuile> getVoisines() {
        return voisines;
    }

    public boolean estMer(){
        return false;
    }
}
