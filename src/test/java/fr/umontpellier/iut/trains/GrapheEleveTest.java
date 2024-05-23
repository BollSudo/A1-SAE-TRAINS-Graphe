package fr.umontpellier.iut.trains;

import fr.umontpellier.iut.graphes.Graphe;
import fr.umontpellier.iut.graphes.Sommet;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;

import javax.sound.midi.Sequence;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class GrapheEleveTest {

    Graphe g;

    public void initVide() {
        g = new Graphe();
    }

    public void initUnSommet() {
        initVide();
        g.ajouterSommet(1);
    }

    public void init5voisinsEntreEux(){
        g = new Graphe(5);
        for (int s = 0; s<5; s++){
            for (int v = 0; v < 5; v++){
                if (v != s){
                    g.getSommet(s).ajouterVoisin(g.getSommet(v));
                }
            }
        }
    }

    public void init5voisinUnPoint(){
        g = new Graphe(5);
        for (int v = 1; v<5; v++){
            g.getSommet(0).ajouterVoisin(g.getSommet(v));
        }
    }


    @Disabled
    @Test
    public void test_degre() {
        initUnSommet();
        g.ajouterSommet(1);
        g.ajouterSommet(2);
        g.ajouterSommet(3);
        g.ajouterSommet(4);
        Sommet s1 = g.getSommet(1);
        Sommet s2 = g.getSommet(2);
        Sommet s3 = g.getSommet(3);
        Sommet s4 = g.getSommet(4);

        s1.ajouterVoisin(s2);
        s1.ajouterVoisin(s4);

        assertTrue(s1.getVoisins().containsAll(Set.of(s2, s4)));
        assertFalse(s1.getVoisins().contains(s3));
        assertEquals(2, g.degre(s1));
        assertEquals(1, g.degre(s2));
        assertEquals(0, g.degre(s3));
        assertEquals(1, g.degre(s4));
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

}