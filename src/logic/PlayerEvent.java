/**
 * @author Johanne
 */
package logic;

/**
 * A PlayerEvent is created every time a player's state changes,
 * i.e. when the turn goes to him, when his turn his over,
 * if he leaves the game and if he wins.
 */
public class PlayerEvent {
	Ludo game;
	int player;
	int state;
	static final int WAITING = 0;
	static final int PLAYING = 1;
	static final int WON = 2;
	
	/**
	 * PlayerEvents for changing turns are created in Ludo's nextPlayer(), one for the player
	 * finishing his turn and another for the player getting his turn.
	 * Winning events are created in movePiece() and leaving events in removePlayer().
	 * @param game The Ludo game
	 * @param player The player that changed state
	 * @param newState The state the player changed to
	 */
	public PlayerEvent(Ludo game, int player, int newState) {
		this.game = game;
		this.player = player;
		state = newState;
	}
	
	/**
	 * Gets the player whose state changed.
	 * @return The player that changed state
	 */
	public int getPlayer() {
		return player;
	}
	
	/**
	 * Gets the new state.
	 * @return The state the player changed to
	 */
	public int getState() {
		return state;
	}
}