/**
 * @author Johanne
 */
package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logic.DiceEvent;
import logic.DiceListener;
import logic.Ludo;
import logic.MovesCheckedEvent;
import logic.PieceEvent;
import logic.PieceListener;
import logic.PlayerEvent;
import logic.PlayerListener;

/**
 * The controller class for the Ludo game itself. Used by LudoController and handles Ludo events in
 * addition to controlling all the graphics for the game.
 */
public class LudoController implements DiceListener, PieceListener, PlayerListener {
	
	@FXML private StackPane stackPane;
    @FXML private Label		player1Name;
    @FXML private ImageView player1Active;
    @FXML private Pane		player1Pane;
    @FXML private Label		player2Name;
    @FXML private ImageView player2Active;
    @FXML private Pane		player2Pane;
    @FXML private Label 	player3Name;
    @FXML private ImageView player3Active;
    @FXML private Pane		player3Pane;
    @FXML private Label 	player4Name;
    @FXML private ImageView player4Active;
    @FXML private Pane		player4Pane;
    @FXML private ImageView diceThrown;
    @FXML private Button 	throwTheDice;
	
	Ludo ludo;	// The Ludo object
	private int dice;	// moveForward() needs the dice

	Circle[][] pieces = new Circle[4][4];	// The graphical pieces
	Text[] labels = new Text[8];		// Labels telling how many pieces there currently are
										 // on each field; only shown when there are two or more
	double[][] labelPos = new double[labels.length][2];	// Stores the X and Y positions of each label
	
	boolean[][] movable = new boolean[4][4]; // Whether each piece can be moved at any time
	
	static final double UNIT = 48;	// The distance between fields
	static final double HOME = 216; // The distance from the center of the board
								  // to the center of each player's "home" area
	static final double OFFSETX = 17; // The X distance to put labels in the corner of fields
	static final double OFFSETY = -15; // The Y distance to put labels in the corner of fields
	
	enum Dir {UP, RIGHT, DOWN, LEFT, UPRIGHT, RIGHTDOWN, DOWNLEFT, LEFTUP}
	
	/**
	 * The fixed pattern used to define each player's path. Rotated depending on the player
	 * and repeated to create its full path from start to finish.
	 * Each slot tells which direction to move next.
	 */
	static final Dir[] pattern = {
			Dir.DOWN,  Dir.DOWN,  Dir.DOWN,  Dir.DOWN,  Dir.RIGHTDOWN,	// Common path
			Dir.RIGHT, Dir.RIGHT, Dir.RIGHT, Dir.RIGHT, Dir.RIGHT,
			Dir.DOWN,  Dir.DOWN,  Dir.LEFT,
			Dir.UP,	   Dir.LEFT,  Dir.LEFT,  Dir.LEFT,  Dir.LEFT, Dir.LEFT	// Home stretch
		};
	
	/**
	 * A mutable copy of pattern[]. Stores the current rotated pattern. Used by moveForward(),
	 * reset each time.
	 */
	Dir[] nextDir;
	
