package de.uol.swp.common.game.card;

/**
 * Die AKtionkarte
 */
public class ActionCard extends Card {

    /**
     * Erstellt eine neue Aktionskarte
     *
     * @param name Der name der Karte
     * @param id   Die ID der Karte
     * @author KenoO
     * @since Sprint 5
     */
    public ActionCard(String name, short id, short costs) {
        super(Type.ActionCard, name, id, costs);
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
}
