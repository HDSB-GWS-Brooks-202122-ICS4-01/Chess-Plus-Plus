import java.util.ArrayList;

public class Player {
   private final String NAME;

   final ArrayList<Piece> LIVE_PIECES = new ArrayList<Piece>();
   final ArrayList<Piece> DEAD_PIECES = new ArrayList<Piece>();

   Player(String name) {
      NAME = name;
   }
}
