package de.uol.swp.common.game.card.parser.action.types;

import de.uol.swp.common.game.card.parser.action.CompositeCardAction;
import de.uol.swp.common.game.card.parser.action.SimpleCardAction;

/**
 * Die IF-Aktion (Bedingte ausführung einer weiteren Aktion).
 */
public class If extends SimpleCardAction {

    private CompositeCardAction condition;   //Die Aktion, deren ergebnis ausgewertet wird
    private boolean expectedResult = true;  //Gewünschtes Ergebnis der vorherigen Aktion
    private CompositeCardAction conditionedAction;   //Die Aktion, deren Ausführung von dem Ergebnis der vorhergegangen Aktion abhängt

    /**
     * Erstellt eine neue If-Aktion
     *
     * @param condition         Die Aktion, deren ergebnis ausgewertet wird
     * @param expectedResult    Gewünschtes Ergebnis der vorherigen Aktion
     * @param conditionedAction Die Aktion, deren ausführung von dem ergebnis der vorhergegangen Aktion abhängt
     * @author KenoO
     * @since Sprint 6
     */
    public If(CompositeCardAction condition, boolean expectedResult, CompositeCardAction conditionedAction) {
        this.condition = condition;
        this.expectedResult = expectedResult;
        this.conditionedAction = conditionedAction;
    }

    @Override
    public boolean execute() {
        //TODO
        return false;
    }
}
