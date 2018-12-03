/**
 * @author Johanne
 */
package logic;

/**
 * A MovesCheckedEvent is created every time checkMoves() is called, which only throwDice() does.
 */
public class MovesCheckedEvent {
	int player;
	boolean[] movable = new boolean[4];
	
	/**
	 * MovesCheckedEvents are created by checkMoves(), which is called by throwDice().
	 * @param game The Ludo game
	 * @param player The player that rolled the dice (current active player)
	 * @param movable boolean[4] Whether each of the pieces was found to be movable
	 */
	public MovesCheckedEvent(int player, boolean[] movable) {
		this.player = player;
		for (int i=0; i<movable.length; i++) {
			this.movable[i] = movable[i];
		}
	}
	
	/**
	 * Gets the player that rolled the dice (current active player)
	 * @return The player that rolled the dice
	 */
	public int getPlayer() {
		return player;
	}
	
	/**
	 * Gets the entire movable array
	 * @return The array containing whether each piece can be moved
	 */
	public boolean[] getMovable() {
		return movable;
	}
}