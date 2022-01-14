import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.core.view.Change;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
/**
 * Board class, handles the interaction of pieces and displaying them.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class Board {
   Properties config = new Properties();

   private static final ArrayList<String> MATCH_TRANSCRIPT = new ArrayList<String>();
   private final GameController GAME;
   private final GridPane gp_CHESS_BOARD;
   private final GridPane gp_DEAD_WHITE_CELLS;
   private final GridPane gp_DEAD_BLACK_CELLS;

   // Current black dead cell and current white dead cell
   private byte cbdc = 0;
   private byte cwdc = 0;

   private final byte[][] GRID = new byte[8][8];

   private final StackPane[][] CELLS = new StackPane[8][8];

   private final Piece[] GAME_PIECES = new Piece[32];
   private final ArrayList<Piece> LIVE_PIECES = new ArrayList<Piece>();
   private final ArrayList<Piece> DEAD_PIECES = new ArrayList<Piece>();

   private StackPane sp_selected;
   private final ArrayList<StackPane> POSSIBLE_MOVES = new ArrayList<StackPane>();

   private byte turn = Constants.pieceIDs.WHITE;

   private final PlayerTimer[] TIMERS = new PlayerTimer[2];

   private final Map[] STATS = new Map[] { new HashMap<String, Integer>(), new HashMap<String, Integer>(),
         new HashMap<String, String>() };
   private byte winner;

   private final Bot bot = new Bot(App.getDiff(), true);
   private final DatabaseReference SERVER_REF;

   private final byte GAME_MODE;


   /**
    * Constructor for the Board class
    * 
    * @param game       Reference to the GameController class.
    * @param chessBoard GridPane element containing the rows, and columns of
    *                   StackPane.
    * @param gm         The game mode; Ai, pass n play
    */
   public Board(GameController game, GridPane[] cells, byte gm) {
      GAME = game;
      gp_CHESS_BOARD = cells[0];
      gp_DEAD_BLACK_CELLS = cells[1];
      gp_DEAD_WHITE_CELLS = cells[2];
      GAME_MODE = gm;

      //Was thinking of adding sound effects - Akil
      //https://mixkit.co/free-sound-effects/instrument/
      //turnsfx = new Media(App.class.getClass().getResource("assets//turnsfx.wav"));

      int gameTime = 600000;
      Label blackLabel = GAME.getTimeReference(Constants.pieceIDs.BLACK);
      Label whiteLabel = GAME.getTimeReference(Constants.pieceIDs.WHITE);

      try {
         FileReader r = new FileReader("src\\main\\resources\\data\\config.properties");
         config.load(r);
         r.close();

         if (config.getProperty("gametime").equals("10")) {
            gameTime = 600000;
            blackLabel.setText("10:00");
            whiteLabel.setText("10:00");
         } else if (config.getProperty("gametime").equals("3")) {
            gameTime = 180000;
            blackLabel.setText("3:00");
            whiteLabel.setText("3:00");
         } else if (config.getProperty("gametime").equals("30")) {
            gameTime = 1800000;
            blackLabel.setText("30:00");
            whiteLabel.setText("30:00");
         } else if(config.getProperty("gametime").equals("infinite")){
            gameTime = 1800000;
            blackLabel.setText("BLACK");
            whiteLabel.setText("WHITE");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      System.out.println(gameTime);

      TIMERS[Constants.pieceIDs.WHITE] = new PlayerTimer(whiteLabel, gameTime, true);
      TIMERS[Constants.pieceIDs.BLACK] = new PlayerTimer(blackLabel, gameTime, false);

      setupBoard();

      if (GAME_MODE == Constants.boardData.MODE_RESUME_GAME) {
         try {
            parseTranscript();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if(GAME_MODE == Constants.boardData.MODE_PASS_N_PLAY){
         App.setTranscriptPath(null);
      }

      SERVER_REF = App.getServerReference();
   }

   /**
    * This method is called once in the constructor of the Board class.
    * The method will setup the boeard by placing all the game pieces in the
    * correct starting locations, it will also initialize all the array lists.
    */
   private void setupBoard() {
      for (byte x = 0; x < 8; x++) {
         for (byte y = 0; y < 8; y++) {
            GRID[x][y] = Constants.boardData.DEFAULT_GAME_SETUP[x][y];
         }
      }

      // Loop through chess board nodes to find find stackpanes.
      App.MOVE_COUNT = 1;
      for (Node node : gp_CHESS_BOARD.getChildren()) {
         StackPane sp = (StackPane) node;

         // x and y coordinates of this specific stackpane.
         int x = (GridPane.getColumnIndex(node) != null) ? GridPane.getColumnIndex(node) : 0;
         int y = (GridPane.getRowIndex(node) != null) ? GridPane.getRowIndex(node) : 0;

         // Add event handler for when the mouse enters.
         sp.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               boolean isPossibleMove = false;

               // Check to see if the current stack pane appears in POSSIBLE_MOVES
               for (StackPane pm : POSSIBLE_MOVES) {
                  if (pm == sp) {
                     isPossibleMove = true;
                     break;
                  }
               }

               // Checks to see if interaction is allowed
               if (GAME_MODE == Constants.boardData.MODE_PASS_N_PLAY) {
                  if ((getAllowInteract(sp, x, y) || isPossibleMove))
                     highlightMouseCellHover(true, sp);
               } else if (GAME_MODE == Constants.boardData.MODE_AI && turn == Constants.pieceIDs.WHITE) {
                  if (getAllowInteract(sp, x, y) || isPossibleMove) {
                     highlightMouseCellHover(true, sp);
                  }
               }
            }
         });

         // Add event handler for when the mouse exits.
         sp.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               highlightMouseCellHover(false, sp);
            }
         });

         // Add event handler for when the mouse is clicked.
         setDefaultMouseClicked(sp, (byte) x, (byte) y);

         sp.getChildren().clear();
         CELLS[x][y] = sp;

      }

      for (byte x = 0; x < GRID.length; x++) {
         for (byte y = 0; y < GRID.length; y++) {
            byte id = GRID[x][y];

            if (id == Constants.pieceIDs.EMPTY_CELL)
               continue;

            Piece piece = null;

            if (id == Constants.pieceIDs.BLACK_KING || id == Constants.pieceIDs.WHITE_KING) {
               piece = new King(id);
            } else if (id == Constants.pieceIDs.BLACK_QUEEN || id == Constants.pieceIDs.WHITE_QUEEN) {
               piece = new Queen(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_BISHOP || id == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                  || id == Constants.pieceIDs.WHITE_KINGS_BISHOP || id == Constants.pieceIDs.WHITE_QUEENS_BISHOP) {
               piece = new Bishop(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_KNIGHT || id == Constants.pieceIDs.BLACK_QUEENS_KNIGHT
                  || id == Constants.pieceIDs.WHITE_KINGS_KNIGHT || id == Constants.pieceIDs.WHITE_QUEENS_KNIGHT) {
               piece = new Knight(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                  || id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
               piece = new Rook(id);
            } else {
               piece = new Pawn(id);
            }

            piece.setGridPos(x, y);
            LIVE_PIECES.add(piece);
            GAME_PIECES[id] = piece;
            CELLS[x][y].getChildren().add(piece.getSprite());
         }
      }

      App.GAME_PIECES = GAME_PIECES.clone();

      STATS[Constants.pieceIDs.WHITE].put("remaining_time", (int) TIMERS[Constants.pieceIDs.WHITE].getTimeMillis());
      STATS[Constants.pieceIDs.WHITE].put("total_moves", 0);
      STATS[Constants.pieceIDs.WHITE].put("pieces_killed", 0);

      STATS[Constants.pieceIDs.BLACK].put("remaining_time", (int) TIMERS[Constants.pieceIDs.BLACK].getTimeMillis());
      STATS[Constants.pieceIDs.BLACK].put("total_moves", 0);
      STATS[Constants.pieceIDs.BLACK].put("pieces_killed", 0);
   }

   /**
    * This method will read a text file containing data as to the moves that have
    * been played.
    * It will then renact each move until the entire file is parsed.
    * 
    * @throws IOException May throw an exception if the file location is not found.
    */
   private void parseTranscript() throws IOException {
      final String REG = "((?=[A-Z])|(?<=[A-Z]))|((?=[a-z])|(?<=[a-z]))";

      System.out.println(App.getTranscriptPath());
      FileReader fileReader = new FileReader(new File(App.getTranscriptPath()));

      try (Scanner r = new Scanner(fileReader)) {
         String ts;

         while (r.hasNextLine()) {
            ts = r.nextLine();

            if (ts.contains("TW")) { // White timer
               TIMERS[Constants.pieceIDs.WHITE].setTime(Long.parseLong(ts.substring(2, ts.length())));
            } else if (ts.contains("TB")) { // Black timer
               TIMERS[Constants.pieceIDs.BLACK].setTime(Long.parseLong(ts.substring(2, ts.length())));
            } else if (ts.contains("PP")) { // Pawn promotion
               MATCH_TRANSCRIPT.add(ts);

               System.out.println(ts);
               ts = ts.substring(2, ts.length());
               System.out.println(ts);

               String[] ids = ts.split("-");
               System.out.println("ID 0: " + ids[0]);
               System.out.println("ID 1: " + ids[1]);

               Piece from = getPieceOnGrid(Byte.parseByte(ids[0]));

               promotePawn(from, ids[1], true);
            } else {
               MATCH_TRANSCRIPT.add(ts);
               App.MOVE_COUNT++;

               String[] data = ts.split(REG);
               System.out.println(Arrays.toString(data));

               Piece piece = getPieceOnGrid(Byte.parseByte(data[0].trim()));

               if (piece == null)
                  continue;

               byte x = getBoardX(data[1]);
               byte y = getBoardY(data[2]);

               System.out.println("(" + x + ", " + y + ")");

               if (x > 0 && x < 8 && y > 0 && y < 8) {
                  App.MOVE_COUNT++;
                  movePiece(piece, piece.getGridX(), piece.getGridY(), x, y, true);
                  System.out.println(203902903);
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();

         setupBoard();
         LIVE_PIECES.clear();
         DEAD_PIECES.clear();
      }
   }

   /**
    * This method is called when the match concludes.
    */
   private void gameover() throws IOException {
      App.setMatchStats(solidifyTranscript());

      GAME.gameOver(winner);
   }

   private byte getBoardX(String value) {
      String[] xPos = Constants.boardData.X_ID;
      for (byte i = 0; i < xPos.length; i++) {
         if (xPos[i].equalsIgnoreCase(value))
            return i;
      }

      return -1;
   }

   private byte getBoardY(String value) {
      String[] yPos = Constants.boardData.Y_ID;
      for (byte i = 0; i < yPos.length; i++) {
         if (yPos[i].equalsIgnoreCase(value))
            return i;
      }

      return -1;
   }

   /**
    * This method will check to see if the player is able to interact with a
    * certain grid element.
    * 
    * @param target Target stackpane
    * @param x      X coordinates of the grid cell
    * @param y      Y coordinates of the grid cell
    * @return true / false
    */
   private boolean getAllowInteract(StackPane target, int x, int y) {
      // Check that the X, Y coordinates contain a game piece.
      if (GRID[x][y] == Constants.pieceIDs.EMPTY_CELL)
         return false;

      // Loop through children of StackPane target
      for (Node node : target.getChildren()) {
         // Check to see if the node is not an instance of ImageView
         if (!(node instanceof ImageView))
            continue;

         // Loop through the live pieces
         for (Piece livePiece : LIVE_PIECES) {
            // Check to see if the coordinates match.
            if (livePiece.getGridX() == x && livePiece.getGridY() == y) {
               return livePiece.getColor() == turn;
            }
         }

         break;
      }

      return false;
   }

   /**
    * This method will return the piece on the inputed board coordinates.
    * 
    * @param x Board x coordinate.
    * @param y Board y coordinate.
    * @return Piece object or null.
    */
   private Piece getPieceOnGrid(int x, int y) {
      for (Piece piece : LIVE_PIECES) {
         if (piece.getGridX() == x && piece.getGridY() == y)
            return piece;
      }

      System.out.println("No piece found at: " + Integer.toString(x) + " " + Integer.toString(y));
      return null;
   }

   /**
    * This method will return the piece on the inputed board coordinates.
    * 
    * @param id The byte identification of a game piece
    * @return Piece object or null.
    */
   private Piece getPieceOnGrid(byte id) {
      for (Piece piece : LIVE_PIECES) {
         if (piece.getId() == id)
            return piece;
      }

      return null;
   }

   /**
    * This method will return the x and y coordinates of a stack pane.
    * 
    * @param target Target stack pane
    * @return Array of type byte is returned; first index contains the x
    *         coordinate, and the second index contains the y coordinate.
    */
   private byte[] getStackPanePosOnGrid(StackPane target) {
      int x = (GridPane.getColumnIndex(target) != null) ? GridPane.getColumnIndex(target) : 0;
      int y = (GridPane.getRowIndex(target) != null) ? GridPane.getRowIndex(target) : 0;

      return new byte[] { (byte) x, (byte) y };
   }

   /**
    * This method will return the grid fxml object
    * 
    * @return GridPane object
    */
   public GridPane getBoard() {
      return gp_CHESS_BOARD;
   }

   /**
    * This method will which player's turn it is.
    * 
    * @return int
    */
   public int getTurn() {
      return turn;
   }

   /**
    * This method will return an array list containing all the live pieces.
    * 
    * @return ArrayList containing pieces
    */
   public ArrayList<Piece> getLivePieces() {
      return LIVE_PIECES;
   }

   /**
    * This method will return an array list containing all the dead pieces.
    * 
    * @return ArrayList containing pieces
    */
   public ArrayList<Piece> getDeadPieces() {
      return DEAD_PIECES;
   }

   /**
    * This method will see if the attempted move is possible
    * 
    * @param id       int value corresponding to the id of the game piece
    * @param location int array containing the x and y coordinates of the game
    *                 piece
    * @return boolean, true or false.
    */
   public boolean tryMove(int id, int[] location) {
      return false;
   }

   /**
    * This method will cycle to the next player's turn
    */
   public void nextTurn() {
      App.MOVE_COUNT++;
      resetPossibleMoves();
      if (sp_selected != null)
         sp_selected.getStyleClass().remove("cell-selected");

      sp_selected = null;
      boolean inCheck = true;
      winner = turn;

      STATS[turn].replace("total_moves", (int) STATS[turn].get("total_moves") + 1);

      TIMERS[turn].pauseTimer();

      if (turn == Constants.pieceIDs.WHITE) {
         turn = Constants.pieceIDs.BLACK;
      } else {
         turn = Constants.pieceIDs.WHITE;
      }

      TIMERS[turn].playTimer();

      for (Piece p : LIVE_PIECES) {
         if (p.getColor() != turn)
            continue;

         if (p.getPossibleMoves(GRID).length > 0)
            inCheck = false;
      }

      if (inCheck) {
         try {
            gameover();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      System.out.println("Next turn: " + turn);

      if (GAME_MODE == Constants.boardData.MODE_AI && turn == Constants.pieceIDs.BLACK) {
         // Bot.BoardInteractions bi = bot.BI.parseAiMove(bot.getMove(GRID,
         // DEAD_PIECES));
         Task<String> move = new Task<String>() {

            @Override
            protected String call() throws Exception {
               return bot.getMove(GRID);
            }

         };

         move.valueProperty();
         ChangeListener<String> listener = new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
               move.valueProperty().removeListener(this);
               playAi(move.getValue());
            }

         };
         move.valueProperty().addListener(listener);

         Thread botThread = new Thread(move);
         botThread.setDaemon(false);
         botThread.start();
         // bot.getMove(GRID, DEAD_PIECES);
      }

   }

   private void playAi(String move) {
      // TODO Need way of fetching promoted piece data
      System.out.println("Move: " + move);
      int endFrom;
      byte id;
      int enemyX;
      int enemyY;
      Pawn primaryPawn;
      Pawn enemyPawn;
      
      switch (move.substring(0, 1)) {
         case Constants.moveTypes.REGULAR:
            System.out.println("Plays regular  move");
            endFrom = (move.charAt(3) == 'f') ? 3 : 2;
            int fromX = Integer.parseInt(move.substring(endFrom+1, endFrom+2));
            int fromY = Integer.parseInt(move.substring(endFrom+2, endFrom+3));
            Piece piece = getPieceOnGrid(fromX, fromY);
            System.out.println("Piece id: " + piece.getId());
            piece.hasMoved = true;
            byte x = Byte.parseByte(move.substring(endFrom + 4, endFrom + 5));
            byte y = Byte.parseByte(move.substring(endFrom + 5, endFrom + 6));
            movePiece(piece, piece.getGridX(), piece.getGridY(), x, y, false);
            break;
         case Constants.moveTypes.CASTLE_RIGHT:
            endFrom = (move.charAt(2) == '.') ? 2 : 3;
            id = (byte) Integer.parseInt(move.substring(1, endFrom));
            castle(GAME_PIECES[id], (byte) (id/Constants.pieceIDs.COLOR_DIVISOR), false);
            // castle right
            break;
         case Constants.moveTypes.CASTLE_LEFT:
            endFrom = (move.charAt(2) == '.') ? 2 : 3;
            id = (byte) Integer.parseInt(move.substring(1, endFrom));
            castle(GAME_PIECES[id], (byte) (id/Constants.pieceIDs.COLOR_DIVISOR), true);
            // castle left
            break;
         case Constants.moveTypes.PASSANT_RIGHT:
            endFrom = (move.charAt(3) == 'f') ? 3 : 2;
            primaryPawn = (Pawn) GAME_PIECES[Byte.parseByte(move.substring(1, endFrom))];
            enemyX = Integer.parseInt(move.substring(endFrom+4, endFrom+5));
            enemyY = Integer.parseInt(move.substring(endFrom+4, endFrom+5));
            enemyPawn = (Pawn) getPieceOnGrid(enemyX,enemyY);
            enPassant(primaryPawn, enemyPawn);
            break;
         case Constants.moveTypes.PASSANT_LEFT:
            endFrom = (move.charAt(3) == 'f') ? 3 : 2;
            primaryPawn = (Pawn) GAME_PIECES[Byte.parseByte(move.substring(1, endFrom))];
            enemyX = Integer.parseInt(move.substring(endFrom+4, endFrom+5));
            enemyY = Integer.parseInt(move.substring(endFrom+4, endFrom+5));
            enemyPawn = (Pawn) getPieceOnGrid(enemyX,enemyY);
            enPassant(primaryPawn, enemyPawn);
            break;
         case Constants.moveTypes.PROMOTION:
            endFrom = (move.charAt(2) == '.') ? 2 : 3;
            promotePawn(GAME_PIECES[Byte.parseByte(move.substring(1,endFrom))],getBotPromotion(Byte.parseByte(move.substring(endFrom+1, endFrom+2))), false);
            break;
         case Constants.moveTypes.NO_MOVES:
            break;
         default:
            return;
      }
      nextTurn();
   }

   /**
    * This method will highlight the current hovered over board cell by adding or
    * removing css classes.
    * 
    * @param entered If the mouse has entered the cell or exited.
    * @param target  The StackPane that is being hovered over.
    */
   private void highlightMouseCellHover(boolean entered, StackPane target) {
      if (entered) { // Mouse entered
         target.getStyleClass().add("cell-hover");
      } else { // Mouse exited
         target.getStyleClass().remove("cell-hover");
      }
   }


   private String getBotPromotion(byte id){
      switch(id){
         case Constants.pieceIDs.BLACK_QUEENS_BISHOP:
            return "BISHOP";
         case Constants.pieceIDs.BLACK_QUEENS_KNIGHT:
            return "KNIGHT";
         case Constants.pieceIDs.BLACK_QUEEN:
            return "QUEEN";
         case Constants.pieceIDs.BLACK_PROMOTED_ROOK:
            return "ROOK";
         case Constants.pieceIDs.WHITE_QUEENS_BISHOP:
            return "BISHOP";
         case Constants.pieceIDs.WHITE_QUEENS_KNIGHT:
            return "KNIGHT";
         case Constants.pieceIDs.WHITE_QUEEN:
            return "QUEEN";
         default:
            System.out.println("couldn't find case for getBotPromotion id: " + id);
            return "QUEEN";
      }
      
   }

   /**
    * This method will call the getPossibleMoves method for a specific game piece
    * and display the data onto the screen.
    * 
    * @param piece Target piece.
    */
   private void displayPossibleMoves(Piece piece) {
      System.out.println("move count: " + App.MOVE_COUNT);
      // Get array of possible moves.
      byte[][] moves = piece.getPossibleMoves(GRID);
      King king = null;
      Pawn pawn = null;

      if (piece.getId() == Constants.pieceIDs.BLACK_KING || piece.getId() == Constants.pieceIDs.WHITE_KING) {
         king = (King) piece;
         if (king.canCastleLeft(GRID)) {
            StackPane s_leftCastle = CELLS[king.getGridX() - 2][king.getGridY()];
            s_leftCastle.getStyleClass().add("cell-move");
            setCastleMoveMouseClicked(s_leftCastle, piece, piece.getColor(), true);
            POSSIBLE_MOVES.add(s_leftCastle);
         }
         if (king.canCastleRight(GRID)) {
            StackPane s_rightCastle = CELLS[king.getGridX() + 2][king.getGridY()];
            s_rightCastle.getStyleClass().add("cell-move");
            setCastleMoveMouseClicked(s_rightCastle, piece, piece.getColor(), false);
            POSSIBLE_MOVES.add(s_rightCastle);
         }
      } else if ((piece.getId() > Constants.pieceIDs.BEGIN_BLACK_PAWNS
            && piece.getId() < Constants.pieceIDs.END_BLACK_PAWNS)
            || (piece.getId() > Constants.pieceIDs.BEGIN_WHITE_PAWNS
                  && piece.getId() < Constants.pieceIDs.END_WHITE_PAWNS)) {
                     //logic for en passant

                     //if the piece is a pawn
                     pawn = (Pawn) piece;

                     // gets the id of the piece to the right and left
                     // makes sure it can check to the left and can check to the right
                     byte pawnLeft = (pawn.gridX - 1 > -1) ? GRID[piece.gridX - 1][piece.gridY] : -1;
                     byte pawnRight = (pawn.gridX + 1 < 8) ? GRID[piece.gridX + 1][piece.gridY] : -1;

                     int pawnDirection = (pawn.getColor() == Constants.pieceIDs.BLACK) ? 1 : -1;

                     if(pawn.canPassantRight(pawnRight, GRID)){
                        StackPane s_rightPawn = CELLS[pawn.gridX + 1][pawn.gridY + 1*pawnDirection];
                        s_rightPawn.getStyleClass().add("cell-enemy");
                        setPassantMoveMouseClicked(s_rightPawn, pawn, (Pawn) GAME_PIECES[pawnRight]);
                        POSSIBLE_MOVES.add(s_rightPawn);
                     }

                     if (pawn.canPassantLeft(pawnLeft, GRID)) {
                        StackPane s_leftPawn = CELLS[pawn.gridX - 1][pawn.gridY + 1*pawnDirection];
                        s_leftPawn.getStyleClass().add("cell-enemy");
                        setPassantMoveMouseClicked(s_leftPawn, pawn, (Pawn) GAME_PIECES[pawnLeft]);
                        POSSIBLE_MOVES.add(s_leftPawn);

                     }

      }

      System.out.println(Arrays.toString(moves));
      // Loop through the moves
      for (int i = 0; i < moves.length; i++) {
         final byte x = moves[i][0];
         final byte y = moves[i][1];

         // Get instance of CELLS at specified indexes
         StackPane sp_move = CELLS[x][y];
         // Add a css style class
         if (sp_move.getChildren().size() == 0) {
            sp_move.getStyleClass().add("cell-move");
         } else {
            sp_move.getStyleClass().add("cell-enemy");
         }

         // Add mouse click event handler
         setPossibleMoveMouseClicked(sp_move, piece, x, y);

         // Add altered stack pane to array.
         POSSIBLE_MOVES.add(sp_move);
      }
   }

   /**
    * This method will reset all the possible moves that were displayed.
    */
   private void resetPossibleMoves() {
      for (StackPane pm : POSSIBLE_MOVES) {
         pm.getStyleClass().remove("cell-move");
         pm.getStyleClass().remove("cell-enemy");
         pm.setOnMouseClicked(null);

         byte[] pos = getStackPanePosOnGrid(pm);
         setDefaultMouseClicked(pm, pos[0], pos[1]);
      }

      POSSIBLE_MOVES.clear();
   }

   /**
    * This method will move a game piece to the specefied board coordinated
    * 
    * @param piece Target piece to be moved.
    * @param fromX Initial board x value.
    * @param fromY Initial board y value.
    * @param toX   Final board x value.
    * @param toY   final board y value.
    */
   private void movePiece(Piece piece, byte fromX, byte fromY, byte toX, byte toY, boolean parsingTranscript) {
      StackPane from = CELLS[fromX][fromY];
      from.getChildren().clear();

      if (piece.getType() == Constants.pieceType.PAWN && Math.abs(toY - fromY) == 2) {
         ((Pawn) piece).setPassant(App.MOVE_COUNT);
         System.out.println("Passant: " + ((Pawn) piece).getPassant());
      }

      StackPane to = CELLS[toX][toY];
      for (Node tChild : to.getChildren()) {
         if (tChild instanceof ImageView) {
            Piece p = getPieceOnGrid(toX, toY);

            LIVE_PIECES.remove(p);
            DEAD_PIECES.add(p);

            STATS[turn].replace("pieces_killed", (int) STATS[turn].get("pieces_killed") + 1);

            displayDeadPiece(p);
            break;
         }
      }
      to.getChildren().clear();
      to.getChildren().add(piece.getSprite());

      piece.setGridPos(toX, toY);

      GRID[fromX][fromY] = Constants.pieceIDs.EMPTY_CELL;
      GRID[toX][toY] = piece.getId();

      String moveTranscript = piece.getId() + Constants.boardData.X_ID[toX] + Constants.boardData.Y_ID[toY];

      // if (piece.getColor() == Constants.pieceIDs.BLACK)
      // BLACK_TRANSCRIPT.add(moveTranscript);
      // else
      // WHITE_TRANSCRIPT.add(moveTranscript);

      MATCH_TRANSCRIPT.add(moveTranscript);

      // System.out.println(BLACK_TRANSCRIPT.toString());
      // System.out.println("\n-------------------------------\n");
      // System.out.println(WHITE_TRANSCRIPT.toString());

      if (piece.getType() == Constants.pieceType.PAWN && (piece.getGridY() == 0 || piece.getGridY() == 7)
            && !parsingTranscript) {
         try {
            GAME.displayPawnPromotion(piece, turn);
         } catch (IOException e) {
         }
      }
   }

   /**
    * This method will display all the possible moves of a selected game piece.
    * 
    * @param target The target game piece.
    */
   private void displayDeadPiece(Piece target) {
      ImageView img = target.getSprite();
      img.setFitWidth(30);
      img.setFitHeight(30);

      StackPane sp;

      if (target.getColor() == Constants.pieceIDs.BLACK) {
         sp = (StackPane) gp_DEAD_BLACK_CELLS.getChildren().get(cbdc);
         cbdc++;
      } else {
         sp = (StackPane) gp_DEAD_WHITE_CELLS.getChildren().get(cwdc);
         cwdc++;
      }

      sp.getChildren().add(img);
   }

   /**
    * This method sets up the default mouse clicked event handler for a StackPane
    * 
    * @param sp Target StackPane
    * @param x  Board x coordinates.
    * @param y  Board y coordinates.
    */
   private void setDefaultMouseClicked(StackPane sp, byte x, byte y) {
      sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent me) {
            // Resets the possible moves being displayed.
            resetPossibleMoves();

            // Check if sp_selected is null, if it isn't it will remove t
            if (sp_selected != null)
               sp_selected.getStyleClass().remove("cell-selected");

            // Check if interaction is allowed
            if (getAllowInteract(sp, x, y)) {
               // Add cell selected style class
               sp.getStyleClass().add("cell-selected");
               sp_selected = sp;

               // Display the possible moves.
               displayPossibleMoves(getPieceOnGrid(x, y));
            } else // Set the selected stack pane node to null.
               sp_selected = null;
         }
      });
   }

   /**
    * This method sets up the mouse clicked event handler for a possible move
    * StackPane
    * 
    * @param sp    Target StackPane
    * @param piece Target game piece
    * @param x     Board x coordinates.
    * @param y     Board y coordinates.
    */
   private void setPossibleMoveMouseClicked(StackPane sp, Piece piece, byte x, byte y) {
      sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent me) {
            // When clicked move the piece.
            piece.hasMoved = true;
            movePiece(piece, piece.getGridX(), piece.getGridY(), x, y, false);
            nextTurn();
         }
      });
   }

   /**
    * 
    * This method serves the same purpose as the setPossibleMoveMouseClicked()
    * method does, except it is
    * attached to castling moves. Castling moves require multiple pieces to be
    * removed so this method needed to be created.
    * 
    * @param sp    StackPane of the king.
    * @param piece King object.
    * @param color Color/team value.
    * @param left  If true, castling to the left of the screen, false if not.
    */
   private void setCastleMoveMouseClicked(StackPane sp, Piece piece, byte color, boolean left) {
      sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent me) {
            // When clicked move the piece.
            castle(piece, color, left);
         }
      });

   }

   /**
    * This method serves the same purpose as setPossibleMoveMouseClicked, except it
    * is only made for
    * en passant moves from pawns. This is because in en passant, a pawn moves to
    * the square behind the other pawn and the other pawn still gets deleted.
    * 
    * @param sp          StackPane of the primary pawn.
    * @param primaryPawn Pawn object of the primary pawn.
    * @param enemyPawn   Pawn object of the enemy pawn.
    */
   private void setPassantMoveMouseClicked(StackPane sp, Pawn primaryPawn, Pawn enemyPawn) {
      sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            enPassant(primaryPawn, enemyPawn);
         }

      });

   }


   public void castle(Piece piece, byte color, boolean left){
      piece.hasMoved = true;
            if (left) {
               movePiece(piece, piece.getGridX(), piece.getGridY(), (byte) (piece.getGridX() - 2), piece.getGridY(),
                     false);
               if (color == Constants.pieceIDs.BLACK) {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.BLACK_QUEENS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() + 1), piece.getGridY(),
                        false);
               } else {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.WHITE_QUEENS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() + 1), piece.getGridY(),
                        false);
               }
            } else {
               movePiece(piece, piece.getGridX(), piece.getGridY(), (byte) (piece.getGridX() + 2), piece.getGridY(),
                     false);
               if (color == Constants.pieceIDs.BLACK) {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.BLACK_KINGS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() - 1), piece.getGridY(),
                        false);
               } else {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.WHITE_KINGS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() - 1), piece.getGridY(),
                        false);
               }
            }
            nextTurn();
      

   }

   public void enPassant(Pawn primaryPawn, Pawn enemyPawn){
      // Moves the primary pawn to its new spot.
      if (primaryPawn.getColor() == Constants.pieceIDs.BLACK) {
         if (enemyPawn.getGridX() < primaryPawn.getGridX()) {
            // pawn is to the left
            movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX - 1),
                  (byte) (primaryPawn.gridY + 1), false);

         } else {
            // pawn is to the right
            movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX + 1),
                  (byte) (primaryPawn.gridY + 1), false);
         }
      } else {
         if (enemyPawn.getGridX() < primaryPawn.getGridX()) {
            // pawn to the left
            movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX - 1),
                  (byte) (primaryPawn.gridY - 1), false);
         } else {
            // pawn is to the right
            movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX + 1),
                  (byte) (primaryPawn.gridY - 1), false);
         }

      }

      // Removes enemyPawn from the game.
      GRID[enemyPawn.gridX][enemyPawn.gridY] = Constants.pieceIDs.EMPTY_CELL;
      StackPane enemyPane = CELLS[enemyPawn.gridX][enemyPawn.gridY];
      enemyPane.getChildren().clear();
      LIVE_PIECES.remove(enemyPawn);
      DEAD_PIECES.add(enemyPawn);

      displayDeadPiece(enemyPawn);
      nextTurn();
   }

   /**
    * This method will promote a pawn piece. It will display a new prompt box
    * allowing the player who reaches the end
    * to choose a piece to replace the pawn.
    * This method is called from the GameController class
    * 
    * @param piece The pawn piece to be promoted
    * @param type  The type of piece to replace it.
    */
   public void promotePawn(Piece piece, String type, boolean parsingTranscript) {
      byte x = piece.getGridX();
      byte y = piece.getGridY();

      byte id;

      Piece newPiece;

      if (type.equalsIgnoreCase("QUEEN")) {
         if (piece.getColor() == Constants.pieceIDs.BLACK)
            id = Constants.pieceIDs.BLACK_QUEEN;
         else
            id = Constants.pieceIDs.WHITE_QUEEN;

         newPiece = new Queen(id);
      } else if (type.equalsIgnoreCase("BISHOP")) {
         if (piece.getColor() == Constants.pieceIDs.BLACK) {
            if (x % 2 == 0)
               id = Constants.pieceIDs.BLACK_QUEENS_BISHOP;
            else
               id = Constants.pieceIDs.BLACK_KINGS_BISHOP;
         }

         else {
            if (x % 2 == 0)
               id = Constants.pieceIDs.WHITE_QUEENS_BISHOP;
            else
               id = Constants.pieceIDs.WHITE_KINGS_BISHOP;
         }

         newPiece = new Bishop(id);
      } else if (type.equalsIgnoreCase("KNIGHT")) {
         if (piece.getColor() == Constants.pieceIDs.BLACK) {
            if (x % 2 == 0)
               id = Constants.pieceIDs.BLACK_QUEENS_BISHOP;
            else
               id = Constants.pieceIDs.BLACK_KINGS_BISHOP;
         }

         else {
            if (x % 2 == 0)
               id = Constants.pieceIDs.WHITE_QUEENS_KNIGHT;
            else
               id = Constants.pieceIDs.WHITE_KINGS_KNIGHT;
         }

         newPiece = new Knight(id);
      } else {
         if (piece.getColor() == Constants.pieceIDs.BLACK) {
            id = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
         } else {
            id = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
         }

         newPiece = new Rook(id);
      }

      MATCH_TRANSCRIPT.add("PP" + piece.getId() + "-" + type);

      movePiece(newPiece, x, y, x, y, parsingTranscript);
      LIVE_PIECES.set(LIVE_PIECES.indexOf(piece), newPiece);
   }

   public void devRequest(byte request) {
      switch (request) {
         case Constants.Dev.GET_AI_MOVES:
            Task<String> move = new Task<String>() {

               @Override
               protected String call() throws Exception {
                  return bot.getMove(GRID);
               }

            };

            move.valueProperty();
            ChangeListener<String> listener = new ChangeListener<String>() {

               @Override
               public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                  move.valueProperty().removeListener(this);
                  playAi(move.getValue());
               }

            };
            move.valueProperty().addListener(listener);

            Thread botThread = new Thread(move);
            botThread.setDaemon(false);
            botThread.start();
            // Bot.BoardInteractions bi = bot.BI.parseAiMove(bot.getMove(GRID,
            // DEAD_PIECES));
            // System.out.println(Arrays.toString(bi.getMove()));
            break;
      }
   }

   public void pauseGame() {
      TIMERS[turn].pauseTimer();
   }

   public void resumeGame() {
      TIMERS[turn].playTimer();
   }

   public void saveGame() throws IOException {
      solidifyTranscript();


      File transcriptFile;
      if(App.getTranscriptPath() !=null){
         transcriptFile = new File(App.getTranscriptPath());
      } else {
         Date date =  new Date(System.currentTimeMillis());
         transcriptFile = new File(Constants.boardData.TRANSCRIPT_DIR_PATH + date.toString().replace(':', '-') + ".txt");
         transcriptFile.createNewFile();
      }
      FileWriter myWriter = new FileWriter(transcriptFile);
      myWriter.write(App.getTranscript());
      myWriter.close();

      GAME.transitionToHome();
   }

   private Map[] solidifyTranscript() {
      TIMERS[Constants.pieceIDs.WHITE].pauseTimer();
      TIMERS[Constants.pieceIDs.BLACK].pauseTimer();

      MATCH_TRANSCRIPT.add("TW" + TIMERS[Constants.pieceIDs.WHITE].getTimeMillis());
      MATCH_TRANSCRIPT.add("TB" + TIMERS[Constants.pieceIDs.BLACK].getTimeMillis());

      String ms = MATCH_TRANSCRIPT.toString();

      ms = ms.replace("[", "");
      ms = ms.replace("]", "");
      ms = ms.replaceAll(",", "\n");
      ms = ms.replaceAll(" ", "");

      App.setTranscript(ms);

      STATS[Constants.pieceIDs.WHITE].replace("remaining_time", (int) TIMERS[Constants.pieceIDs.WHITE].getTimeMillis());
      STATS[Constants.pieceIDs.BLACK].replace("remaining_time", (int) TIMERS[Constants.pieceIDs.BLACK].getTimeMillis());

      return STATS;
   }
}
