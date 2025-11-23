package Java.COMP3211_JungleGame.Test;

import Java.COMP3211_JungleGame.components.*;
import Java.COMP3211_JungleGame.components.Animals.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Jungle Game
 * Tests all game rules according to COMP3211 Project Specification
 */
public class MainTest {

    private GameManager gameManager;
    private Player player0;
    private Player player1;
    private Board board;

    @BeforeEach
    public void setUp() {
        player0 = new Player("Alice", 0);
        player1 = new Player("Bob", 1);
        gameManager = new GameManager(player0, player1);
        board = gameManager.getBoard();
    }

    /**
     * Helper method to create a custom board setup for testing specific scenarios
     * Clears the current board and sets up only the pieces you specify
     */
    private void setupCustomBoard(CustomPiece... pieces) {
        // Clear all existing pieces from the board
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);
                if (piece != null) {
                    board.removePieceAt(pos);
                    piece.getOwner().removePiece(piece);
                }
            }
        }

        // Place custom pieces
        for (CustomPiece cp : pieces) {
            board.setPieceAt(cp.position, cp.piece);
            cp.piece.getOwner().addPiece(cp.piece);
        }
    }

    /**
     * Helper class to define custom piece placements
     */
    private static class CustomPiece {
        Piece piece;
        Position position;

        CustomPiece(Piece piece, Position position) {
            this.piece = piece;
            this.position = position;
        }
    }

    // ========== BASIC MOVEMENT TESTS ==========

    @Test
    @DisplayName("Test basic horizontal movement - piece moves one square horizontally")
    public void testBasicHorizontalMovement() {
        // Move player 0's Lion from (0,0) to (0,1)
        Position from = new Position(0, 0);
        Position to = new Position(0, 1);

        Piece piece = board.getPieceAt(from);
        assertNotNull(piece, "Lion should be at starting position");
        assertEquals("Lion", piece.getName());

        gameManager.executeMove(from, to);

        assertNull(board.getPieceAt(from), "Original position should be empty");
        assertNotNull(board.getPieceAt(to), "New position should have the piece");
        assertEquals(piece, board.getPieceAt(to), "Same piece should be at new position");
    }

    @Test
    @DisplayName("Test basic vertical movement - piece moves one square vertically")
    public void testBasicVerticalMovement() {
        // Move player 0's Rat from (2,0) to (3,0)
        Position from = new Position(2, 0);
        Position to = new Position(3, 0);

        Piece rat = board.getPieceAt(from);
        assertEquals("Rat", rat.getName());

        gameManager.executeMove(from, to);

        assertNull(board.getPieceAt(from));
        assertEquals(rat, board.getPieceAt(to));
    }

    @Test
    @DisplayName("Test invalid diagonal movement - should throw exception")
    public void testInvalidDiagonalMovement() {
        // Try to move Lion diagonally from (0,0) to (1,1)
        Position from = new Position(0, 0);
        Position to = new Position(1, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(from, to);
        }, "Diagonal movement should not be allowed");
    }

    @Test
    @DisplayName("Test moving more than one square - should throw exception")
    public void testInvalidMultiSquareMovement() {
        // Try to move Dog from (1,1) to (3,1) without jumping
        Position from = new Position(1, 1);
        Position to = new Position(3, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(from, to);
        }, "Cannot move more than one square without jumping ability");
    }

    // ========== RANK-BASED CAPTURE TESTS ==========

    @Test
    @DisplayName("Test higher rank captures lower rank")
    public void testHigherRankCapturesLower() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece dog = new Dog(p0, new Position(3, 3));
        Piece cat = new Cat(p1, new Position(3, 4));

        assertTrue(dog.canCapture(cat), "Dog (rank 3) should be able to capture Cat (rank 2)");
    }

    @Test
    @DisplayName("Test same rank captures same rank")
    public void testSameRankCapturesSameRank() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece cat1 = new Cat(p0, new Position(0, 0));
        Piece cat2 = new Cat(p1, new Position(0, 1));

        assertTrue(cat1.canCapture(cat2), "Same rank should be able to capture each other");
    }

    @Test
    @DisplayName("Test lower rank cannot capture higher rank")
    public void testLowerRankCannotCaptureHigher() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece cat = new Cat(p0, new Position(0, 0)); // Rank 2
        Piece dog = new Dog(p1, new Position(0, 1)); // Rank 3

        assertFalse(cat.canCapture(dog), "Cat (rank 2) should not be able to capture Dog (rank 3)");
    }

    @Test
    @DisplayName("Test Rat can capture Elephant - special rule exception")
    public void testRatCapturesElephant() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece rat = new Rat(p0, new Position(0, 0)); // Rank 1
        Piece elephant = new Elephant(p1, new Position(0, 1)); // Rank 8

        assertTrue(rat.canCapture(elephant), "Rat should be able to capture Elephant (special rule)");
    }

    @Test
    @DisplayName("Test Elephant cannot capture Rat - special rule exception")
    public void testElephantCannotCaptureRat() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece elephant = new Elephant(p0, new Position(0, 0)); // Rank 8
        Piece rat = new Rat(p1, new Position(0, 1)); // Rank 1

        assertFalse(elephant.canCapture(rat), "Elephant should not be able to capture Rat (special rule)");
    }

    // ========== WATER SQUARE TESTS ==========

    @Test
    @DisplayName("Test only Rat can enter water")
    public void testOnlyRatCanEnterWater() {
        Player p0 = new Player("P0", 0);
        Piece rat = new Rat(p0, new Position(3, 1));
        Piece lion = new Lion(p0, new Position(0, 0));

        assertTrue(rat.canEnterWater(), "Rat should be able to enter water");
        assertFalse(lion.canEnterWater(), "Lion should not be able to enter water");
    }

    @Test
    @DisplayName("Test Rat can attack opponent Rat if both in water")
    public void testRatCanAttackRatBothInWater() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece rat1 = new Rat(p0, new Position(3, 1));
        Piece rat2 = new Rat(p1, new Position(4, 1));

        assertTrue(rat1.canCapture(rat2), "Rat in water can capture another Rat in water");
    }

    @Test
    @DisplayName("Test Rat can attack opponent Rat if both on land")
    public void testRatCanAttackRatBothOnLand() {
        Player p0 = new Player("P0", 0);
        Player p1 = new Player("P1", 1);

        Piece rat1 = new Rat(p0, new Position(2, 0));
        Piece rat2 = new Rat(p1, new Position(2, 1));

        assertTrue(rat1.canCapture(rat2), "Rat on land can capture another Rat on land");
    }

    // ========== LION AND TIGER JUMPING TESTS ==========

    @Test
    @DisplayName("Test Lion can jump over river horizontally")
    public void testLionJumpRiverHorizontally() {
        Position beforeRiver = new Position(3, 0);
        Position afterRiver = new Position(3, 3);

        Piece lion = new Lion(player0, beforeRiver);

        // Setup custom board with Lion ready to jump
        setupCustomBoard(
                new CustomPiece(lion, beforeRiver)
        );

        assertTrue(lion.canJumpWater(), "Lion should have jump ability");

        // Execute jump
        gameManager.executeMove(beforeRiver, afterRiver);

        assertEquals(lion, board.getPieceAt(afterRiver), "Lion should jump to other side");
        assertNull(board.getPieceAt(beforeRiver), "Original position should be empty");
    }

    @Test
    @DisplayName("Test Tiger can jump over river vertically")
    public void testTigerJumpRiverVertically() {
        Player p0 = new Player("P0", 0);

        Piece tiger = new Tiger(p0, new Position(2, 1));

        assertTrue(tiger.canJumpWater(), "Tiger should have jump ability");
    }

    @Test
    @DisplayName("Test Lion cannot jump if Rat blocks the river")
    public void testLionCannotJumpWithRatBlocking() {
        Position beforeRiver = new Position(3, 0);
        Position inRiver = new Position(3, 1); // Water square in jump path
        Position afterRiver = new Position(3, 3);

        Piece lion = new Lion(player0, beforeRiver);
        Piece rat = new Rat(player1, inRiver);

        // Setup custom board
        setupCustomBoard(
                new CustomPiece(lion, beforeRiver),
                new CustomPiece(rat, inRiver)
        );

        // Try to jump - should fail because Rat blocks
        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(beforeRiver, afterRiver);
        }, "Lion cannot jump when Rat blocks the river");
    }

    @Test
    @DisplayName("Test Lion can capture enemy piece after jumping")
    public void testLionCaptureAfterJump() {
        Position beforeRiver = new Position(3, 0);
        Position afterRiver = new Position(3, 3);

        Piece lion = new Lion(player0, beforeRiver);
        Piece cat = new Cat(player1, afterRiver);

        // Setup custom board
        setupCustomBoard(
                new CustomPiece(lion, beforeRiver),
                new CustomPiece(cat, afterRiver)
        );

        assertEquals(1, player1.getPieces().size(), "Player 1 should have 1 piece");

        // Lion jumps and captures
        gameManager.executeMove(beforeRiver, afterRiver);

        assertEquals(lion, board.getPieceAt(afterRiver), "Lion should be at destination");
        assertTrue(cat.isCaptured(), "Cat should be captured");
        assertEquals(0, player1.getPieces().size(), "Player 1 should have 0 pieces");
    }

    @Test
    @DisplayName("Test Tiger cannot jump with friendly Rat in river")
    public void testTigerCannotJumpWithFriendlyRatBlocking() {
        Position beforeRiver = new Position(2, 1);
        Position inRiver = new Position(4, 1); // Water square
        Position afterRiver = new Position(6, 1);

        Piece tiger = new Tiger(player0, beforeRiver);
        Piece rat = new Rat(player0, inRiver); // Friendly rat

        // Setup custom board
        setupCustomBoard(
                new CustomPiece(tiger, beforeRiver),
                new CustomPiece(rat, inRiver)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(beforeRiver, afterRiver);
        }, "Tiger cannot jump when friendly Rat blocks the river");
    }

    // ========== DEN TESTS ==========

    @Test
    @DisplayName("Test piece cannot enter its own den")
    public void testCannotEnterOwnDen() {
        // Player 0's den is at (0, 3)
        Position nearDen = new Position(0, 2);
        Position den = new Position(0, 3);

        Piece lion = new Lion(player0, nearDen);

        // Setup custom board with Lion next to its own den
        setupCustomBoard(
                new CustomPiece(lion, nearDen)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(nearDen, den);
        }, "Piece cannot enter its own den");
    }

    @Test
    @DisplayName("Test piece can enter opponent's den to win")
    public void testEnterOpponentDenToWin() {
        Position nearP1Den = new Position(8, 2);
        Position p1Den = new Position(8, 3); // Player 1's den

        Piece lion = new Lion(player0, nearP1Den);

        // Setup custom board with Lion next to opponent's den
        setupCustomBoard(
                new CustomPiece(lion, nearP1Den)
        );

        assertFalse(gameManager.isGameOver(), "Game should not be over yet");

        // Enter opponent's den
        gameManager.executeMove(nearP1Den, p1Den);

        assertTrue(gameManager.isGameOver(), "Game should be over");
        assertEquals(player0, gameManager.getWinner(), "Player 0 should win");
    }

    // ========== TURN-BASED TESTS ==========

    @Test
    @DisplayName("Test players alternate turns")
    public void testPlayersTakeTurns() {
        assertEquals(0, gameManager.getCurrentPlayerIndex(), "Player 0 should start");

        // Player 0 moves
        gameManager.executeMove(new Position(2, 0), new Position(3, 0));
        gameManager.confirmTurn();
        assertEquals(1, gameManager.getCurrentPlayerIndex(), "Should be Player 1's turn");

        // Player 1 moves
        gameManager.executeMove(new Position(6, 6), new Position(5, 6));
        gameManager.confirmTurn();
        assertEquals(0, gameManager.getCurrentPlayerIndex(), "Should be Player 0's turn again");
    }

    @Test
    @DisplayName("Test cannot move opponent's piece")
    public void testCannotMoveOpponentPiece() {
        // Player 0's turn, try to move Player 1's piece
        Position p1PiecePos = new Position(6, 6); // Player 1's Rat
        Position destination = new Position(5, 6);

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(p1PiecePos, destination);
        }, "Cannot move opponent's piece");
    }

    // ========== CAPTURE AND WIN CONDITION TESTS ==========

    @Test
    @DisplayName("Test capturing removes piece from opponent's collection")
    public void testCapturingRemovesPiece() {
        Position attackerPos = new Position(3, 3);
        Position victimPos = new Position(4, 3);

        Piece dog = new Dog(player0, attackerPos);
        Piece cat = new Cat(player1, victimPos);

        // Setup custom board with just these two pieces
        setupCustomBoard(
                new CustomPiece(dog, attackerPos),
                new CustomPiece(cat, victimPos)
        );

        assertEquals(1, player0.getPieces().size(), "Player 0 should have 1 piece");
        assertEquals(1, player1.getPieces().size(), "Player 1 should have 1 piece");

        // Execute capture
        gameManager.executeMove(attackerPos, victimPos);

        assertTrue(cat.isCaptured(), "Cat should be captured");
        assertEquals(0, player1.getPieces().size(), "Player 1 should have 0 pieces");
        assertEquals(dog, board.getPieceAt(victimPos), "Dog should be at victim position");
    }

    @Test
    @DisplayName("Test win by capturing all opponent pieces")
    public void testWinByCaptureAll() {
        Position lionPos = new Position(3, 3);
        Position catPos = new Position(4, 3);

        Piece lion = new Lion(player0, lionPos);
        Piece cat = new Cat(player1, catPos); // Player 1's last piece

        // Setup board where Player 1 has only one piece left
        setupCustomBoard(
                new CustomPiece(lion, lionPos),
                new CustomPiece(cat, catPos)
        );

        assertFalse(gameManager.isGameOver(), "Game should not be over yet");
        assertEquals(1, player1.getPieces().size(), "Player 1 should have 1 piece");

        // Capture last piece
        gameManager.executeMove(lionPos, catPos);

        assertTrue(gameManager.isGameOver(), "Game should be over");
        assertEquals(player0, gameManager.getWinner(), "Player 0 should win by capturing all pieces");
        assertEquals(0, player1.getPieces().size(), "Player 1 should have 0 pieces");
    }

    // ========== INVALID MOVE TESTS ==========

    @Test
    @DisplayName("Test cannot move to same position")
    public void testCannotMoveToSamePosition() {
        Position pos = new Position(0, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(pos, pos);
        }, "Cannot move to the same position");
    }

    @Test
    @DisplayName("Test cannot move from empty square")
    public void testCannotMoveFromEmptySquare() {
        Position emptyPos = new Position(4, 3);
        Position destination = new Position(4, 4);

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(emptyPos, destination);
        }, "Cannot move from empty square");
    }

    @Test
    @DisplayName("Test cannot capture own piece")
    public void testCannotCaptureOwnPiece() {
        Position pos1 = new Position(3, 3);
        Position pos2 = new Position(3, 4);

        Piece lion = new Lion(player0, pos1);
        Piece tiger = new Tiger(player0, pos2);

        // Setup custom board
        setupCustomBoard(
                new CustomPiece(lion, pos1),
                new CustomPiece(tiger, pos2)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(pos1, pos2);
        }, "Cannot capture own piece");
    }

    @Test
    @DisplayName("Test cannot move out of bounds")
    public void testCannotMoveOutOfBounds() {
        // Position constructor itself validates bounds and throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1, 0);
        }, "Cannot create position with negative row");

        assertThrows(IllegalArgumentException.class, () -> {
            new Position(0, -1);
        }, "Cannot create position with negative column");

        assertThrows(IllegalArgumentException.class, () -> {
            new Position(9, 0);
        }, "Cannot create position with row >= 9");

        assertThrows(IllegalArgumentException.class, () -> {
            new Position(0, 7);
        }, "Cannot create position with column >= 7");
    }

    // ========== UNDO TESTS ==========

    @Test
    @DisplayName("Test undo move restores board state")
    public void testUndoMove() {
        Position from = new Position(2, 0);
        Position to = new Position(3, 0);

        Piece rat = board.getPieceAt(from);

        gameManager.executeMove(from, to);
        assertEquals(rat, board.getPieceAt(to), "Rat should be at new position");

        gameManager.undoMove();
        assertEquals(rat, board.getPieceAt(from), "Rat should be back at original position");
        assertNull(board.getPieceAt(to), "New position should be empty");
    }

    @Test
    @DisplayName("Test undo move restores captured piece")
    public void testUndoRestoresCapturedPiece() {
        Position attackerPos = new Position(3, 3);
        Position victimPos = new Position(4, 3);

        Piece dog = new Dog(player0, attackerPos);
        Piece cat = new Cat(player1, victimPos);

        // Setup custom board
        setupCustomBoard(
                new CustomPiece(dog, attackerPos),
                new CustomPiece(cat, victimPos)
        );

        int p1PieceCount = player1.getPieces().size();

        // Execute capture
        gameManager.executeMove(attackerPos, victimPos);

        assertTrue(cat.isCaptured(), "Cat should be captured");
        assertEquals(p1PieceCount - 1, player1.getPieces().size());

        // Undo the capture
        gameManager.undoMove();

        assertFalse(cat.isCaptured(), "Cat should be restored");
        assertEquals(p1PieceCount, player1.getPieces().size());
        assertEquals(cat, board.getPieceAt(victimPos), "Cat should be back at original position");
        assertEquals(dog, board.getPieceAt(attackerPos), "Dog should be back at original position");
    }

    @Test
    @DisplayName("Test cannot undo when no moves made")
    public void testCannotUndoWithNoMoves() {
        assertThrows(IllegalStateException.class, () -> {
            gameManager.undoMove();
        }, "Cannot undo when no moves have been made");
    }


    // ========== PIECE RANK TESTS ==========

    @Test
    @DisplayName("Test all pieces have correct ranks")
    public void testPieceRanks() {
        Player p = new Player("Test", 0);

        assertEquals(8, new Elephant(p, new Position(0, 0)).getRank(), "Elephant rank should be 8");
        assertEquals(7, new Lion(p, new Position(0, 0)).getRank(), "Lion rank should be 7");
        assertEquals(6, new Tiger(p, new Position(0, 0)).getRank(), "Tiger rank should be 6");
        assertEquals(5, new Leopard(p, new Position(0, 0)).getRank(), "Leopard rank should be 5");
        assertEquals(4, new Wolf(p, new Position(0, 0)).getRank(), "Wolf rank should be 4");
        assertEquals(3, new Dog(p, new Position(0, 0)).getRank(), "Dog rank should be 3");
        assertEquals(2, new Cat(p, new Position(0, 0)).getRank(), "Cat rank should be 2");
        assertEquals(1, new Rat(p, new Position(0, 0)).getRank(), "Rat rank should be 1");
    }

    // ========== INITIAL BOARD SETUP TESTS ==========

    @Test
    @DisplayName("Test initial board setup is correct")
    public void testInitialBoardSetup() {
        // Check Player 0's pieces
        assertEquals("Lion", board.getPieceAt(new Position(0, 0)).getName());
        assertEquals("Tiger", board.getPieceAt(new Position(0, 6)).getName());
        assertEquals("Dog", board.getPieceAt(new Position(1, 1)).getName());
        assertEquals("Cat", board.getPieceAt(new Position(1, 5)).getName());
        assertEquals("Rat", board.getPieceAt(new Position(2, 0)).getName());
        assertEquals("Leopard", board.getPieceAt(new Position(2, 2)).getName());
        assertEquals("Wolf", board.getPieceAt(new Position(2, 4)).getName());
        assertEquals("Elephant", board.getPieceAt(new Position(2, 6)).getName());

        // Check Player 1's pieces
        assertEquals("Lion", board.getPieceAt(new Position(8, 6)).getName());
        assertEquals("Tiger", board.getPieceAt(new Position(8, 0)).getName());
        assertEquals("Dog", board.getPieceAt(new Position(7, 5)).getName());
        assertEquals("Cat", board.getPieceAt(new Position(7, 1)).getName());
        assertEquals("Rat", board.getPieceAt(new Position(6, 6)).getName());
        assertEquals("Leopard", board.getPieceAt(new Position(6, 4)).getName());
        assertEquals("Wolf", board.getPieceAt(new Position(6, 2)).getName());
        assertEquals("Elephant", board.getPieceAt(new Position(6, 0)).getName());
    }

    @Test
    @DisplayName("Test board dimensions are correct")
    public void testBoardDimensions() {
        assertEquals(9, board.getRows(), "Board should have 9 rows");
        assertEquals(7, board.getCols(), "Board should have 7 columns");
    }

    // ========== SPECIAL TERRAIN TESTS ==========

    @Test
    @DisplayName("Test water squares are at correct positions")
    public void testWaterSquarePositions() {
        // Water squares are at rows 3-5, columns 1-2 and 4-5
        assertTrue(board.isWater(new Position(3, 1)));
        assertTrue(board.isWater(new Position(3, 2)));
        assertTrue(board.isWater(new Position(4, 1)));
        assertTrue(board.isWater(new Position(4, 2)));
        assertTrue(board.isWater(new Position(5, 1)));
        assertTrue(board.isWater(new Position(5, 2)));
        assertTrue(board.isWater(new Position(3, 4)));
        assertTrue(board.isWater(new Position(3, 5)));
        assertTrue(board.isWater(new Position(4, 4)));
        assertTrue(board.isWater(new Position(4, 5)));
        assertTrue(board.isWater(new Position(5, 4)));
        assertTrue(board.isWater(new Position(5, 5)));

        assertFalse(board.isWater(new Position(3, 3)), "Middle column should not be water");
    }

    @Test
    @DisplayName("Test trap positions are correct")
    public void testTrapPositions() {
        // Player 0 traps: (0,2), (1,3), (2,3)
        assertTrue(board.isTrap(new Position(0, 2), 0));
        assertTrue(board.isTrap(new Position(1, 3), 0));
        assertTrue(board.isTrap(new Position(0, 4), 0));

        // Player 1 traps: (8,4), (7,3), (6,3)
        assertTrue(board.isTrap(new Position(8, 4), 1));
        assertTrue(board.isTrap(new Position(7, 3), 1));
        assertTrue(board.isTrap(new Position(8, 2), 1));
    }

    @Test
    @DisplayName("Test den positions are correct")
    public void testDenPositions() {
        assertTrue(board.isDen(new Position(0, 3), 0), "Player 0 den at (0,3)");
        assertTrue(board.isDen(new Position(8, 3), 1), "Player 1 den at (8,3)");
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Test non-water animals cannot move into water")
    public void testNonWaterAnimalCannotEnterWater() {
        Position lionPos = new Position(2, 1);
        Position waterPos = new Position(3, 1);

        Piece lion = new Lion(player0, lionPos);

        // Setup custom board with Lion next to water
        setupCustomBoard(
                new CustomPiece(lion, lionPos)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.executeMove(lionPos, waterPos);
        }, "Lion cannot enter water");
    }

    @Test
    @DisplayName("Test piece abilities are correctly assigned")
    public void testPieceAbilities() {
        Player p = new Player("Test", 0);

        // Only Rat can enter water
        assertTrue(new Rat(p, new Position(0, 0)).canEnterWater());
        assertFalse(new Lion(p, new Position(0, 0)).canEnterWater());
        assertFalse(new Tiger(p, new Position(0, 0)).canEnterWater());
        assertFalse(new Elephant(p, new Position(0, 0)).canEnterWater());

        // Only Lion and Tiger can jump water
        assertTrue(new Lion(p, new Position(0, 0)).canJumpWater());
        assertTrue(new Tiger(p, new Position(0, 0)).canJumpWater());
        assertFalse(new Rat(p, new Position(0, 0)).canJumpWater());
        assertFalse(new Elephant(p, new Position(0, 0)).canJumpWater());
    }

    @Test
    @DisplayName("Test game recorder tracks moves")
    public void testGameRecorderTracksMoves() {
        GameRecorder recorder = gameManager.getGameRecord();

        assertEquals(0, recorder.getMoveCount(), "Initially no moves");

        gameManager.executeMove(new Position(2, 0), new Position(3, 0));
        gameManager.confirmTurn();
        assertEquals(1, recorder.getMoveCount(), "Should have 1 move recorded");

        gameManager.executeMove(new Position(6, 6), new Position(5, 6));
        gameManager.confirmTurn();
        assertEquals(2, recorder.getMoveCount(), "Should have 2 moves recorded");
    }
}
