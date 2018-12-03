/**
 * @author Johanne
 */
package logic;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * The Ludo logic class. This class handles the logical game: player turns, piece movement
 * and everything else related to the game mechanics.
 */
public class Ludo {
	// === PLAYERS ================================================================================= //
	public static final int RED = 0;	// Constants representing player indices
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	
	String[] playerName = new String[4];  // Players' display names
	boolean[] playerStatus = new boolean[4]; // Players' status (active/inactive)

	
	// === FIELDS AND PIECES ======================================================================= //
	int[][] position = new int[4][4]; // Each piece's current local field, by player and piece number
	
	public static class Field {	// Each field can have 0-4 pieces on it, but only of one color at a time
		boolean[] pieces; // Provides indices for pieces on this field; corresponds with player pieces
		int color = -1; // Which player the pieces on this field belong to
		
		protected Field() {
			pieces = new boolean[4];
		}
		
		/**
		 * Sums the filled (true) slots of pieces[] to get the number of pieces.
		 * @return Number of pieces on this field
		 */
		public int noOfPieces() {
			int count = 0;
			for (int i=0; i<4; i++) {
				if (pieces[i]) {
					count++;
				}
			}
			return count;
		}
		
		/**
		 * Gets the index of a single piece on the field.
		 * @return The piece number of the first piece found (0-3)
		 */
		public int getPiece() {
			for (int i=0; i<4; i++) {
				if (pieces[i]) {
					return i;
				}
			}
			return -1;	// There are no pieces
		}
	}
	
	Field[] fields;	// 92 global fields, instantiated by Ludo's constructors
	