	@FXML
	void initialize() {
		ludo = new Ludo("Red", "Blue", "Yellow", "Green");	// Initialize the Ludo object
		ludo.addDiceListener(this);
		ludo.addPieceListener(this);
		ludo.addPlayerListener(this);
		
		// Add player display names
		player1Name.setText(ludo.getPlayerName(Ludo.RED));
		player2Name.setText(ludo.getPlayerName(Ludo.BLUE));
		player3Name.setText(ludo.getPlayerName(Ludo.YELLOW));
		player4Name.setText(ludo.getPlayerName(Ludo.GREEN));
		
		// Remove the dice icon for all but the first player
		player2Pane.getChildren().remove(player2Active);
		player3Pane.getChildren().remove(player3Active);
		player4Pane.getChildren().remove(player4Active);
		
		nextDir = new Dir[pattern.length];	// Allocate to nextDir[] the same size as pattern[]
		
		for (int i=0; i<labels.length; i++) {	// Initialize labels[] and labelPos[][]
			labels[i] = null;
			labelPos[i][0] = 0;
			labelPos[i][1] = 0;
		}

		// Create and draw pieces on the board
		for (int i=0; i < ludo.noOfPlayers(); i++) {
			for (int j=0; j<4; j++) {
				movable[i][j] = false;	// A player's pieces are enabled when their turn starts
				
				pieces[i][j] = new Circle();
				stackPane.getChildren().add(pieces[i][j]);
				
				goHome(i, j);
				pieces[i][j].setRadius(12);
				
				switch (i) { // Paint each player's pieces
				case 0: // RED
					pieces[i][j].setFill(Color.DARKRED);
					pieces[i][j].setStroke(Color.web("6F0000"));
					break;
				case 1: // BLUE
					pieces[i][j].setFill(Color.DARKBLUE);					
					pieces[i][j].setStroke(Color.web("00006A"));
					break;
				case 2: // YELLOW
					pieces[i][j].setFill(Color.GOLDENROD);
					pieces[i][j].setStroke(Color.web("C8930E"));
					break;
				case 3: // GREEN
					pieces[i][j].setFill(Color.GREEN);
					pieces[i][j].setStroke(Color.web("006A00"));
					break;
				default: break;
				}
				final int player = i;	// EventHandler requires final parameters
				final int piece = j;
				pieces[i][j].setOnMouseClicked(
					// Called every time a piece is clicked
					(MouseEvent t) -> handlePieceClicked(player, piece)
				);
			}
		}
	}
	
	/**
	 * Called every time the throw dice button is clicked. Calls both Ludo's throwDice() methods
	 * and is disabled and enabled as needed by event handlers.
	 */
	@FXML
	public void throwDiceButtonPressed() {
		throwTheDice.setDisable(true); // Disabled after use, reenabled by event handlers
		dice = ludo.throwDice();
		diceThrown.setImage(new Image("images/dice"+dice+".png"));
		ludo.throwDice(dice);
	}
	
	/**
	 * Called every time a piece is clicked. Together with event handlers, this method handles
	 * the restrictions for when a player can click a piece, and what happens when they legally do.
	 * @param player The player that owns the clicked piece
	 * @param piece The piece number
	 */
	public void handlePieceClicked(int player, int piece) {
		int localPos = ludo.getLocalPosition(player, piece);
		
		// Check that the piece is movable and the game has started
		if (movable[player][piece] && ludo.getStatus() == "STARTED") {
			// Determine whether to call placeAtBoard() or moveForward()
			if (localPos == 0) {
				for (int i=0; i<4; i++) { // Go through the pieces - must put out pieces in order
					if (ludo.getLocalPosition(player, i) == 0) { // Moves the first piece found at home
						placeAtBoard(player, i); // Calls ludo.movePiece()
						break;
					}
				}
			} else {
				// Check if there are several pieces on this field and pick the first
				int globalPos = ludo.getGlobalFromLocal(player, localPos);
				if (ludo.getFields()[globalPos].noOfPieces() > 1) { // Calls ludo.movePiece()
					moveForward(player, ludo.getFields()[globalPos].getPiece(), dice);
				} else {
					moveForward(player, piece, dice); // Calls ludo.movePiece()
				}
			}
		}
	}
	
