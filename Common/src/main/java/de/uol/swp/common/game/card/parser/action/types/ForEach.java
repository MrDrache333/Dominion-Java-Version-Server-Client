package de.uol.swp.common.game.card.parser.action.types;

import de.uol.swp.common.game.card.Card;
import de.uol.swp.common.game.card.parser.action.CardAction;
import de.uol.swp.common.game.card.parser.action.CompositeCardAction;

import java.util.ArrayList;

public class ForEach extends CardAction {

    private ArrayList<Card> cards;
    private ArrayList<CompositeCardAction> actions;

    public ForEach(ArrayList<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean execute() {
        return false;
    }
}
