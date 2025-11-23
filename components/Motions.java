package Java.COMP3211_JungleGame.components;
import java.time.LocalDateTime;

public class Motions {
    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final int playerIndex;
    private final LocalDateTime timestamp;

    public Motions(Position from, Position to, Piece movedPiece, Piece capturedPiece, int playerIndex) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.playerIndex = playerIndex;
        this.timestamp = LocalDateTime.now();
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isCapture() {
        return capturedPiece != null;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public String toString() {
        String moveStr = movedPiece.getName() + " from " + from + " to " + to;
        if (isCapture()) {
            moveStr += " (captured " + capturedPiece.getName() + ")";
        }
        return moveStr;
    }
}

