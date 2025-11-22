package Java.COMP3211_JungleGame.components;
import Java.COMP3211_JungleGame.components.Animals.Rat;

public class Board {
    private static final int ROWS = 9;
    private static final int COLS = 7;
    private final Piece[][] grid;

    private static final Position[] WATER_SQUARES = {
            new Position(3, 1), new Position(3, 2),
            new Position(4, 1), new Position(4, 2),
            new Position(5, 1), new Position(5, 2),
            new Position(3, 4), new Position(3, 5),
            new Position(4, 4), new Position(4, 5),
            new Position(5, 4), new Position(5, 5)
    };

    private static final Position[] PLAYER0_TRAPS = {
            new Position(0, 2), new Position(1, 3), new Position(0, 4)
    };

    private static final Position[] PLAYER1_TRAPS = {
            new Position(8, 4), new Position(7, 3), new Position(8, 2)
    };

    private static final Position PLAYER0_DEN = new Position(0, 3);
    private static final Position PLAYER1_DEN = new Position(8, 3);

    public Board() {
        grid = new Piece[ROWS][COLS];
    }

    public void setPieceAt(Position position, Piece piece) {
        validatePosition(position);
        grid[position.getRow()][position.getColumn()] = piece;
        if (piece != null) {
            piece.setPosition(position);
        }
    }

    public Piece getPieceAt(Position position) {
        validatePosition(position);
        return grid[position.getRow()][position.getColumn()];
    }

    public void removePieceAt(Position position) {
        validatePosition(position);
        grid[position.getRow()][position.getColumn()] = null;
    }

    public boolean isEmpty(Position position) {
        return getPieceAt(position) == null;
    }

    public boolean isWater(Position position) {
        for (Position water : WATER_SQUARES) {
            if (water.equals(position)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTrap(Position position, int playerId) {
        Position[] traps = (playerId == 0) ? PLAYER0_TRAPS : PLAYER1_TRAPS;
        for (Position trap : traps) {
            if (trap.equals(position)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDen(Position position, int playerId) {
        Position den = (playerId == 0) ? PLAYER0_DEN : PLAYER1_DEN;
        return den.equals(position);
    }

    public Position getDenPosition(int playerId) {
        return (playerId == 0) ? PLAYER0_DEN : PLAYER1_DEN;
    }

    public boolean hasRatInWaterBetween(Position from, Position to) {
        if (from.getRow() == to.getRow()) {
            int minCol = Math.min(from.getColumn(), to.getColumn());
            int maxCol = Math.max(from.getColumn(), to.getColumn());
            for (int col = minCol + 1; col < maxCol; col++) {
                Position pos = new Position(from.getRow(), col);
                if (isWater(pos)) {
                    Piece piece = getPieceAt(pos);
                    if (piece instanceof Rat) {
                        return true;
                    }
                }
            }
        }

        if (from.getColumn() == to.getColumn()) {
            int minRow = Math.min(from.getRow(), to.getRow());
            int maxRow = Math.max(from.getRow(), to.getRow());
            for (int row = minRow + 1; row < maxRow; row++) {
                Position pos = new Position(row, from.getColumn());
                if (isWater(pos)) {
                    Piece piece = getPieceAt(pos);
                    if (piece instanceof Rat) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Board copy() {
        Board newBoard = new Board();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                newBoard.grid[row][col] = this.grid[row][col];
            }
        }
        return newBoard;
    }

    private void validatePosition(Position position) {
        if (position.getRow() < 0 || position.getRow() >= ROWS ||
                position.getColumn() < 0 || position.getColumn() >= COLS) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
    }

    public int getRows() {
        return ROWS;
    }

    public int getCols() {
        return COLS;
    }
}
