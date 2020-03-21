package de.uol.swp.common.game.card;

import de.uol.swp.common.game.card.parser.action.CompositeCardAction;

import java.util.ArrayList;

/**
 * Die AKtionkarte
 */
public class ActionCard extends Card {

    private ArrayList<CompositeCardAction> actions;

    /**
     * Erstellt eine neue Aktionskarte
     *
     * @param name Der name der Karte
     * @param id   Die ID der Karte
     * @author KenoO
     * @since Sprint 5
     */
    public ActionCard(String name, short id, short costs, ArrayList<CompositeCardAction> actions) {
        super(Type.ActionCard, name, id, costs);
        this.actions = actions;
    }

    /**
     * Bestimmt den Aktionstyp einer Karte.
     */
    public enum ActionType {
        /**
         * Reaktionskarte.
         */
        Reaction,
        /**
         * Angriffskarte.
         */
        Attack,
        /**
         * Normale Aktionskarte.
         */
        Action
    }

    public ArrayList<CompositeCardAction> getActions() {
        return actions;
    }
}
