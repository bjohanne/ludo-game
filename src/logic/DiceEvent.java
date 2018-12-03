/**
 * @author Johanne
 */
package logic;

/**
 * A DiceEvent is created every time the dice is thrown.
 */
public class DiceEvent {
	Ludo game;
	int player;
	int dice;
	
	/**
	 * New DiceEvents are created in Ludo's throwDice(dice).
	 * @param game The Ludo game
	 * @param player The player that threw the dice (current active player)
	 * @param dice The dice that was rolled
	 */
	public DiceEvent(Ludo game, int player, int dice) {
		this.game = game;
		this.player = player;
		this.dice = dice;
	}
	
	/**
	 * Gets the player that threw the dice.
	 * @return The player that threw the dice
	 */
	public int getPlayer() {
		return player;
	}
	
	/**
	 * Gets the dice that was thrown.
	 * @return The dice that was thrown
	 */
	public int getDice() {
		return dice;
	}
}