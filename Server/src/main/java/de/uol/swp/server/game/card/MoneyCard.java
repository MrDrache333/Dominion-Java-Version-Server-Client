package de.uol.swp.server.game.card;

/**
 * Die Geldkarte
 */
public class MoneyCard extends Card {

    private short Value;

    /**
     * Erstellt eine neue Geldkarte
     *
     * @param name Der name der Karte
     * @param id   Die ID der Karte
     */
    public MoneyCard(String name, short id) {
        super(Type.MoneyCard, name, id);
    }

    /**
     * Gibt die Kaufkraft der Karte zurück
     *
     * @return Der Kaufkraft
     */
    public short getValue() {
        return Value;
    }

    /**
     * Setzt die Kaufkraft der Karte
     *
     * @param value Die Kaufkraft
     */
    public void setValue(short value) {
        Value = value;
    }
}
