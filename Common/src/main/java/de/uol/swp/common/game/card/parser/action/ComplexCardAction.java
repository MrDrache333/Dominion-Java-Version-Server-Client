package de.uol.swp.common.game.card.parser.action;

import de.uol.swp.common.game.AbstractPlayground;
import de.uol.swp.common.game.card.Card;

public abstract class ComplexCardAction extends CardAction {

    //Gibt an, ob eine durch eine Aktionskarte gezogene Karte direkt auf die Hand genommen werden muss
    private boolean directHand = false;
    //Gibt an, welcher Kartentyp bei der Aktion verwendet werden darf(z.B. beim Ablegen/ Ziehen einer Karte)
    private Card.Type allowedCardType = Card.Type.None;
    //Gibt an, wie hoch der Wert der durch die Aktion aufgenommene/ abgelegte Karte mindestens/ höchstens sein muss/darf
    private Value hasWorth = new Value((short) 0);
    //Gibt an, wie hoch der Preis der durch die Aktion aufgenommene/ abgelegte Karte mindestens/ höchstens sein muss/darf
    private Value hasCost = new Value((short) 0);
    //Gibt an, auf wen die Aktion anzuwenden ist aus sicht des aktuellen Spielers.
    private ExecuteType executeType = ExecuteType.None;
    //Gibt an, ob für jede hinzugefügte Karte eine entfernt werden muss und umgekehrt.
    private boolean equalizeCards = false;
    //Gibt an, woher eine Karte genommen werden muss.
    private AbstractPlayground.ZoneType cardSource = AbstractPlayground.ZoneType.None;
    //Gibt an, wohin eine Karte gelegt werden muss.
    private AbstractPlayground.ZoneType cardDestination = AbstractPlayground.ZoneType.None;
    //Gibt an, ob die Ausführung der Aktion durch den Benutzer abgebrochen werden kann (liefert bei Abbruch false als Rückgabewert bei execute())
    private boolean executionOptional = false;
    //Gibt an, ob die Karte, nachdem sie gespielt wurde, Entsorgt wird.
    private boolean removeCardAfter = false;
    //Gibt an, ob Karten, die durch diese Aktion abgelegt/ gezogen wurden, den anderen Spielern sichtbar sind. 
    private boolean hideCardDuringAction = true;
    //Aktion, die das ergebnis der vorrangegangenen Aktion als Eingabe erhält. 
    private CardAction nextAction = null;

    /**
     * Gets executeType
     *
     * @return Value of executeType.
     */
    public ExecuteType getExecuteType() {
        return executeType;
    }

    /**
     * Sets new executeType.
     *
     * @param executeType New value of executeType.
     */
    public void setExecuteType(ExecuteType executeType) {
        this.executeType = executeType;
    }

    /**
     * Gets nextAction.
     *
     * @return Value of nextAction.
     */
    public CardAction getNextAction() {
        return nextAction;
    }

    /**
     * Sets new nextAction.
     *
     * @param nextAction New value of nextAction.
     */
    public void setNextAction(CardAction nextAction) {
        this.nextAction = nextAction;
    }

    /**
     * Gets allowedCardType.
     *
     * @return Value of allowedCardType.
     */
    public Card.Type getAllowedCardType() {
        return allowedCardType;
    }

    /**
     * Sets new allowedCardType.
     *
     * @param allowedCardType New value of allowedCardType.
     */
    public void setAllowedCardType(Card.Type allowedCardType) {
        this.allowedCardType = allowedCardType;
    }

    /**
     * Gets executionOptional.
     *
     * @return Value of executionOptional.
     */
    public boolean isExecutionOptional() {
        return executionOptional;
    }

    /**
     * Sets new executionOptional.
     *
     * @param executionOptional New value of executionOptional.
     */
    public void setExecutionOptional(boolean executionOptional) {
        this.executionOptional = executionOptional;
    }

    /**
     * Gets removeCardAfter.
     *
     * @return Value of removeCardAfter.
     */
    public boolean isRemoveCardAfter() {
        return removeCardAfter;
    }

