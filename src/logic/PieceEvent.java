/**
 * @author Johanne
 */
package logic;

/**
 * A PieceEvent is created every time a piece is moved.
 */
public class PieceEvent {
	Ludo game;
	int player;
	int piece;
	int currentPos;
	int newPos;
	
	/**
	 * New PieceEvents are created in Ludo's performMove(). If a piece is knocked home
	 * by another, this creates an additional PieceEvent.
	 * @param game The Ludo game
	 * @param player The player that owns the piece
	 * @param piece	The piece that was moved
	 * @param currentPos The piece's position before the move
	 * @param newPos The piece's position after the move
	 */
	public PieceEvent(Ludo game, int player, int piece, int currentPos, int newPos) {
		this.game = game;
		this.player = player;
		this.piece = piece;
		this.currentPos = currentPos;
		this.newPos = newPos;
	}
	
	/**
	 * Gets the player that owns the piece.
	 * @return The player that owns the moved piece
	 */
	public int getPlayer() {
		return player;
	}
	
	/**
	 * Gets the piece that was moved.
	 * @return The piece that was moved
	 */
	public int getPiece() {
		return piece;
	}
	
	/**
	 * Gets the piece's position after the move
	 * @return The piece's new local position
	 */
	public int getNewPos() {
		return newPos;
	}
}