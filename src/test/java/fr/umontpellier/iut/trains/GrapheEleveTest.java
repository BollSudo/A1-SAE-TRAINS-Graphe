package fr.umontpellier.iut.trains;

import fr.umontpellier.iut.graphes.Graphe;
import fr.umontpellier.iut.graphes.Sommet;
import org.junit.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class GrapheEleveTest {

    Graphe g;

    public void initVide() {
        g = new Graphe();
    }

    public void initSommet(int i) {
        g = new Graphe(i);
    }

    public void relierUnSommetATous(Sommet s){
        for (Sommet v : g.getSommets()) {
            s.ajouterVoisin(v);
        }
    }

    public void relierAllSommets() {
        for (Sommet s : g.getSommets()) {
            relierUnSommetATous(s);
        }
    }

    public void initChaine(int i) {
        initSommet(i);
        for (int j = 0; j < i-1; j++) {
            g.getSommet(j).ajouterVoisin(g.getSommet(j+1));
        }
    }

    public void initCycle(int i) {
        if (i > 2) {
            initSommet(i);
            for (int j = 0; j < i-1; j++) {
                g.getSommet(j).ajouterVoisin(g.getSommet(j+1));
            }
            g.getSommet(i-1).ajouterVoisin(g.getSommet(0));
        }
    }

    public List<Integer> ajouterChaineNonReliee(int i) {
        List<Integer> sIndiceAdd = new ArrayList<>();
        int offset = 777;
        for (int j = 0; j < i; j++) {
            while (!g.ajouterSommet(j+offset)) {
                offset+=10;
            }
            sIndiceAdd.add(j+offset);
        }
        for (int j = 0; j < i -1; j++) {
            g.getSommet(j+offset).ajouterVoisin(g.getSommet(j+offset+1));
        }
        return sIndiceAdd;
    }

    public List<Integer> ajouterCycleNonReliee(int i) {
        List<Integer> sIndiceAdd = new ArrayList<>();
        if (i > 2) {
            int offset = 777;
            for (int j = 0; j < i; j++) {
                while (!g.ajouterSommet(j+offset)) {
                    offset+=10;
                }
                sIndiceAdd.add(j+offset);
            }
            for (int j = 0; j < i -1; j++) {
                g.getSommet(j+offset).ajouterVoisin(g.getSommet(j+offset+1));
            }
            g.getSommet(offset).ajouterVoisin(g.getSommet(offset+i-1));
        }
        return sIndiceAdd;
    }

    public void ajouterArbreNonReliee(int longueurTronc, int longueurBranche) {
        //Tronc de longueur longueurTronc et dont chaque branche et reliee à une chqine de longeurBranche
        List<Integer> indiceTronc = ajouterChaineNonReliee(longueurTronc);
        for (Integer indice : indiceTronc) {
            List<Integer> indiceBranche = ajouterChaineNonReliee(longueurBranche);
            g.getSommet(indice).ajouterVoisin(g.getSommet(indiceBranche.get(0)));
        }
    }

    //====DEBUT TESTS====================================================================================

    // @Disabled
    @Test
    public void test_degre() {
        initSommet(4);
        Sommet s0 = g.getSommet(0);
        Sommet s1 = g.getSommet(1);
        Sommet s2 = g.getSommet(2);
        Sommet s3 = g.getSommet(3);

        s1.ajouterVoisin(s0);
        s1.ajouterVoisin(s2);

        assertTrue(s1.getVoisins().containsAll(Set.of(s2, s0)));
        assertFalse(s1.getVoisins().contains(s3));
        assertEquals(1, g.degre(s0));
        assertEquals(2, g.degre(s1));
        assertEquals(1, g.degre(s2));
        assertEquals(0, g.degre(s3));
    }

    // @Disabled
    @Test
    public void test_estComplet_vrai() {
        initSommet(4);
        relierAllSommets();

        for (Sommet s : g.getSommets()) {
            assertEquals(3, s.getVoisins().size());
        }
        assertEquals(g.getNbAretes(), 6);
        assertEquals(g.getNbSommets(), 4);
        assertTrue(g.estComplet());
    }

    // @Disabled
    @Test
    public void test_estComplet_false() {
        initSommet(4);
        relierUnSommetATous(g.getSommet(0));

        assertEquals(3, g.getSommet(0).getVoisins().size());
        assertEquals(3, g.getNbAretes());
        assertEquals(4, g.getNbSommets());
        assertFalse(g.estComplet());
    }

    // @Disabled
    @Test
    public void test_estComplet_g_vide() {
        initVide();
        assertTrue(g.estComplet());
    }

    // @Disabled
    @Test
    public void test_estChaine_g_vide() {
        initVide();
        assertTrue(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_g_ordre1() {
        initSommet(1);
        assertTrue(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_true() {
        initChaine(4);

        for (int i = 1; i < g.getNbSommets() - 1; i++) {
            assertEquals(2, g.degre(g.getSommet(i)));
        }
        assertEquals(1, g.degre(g.getSommet(0)));
        assertEquals(1, g.degre(g.getSommet(3)));
        assertTrue(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_false_non_connexe_deux_chaines() {
        initChaine(4);
        ajouterChaineNonReliee(8);

        assertFalse(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_true_bis() {
        initChaine(23);
        g.getSommet(0).ajouterVoisin(g.getSommet(ajouterChaineNonReliee(32).get(0)));

        assertTrue(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_true_bis2() {
        initVide();
        ajouterChaineNonReliee(23);

        assertTrue(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_false() {
        initSommet(4);
        g.getSommet(1).ajouterVoisin(g.getSommet(2));

        assertFalse(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_estChaine_false_cycle() {
        initCycle(9);
        assertFalse(g.estChaine());
    }

    // @Disabled
    @Test
    public void test_sequenceEstGraphe(){
        List<Integer> suiteVide = new ArrayList<>();
        List<Integer> suite1sommetValide = new ArrayList<>();
        List<Integer> suite1sommetInvalide = new ArrayList<>();
        List<Integer> suite5sommetValide = new ArrayList<>();
        List<Integer> suite5sommetInvalide = new ArrayList<>();
        suite1sommetValide.add(0);
        suite1sommetInvalide.add(2);
        suite5sommetValide.add(1);suite5sommetValide.add(1);suite5sommetValide.add(1);suite5sommetValide.add(1);suite5sommetValide.add(4);
        suite5sommetInvalide.add(0);suite5sommetInvalide.add(4);suite5sommetInvalide.add(4);suite5sommetInvalide.add(4);suite5sommetInvalide.add(4);

        assertTrue(Graphe.sequenceEstGraphe(suiteVide));
        assertTrue(Graphe.sequenceEstGraphe(suite1sommetValide));
        assertFalse(Graphe.sequenceEstGraphe(suite1sommetInvalide));
        assertTrue(Graphe.sequenceEstGraphe(suite5sommetValide));
        assertFalse(Graphe.sequenceEstGraphe(suite5sommetInvalide));
    }

    // @Disabled
    @Test
    public void test_degre_max_g_vide() {
        initVide();
        assertEquals(0, g.degreMax());
    }

    // @Disabled
    @Test
    public void test_degre_max_g_ordre1() {
        initSommet(1);
        assertEquals(0, g.degreMax());
    }

    // @Disabled
    @Test
    public void test_degre_max_g_complet_ordre23() {
        initSommet(23);
        relierAllSommets();
        assertEquals(22, g.degreMax());
    }

    // @Disabled
    @Test
    public void test_degre_max_g_cycle_ordre23() {
        initCycle(23);
        assertEquals(2, g.degreMax());
    }

    // @Disabled
    @Test
    public void test_degre_max_g_chaine_ordre23() {
        initChaine(23);
        assertEquals(2, g.degreMax());
    }

    // @Disabled
    @Test
    public void test_degre_max_bis() {
        initSommet(23);
        Sommet g0 = g.getSommet(0);
        for (int i=1; i< 10; i++) {
            g0.ajouterVoisin(g.getSommet(i));
        }
        assertEquals(9, g.degreMax());
    }

    // @Disabled
    @Test
    public void test_est_connexe_faux_1() {
        initSommet(23);

        assertEquals(23, g.getEnsembleClassesConnexite().size());
        assertFalse(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_faux_vide() {
        initVide();

        assertEquals(0, g.getEnsembleClassesConnexite().size());
        assertFalse(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_true_ordre1() {
        initSommet(1);

        assertEquals(1, g.getEnsembleClassesConnexite().size());
        assertTrue(g.estConnexe());
    }
    //A CONFIRMER SI FAUX

    // @Disabled
    @Test
    public void test_est_connexe_faux_2() {
        initSommet(23);
        relierAllSommets();
        ajouterChaineNonReliee(23);

        assertEquals(2, g.getEnsembleClassesConnexite().size());
        assertFalse(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_faux_3() {
        initVide();
        ajouterChaineNonReliee(23);
        ajouterChaineNonReliee(12);
        ajouterChaineNonReliee(3);

        assertEquals(3, g.getEnsembleClassesConnexite().size());
        assertFalse(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_true_chaine() {
        initChaine(23);

        assertEquals(1, g.getEnsembleClassesConnexite().size());
        assertTrue(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_true_cycle() {
        initCycle(23);

        assertEquals(1, g.getEnsembleClassesConnexite().size());
        assertTrue(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_true_1() {
        initSommet(23);
        relierAllSommets();

        assertEquals(1, g.getEnsembleClassesConnexite().size());
        assertTrue(g.estConnexe());
    }

    // @Disabled
    @Test
    public void test_est_connexe_true_2() {
        initSommet(23);
        relierAllSommets();
        g.getSommet(0).ajouterVoisin(g.getSommet(ajouterChaineNonReliee(3).get(0)));

        assertEquals(1, g.getEnsembleClassesConnexite().size());
        assertTrue(g.estConnexe());
    }


    // @Disabled
    @Test
    public void test_ajouter_arete_null() {
        initVide();
        assertDoesNotThrow(() -> g.ajouterArete(null, null));
    }

    // @Disabled
    @Test
    public void test_ajouter_arete_meme_sommet() {
        initSommet(2);
        g.ajouterArete(g.getSommet(1), g.getSommet(1));

        assertEquals(0, g.getNbAretes());
        assertFalse(g.getSommet(1).estVoisin(g.getSommet(0)));
        assertFalse(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertFalse(g.getSommet(1).estVoisin(g.getSommet(1)));
    }

    // @Disabled
    @Test
    public void test_ajouter_arete_deja_relies() {
        initSommet(2);
        relierAllSommets();
        g.ajouterArete(g.getSommet(0), g.getSommet(1));

        assertEquals(1, g.getNbAretes());
        assertTrue(g.getSommet(1).estVoisin(g.getSommet(0)));
        assertTrue(g.getSommet(0).estVoisin(g.getSommet(1)));
    }

    // @Disabled
    @Test
    public void test_ajouter_arete() {
        initSommet(2);
        g.ajouterArete(g.getSommet(0), g.getSommet(1));

        assertEquals(1, g.getNbAretes());
        assertTrue(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertTrue(g.getSommet(1).estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_ajouter_arete_bis() {
        initSommet(2);
        g.ajouterArete(g.getSommet(1), g.getSommet(0));

        assertEquals(1, g.getNbAretes());
        assertTrue(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertTrue(g.getSommet(1).estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_ajouter_arete_sommets_non_inclus_dans_g() {
        initSommet(1);
        Sommet s = new Sommet(Sommet.sommetBuilder.setIndice(10).createSommet());
        g.ajouterArete(g.getSommet(0), s);

        assertEquals(0, g.getNbAretes());
        assertFalse(g.getSommet(0).estVoisin(s));
        assertFalse(s.estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_supprimer_arete_null() {
        initVide();
        assertDoesNotThrow(() -> g.supprimerArete(null, null));
    }

    // @Disabled
    @Test
    public void test_supprimer_arete_meme_sommet() {
        initSommet(2);
        relierAllSommets();
        g.supprimerArete(g.getSommet(0), g.getSommet(0));

        assertEquals(1, g.getNbAretes());
        assertTrue(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertTrue(g.getSommet(1).estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_supprimer_arete_deja_non_relies() {
        initSommet(2);
        g.supprimerArete(g.getSommet(0), g.getSommet(1));

        assertEquals(0, g.getNbAretes());
        assertFalse(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertFalse(g.getSommet(1).estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_supprimer_arete() {
        initSommet(3);
        relierAllSommets();
        g.supprimerArete(g.getSommet(0), g.getSommet(1));

        assertEquals(2, g.getNbAretes());
        assertFalse(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertFalse(g.getSommet(1).estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_supprimer_arete_bis() {
        initSommet(3);
        g.supprimerArete(g.getSommet(1), g.getSommet(0));

        assertEquals(0, g.getNbAretes());
        assertFalse(g.getSommet(0).estVoisin(g.getSommet(1)));
        assertFalse(g.getSommet(1).estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_supprimer_arete_sommets_non_inclus_dans_g() {
        initSommet(1);
        Sommet s = new Sommet(Sommet.sommetBuilder.setIndice(10).createSommet());
        g.getSommet(0).ajouterVoisin(s); //bidouille
        g.supprimerArete(g.getSommet(0), s);

        assertEquals(0, g.getNbAretes());
        assertFalse(g.getSommet(0).estVoisin(s));
        assertFalse(s.estVoisin(g.getSommet(0)));
    }

    // @Disabled
    @Test
    public void test_fusionnerEnsembleSommets_vide() {
        initSommet(10);
        Graphe res = Graphe.fusionnerEnsembleSommets(g, new HashSet<>());

        assertNotSame(g, res);
        assertEquals(10, g.getNbSommets());
        assertEquals(10, res.getNbSommets());
        assertTrue(res.getSommets().containsAll(g.getSommets()));
    }

    // @Disabled
    @Test
    public void test_fusionnerEnsembleSommets_non_inclus_dans_g() {
        initVide();
        Sommet s = Sommet.sommetBuilder.setIndice(1).createSommet();
        Graphe res = Graphe.fusionnerEnsembleSommets(g, new HashSet<>(Set.of(s)));

        assertNotSame(g, res);
        assertEquals(0, g.getNbSommets());
        assertEquals(1, res.getNbSommets());
        assertTrue(res.getSommets().contains(s));
        assertFalse(g.getSommets().contains(s));
        //VERIFIER LES PRE-REQUIS AVEC LES EXCEPTIONS ?
    }

    // @Disabled
    @Test
    public void test_fusionnerEnsembleSommets() {
        initSommet(10);
        relierAllSommets();
        Set<Sommet> sommets = new HashSet<>(g.getSommets());
        g.getSommet(0).ajouterVoisin(g.getSommet(ajouterChaineNonReliee(32).get(0)));

        Graphe res = Graphe.fusionnerEnsembleSommets(g, sommets);
        Sommet s = res.getSommet(0);

        assertNotSame(g, res);
        assertEquals(10, sommets.size());
        assertEquals(42, g.getNbSommets());
        assertEquals(33, res.getNbSommets());
        assertNotSame(g.getSommet(0), s);
        assertEquals(0, s.getIndice());
        assertEquals(0, s.getSurcout());
        assertEquals(0, s.getNbPointsVictoire());
        assertEquals(0, s.getJoueurs().size());
        assertFalse(res.getSommets().containsAll(g.getSommets()));
    }

    // @Disabled
    @Test
    public void test_fusionnerEnsembleSommets_valeurs() {
        Set<Integer> nullTest = new HashSet<>();
        nullTest.add(null);
        Sommet s1 = Sommet.sommetBuilder.setIndice(7).setSurcout(1).setNbPointsVictoire(10).setJoueurs(new HashSet<>(Set.of(1))).createSommet();
        Sommet s2 = Sommet.sommetBuilder.setIndice(70).setSurcout(10).setNbPointsVictoire(5).setJoueurs(new HashSet<>(Set.of(2))).createSommet();
        Sommet s3 = Sommet.sommetBuilder.setIndice(19).setSurcout(3).setNbPointsVictoire(13).setJoueurs(new HashSet<>(nullTest)).createSommet();
        Sommet s4 = Sommet.sommetBuilder.setIndice(1).setSurcout(1).setNbPointsVictoire(1).setJoueurs(new HashSet<>(Set.of(3))).createSommet();
        initVide();
        g.ajouterSommet(s1);
        g.ajouterSommet(s2);
        g.ajouterSommet(s3);
        g.ajouterSommet(s4);

        Graphe res = Graphe.fusionnerEnsembleSommets(g, new HashSet<>(Set.of(s1, s2, s3)));
        Sommet s = res.getSommet(7);

        assertNotSame(g, res);
        assertEquals(4, g.getNbSommets());
        assertEquals(2, res.getNbSommets());
        assertNotSame(g.getSommet(0), s);
        assertNotEquals(g.getSommet(0), s);
        assertEquals(7, s.getIndice());
        assertEquals(14, s.getSurcout());
        assertEquals(28, s.getNbPointsVictoire());
        assertEquals(3, s.getJoueurs().size());
        assertTrue(s.getJoueurs().containsAll(Set.of(1, 2)));
        assertTrue(s.getJoueurs().contains(null));
        assertFalse(res.getSommets().containsAll(g.getSommets()));
    }

    // @Disabled
    @Test
    public void test_ajouter_sommet_null() {
        initVide();
        g.ajouterSommet(null);

        assertEquals(0, g.getNbSommets());
        assertTrue(g.getSommets().isEmpty());
    }

    // @Disabled
    @Test
    public void test_ajouter_voisin_null() {
        Sommet s = Sommet.sommetBuilder.setIndice(0).createSommet();
        s.ajouterVoisin(null);

        assertEquals(0, s.getVoisins().size());
        assertTrue(s.getVoisins().isEmpty());
    }


    // @Disabled
    @Test
    public void test_est_cycle_true_ordre_3() {
        initCycle(3);
        assertTrue(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_true() {
        initCycle(13);

        assertTrue(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false_vide() {
        initVide();
        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false_ordre_1() {
        initSommet(1);
        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false_ordre_2() {
        initChaine(2);
        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false() {
        initChaine(13);

        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false_bis() {
        initCycle(13);
        ajouterCycleNonReliee(12);

        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false_bis2() {
        initSommet(10);

        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_cycle_false_bis3() {
        initChaine(4);
        g.getSommet(0).ajouterVoisin(g.getSommet(2));

        assertFalse(g.estCycle());
    }

    // @Disabled
    @Test
    public void test_est_arbre_true_vide() {
        initVide();

        assertTrue(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_arbre_true_graine() {
        initSommet(1);

        assertTrue(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_arbre_true_chaine() {
        initChaine(3);

        assertTrue(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_arbre_true_arbre() {
        initVide();
        ajouterArbreNonReliee(3, 2);

        assertTrue(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_arbre_false_non_connexe() {
        initChaine(3);
        ajouterChaineNonReliee(4);
        ajouterChaineNonReliee(1);

        assertFalse(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_arbre_false_non_connexe_bis() {
        initVide();
        ajouterArbreNonReliee(3, 4);
        ajouterChaineNonReliee(1);

        assertFalse(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_arbre_false_cycle() {
        initCycle(33);

        assertFalse(g.estArbre());
    }

    // @Disabled
    @Test
    public void test_est_foret_true_une_chaine() {
        initChaine(33);

        assertTrue(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_true_plusieurs_chaines() {
        initChaine(33);
        ajouterChaineNonReliee(12);
        ajouterChaineNonReliee(1);

        assertTrue(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_true_un_arbre() {
        initVide();
        ajouterArbreNonReliee(10, 3);

        assertTrue(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_true_plusieurs_arbres() {
        initVide();
        ajouterArbreNonReliee(3, 2);
        ajouterArbreNonReliee(9, 1);
        ajouterArbreNonReliee(9, 2);
        ajouterArbreNonReliee(0, 0);

        assertTrue(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_true_vide() {
        initVide();

        assertTrue(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_false_un_cycle() {
        initCycle(3);

        assertFalse(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_false_plusieurs_cycle() {
        initCycle(3);
        ajouterCycleNonReliee(33);
        ajouterCycleNonReliee(12);

        assertFalse(g.estForet());
    }

    // @Disabled
    @Test
    public void test_est_foret_false_plusieurs_cycle_et_arbres() {
        initChaine(13);
        ajouterArbreNonReliee(12, 3);
        ajouterChaineNonReliee(1);
        ajouterCycleNonReliee(3);
        ajouterCycleNonReliee(0);

        assertFalse(g.estForet());
    }

    // @Disabled
    @Test
    public void test_possedeUnCycle_false_graphe_vide() {
        initVide();

        assertFalse(g.possedeUnCycle());
    }

    // @Disabled
    @Test
    public void test_possedeUnCycle_true_graphe_connexe() {
        initCycle(23);

        assertTrue(g.estConnexe());
        assertTrue(g.possedeUnCycle());
    }

    // @Disabled
    @Test
    public void test_possedeUnCycle_true_graphe_non_connexe() {
        initCycle(22);
        ajouterCycleNonReliee(30);
        ajouterCycleNonReliee(3);
        ajouterChaineNonReliee(12);

        assertEquals(67, g.getNbSommets());
        assertEquals(66, g.getNbAretes());
        assertFalse(g.estConnexe());
        assertTrue(g.possedeUnCycle());
    }

    // @Disabled
    @Test
    public void test_possedeUnCycle_false_graphe_connexe() {
        initSommet(23);
        relierUnSommetATous(g.getSommet(0));

        assertTrue(g.estConnexe());
        assertFalse(g.possedeUnCycle());
    }

    // @Disabled
    @Test
    public void test_possedeUnCycle_false_graphe_non_connexe() {
        initChaine(33);
        ajouterChaineNonReliee(3);
        ajouterChaineNonReliee(1);

        assertFalse(g.estConnexe());
        assertFalse(g.possedeUnCycle());
    }

    // @Disabled
    @Test
    public void test_possedeUnCycle_false_graphe_non_connexe_0_arete() {
        initSommet(30);

        assertFalse(g.estConnexe());
        assertFalse(g.possedeUnCycle());
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_true_cas_extreme() {
        initChaine(10);

        assertTrue(g.possedeSousGrapheComplet(0));
        assertTrue(g.possedeSousGrapheComplet(1));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_false_k_pas_valide() {
        initChaine(10);

        assertFalse(g.possedeSousGrapheComplet(12));
        assertFalse(g.possedeSousGrapheComplet(-1));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_chaine() {
        initChaine(33);

        assertFalse(g.possedeSousGrapheComplet(13));
        assertFalse(g.possedeSousGrapheComplet(3));
        assertTrue(g.possedeSousGrapheComplet(2));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_cycle() {
        initCycle(33);

        assertFalse(g.possedeSousGrapheComplet(13));
        assertFalse(g.possedeSousGrapheComplet(3));
        assertTrue(g.possedeSousGrapheComplet(2));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_1() {
        initCycle(3);

        assertFalse(g.possedeSousGrapheComplet(13));
        assertTrue(g.possedeSousGrapheComplet(3));
        assertTrue(g.possedeSousGrapheComplet(2));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_2() {
        initSommet(20);
        relierAllSommets();
        ajouterCycleNonReliee(30);

        assertTrue(g.possedeSousGrapheComplet(20));
        assertTrue(g.possedeSousGrapheComplet(3));
        assertTrue(g.possedeSousGrapheComplet(2));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_3() {
        initSommet(10);
        relierUnSommetATous(g.getSommet(0));
        ajouterChaineNonReliee(10);
        ajouterCycleNonReliee(3);
        ajouterArbreNonReliee(20, 20);

        assertFalse(g.possedeSousGrapheComplet(10));
        assertFalse(g.possedeSousGrapheComplet(5));
        assertTrue(g.possedeSousGrapheComplet(3));
        assertTrue(g.possedeSousGrapheComplet(2));
    }

    // @Disabled
    @Test
    public void test_possedeSousGrapheComplet_4() {
        initSommet(10);
        relierUnSommetATous(g.getSommet(0));
        g.getSommet(2).ajouterVoisin(g.getSommet(7));
        g.ajouterSommet(Sommet.sommetBuilder.setIndice(99).createSommet());
        ajouterChaineNonReliee(10);
        ajouterCycleNonReliee(3);
        ajouterArbreNonReliee(20, 5);

        assertFalse(g.possedeSousGrapheComplet(5));
        assertTrue(g.possedeSousGrapheComplet(3));
        assertTrue(g.possedeSousGrapheComplet(2));
    }

    // @Disabled
    @Test
    public void test_getSommetsDegresDecroissant() {
        initSommet(4);
        relierUnSommetATous(g.getSommet(0));
        ajouterChaineNonReliee(3);
        ajouterCycleNonReliee(3);
        g.ajouterSommet(Sommet.sommetBuilder.setIndice(99).createSommet());

        // (3,2,2,2,2,1,1,1,1,1,0)
        List<Integer> expected = new ArrayList<>(List.of(3,2,2,2,2,1,1,1,1,1,0));
        List<Integer> expected2 = new ArrayList<>(List.of(3,2,2,2,2));

        assertEquals(11, g.getSommetsDegresDecroissant().size());
        assertEquals(5, g.getSommetsDegresDecroissantDegreSuperieurAOrdre(3).size());
        assertIterableEquals(expected, g.getSequenceDegres());
        assertIterableEquals(expected2, g.getSequenceDegres(3));
    }
}