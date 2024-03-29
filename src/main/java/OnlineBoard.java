import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

/**
 * OnlineBoard class, handles the interaction of pieces and displaying for the
 * online mode.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class OnlineBoard {
   Properties config = new Properties();

   private final ArrayList<String> MATCH_TRANSCRIPT = new ArrayList<String>();
   private final GameController GAME;
   private final GridPane gp_CHESS_BOARD;
   private final GridPane gp_DEAD_WHITE_CELLS;
   private final GridPane gp_DEAD_BLACK_CELLS;

   // Current black dead cell and current white dead cell
   private byte cbdc = 0;
   private byte cwdc = 0;

   // Grid position with the ids
   private final byte[][] GRID = new byte[8][8];

   private final StackPane[][] CELLS = new StackPane[8][8];

   // List of game pieces
   private final Piece[] GAME_PIECES = new Piece[34];
   private final ArrayList<Piece> LIVE_PIECES = new ArrayList<Piece>();
   private final ArrayList<Piece> DEAD_PIECES = new ArrayList<Piece>();

   private StackPane sp_selected;
   private final ArrayList<StackPane> POSSIBLE_MOVES = new ArrayList<StackPane>();

   private final PlayerTimer[] TIMERS = new PlayerTimer[2];

   private final Map[] STATS = new Map[] { new HashMap<String, Integer>(), new HashMap<String, Integer>(),
         new HashMap<String, String>() };
   private byte winner;

   private final DatabaseReference SERVER_REF;

   private boolean isTurn = false;
   private byte color;

   private int matchTranscriptIdx = 0;

   private String opponentRef;

   private boolean promotingPawn = false;

   protected boolean cancelledMatch = false;
   protected boolean matchOver = false;

   protected boolean matchBegun = false;

   /**
    * Constructor for the Board class
    * 
    * @param game       Reference to the GameController class.
    * @param chessBoard GridPane element containing the rows, and columns of
    *                   StackPane.
    * @throws InterruptedException Will throw an error if the thread is interrupted
    */
   public OnlineBoard(GameController game, GridPane[] cells) throws InterruptedException {
      GAME = game;
      gp_CHESS_BOARD = cells[0];
      gp_DEAD_BLACK_CELLS = cells[1];
      gp_DEAD_WHITE_CELLS = cells[2];

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
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      TIMERS[Constants.pieceIDs.WHITE] = new PlayerTimer(whiteLabel,
            GAME.getHiddenTimeReference(Constants.pieceIDs.WHITE), gameTime, false, true, this);
      TIMERS[Constants.pieceIDs.BLACK] = new PlayerTimer(blackLabel,
            GAME.getHiddenTimeReference(Constants.pieceIDs.BLACK), gameTime, false, true, this);

      setupBoard();

      opponentRef = App.getOpponentRefId();

      SERVER_REF = App.getServerReference();

      // Update match begun value

      SERVER_REF.addValueEventListener(new ValueEventListener() {
         @Override
         /**
          * This method is called when the data changes
          * 
          * @param snapshot DataSnapshot
          */
         public void onDataChange(DataSnapshot dataSnapshot) {
            if (!matchBegun) {
               try {
                  isTurn = (Boolean) dataSnapshot.child("USER " + config.getProperty("UID")).child("turn").getValue();

                  color = Byte.parseByte(
                        (String) dataSnapshot.child("USER " + config.getProperty("UID")).child("color").getValue());
               } catch (Exception e) {
                  e.printStackTrace();
               }
               matchBegun = true;
            } else if (dataSnapshot.getChildrenCount() < 2) {
               Platform.runLater(new Runnable() {

                  @Override
                  public void run() {
                     if (cancelledMatch) {
                        if (color == Constants.pieceIDs.WHITE)
                           winner = Constants.pieceIDs.BLACK;
                        else
                           winner = Constants.pieceIDs.WHITE;

                        App.setWinMsg("Match over");
                     } else {
                        winner = color;
                        App.setWinMsg("You won! Opponent left the match.");
                     }

                     App.setWinner(winner);

                     try {
                        if (SERVER_REF != null)
                           SERVER_REF.setValueAsync(null);

                        gameover();
                     } catch (IOException e) {
                        e.printStackTrace();
                     }

                  }

               });
            }
         }

         @Override
         /**
          * Not used
          */
         public void onCancelled(DatabaseError error) {

         }

      });

      SERVER_REF.child("matchBegun").setValueAsync(true);

      SERVER_REF.child("USER " + config.getProperty("UID")).child("turn")
            .addValueEventListener(new ValueEventListener() {

               @Override
               /**
                * This method is called when the data changes
                * 
                * @param snapshot DataSnapshot
                */
               public void onDataChange(DataSnapshot snapshot) {
                  // Execute on JavaFX thread

                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           isTurn = (Boolean) snapshot.getValue();

                           if (isTurn) {
                              TIMERS[color].playTimer();
                           } else {
                              TIMERS[color].pauseTimer();
                           }

                        } catch (Exception e) {
                           // Match is over --> do nothing.
                        }
                     }
                  });

               }

               @Override
               /**
                * Not used
                */
               public void onCancelled(DatabaseError error) {

               }

            });

      SERVER_REF.child("matchTranscript").addValueEventListener(new ValueEventListener() {

         @Override
         /**
          * This method is called when the data changes
          * 
          * @param snapshot DataSnapshot
          */
         public void onDataChange(DataSnapshot snapshot) {
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                  MATCH_TRANSCRIPT.clear();
                  for (DataSnapshot child : snapshot.getChildren()) {
                     MATCH_TRANSCRIPT.add((String) child.getValue());
                  }

                  parseOpponentMove();
               }
            });
         }

         @Override
         /**
          * Not used
          */
         public void onCancelled(DatabaseError error) {
         }

      });

      SERVER_REF.child(opponentRef).child("timer").addValueEventListener(new ValueEventListener() {

         @Override
         /**
          * This method is called when the data changes
          * 
          * @param snapshot DataSnapshot
          */
         public void onDataChange(DataSnapshot snapshot) {
            // Update the timers on the JavaFX thread
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                  try {
                     TIMERS[(color == Constants.pieceIDs.WHITE) ? Constants.pieceIDs.BLACK : Constants.pieceIDs.WHITE]
                           .setTime((Long) snapshot.getValue());
                  } catch (Exception e) {
                     // Match is over --> do nothing.
                  }
               }
            });
         }

         @Override
         /**
          * Not used
          */
         public void onCancelled(DatabaseError error) {
         }

      });

      GAME.getHiddenTimeReference(color).textProperty().addListener(new ChangeListener<String>() {

         @Override
         /**
          * This method is called when the invisible text is changed.
          * 
          * @param observable ObservableValue object
          * @param oldValue   String object
          * @param newValue   String object
          */
         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            // True if the duratio is less than 0
            if (Long.parseLong(newValue) <= 0) {
               byte winner;

               // declare the winner
               if (color == Constants.pieceIDs.BLACK)
                  winner = Constants.pieceIDs.WHITE;
               else
                  winner = Constants.pieceIDs.BLACK;

               // update the winner and win message
               SERVER_REF.child("winner").setValueAsync(Byte.toString(winner));

               // Let the users know the game has concluded.
               SERVER_REF.child("gameover").setValueAsync(true);
            }
         }
      });

      SERVER_REF.addChildEventListener(new ChildEventListener() {

         @Override
         /**
          * Not used
          */
         public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
         }

         @Override
         /**
          * This method is executed when a child is changed
          * 
          * @param snapshot          DataSnapshot object
          * @param previousChildName String object
          */
         public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            Platform.runLater(new Runnable() {

               @Override
               /**
                * Run on the JavaFX thread
                */
               public void run() {
                  // Key and value of data
                  String key = snapshot.getKey();
                  Object value = snapshot.getValue();

                  if (key.equalsIgnoreCase("winner")) {
                     App.setWinner(Byte.parseByte((String) value));

                     if (color == Byte.parseByte((String) value)) {
                        App.setWinMsg("You won!");
                     } else {
                        App.setWinMsg("You lost!");
                     }
                  } else if (key.equalsIgnoreCase("gameover")) {
                     if ((Boolean) value) {
                        matchOver = true;
                        if (SERVER_REF != null)
                           SERVER_REF.setValueAsync(null);

                        try {
                           gameover();
                        } catch (IOException e) {
                        }
                     }
                  }

               }
            });
         }

         /**
          * Not used
          */
         @Override
         public void onChildRemoved(DataSnapshot snapshot) {
         }

         /**
          * Not used
          */
         @Override
         public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
         }

         /**
          * Not used
          */
         @Override
         public void onCancelled(DatabaseError error) {
         }

      });

      App.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {

         @Override
         public void handle(WindowEvent event) {
            // Cancel match, credited with a loss

            try {
               com.google.cloud.storage.Bucket bucket = StorageClient.getInstance().bucket();

               // Get user's stats
               FileOutputStream fos = new FileOutputStream(Constants.Online.PATH_TO_STATS);
               fos.close();

               Scanner statsReader = new Scanner(new FileReader(Constants.Online.PATH_TO_STATS));

               int wins = 0;
               int losses = 0;
               int score = 0;

               // Read the data
               while (statsReader.hasNextLine()) {
                  String line = statsReader.nextLine();

                  if (line.contains("wins")) {
                     wins = Integer.parseInt(line.replace("wins=", ""));
                  } else if (line.contains("losses")) {
                     losses = Integer.parseInt(line.replace("losses=", ""));
                  } else if (line.contains("score")) {
                     score = Integer.parseInt(line.replace("score=", ""));
                  }
               }

               // Add wins or losses and update score
               losses++;

               if (score - 50 > 0)
                  score -= 50;

               String statsPush = "wins=" + wins + "\nlosses=" + losses + "\nplayTime=0\nscore=" + score;

               FileWriter writer = new FileWriter(new File(Constants.Online.PATH_TO_STATS));
               // write data
               writer.write(statsPush);
               writer.close();
               // Push data to server
               bucket.create("profiles/" + config.getProperty("UID") + "/stats.txt",
                     java.nio.file.Files.readAllBytes(Paths.get(Constants.Online.PATH_TO_STATS)));

            } catch (Exception e) {

            }

            App.getServerReference().setValueAsync(null);
            System.exit(0);
         }
      });
   }

   /**
    * This method will parse an opponents move
    */
   private void parseOpponentMove() {
      final String REG = "((?=[A-Z])|(?<=[A-Z]))|((?=[a-z])|(?<=[a-z]))";

      for (int i = matchTranscriptIdx; i < MATCH_TRANSCRIPT.size(); i++) {
         try {

            String ts = MATCH_TRANSCRIPT.get(i);

            if (ts.contains("PP") && !isTurn) { // Pawn promotion
               ts = ts.substring(2, ts.length());
               
               String[] ids = ts.split("-");
               Piece from = getPieceOnGrid(Byte.parseByte(ids[0]), true);

               if (from != null)
                  promotePawn(from, ids[1], true);
            } else if (ts.contains("KILL")) { // piece killed
               System.out.println("KILL FOUND");
               ts = ts.substring(4, ts.length());
               byte id = Byte.parseByte(ts);

               Piece piece = getPieceOnGrid(id, false);

               LIVE_PIECES.remove(piece);
               DEAD_PIECES.add(piece);

               GRID[piece.getGridX()][piece.getGridY()] = Constants.pieceIDs.EMPTY_CELL;
               displayDeadPiece(piece);

               StackPane sp = CELLS[piece.getGridX()][piece.getGridY()];
               sp.getChildren().clear();
            } else { // Normal move
               String[] data = ts.split(REG);

               Piece piece = getPieceOnGrid(Byte.parseByte(data[0].trim()), false);

               if (piece == null)
                  return;

               byte x = getBoardX(data[1]);
               byte y = getBoardY(data[2]);

               // Check move is valid
               if (x >= 0 && x < 8 && y >= 0 && y < 8) {

                  movePiece(piece, piece.getGridX(), piece.getGridY(), x, y, true, false);

                  App.MOVE_COUNT++;
               }
            }
         } catch (Exception err) {
            err.printStackTrace();
         }
      }

      matchTranscriptIdx = MATCH_TRANSCRIPT.size();
   }

   /**
    * This method is called once in the constructor of the Board class.
    * The method will setup the board by placing all the game pieces in the
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
               if ((getAllowInteract(sp, x, y) || isPossibleMove))
                  highlightMouseCellHover(true, sp);
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

      // Loop through grid pieces
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
    * This method is called when the match concludes.
    */
   private void gameover() throws IOException {
      App.setMatchStats(solidifyTranscript());

      GAME.gameOver();
   }

   /**
    * This method will return the x position of a string for a board value
    * 
    * @param value String value (A -> H)
    * @return byte value corresponding to index of location
    */
   private byte getBoardX(String value) {
      String[] xPos = Constants.boardData.X_ID;
      for (byte i = 0; i < xPos.length; i++) {
         if (xPos[i].equalsIgnoreCase(value))
            return i;
      }

      return -1;
   }

   /**
    * This method will
    * 
    * @param value
    * @return
    */
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
               return isTurn && livePiece.getColor() == color;
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

      return null;
   }

   /**
    * This method will return the piece on the inputed board coordinates.
    * 
    * @param id The byte identification of a game piece
    * @return Piece object or null.
    */
   private Piece getPieceOnGrid(byte id, boolean promoted) {
      for (Piece piece : LIVE_PIECES) {
         if ((piece.getId() == id && !promoted) || (promoted && piece.getId() == id
               && piece.getGridY() == ((piece.getColor() == Constants.pieceIDs.WHITE) ? 0 : 7)))
            return piece;
      }

      return null;
   }

   /**
    * This method will return the piece on the inputed board coordinates.
    * 
    * @param id The byte identification of a game piece
    * @return Piece object or null.
    */
   private Piece getAwaitingPawnPromotion(byte color) {
      int y = (color == Constants.pieceIDs.WHITE) ? 0 : 7;

      for (Piece piece : LIVE_PIECES) {
         if (piece.getColor() == color) {
            if (piece.getGridY() == y)
               return piece;
         }
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
    * @return byet
    */
   public byte getTurn() {
      return (isTurn) ? Constants.pieceIDs.WHITE : Constants.pieceIDs.BLACK;
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
      boolean checkmate = true;
      winner = color;

      STATS[getTurn()].replace("total_moves", (int) STATS[getTurn()].get("total_moves") + 1);

      for (Piece p : LIVE_PIECES) {
         if (p.getColor() == color)
            continue;

         if (p.getPossibleMoves(GRID).length > 0)
            checkmate = false;
      }

      // Check mate
      if (checkmate) {
         Platform.runLater(new Runnable() {

            @Override
            public void run() {
               SERVER_REF.child("winner").setValueAsync(Byte.toString(winner)).addListener(new Runnable() {

                  @Override
                  public void run() {
                     SERVER_REF.child("gameover").setValueAsync(true);
                     try {
                        gameover();
                     } catch (IOException e) {
                     }

                  }

               }, new Executor() {

                  @Override
                  public void execute(Runnable command) {
                     command.run();
                  }

               });
            }

         });
      }

      // Won't update the data if the user is promoting a pawn
      if (!promotingPawn) {
         pushToServer();
      }
   }

   /**
    * This method will push updated data to the server.
    */
   private void pushToServer() {
      matchTranscriptIdx = MATCH_TRANSCRIPT.size();

      SERVER_REF.child("matchTranscript").setValueAsync(MATCH_TRANSCRIPT).addListener(new Runnable() {

         @Override
         public void run() {
            SERVER_REF.child("USER " + config.getProperty("UID")).child("turn").setValueAsync(false)
                  .addListener(new Runnable() {

                     @Override
                     public void run() {
                        SERVER_REF.child(opponentRef).child("turn").setValueAsync(true);
                     }

                  }, new Executor() {

                     @Override
                     public void execute(Runnable command) {
                        command.run();
                     }

                  });
            ;
         }

      }, new Executor() {

         @Override
         public void execute(Runnable command) {
            command.run();
         }
      });

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

   /**
    * This method will call the getPossibleMoves method for a specific game piece
    * and display the data onto the screen.
    * 
    * @param piece Target piece.
    */
   private void displayPossibleMoves(Piece piece) {
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
         // logic for en passant

         // if the piece is a pawn
         pawn = (Pawn) piece;

         // gets the id of the piece to the right and left
         // makes sure it can check to the left and can check to the right
         byte pawnLeft = (pawn.gridX - 1 > -1) ? GRID[piece.gridX - 1][piece.gridY] : -1;
         byte pawnRight = (pawn.gridX + 1 < 8) ? GRID[piece.gridX + 1][piece.gridY] : -1;

         int pawnDirection = (pawn.getColor() == Constants.pieceIDs.BLACK) ? 1 : -1;

         if (pawn.canPassantRight(pawnRight, GRID)) {
            StackPane s_rightPawn = CELLS[pawn.gridX + 1][pawn.gridY + 1 * pawnDirection];
            s_rightPawn.getStyleClass().add("cell-enemy");
            setPassantMoveMouseClicked(s_rightPawn, pawn, (Pawn) GAME_PIECES[pawnRight]);
            POSSIBLE_MOVES.add(s_rightPawn);
         }

         if (pawn.canPassantLeft(pawnLeft, GRID)) {
            StackPane s_leftPawn = CELLS[pawn.gridX - 1][pawn.gridY + 1 * pawnDirection];
            s_leftPawn.getStyleClass().add("cell-enemy");
            setPassantMoveMouseClicked(s_leftPawn, pawn, (Pawn) GAME_PIECES[pawnLeft]);
            POSSIBLE_MOVES.add(s_leftPawn);

         }

      }

      // Loop through the moves
      for (byte i = 0; i < moves.length; i++) {
         final byte x = moves[i][0];
         final byte y = moves[i][1];

         // Get instance of CELLS at specified indexes
         StackPane sp_move = CELLS[moves[i][0]][moves[i][1]];
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
   private void movePiece(Piece piece, byte fromX, byte fromY, byte toX, byte toY, boolean parsingTranscript,
         boolean promoted) {
      StackPane from = CELLS[fromX][fromY];
      from.getChildren().clear();

      if (piece.getType() == Constants.pieceType.PAWN && Math.abs(toY - fromY) == 2) {
         ((Pawn) piece).setPassant(App.MOVE_COUNT);
      }

      StackPane to = CELLS[toX][toY];
      for (Node tChild : to.getChildren()) {
         if (tChild instanceof ImageView) {
            Piece p = getPieceOnGrid(toX, toY);

            LIVE_PIECES.remove(p);
            DEAD_PIECES.add(p);

            STATS[getTurn()].replace("pieces_killed", (int) STATS[getTurn()].get("pieces_killed") + 1);

            displayDeadPiece(p);
            break;
         }
      }

      to.getChildren().clear();
      to.getChildren().add(piece.getSprite());

      piece.setGridPos(toX, toY);

      GRID[fromX][fromY] = Constants.pieceIDs.EMPTY_CELL;
      GRID[toX][toY] = piece.getId();

      if (!parsingTranscript) {
         String moveTranscript = piece.getId() + Constants.boardData.X_ID[toX] + Constants.boardData.Y_ID[toY];
         MATCH_TRANSCRIPT.add(moveTranscript);

      }

      if (!promoted && piece.getType() == Constants.pieceType.PAWN && (piece.getGridY() == 0 || piece.getGridY() == 7)
            && !parsingTranscript) {
         try {
            promotingPawn = true;
            GAME.displayPawnPromotion(piece, color);
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
            movePiece(piece, piece.getGridX(), piece.getGridY(), x, y, false, false);
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
            piece.hasMoved = true;
            if (left) {
               movePiece(piece, piece.getGridX(), piece.getGridY(), (byte) (piece.getGridX() - 2), piece.getGridY(),
                     false, false);
               if (color == Constants.pieceIDs.BLACK) {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.BLACK_QUEENS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() + 1), piece.getGridY(),
                        false, false);
               } else {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.WHITE_QUEENS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() + 1), piece.getGridY(),
                        false, false);
               }
            } else {
               movePiece(piece, piece.getGridX(), piece.getGridY(), (byte) (piece.getGridX() + 2), piece.getGridY(),
                     false, false);
               if (color == Constants.pieceIDs.BLACK) {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.BLACK_KINGS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() - 1), piece.getGridY(),
                        false, false);
               } else {
                  Piece rook = GAME_PIECES[Constants.pieceIDs.WHITE_KINGS_ROOK];
                  rook.hasMoved = true;
                  movePiece(rook, rook.getGridX(), rook.getGridY(), (byte) (piece.getGridX() - 1), piece.getGridY(),
                        false, false);
               }
            }
            nextTurn();
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
      GRID[enemyPawn.gridX][enemyPawn.gridY] = Constants.pieceIDs.EMPTY_CELL;

      sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {

            // Moves the primary pawn to its new spot.
            if (primaryPawn.getColor() == Constants.pieceIDs.BLACK) {
               if (enemyPawn.getGridX() < primaryPawn.getGridX()) {
                  // pawn is to the left
                  movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX - 1),
                        (byte) (primaryPawn.gridY + 1), false, false);

               } else {
                  // pawn is to the right
                  movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX + 1),
                        (byte) (primaryPawn.gridY + 1), false, false);
               }
            } else {
               if (enemyPawn.getGridX() < primaryPawn.getGridX()) {
                  // pawn to the left
                  movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX - 1),
                        (byte) (primaryPawn.gridY - 1), false, false);
               } else {
                  // pawn is to the right
                  movePiece(primaryPawn, primaryPawn.gridX, primaryPawn.gridY, (byte) (primaryPawn.gridX + 1),
                        (byte) (primaryPawn.gridY - 1), false, false);
               }

            }

            // Removes enemyPawn from the game.
            GRID[enemyPawn.gridX][enemyPawn.gridY] = Constants.pieceIDs.EMPTY_CELL;
            MATCH_TRANSCRIPT.add("KILL" + enemyPawn.getId());

            StackPane enemyPane = CELLS[enemyPawn.gridX][enemyPawn.gridY];
            enemyPane.getChildren().clear();
            LIVE_PIECES.remove(enemyPawn);
            DEAD_PIECES.add(enemyPawn);

            displayDeadPiece(enemyPawn);
            nextTurn();
         }

      });

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

      LIVE_PIECES.set(LIVE_PIECES.indexOf(piece), newPiece);

      movePiece(newPiece, x, y, x, y, parsingTranscript, true);

      promotingPawn = false;
      if (!parsingTranscript) {
         MATCH_TRANSCRIPT.add("PP" + piece.getId() + "-" + type);
         pushToServer();
      }
   }

   /**
    * This method will complete the game transcript by including the timers and
    * replacing useless characters.
    * 
    * @return Map[] containing the game stats.
    */
   private Map[] solidifyTranscript() {
      // Pause timers
      TIMERS[Constants.pieceIDs.WHITE].pauseTimer();
      TIMERS[Constants.pieceIDs.BLACK].pauseTimer();

      // Add timer values
      MATCH_TRANSCRIPT.add("TW" + TIMERS[Constants.pieceIDs.WHITE].getTimeMillis());
      MATCH_TRANSCRIPT.add("TB" + TIMERS[Constants.pieceIDs.BLACK].getTimeMillis());

      String ms = MATCH_TRANSCRIPT.toString();

      ms = ms.replace("[", "");
      ms = ms.replace("]", "");
      ms = ms.replaceAll(",", "\n");
      ms = ms.replaceAll(" ", "");

      // Set transcript
      App.setTranscript(ms);

      STATS[Constants.pieceIDs.WHITE].replace("remaining_time", (int) TIMERS[Constants.pieceIDs.WHITE].getTimeMillis());
      STATS[Constants.pieceIDs.BLACK].replace("remaining_time", (int) TIMERS[Constants.pieceIDs.BLACK].getTimeMillis());

      return STATS;
   }

   /**
    * This method updates the remaining timer to the server
    * 
    * @param duration The duration remaining in millis
    */
   public void updateTimeToServer(long duration) {
      SERVER_REF.child("USER " + config.getProperty("UID")).child("timer").setValueAsync(duration);
   }
}