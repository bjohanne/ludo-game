/**
 * @author Johanne
 */
package logic;

/**
 * Interface for listening to PlayerEvents.
 */
public interface PlayerListener {
	void playerStateChanged(PlayerEvent event);
}
