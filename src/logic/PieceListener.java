/**
 * @author Johanne
 */
package logic;

/**
 * Interface for listening to PieceEvents.
 */
public interface PieceListener {
	void pieceMoved(PieceEvent event);
}