    /**
     * Sets new removeCardAfter.
     *
     * @param removeCardAfter New value of removeCardAfter.
     */
    public void setRemoveCardAfter(boolean removeCardAfter) {
        this.removeCardAfter = removeCardAfter;
    }

    /**
     * Gets directHand.
     *
     * @return Value of directHand.
     */
    public boolean isDirectHand() {
        return directHand;
    }

    /**
     * Sets new directHand.
     *
     * @param directHand New value of directHand.
     */
    public void setDirectHand(boolean directHand) {
        this.directHand = directHand;
    }

    /**
     * Gets hasCost.
     *
     * @return Value of hasCost.
     */
    public Value getHasCost() {
        return hasCost;
    }

    /**
     * Sets new hasCost.
     *
     * @param hasCost New value of hasCost.
     */
    public void setHasCost(Value hasCost) {
        this.hasCost = hasCost;
    }

    /**
     * Gets cardSource.
     *
     * @return Value of cardSource.
     */
    public AbstractPlayground.ZoneType getCardSource() {
        return cardSource;
    }

    /**
     * Sets new cardSource.
     *
     * @param cardSource New value of cardSource.
     */
    public void setCardSource(AbstractPlayground.ZoneType cardSource) {
        this.cardSource = cardSource;
    }

    /**
     * Gets cardDestination.
     *
     * @return Value of cardDestination.
     */
    public AbstractPlayground.ZoneType getCardDestination() {
        return cardDestination;
    }

    /**
     * Sets new cardDestination.
     *
     * @param cardDestination New value of cardDestination.
     */
    public void setCardDestination(AbstractPlayground.ZoneType cardDestination) {
        this.cardDestination = cardDestination;
    }

    /**
     * Gets equalizeCards.
     *
     * @return Value of equalizeCards.
     */
    public boolean isEqualizeCards() {
        return equalizeCards;
    }

    /**
     * Sets new equalizeCards.
     *
     * @param equalizeCards New value of equalizeCards.
     */
    public void setEqualizeCards(boolean equalizeCards) {
        this.equalizeCards = equalizeCards;
    }

    /**
     * Gets hasWorth.
     *
     * @return Value of hasWorth.
     */
    public Value getHasWorth() {
        return hasWorth;
    }

    /**
     * Sets new hasWorth.
     *
     * @param hasWorth New value of hasWorth.
     */
    public void setHasWorth(Value hasWorth) {
        this.hasWorth = hasWorth;
    }

    /**
     * Gets hideCardDuringAction.
     *
     * @return Value of hideCardDuringAction.
     */
    public boolean isHideCardDuringAction() {
        return hideCardDuringAction;
    }

    /**
     * Sets new hideCardDuringAction.
     *
     * @param hideCardDuringAction New value of hideCardDuringAction.
     */
    public void setHideCardDuringAction(boolean hideCardDuringAction) {
        this.hideCardDuringAction = hideCardDuringAction;
    }
}

class Value {

    private boolean set;    //Gibt an, ob ein Wert gesetzt wurde
    private short min;  //Minimal Value
    private short max;  //Maximal Value

    Value(short min) {
        this.min = min;
        this.max = min;
        this.set = min != 0;
    }

    /**
     * Gets min.
     *
     * @return Value of min.
     */
    public short getMin() {
        return min;
    }

    /**
     * Sets new min.
     *
     * @param min New value of min.
     */
    public void setMin(short min) {
        this.set = min != 0 && max != 0;
        this.min = min;
    }

    /**
     * Gets max.
     *
     * @return Value of max.
     */
    public short getMax() {
        return max;
    }

    /**
     * Sets new max.
     *
     * @param max New value of max.
     */
    public void setMax(short max) {
        this.set = max != 0 && min != 0;
        this.max = max;
    }

    /**
     * Gets set.
     *
     * @return Value of set.
     */
    public boolean isSet() {
        return set;
    }
}