package Java.COMP3211_JungleGame.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private final String name;
    private final int playerId;
    private final List<Piece> pieces;

    public Player(String name, int playerId) {
        this.name = name;
        this.playerId = playerId;
        this.pieces = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getPlayerId() {
        return playerId;
    }

    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    public List<Piece> getActivePieces() {
        List<Piece> active = new ArrayList<>();
        for (Piece piece : pieces) {
            if (!piece.isCaptured()) {
                active.add(piece);
            }
        }
        return active;
    }

    public boolean hasLost() {
        return getActivePieces().isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return playerId == player.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String toString() {
        return name + " (Player " + playerId + ")";
    }
}
