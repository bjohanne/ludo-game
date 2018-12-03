/**
 * @author Johanne
 */
package logic;

/**
 * Interface for listening to DiceEvents and MovesCheckedEvents.
 */
public interface DiceListener {
	void diceThrown(DiceEvent event);
	void movesChecked(MovesCheckedEvent event);
}