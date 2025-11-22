package Java.COMP3211_JungleGame.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameRecorder {
    private final Stack<Motions> moveHistory;
    private int player0UndoCount;  // Separate counter for Player 0
    private int player1UndoCount;  // Separate counter for Player 1
    private static final int MAX_UNDO_PER_PLAYER = 3;

    public GameRecorder() {
        this.moveHistory = new Stack<>();
        this.player0UndoCount = 0;
        this.player1UndoCount = 0;
    }

    public void recordMove(Motions move) {
        moveHistory.push(move);
    }

    public Motions peekLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        return moveHistory.peek();
    }

    public Motions undoLastMove(int currentPlayerIndex) {
        if (!canUndo(currentPlayerIndex)) {
            throw new IllegalStateException(getUndoMessage(currentPlayerIndex));
        }

        Motions move = moveHistory.pop();

        // Increment undo count for the current player
        if (currentPlayerIndex == 0) {
            player0UndoCount++;
        } else {
            player1UndoCount++;
        }

        return move;
    }

    public boolean canUndo(int currentPlayerIndex) {
        if (moveHistory.isEmpty()) {
            return false;
        }

        int playerUndoCount = (currentPlayerIndex == 0) ? player0UndoCount : player1UndoCount;
        return playerUndoCount < MAX_UNDO_PER_PLAYER;
    }

    public int getRemainingUndos(int playerIndex) {
        int playerUndoCount = (playerIndex == 0) ? player0UndoCount : player1UndoCount;
        return MAX_UNDO_PER_PLAYER - playerUndoCount;
    }

    public int getPlayer0UndoCount() {
        return player0UndoCount;
    }

    public int getPlayer1UndoCount() {
        return player1UndoCount;
    }

    public List<Motions> getAllMoves() {
        return new ArrayList<>(moveHistory);
    }

    public int getMoveCount() {
        return moveHistory.size();
    }

    private String getUndoMessage(int playerIndex) {
        if (moveHistory.isEmpty()) {
            return "No moves to undo";
        }
        return "Maximum " + MAX_UNDO_PER_PLAYER + " undos per player reached";
    }
}
