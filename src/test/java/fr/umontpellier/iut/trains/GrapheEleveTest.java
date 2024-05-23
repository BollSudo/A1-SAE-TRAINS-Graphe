package fr.umontpellier.iut.trains;

import fr.umontpellier.iut.graphes.Graphe;
import fr.umontpellier.iut.graphes.Sommet;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;

import java.util.Set;

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


    @Disabled
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
        assertIterableEquals(s1.getVoisins(), Set.of(s2, s0));
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

    @Disabled
    @Test
    public void test_sequenceEstGraphe(){
        throw new RuntimeException("Pas encore implémenter");
    }

}