	/**
	 * Moves the given piece to its field in the home area. Used both in initialization
	 * and when a piece is knocked back by another piece. Do not call moveForward() after this
	 * without first calling placeAtBoard().
	 * @param pl Player (RED, BLUE, YELLOW, GREEN)
	 * @param pi Piece (0-3)
	 */
	public void goHome(int pl, int pi) {
		switch (pl) { // Move the piece to the center of its home area
		case 0: // RED
			pieces[pl][pi].setTranslateY(-HOME);
			pieces[pl][pi].setTranslateX(HOME);
			break;
		case 1: // BLUE
			pieces[pl][pi].setTranslateY(HOME);
			pieces[pl][pi].setTranslateX(HOME);
			break;
		case 2: // YELLOW
			pieces[pl][pi].setTranslateY(HOME);
			pieces[pl][pi].setTranslateX(-HOME);
			break;
		case 3: // GREEN
			pieces[pl][pi].setTranslateY(-HOME);
			pieces[pl][pi].setTranslateX(-HOME);
			break;
		default: break;
		}
		switch (pi) { // Move the piece from home center to its field in the home area
		case 0:
			pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()-UNIT);
			break;
		case 1:
			pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()+UNIT);
			break;
		case 2:
			pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()+UNIT);
			break;
		case 3:
			pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()-UNIT);
			break;
		default: break;
		}
	}
	
	/**
	 * Moves a piece to its board entry field. From there it can be moved around the board.
	 * This must be called between goHome() and moveForward().
	 * Calls Ludo's movePiece().
	 * @param pl Player (RED, BLUE, YELLOW, GREEN)
	 * @param pi Piece (0-3)
	 */
	public void placeAtBoard(int pl, int pi) {
		ludo.movePiece(pl, 0, 1);
		switch(pl) {
		case 0:
			pieces[pl][pi].setTranslateY(-6*UNIT);
			pieces[pl][pi].setTranslateX(UNIT);
			break;
		case 1:
			pieces[pl][pi].setTranslateY(UNIT);
			pieces[pl][pi].setTranslateX(6*UNIT);
			break;
		case 2:
			pieces[pl][pi].setTranslateY(6*UNIT);
			pieces[pl][pi].setTranslateX(-UNIT);
			break;
		case 3:
			pieces[pl][pi].setTranslateY(-UNIT);
			pieces[pl][pi].setTranslateX(-6*UNIT);
			break;
		default: break;
		}
		checkPieceSharingPos(pl, pi, -1, -1);	// Called with -1 because the old field should be
	}											 // ignored when placing a piece on the entry field
	
	/**
	 * Moves the piece forward the given number of fields, following the player's path.
	 * Calls Ludo's movePiece(). placeAtBoard() must be called before this.
	 * @param pl Player (RED, BLUE, YELLOW, GREEN)
	 * @param pi Piece (0-3)
	 * @param dice The dice that was thrown (1-6)
	 */
	public void moveForward(int pl, int pi, int dice) {
		double oldX = pieces[pl][pi].getTranslateX();	// The piece's current (old)
		double oldY = pieces[pl][pi].getTranslateY();	 // graphical position
		
		int logicalPos = ludo.getLocalPosition(pl, pi); // Current logical position
		ludo.movePiece(pl, logicalPos, logicalPos+dice);

		for (int i=0; i<nextDir.length; i++) {	// Make a fresh copy of pattern[]
			nextDir[i] = pattern[i];
		}
		rotate(pl);	// Rotate nextDir[] to hold the correct starting directions for this player
		int arrayPos;		// The position to look up in nextDir[]
		int quarters = 0;	// Times the pattern has been repeated (0-3)
		
		// Starting position
		if (logicalPos <= 13) {			// 1-13
			arrayPos = logicalPos - 1;
		} else if (logicalPos <= 26) {	// 14-26
			arrayPos = logicalPos - 14;
			rotate(1);
			quarters = 1;
		} else if (logicalPos <= 39) {	// 27-39
			arrayPos = logicalPos - 27;
			rotate(2);
			quarters = 2;
		} else {						// 40-52 and 53-59, home stretch
			arrayPos = logicalPos - 40;
			rotate(3);
			quarters = 3;
		}
				
		for (int j=0; j<dice; j++) {
			if (arrayPos == 13 && quarters < 3) {	// Finished an iteration of the pattern
				rotate(1);
				arrayPos = 0;	// After rotating, start over at 0
				quarters++;		// Pattern should be iterated 4 times
			}	// When iterated 3 times, just continue with 14 and so on - home stretch
						
			switch(nextDir[arrayPos]) {	// Now nextDir() is ready and we know where to look
			case UP:
				pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()-UNIT);
				break;
			case RIGHT:
				pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()+UNIT);
				break;
			case DOWN:
				pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()+UNIT);
				break;
			case LEFT:
				pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()-UNIT);
				break;
			case UPRIGHT:
				pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()-UNIT);
				pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()+UNIT);
				break;
			case RIGHTDOWN:
				pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()+UNIT);
				pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()+UNIT);
				break;
			case DOWNLEFT:
				pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()+UNIT);
				pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()-UNIT);
				break;
			case LEFTUP:
				pieces[pl][pi].setTranslateX(pieces[pl][pi].getTranslateX()-UNIT);
				pieces[pl][pi].setTranslateY(pieces[pl][pi].getTranslateY()-UNIT);
				break;
			default: break;
			}
			arrayPos++;
		}
		checkPieceSharingPos(pl, pi, oldX, oldY);
	}
	
	/**
	 * Rotates nextDir 90 degrees clockwise the given number of times.
	 * This is a helper method for moveForward().
	 * @param times 0-3 (0 for RED player)
	 */
	protected void rotate(int times) {
		for (int i=0; i<times; i++) {
			for (int j=0; j<nextDir.length; j++) {
				switch(nextDir[j]) {
				case UP:
					nextDir[j] = Dir.RIGHT;
					break;
				case RIGHT:
					nextDir[j] = Dir.DOWN;
					break;
				case DOWN:
					nextDir[j] = Dir.LEFT;
					break;
				case LEFT:
					nextDir[j] = Dir.UP;
					break;
				case UPRIGHT:
					nextDir[j] = Dir.RIGHTDOWN;
					break;
				case RIGHTDOWN:
					nextDir[j] = Dir.DOWNLEFT;
					break;
				case DOWNLEFT:
					nextDir[j] = Dir.LEFTUP;
					break;
				case LEFTUP:
					nextDir[j] = Dir.UPRIGHT;
					break;
				default: break;
				}
			}
		}
	}
	
	/**
	 * Compares the graphical position of a piece to the player's other pieces.
	 * Handles displaying when there are several pieces on the same field, both
	 * adding and removing. Called by placeAtBoard() and moveForward(), after a move.
	 * @param player Player (RED, BLUE, YELLOW, GREEN)
	 * @param piece Piece (0-3)
	 * @param oldX The TranslateX property of the old field (stored before the move)
	 * @param oldY The TranslateY property of the old field (stored before the move)
	 */
	protected void checkPieceSharingPos(int player, int piece, double oldX, double oldY) {
		double newX = pieces[player][piece].getTranslateX();	// The piece's current (new)
		double newY = pieces[player][piece].getTranslateY();	 // graphical position
		int countOld = 0;	// Number of pieces on the old field
		int countNew = 0;	// Number of pieces on the new field
		
		for (int i=0; i<4; i++) {	// Go through each of the player's other pieces
			if (i != piece) {
				if (pieces[player][i].getTranslateX() == oldX && // Compare pieces with the old field
					pieces[player][i].getTranslateY() == oldY) {
					countOld++;
				}
				if (pieces[player][i].getTranslateX() == newX && // Compare pieces with the new field
					pieces[player][i].getTranslateY() == newY) {
					countNew++;
				}
			}
		}
		
		// Old field
		removeLabel(oldX, oldY);
		if (countOld > 1) {
			addLabel(countOld, oldX, oldY);
		}
		// New field
		removeLabel(newX, newY);
		if (countNew > 0) {	// The current piece adds to the count
			addLabel(countNew + 1, newX, newY);
		}
	}
	
	/**
	 * Finds and removes the label at the field (posX, posY).
	 * Helper method for checkPieceSharingPos().
	 * @param posX The X translate of the label's field
	 * @param posY The Y translate of the label's field
	 */
	protected void removeLabel(double posX, double posY) {
		// Search labels[] for a match of both posX and posY
		for (int i=0; i<labels.length; i++) {
			if (labelPos[i][0] == posX && labelPos[i][1] == posY) {
				stackPane.getChildren().remove(labels[i]);
				labelPos[i][0] = 0;
				labelPos[i][1] = 0;
				labels[i] = null;
				return;
			}
		}
	}
	
	/**
	 * Adds a new label at the field (posX, posY). Assumes count > 1 and displays it.
	 * Helper method for checkPieceSharingPos().
	 * @param count The number of pieces on this field (should be greater than 1)
	 * @param posX The X translate of the field
	 * @param posY The Y translate of the field
	 */
	protected void addLabel(int count, double posX, double posY) {
		// Add to the first free slot of labels[]
		for (int i=0; i<labels.length; i++) {
			if (labels[i] == null) {
				labelPos[i][0] = posX;
				labelPos[i][1] = posY;
				labels[i] = new Text();
				labels[i].setTranslateX(posX+OFFSETX);
				labels[i].setTranslateY(posY+OFFSETY);
				labels[i].setText(Integer.toString(count));
				labels[i].setFont(new Font(15));
				labels[i].setOpacity(0.8);
				stackPane.getChildren().add(labels[i]);
				break;
			}
		}
	}

	// === NON-JFX EVENT HANDLERS ================================================================== //
	
	
	/**
	 * Enables guaranteed possible moves only.
	 * @param event The DiceEvent that occurred
	 */
	@Override
	public void diceThrown(DiceEvent event) {
		if (ludo.getAttempt() > -1 && ludo.getAttempt() < 2 && dice != 6) {
			throwTheDice.setDisable(false);	// Can throw the dice again
		} else if (dice == 6) {
			for (int piece=0; piece<4; piece++) {
				if (ludo.getLocalPosition(event.getPlayer(), piece) == 0) {
					movable[event.getPlayer()][piece] = true;	// Can move a piece out of home
				}
			}
		}
	}
	
	/**
	 * Enables moves that are found to be available.
	 * @param event The MovesCheckedEvent that occurred
	 */
	@Override
	public void movesChecked(MovesCheckedEvent event) {
		for (int piece=0; piece<4; piece++) {
			this.movable[event.getPlayer()][piece] = event.getMovable()[piece];
			// checkMoves() also checks for pieces that can be moved out, so overwriting
			 // what's done by diceThrown() above is okay
		}
	}

	/**
	 * Enables rerolling and moves graphical pieces home.
	 * @param event The PieceEvent that occurred
	 */
	@Override
	public void pieceMoved(PieceEvent event) {
		if (event.getNewPos() == 0) {	// Piece knocked home
			goHome(event.getPlayer(), event.getPiece());
		} else if (ludo.getExtraThrow()) {// Separate PieceEvents for pieces moved and knocked home
			throwTheDice.setDisable(false);	// Can throw the dice again
		}
		for (int i=0; i<4; i++) {	// Even if the player has an extra throw, they must throw
			movable[event.getPlayer()][i] = false; // the dice again to be able to move
		}
	}
	
	/**
	 * Responds to players changing states: displays when the turn moves to the next player
	 * and disables controls when the game is won.
	 * @param event The PlayerEvent that occurred
	 */
	@Override
	public void playerStateChanged(PlayerEvent event) {
		int player = event.getPlayer();
		
		switch (event.getState()) {
		case 0:	// WAITING
			// Remove dice icon for this player
			switch(player) {
			case 0: // RED
				player1Pane.getChildren().remove(player1Active);
				break;
			case 1: // BLUE
				player2Pane.getChildren().remove(player2Active);
				break;
			case 2: // YELLOW
				player3Pane.getChildren().remove(player3Active);
				break;
			case 3: // GREEN
				player4Pane.getChildren().remove(player4Active);
				break;
			default: break;
			}
			
			for (int i=0; i<4; i++) {	// Disable all pieces for this player
				movable[player][i] = false;
			}
			break;
			
		case 1: // PLAYING
			// Add dice icon for this player
			switch(player) {
			case 0: // RED
				player1Pane.getChildren().add(player1Active);
				break;
			case 1: // BLUE
				player2Pane.getChildren().add(player2Active);
				break;
			case 2: // YELLOW
				player3Pane.getChildren().add(player3Active);
				break;
			case 3: // GREEN
				player4Pane.getChildren().add(player4Active);
				break;
			default: break;
			}
			throwTheDice.setDisable(false); // Enable throwing the dice
			break;
			
		case 2: // WON
			// Disable all pieces and throwing the dice
			for (int i=0; i<4; i++) {
				for (int j=0; j<4; j++) {
					movable[i][j] = false;
				}
			}
			throwTheDice.setDisable(true);
			break;
			
		default: break;
		}
	}
}