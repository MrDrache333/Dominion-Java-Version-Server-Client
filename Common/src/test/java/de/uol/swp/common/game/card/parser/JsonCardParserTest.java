package de.uol.swp.common.game.card.parser;

import de.uol.swp.common.game.card.ActionCard;
import de.uol.swp.common.game.card.Card;
import de.uol.swp.common.game.card.parser.components.CardAction.CardAction;
import de.uol.swp.common.game.card.parser.components.CardPack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonCardParserTest {


    @Test
    void loadPack() {

        JsonCardParser parser = new JsonCardParser();
        CardPack pack = parser.loadPack("Basispack");
        assertNotNull(pack);

        //Pack
        assertEquals("Basispack", pack.getName(), "Name des Packs konnte nicht gelesen werden");
        assertEquals("...", pack.getDescription(), "Beschreibung des Packs konnte nicht gelesen werden");
        assertNotNull(pack.getCards());

        //Stack
        assertEquals(3, pack.getCards().getMoneyCards().size());
        assertEquals(4, pack.getCards().getValueCards().size());
        assertEquals(24, pack.getCards().getActionCards().size());
        assertEquals(1, pack.getCards().getCurseCards().size());

        //Karten sammeln
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(pack.getCards().getActionCards());
        cards.addAll(pack.getCards().getMoneyCards());
        cards.addAll(pack.getCards().getValueCards());
        cards.addAll(pack.getCards().getCurseCards());

        //All Cards
        for (Card card : cards) {
            assertNotNull(card);
            assertNotNull(card.getName());

            if (card.getCardType().equals(Card.Type.ActionCard)) {
                ActionCard actionCard = (ActionCard) card;
                if (!actionCard.getType().equals(ActionCard.ActionType.Reaction)) {
                    assertNotNull(actionCard.getActions());
                    for (CardAction action : actionCard.getActions()) {
                        assertNotNull(action);
                    }
                }
            }
        }
    }
}