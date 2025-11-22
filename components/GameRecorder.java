package Java.COMP3211_JungleGame.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameRecorder {
    private final Stack<Motions> moveHistory;
    private int totalUndoCount;
    private static final int MAX_UNDO_TOTAL = 3;

    public GameRecorder() {
        this.moveHistory = new Stack<>();
        this.totalUndoCount = 0;
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

    public Motions undoLastMove() {
        if (!canUndo()) {
            throw new IllegalStateException(getUndoMessage());
        }
        Motions move = moveHistory.pop();
        totalUndoCount++;
        return move;
    }

    public boolean canUndo() {
        return !moveHistory.isEmpty() && totalUndoCount < MAX_UNDO_TOTAL;
    }

    public int getRemainingUndos() {
        return MAX_UNDO_TOTAL - totalUndoCount;
    }

    public List<Motions> getAllMoves() {
        return new ArrayList<>(moveHistory);
    }

    public void clear() {
        moveHistory.clear();
        totalUndoCount = 0;
    }

    public int getMoveCount() {
        return moveHistory.size();
    }

    private String getUndoMessage() {
        if (moveHistory.isEmpty()) {
            return "No moves to undo";
        }
        return "Maximum 3 undos per game reached";
    }
}
