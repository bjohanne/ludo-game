/**
 * @author Johanne
 */
package logic;

/**
 * Exception thrown when Ludo's constructor is called with fewer than two players,
 * but not the empty constructor.
 */
public class NotEnoughPlayersException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}