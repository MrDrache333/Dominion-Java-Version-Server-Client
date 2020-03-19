package de.uol.swp.common.game.card;

import com.google.gson.annotations.SerializedName;

/**
 * Die Spielkarte
 */
public abstract class Card {

    /**
     * The Name.
     */
    private String name;
    /**
     * The Id.
     */
    private short id;
    /**
     * The Cardtype.
     */
    private Type cardtype;
    /**
     * The Costs.
     */
    @SerializedName("cost")
    private short costs;

    /**
     * Erstellt eine neue Spielkarte
     *
     * @param cardtype Der Kartentyp
     * @param name     Der Kartenname
     * @param id       Die KartenID
     * @author KenoO
     * @since Sprint 5
     */
    Card(Type cardtype, String name, short id, short costs) {
        this.cardtype = cardtype;
        this.name = name;
        this.id = id;
        this.costs = costs;
    }

    /**
     * Gets costs.
     *
     * @return the costs
     * @author KenoO
     * @since
     */
    public short getCosts() {
        return costs;
    }

    /**
     * Gibt den namen der Karte zurück
     *
     * @return Der name der Karte
     * @author KenoO
     * @since Sprint 5
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt die ID der Karte zurück
     *
     * @return Die ID der Karte
     * @author KenoO
     * @since Sprint 5
     */
    public short getId() {
        return id;
    }

    /**
     * Gibt den Typ einer Karte zurück
     *
     * @return Der Kartentyp
     * @author KenoO
     * @since Sprint 5
     */
    public Type getCardtype() {
        return cardtype;
    }

    /**
     * The enum Type.
     */
    public enum Type {
        /**
         * Kein Festgelegter Kartentyp
         */
        None,
        /**
         * Der Typ Aktionskarte
         */
        ActionCard,
        /**
         * Der Typ Reaktionskarte
         */
        ReactionCard,
        /**
         * Der Typ Provinzkarte
         */
        ValueCard,
        /**
         * Der Typ Geldkarte
         */
        MoneyCard,
        /**
         * Der Typ Fluchkarte.
         */
        Cursecard
    }
}
