package Java.COMP3211_JungleGame.components;

import Java.COMP3211_JungleGame.components.Animals.*;

public class GameManager {
    private final Board board;
    private final Player[] players;
    private final GameRecorder gameRecord;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Player winner;

    public GameManager(Player player0, Player player1) {
        this.board = new Board();
        this.players = new Player[]{player0, player1};
        this.gameRecord = new GameRecorder();
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.winner = null;
        initializeBoard();
    }

    private void initializeBoard() {
        board.setPieceAt(new Position(0, 0), new Lion(players[0], new Position(0, 0)));
        board.setPieceAt(new Position(0, 6), new Tiger(players[0], new Position(0, 6)));
        board.setPieceAt(new Position(1, 1), new Dog(players[0], new Position(1, 1)));
        board.setPieceAt(new Position(1, 5), new Cat(players[0], new Position(1, 5)));
        board.setPieceAt(new Position(2, 0), new Rat(players[0], new Position(2, 0)));
        board.setPieceAt(new Position(2, 2), new Leopard(players[0], new Position(2, 2)));
        board.setPieceAt(new Position(2, 4), new Wolf(players[0], new Position(2, 4)));
        board.setPieceAt(new Position(2, 6), new Elephant(players[0], new Position(2, 6)));

        board.setPieceAt(new Position(8, 6), new Lion(players[1], new Position(8, 6)));
        board.setPieceAt(new Position(8, 0), new Tiger(players[1], new Position(8, 0)));
        board.setPieceAt(new Position(7, 5), new Dog(players[1], new Position(7, 5)));
        board.setPieceAt(new Position(7, 1), new Cat(players[1], new Position(7, 1)));
        board.setPieceAt(new Position(6, 6), new Rat(players[1], new Position(6, 6)));
        board.setPieceAt(new Position(6, 4), new Leopard(players[1], new Position(6, 4)));
        board.setPieceAt(new Position(6, 2), new Wolf(players[1], new Position(6, 2)));
        board.setPieceAt(new Position(6, 0), new Elephant(players[1], new Position(6, 0)));

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    piece.getOwner().addPiece(piece);
                }
            }
        }
    }

    /**
     * Execute a move without switching turns.
     * Turn will be switched only when confirmTurn() is called.
     */
    public void executeMove(Position from, Position to) {
        validateMove(from, to);
        Piece piece = board.getPieceAt(from);
        Piece capturedPiece = board.getPieceAt(to);

        if (capturedPiece != null) {
            capturedPiece.setCaptured(true);
            capturedPiece.getOwner().removePiece(capturedPiece);
        }

        board.removePieceAt(from);
        board.setPieceAt(to, piece);
        piece.setPosition(to);

        Motions move = new Motions(from, to, piece, capturedPiece, currentPlayerIndex);
        gameRecord.recordMove(move);

        checkWinCondition();
        // NOTE: Turn switching is now deferred to confirmTurn()
    }

    /**
     * Confirm the current player's turn and switch to the next player.
     * This should be called after the player confirms they don't want to undo.
     */
    public void confirmTurn() {
        if (!gameOver) {
            switchTurn();
        }
    }

    private void validateMove(Position from, Position to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        Piece piece = board.getPieceAt(from);
        if (piece == null) {
            throw new IllegalArgumentException("No piece at position " + positionToString(from));
        }

        if (piece.getOwner().getPlayerId() != currentPlayerIndex) {
            throw new IllegalArgumentException("That piece belongs to " +
                    piece.getOwner().getName() + ", not the current player");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot move to the same position");
        }

        Piece targetPiece = board.getPieceAt(to);
        if (targetPiece != null && targetPiece.getOwner().equals(piece.getOwner())) {
            throw new IllegalArgumentException("Cannot capture your own piece at " + positionToString(to));
        }

        if (board.isDen(to, currentPlayerIndex)) {
            throw new IllegalArgumentException("Cannot enter your own den");
        }

        if (piece.canJumpWater() && canJumpToPosition(piece, from, to)) {
            return;
        }

        if (!from.isAdjacentTo(to)) {
            throw new IllegalArgumentException("Can only move to adjacent squares (or jump water for Lion/Tiger)");
        }

        if (board.isWater(to) && !piece.canEnterWater()) {
            throw new IllegalArgumentException(piece.getName() + " cannot enter water (only Rat can)");
        }

        if (piece instanceof Rat && board.isWater(from)) {
            if (targetPiece instanceof Rat && !board.isWater(to)) {
                throw new IllegalArgumentException("Rat in water cannot capture Rat on land");
            }
            if (targetPiece instanceof Elephant) {
                throw new IllegalArgumentException("Rat in water cannot capture Elephant");
            }
        }

        if (piece instanceof Rat && !board.isWater(from)) {
            if (targetPiece instanceof Rat && board.isWater(to)) {
                throw new IllegalArgumentException("Rat on land cannot capture Rat in water");
            }
        }

        if (targetPiece != null) {
            if (board.isTrap(from, 1 - currentPlayerIndex)) {
                return;
            }
            if (!piece.canCapture(targetPiece)) {
                throw new IllegalArgumentException(piece.getName() + " (rank " + piece.getRank() +
                        ") cannot capture " + targetPiece.getName() + " (rank " + targetPiece.getRank() + ")");
            }
        }
    }

    private boolean canJumpToPosition(Piece piece, Position from, Position to) {
        if (from.getRow() != to.getRow() && from.getColumn() != to.getColumn()) {
            return false;
        }

        boolean jumpingOverWater = false;
        if (from.getRow() == to.getRow()) {
            int minCol = Math.min(from.getColumn(), to.getColumn());
            int maxCol = Math.max(from.getColumn(), to.getColumn());
            for (int col = minCol + 1; col < maxCol; col++) {
                Position middle = new Position(from.getRow(), col);
                if (board.isWater(middle)) {
                    jumpingOverWater = true;
                    break;
                }
            }
            if (jumpingOverWater && board.hasRatInWaterBetween(from, to)) {
                throw new IllegalArgumentException(piece.getName() + " cannot jump - Rat blocking the water");
            }
        }

        if (from.getColumn() == to.getColumn()) {
            int minRow = Math.min(from.getRow(), to.getRow());
            int maxRow = Math.max(from.getRow(), to.getRow());
            for (int row = minRow + 1; row < maxRow; row++) {
                Position middle = new Position(row, from.getColumn());
                if (board.isWater(middle)) {
                    jumpingOverWater = true;
                    break;
                }
            }
            if (jumpingOverWater && board.hasRatInWaterBetween(from, to)) {
                throw new IllegalArgumentException(piece.getName() + " cannot jump - Rat blocking the water");
            }
        }

        if (!jumpingOverWater) {
            return false;
        }

        Piece targetPiece = board.getPieceAt(to);
        if (targetPiece != null) {
            if (targetPiece.getOwner().equals(piece.getOwner())) {
                throw new IllegalArgumentException("Cannot land on your own piece");
            }
            if (!piece.canCapture(targetPiece)) {
                throw new IllegalArgumentException(piece.getName() + " cannot capture " +
                        targetPiece.getName() + " after jumping");
            }
        }

        return true;
    }

    /**
     * Undo the last move made by the current player.
     * Does NOT switch turns since the turn hasn't been confirmed yet.
     */
    public void undoMove() {
        if (!gameRecord.canUndo(currentPlayerIndex)) {
            if (gameRecord.getMoveCount() == 0) {
                throw new IllegalStateException("No moves to undo");
            } else {
                throw new IllegalStateException("You have already used all 3 undos");
            }
        }

        Motions lastMove = gameRecord.undoLastMove(currentPlayerIndex);
        Piece piece = board.getPieceAt(lastMove.getTo());
        board.removePieceAt(lastMove.getTo());
        board.setPieceAt(lastMove.getFrom(), piece);
        piece.setPosition(lastMove.getFrom());

        if (lastMove.isCapture()) {
            Piece capturedPiece = lastMove.getCapturedPiece();
            capturedPiece.setCaptured(false);
            board.setPieceAt(lastMove.getTo(), capturedPiece);
            capturedPiece.getOwner().addPiece(capturedPiece);
        }

        // NOTE: Turn is NOT switched here because it was never switched in executeMove()
        gameOver = false;
        winner = null;
    }

    private void checkWinCondition() {
        Position opponentDen = board.getDenPosition(1 - currentPlayerIndex);
        Piece pieceInOpponentDen = board.getPieceAt(opponentDen);
        if (pieceInOpponentDen != null &&
                pieceInOpponentDen.getOwner().getPlayerId() == currentPlayerIndex) {
            gameOver = true;
            winner = players[currentPlayerIndex];
            return;
        }

        if (players[1 - currentPlayerIndex].hasLost()) {
            gameOver = true;
            winner = players[currentPlayerIndex];
        }
    }

    private void switchTurn() {
        currentPlayerIndex = 1 - currentPlayerIndex;
    }

    private String positionToString(Position pos) {
        char column = (char)('A' + pos.getColumn());
        return "" + column + pos.getRow();
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public Player getPlayer(int index) {
        return players[index];
    }

    public GameRecorder getGameRecord() {
        return gameRecord;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int playerIndex) {
        if (playerIndex != 0 && playerIndex != 1) {
            throw new IllegalArgumentException("Player index must be 0 or 1");
        }
        this.currentPlayerIndex = playerIndex;
    }
}
