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
        for (Sommet s : X){
            ajouterSommet(g.getSommet(s.getIndice()));
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
        for (int x : sequence){
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
            }
            res.getSommets().removeAll(ensemble);
            res.ajouterSommet(Sommet.sommetBuilder.setIndice(indiceMin).setSurcout(sommeSurcouts).
                    setNbPointsVictoire(sommePtsVictoire).setJoueurs(unionJoueurs).createSommet());
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
                Set<Sommet> paire = new HashSet<>();
                paire.add(sommet);
                paire.add(voisin);
                aretes.add(paire);
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
        if (s==null) {
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
        return getNbAretes() == n * (n-1) / 2;
    }

    /**
     * @return true si et seulement si this est une chaîne. On considère que le graphe vide est une chaîne.
     */
    public boolean estChaine() {
        //CAS PARTICULIERS
        if (sommets.isEmpty() || sommets.size()==1) {
            return true;
        }

        //INIT
        List<Sommet> sommetsParcourus = new ArrayList<>();
        boolean estChaine = true;
        boolean aTrouveSommetDegUn = false;

        Iterator<Sommet> it = sommets.iterator();
        Sommet currentSommet = null;
        int currentSommetDeg = 0;

        while (it.hasNext() && estChaine && !aTrouveSommetDegUn) {
            currentSommet = it.next();
            currentSommetDeg = degre(currentSommet);
            if (currentSommetDeg == 0 || currentSommetDeg > 2) {
                estChaine = false;
            } else if (currentSommetDeg == 1) {
                aTrouveSommetDegUn = true;
            }
        }

        //BOUCLE
        if (!aTrouveSommetDegUn) {
            estChaine = false;
        } else {
            boolean finChaine = false;
            while (estChaine && !finChaine) {
                if (currentSommetDeg > 2) {
                    estChaine = false;
                } else {
                    int nbVoisinsDejaParcourus = 0;
                    for (Sommet voisin : currentSommet.getVoisins()) {
                        if (sommetsParcourus.contains(voisin)) {
                            nbVoisinsDejaParcourus++;
                            if (nbVoisinsDejaParcourus == 2) {
                                estChaine = false;
                            }
                        } else {
                            sommetsParcourus.add(currentSommet);
                            currentSommet = voisin;
                            currentSommetDeg = degre(voisin);
                            if (currentSommetDeg == 1) {
                                sommetsParcourus.add(voisin);
                                finChaine = true;
                            }
                        }
                    }
                }
            }
        }
        return estChaine && (sommetsParcourus.size()==getNbSommets());
    }

    /**
     * @return true si et seulement si this est un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean estCycle() {
        if (getNbSommets() < 3 || getNbAretes() < 3) {
            return false;
        } else {
            Sommet sDepart = sommets.iterator().next();
            Sommet currentSommet = sDepart;
            Set<Sommet> sommetsParcourus = new HashSet<>();
            Set<Sommet> sommetsVoisins;
            boolean aCycle = false;
            boolean fini = false;
            while (!fini) {
                if (degre(currentSommet) != 2) {
                    fini = true;
                } else {
                    sommetsVoisins = currentSommet.getVoisins();
                    Iterator<Sommet> it = sommetsVoisins.iterator();
                    int compteurVoisinsDejaParcourus = 0;
                    boolean aVoisinsPasParcourus = false;
                    while (!aVoisinsPasParcourus && compteurVoisinsDejaParcourus < 2) {
                        Sommet voisin = it.next();
                        if (sommetsParcourus.contains(voisin)) {
                            compteurVoisinsDejaParcourus++;
                        } else {
                            sommetsParcourus.add(currentSommet);
                            currentSommet = voisin;
                            aVoisinsPasParcourus = true;
                        }
                    }
                    if (compteurVoisinsDejaParcourus == 2) {
                        sommetsParcourus.add(currentSommet);
                        aCycle = true;
                        fini = true;
                    }
                }
            }
            return aCycle && (sommetsParcourus.size()==getNbSommets()) && (currentSommet.getVoisins().contains(sDepart));
        }
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
        throw new RuntimeException("Méthode à implémenter");
        // if chaine (oui) vide (non) ou cycle (non) nbaretes==0 (non)
        //if not connexe alors false
        //else parcourir les aretes, les enlever verfier si toujours connexe, les remettre
    }

    public void ajouterArete(Sommet s, Sommet t) {
        try {
            if (sommets.contains(s) && sommets.contains(t)) {
                s.ajouterVoisin(t);
                t.ajouterVoisin(s);
            }
        } catch (NullPointerException ignored) {}
    }

    public void supprimerArete(Sommet s, Sommet t) {
        try {
            s.getVoisins().remove(t);
            t.getVoisins().remove(s);
        } catch (NullPointerException ignored){}
    }

    /**
     * @return une coloration gloutonne du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * L'ordre de coloration des sommets est suivant l'ordre décroissant des degrés des sommets
     * (si deux sommets ont le même degré, alors on les ordonne par indice croissant).
     */
    public Map<Integer, Set<Sommet>> getColorationGloutonne() {
        throw new RuntimeException("Méthode à implémenter");
    }

    /**
     * @param depart  - ensemble non-vide de sommets
     * @param arrivee
     * @return le surcout total minimal du parcours entre l'ensemble de depart et le sommet d'arrivée
     * pré-requis : l'ensemble de départ et le sommet d'arrivée sont inclus dans l'ensemble des sommets de this
     */
    public int getDistance(Set<Sommet> depart, Sommet arrivee) {
        throw new RuntimeException("Méthode à implémenter");
    }

    /**
     * @return le surcout total minimal du parcours entre le sommet de depart et le sommet d'arrivée
     */
    public int getDistance(Sommet depart, Sommet arrivee) {
        throw new RuntimeException("Méthode à implémenter");
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
        return getEnsembleClassesConnexite().size()==1;
    }

    /**
     * @return le degré maximum des sommets du graphe
     */
    public int degreMax() {
        int degreMax=0;
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
        throw new RuntimeException("Méthode à implémenter");
    }

    /**
     * @return true si et seulement si this possède un sous-graphe complet d'ordre {@code k}
     */
    public boolean possedeSousGrapheComplet(int k) {
        throw new RuntimeException("Méthode à implémenter");
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
    public Set<Sommet> getEnsembleCritique(Sommet s, Sommet t){
        throw new RuntimeException("Méthode à implémenter");
    }

    //METHODES AJOUTEES =======================================================================

    /**
     * @return true si et seulement si this est un arbre. On considère que le graphe vide est un arbre.
     */
    public boolean estArbre() {
        if (sommets.isEmpty()) {
            return true;
        }
        return estConnexe() && (getNbAretes() == getNbSommets()-1);
    }
}
