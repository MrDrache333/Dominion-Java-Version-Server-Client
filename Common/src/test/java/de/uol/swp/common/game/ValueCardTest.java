package de.uol.swp.common.game;


import de.uol.swp.common.game.card.Card;
import de.uol.swp.common.game.card.ValueCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValueCardTest {


    /**
     * Testet, ob eine Karte des Typs Value erstellt werden kann
     *
     * @author Paula
     * @since Sprint10
     */
    @Test
    public void createCardTest() {
        ValueCard card = new ValueCard("Provinz",(short)02, (short)5, (short) 10);
        assertEquals("Provinz", card.getName());
        assertEquals(2, card.getId());
        assertEquals(10, card.getValue());
        assertEquals(Card.Type.VALUECARD, card.getCardType());
        assertEquals((short) 5, card.getCosts());
    }

    /**
     * Testet den Setter der ValueCard
     *
     * @author Paula
     * @since Sprint10
     */
    @Test
    public void setValueCardTest() {
       ValueCard card = new ValueCard("Provinz",(short)02, (short)5, (short) 10);
        card.setValue((short) 100);
        assertEquals("Provinz", card.getName());
        assertEquals(2, card.getId());
        assertEquals(100, card.getValue());
        assertEquals(Card.Type.VALUECARD, card.getCardType());
        assertEquals((short) 5, card.getCosts());
    }
}
