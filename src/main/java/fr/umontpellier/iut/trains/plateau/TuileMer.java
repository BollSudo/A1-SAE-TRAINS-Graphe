package fr.umontpellier.iut.trains.plateau;

import fr.umontpellier.iut.trains.Joueur;

public class TuileMer extends Tuile {
    public TuileMer() {
        super();
    }

    @Override
    public boolean peutPlacerRail(Joueur joueur) {
        return false;
    }

    @Override
    public boolean peutEtrePositionDepart() {
        return false;
    }

    @Override
    public boolean estMer(){
        return true;
    }
}
