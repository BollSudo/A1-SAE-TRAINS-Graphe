package fr.umontpellier.iut.trains;

import fr.umontpellier.iut.graphes.Graphe;
import fr.umontpellier.iut.graphes.Sommet;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
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
        initSommet(i);
        for (int j = 0; j < i-1; j++) {
            g.getSommet(j).ajouterVoisin(g.getSommet(j+1));
        }
        g.getSommet(i-1).ajouterVoisin(g.getSommet(0));
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

    @Disabled
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

}