	/**
	 * Map of global to local fields for each player.
	 * Size 5*92; row index 4 contains the global fields.
	 */
	static final int[][] board = {
			{0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 153, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 54, 55, 56, 57, 58, 59, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, 
			{-1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 153, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, -1, -1, -1, -1, -1, -1, 54, 55, 56, 57, 58, 59, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, 
			{-1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 153, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 54, 55, 56, 57, 58, 59, -1, -1, -1, -1, -1, -1}, 
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 153, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 54, 55, 56, 57, 58, 59},
			{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91} 
		};
	
	/**
	 * The global fields in order of each player's path from start to finish.
	 * Size 4*60.
	 */
	static final int[][] paths = {
			{0, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 16, 68, 69, 70, 71, 72, 73},
			{4, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 74, 75, 76, 77, 78, 79},
			{8, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 80, 81, 82, 83, 84, 85},
			{12, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 86, 87, 88, 89, 90, 91}
		};
	
	
	// === GAME STATES ============================================================================= //
	int currentPlayer = 0;	// The player whose turn it currently is (0-3). RED starts.
	int attempt = -1; // -1 for in-game turns, 0-2 for when player gets 3 throws
	boolean extraThrow = false; // True when rolling 6 on a normal turn,
										// false when extra turn is over
	int currentDice;		// The current dice
	boolean checkOnly = false; // Whether movePiece() is called to just check or actually move
	int winner = -1;	// Used to set and get the winner of the game
	
	/**
	 * The status of the game.
	 * Created: a game with no players.
	 * Initiated: a game with players.
	 * Started: a game where the dice has been thrown.
	 * Finished: a game with a winner.
	 */
	enum Status {CREATED, INITIATED, STARTED, FINISHED}
	Status status;
	
	ArrayList<DiceListener> diceListeners = new ArrayList<>();
	ArrayList<PieceListener> pieceListeners = new ArrayList<>();
	ArrayList<PlayerListener> playerListeners = new ArrayList<>();
	
	// === METHODS ================================================================================= //
	
	/**
	 * Creates a Ludo game with the given players. Called with at least two names;
	 * unused parameters should be null.
	 * @param p1 The name of player 1
	 * @param p2 The name of player 2
	 * @param p3 The name of player 3
	 * @param p4 The name of player 4
	 * @throws NotEnoughPlayersException if fewer than two names are given
	 */
	public Ludo(String p1, String p2, String p3, String p4) {
		String[] args = {p1, p2, p3, p4};
		int j=0;					// Fills the first j slots of player[], playerStatus[] and
		for (int i=0; i<4; i++) {	 // playerListeners[], vacant slots are at the end
			if (args[i] != null) {
				playerName[j] = args[i];
				playerStatus[j] = true;
				j++;
			}
		}
		if (j < 2) {
			throw new NotEnoughPlayersException();
		}
		
		fields = new Field[92];
		for (int i=0; i<92; i++) {
			fields[i] = new Field();
		}
		
		for (int pl=0; pl < noOfPlayers(); pl++) {
			for (int pi=0; pi<4; pi++) {
				position[pl][pi] = 0;
				fields[pl*4].pieces[pi] = true;
				fields[pl*4].color = pl;
			}
		}
		status = Status.INITIATED;
	}
	
	/**
	 * Gets the entire fields array.
	 * @return fields[]
	 */
	public Field[] getFields() {
		return fields;
	}
	
	/**
	 * Gets the status of the game.
	 * @return The game status (Created, Initiated, Started, Finished)
	 */
	public String getStatus() {
		return status.name();
	}
	
	/**
	 * Gets the number of registered players, active and inactive.
	 * @return Number of players
	 */
	public int noOfPlayers() {
		int j=0;
		for (int i=0; i<4; i++) {
			if (playerName[i] != null) {
				j++;
			}
		}
		return j;
	}
	
	/**
	 * Gets the number of active players.
	 * @return Number of active players
	 */
	public int activePlayers() {
		int j=0;
		for (int i=0; i<4; i++) {
			if (playerStatus[i]) {
				j++;
			}
		}
		return j;
	}
	
	/**
	 * Gets the name of the given player. If the player is inactive,
	 * prepends "Inactive: " to the return value, but does not change the stored name.
	 * @param index Player (RED, BLUE, YELLOW, GREEN)
	 * @return The name of the player, or null if the player does not exist
	 */
	public String getPlayerName(int index) {
		if (!playerStatus[index] && playerName[index] != null) {
			return "Inactive: "+playerName[index];
		}
		return playerName[index];
	}
	
	/**
	 * Gets the local position of the given piece.
	 * @param player Whose piece (RED, BLUE, YELLOW, GREEN)
	 * @param piece Which piece (0-3)
	 * @return The local position of the piece (0-53)
	 */
	public int getLocalPosition(int player, int piece) {
		return position[player][piece];
	}
	
	/**
	 * Converts from player (local) positions to board (global) ones.
	 * @param player The player (RED, BLUE, YELLOW, GREEN)
	 * @param local The local field to convert
	 * @return The global field that the local field translates to
	 */
	public int getGlobalFromLocal(int player, int local) {
		return paths[player][local];
	}

	/**
	 * Gets the player whose turn it currently is.
	 * @return The player whose turn it is (0-3)
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Gets the current attempt number.
	 * @return Current attempt (-1 or 0-3)
	 */
	public int getAttempt() {
		return attempt;
	}
	
	/**
	 * Gets whether the current player has an extra throw.
	 * @return Whether the current player has an extra throw
	 */
	public boolean getExtraThrow() {
		return extraThrow;
	}
	
	/**
	 * Gets the player that won (0-3).
	 * @return 0-3 if the game is won, -1 otherwise
	 */
	public int getWinner() {
		return winner;
	}
	
	/**
	 * Adds a new listener to the list of DiceListeners.
	 * @param dl An object that implements DiceListener
	 */
	public void addDiceListener(DiceListener dl) {
		diceListeners.add(dl);
	}
	
	/**
	 * Adds a new listener to the list of PieceListeners.
	 * @param pl An object that implements PieceListener
	 */
	public void addPieceListener(PieceListener pl) {
		pieceListeners.add(pl);
	}
	
	/**
	 * Adds a new listener to the list of PlayerListeners.
	 * @param pl An object that implements PlayerListener
	 */
	public void addPlayerListener(PlayerListener pl) {
		playerListeners.add(pl);
	}
	
	/**
	 * "Throws the dice", generating a number 1 through 6.
	 * @return A random number (1-6)
	 */
	public int throwDice() {
		SecureRandom random = new SecureRandom();
		return random.nextInt(6)+1;
	}
	
	/**
	 * With the dice rolled, the current player gets to move.
	 * Afterward, the turn goes to the next player if the current player
	 * doesn't get more moves. This method controls turns and rerolls.
	 * @param dice The number that was rolled (1-6)
	 * @return dice The dice that was rolled
	 */
	public int throwDice(int dice) {
		int player = getCurrentPlayer();
		
		currentDice = dice;
		status = Status.STARTED;
		
	  	int[] pos = new int[4];		// Current player's piece positions
		boolean[] inGame = new boolean[4]; // Whether each piece is on the board (in the game)
		
		// Determine whether the player gets 3 rolls
		for (int i=0; i<4; i++) {	// Go through the four pieces
			pos[i] = getLocalPosition(player, i);
			// True if piece is at home or goal, false if elsewhere on the board
			inGame[i] = (pos[i] == 0 || pos[i] == 59);
		}
		
		// The player gets 3 dice rolls
		if (inGame[0] && inGame[1] && inGame[2] && inGame[3] && attempt == -1) {
			attempt = 0;
		}
		
		for (DiceListener dl : diceListeners) {	// Call all DiceListeners
			dl.diceThrown(new DiceEvent(this, player, dice)); // The event handler reads from attempt
		}

		if (attempt > -1) {	// The player is on one of 3 attempts to roll 6
			if (attempt < 2) {	// Non-final attempt
				if (dice == 6) {// Guaranteed possible to move
					attempt = -1;
				} else {			// Impossible to move
					attempt++;
				}
			} else {				// Final attempt
				attempt = -1;
				if (dice != 6) {// Impossible to move
					nextPlayer();
				}
			}
		} else if (extraThrow) { // The player is on an extra throw
			extraThrow = false;
			checkMoves(player, dice);
		} else {	// Standard throw
			checkMoves(player, dice);
		}
		return dice;
	}
	
	/**
	 * Checks whether a player has any available moves, given that they're on a standard throw,
	 * i.e. has at least one piece on the board. This is done by setting the checkOnly flag
	 * and calling movePiece() for all of the player's pieces that are on the board.
	 * If the player has no moves, the turn goes to the next player.
	 * This is a helper method for throwDice().
	 * @param player The current player
	 * @param dice The dice that was thrown
	 */
	protected void checkMoves(int player, int dice) {
	  	int[] pos = new int[4];		// The player's piece positions
		boolean[] move = new boolean[4]; // Whether each piece can be moved with the given dice
		
		checkOnly = true;
		// For each piece on the board, call movePiece() and get the return value
		// If dice is 6, pieces at home can be moved out. The rest are set to false
		for (int i=0; i<4; i++) {
			pos[i] = getLocalPosition(player, i);
			if (pos[i] != 0 && pos[i] != 59) {				
				move[i] = movePiece(player, pos[i], pos[i]+dice); // Check if piece can be moved
			} else if (pos[i] == 0 && dice == 6) { // This is a guaranteed possible move
				move[i] = true;
			} else {
				move[i] = false;	// Impossible to move
			}
		}
		
		if (!move[0] && !move[1] && !move[2] && !move[3]) { // No moves available, next player
			nextPlayer();
		} else {	// Moves available, notify the controller which pieces
			for (DiceListener dl : diceListeners) {
				dl.movesChecked(new MovesCheckedEvent(player, move));
			}
		}
		checkOnly = false;
	}
	
	/**
	 * Updates currentPlayer. Called at the end of each turn. 
	 * If the game is not won, the turn goes to the next player.
	 */
	protected void nextPlayer() {
		for (PlayerListener pl : playerListeners) {					// Old player's turn is over
			pl.playerStateChanged(new PlayerEvent(this, currentPlayer, PlayerEvent.WAITING));
		}
		do {
			if (currentPlayer == noOfPlayers()-1) {
				currentPlayer = 0;
			} else {
				currentPlayer++;
			}
		} while (!playerStatus[currentPlayer]);
		
		for (PlayerListener pl : playerListeners) {				// Now there is a new currentPlayer
			pl.playerStateChanged(new PlayerEvent(this, currentPlayer, PlayerEvent.PLAYING));
		}
	}
	
	/**
	 * Checks the given move, performs it if valid and checkOnly is false,
	 * and returns whether it is valid. As the name implies, this method
	 * controls piece movement.
	 * @param player RED, BLUE, YELLOW, GREEN
	 * @param currentLocal Current local position
	 * @param newLocal New local position (usually current+dice)
	 * @return Whether given move is legal
	 */
	public boolean movePiece(int player, int currentLocal, int newLocal) {
		int currentGlobal = getGlobalFromLocal(player, currentLocal); // The global current field
		int newGlobal; // The global destination field
		int tempLocal = currentLocal; // For checking each field between current and destination
		int tempGlobal;
				
		// Determine whether player gets an extra throw
		if (currentDice == 6) { // Extra throw when this move is not putting out a new piece
			extraThrow = (currentLocal == 0) ? false : true;
		}
				
		// Dice is too high to get to the finish - invalid destination
		if (newLocal > 59) {
			if (!checkOnly) {
				nextPlayer();
			}
			return false;
		} else {	// The destination is within bounds, so we can convert it to global
			newGlobal = getGlobalFromLocal(player, newLocal);
		}
		
		// Check for road blockages up ahead
		while (tempLocal < newLocal) {
			tempLocal++;	// Increment the local position, then convert that to global
			// Because we increment first, will also check for blockages at the destination
			tempGlobal = getGlobalFromLocal(player, tempLocal);
			if (fields[tempGlobal].noOfPieces() > 1 && fields[tempGlobal].color != player) {
				if (!checkOnly) {
					nextPlayer();
				}
				return false;	// You shall not pass
			}
		}
		
		// There is another player's piece at the destination
		if (fields[newGlobal].noOfPieces() == 1 && fields[newGlobal].color != player) {
			int opponent = fields[newGlobal].color;
			// The destination is not the opponent's safe field
			if (newGlobal != 16+opponent*13) {
				if (!checkOnly) {
					// Knock the existing piece home
					performMove(board[opponent][newGlobal], newGlobal, opponent*4);
					// Move my piece to the destination
					performMove(currentLocal, currentGlobal, newGlobal);
					if (!extraThrow) {
						nextPlayer();
					}
				}
				return true;
			} else {	// Can't land on a piece that sits on its safe field
				if (!checkOnly) {
					nextPlayer();
				}
				return false;
			}
		} else if (newLocal == 59 && fields[73+player*6].noOfPieces() == 3) {
			// Winning move. The game is finished here!
			if (!checkOnly) {
				performMove(currentLocal, currentGlobal, newGlobal);
				winner = player;
				for (PlayerListener pl : playerListeners) {
					pl.playerStateChanged(new PlayerEvent(this, winner, PlayerEvent.WON));
				}
				status = Status.FINISHED;
			}
			return true;
		} else { // Standard - the destination is vacant
			if (!checkOnly) {
				performMove(currentLocal, currentGlobal, newGlobal);
				if (!extraThrow) {
					nextPlayer();
				}
			}
			return true;
		}
	}
	
	/**
	 * Moves the first piece on the current field to its slot on the new field.
	 * This is a helper method for movePiece().
	 * @param currentGlobal Current global position
	 * @param newGlobal New global position
	 */
	protected void performMove(int currentLocal, int currentGlobal, int newGlobal) {
		int player = fields[currentGlobal].color;
		int piece = fields[currentGlobal].getPiece();
		int newLocal = board[player][newGlobal];
		
		if (newLocal == 153) {		// Disambiguate the field 1/53
			newLocal = (currentGlobal < 16) ? 1 : 53;	// 1 if moving out from home
		}
		
		fields[currentGlobal].pieces[piece] = false;	// Remove from old field
		if (fields[currentGlobal].noOfPieces() == 0) {	// This was the last piece on the old field
			fields[currentGlobal].color = -1;			// Reset color
		}
		fields[newGlobal].pieces[piece] = true;		// Add to new field
		fields[newGlobal].color = player;			// Set color even if already set
		position[player][piece] = newLocal;			// Overwrite position
		
		for (PieceListener pl : pieceListeners) {
			pl.pieceMoved(new PieceEvent(this, player, piece, currentLocal, newLocal));
		}
	}
}