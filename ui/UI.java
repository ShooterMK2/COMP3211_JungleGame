package Java.COMP3211_JungleGame.ui;

import Java.COMP3211_JungleGame.components.*;

public class UI {
    // ANSI Color codes
    private static final String DEFAULT = "\033[0m";
    private static final String RED = "\033[31m";      // Player 0
    private static final String CYAN = "\033[36m";     // Player 1// Water
    private static final String BOLD = "\033[1m";

    private static final String WATER = " ≈≈ ";
    private static final String TRAP = " xx ";
    private static final String DEN = " ## ";

    public void displayWelcomeMessage() {
        System.out.println("===========================");
        System.out.println("WELCOME TO JUNGLE GAME!");
        System.out.println("===========================");
    }

    public String showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. New Game");
        System.out.println("2. Load Game");
        System.out.println("3. Replay Game Record");
        System.out.println("4. Exit");
        System.out.print("Enter choice (1-4): ");
        return requestInput("").trim();
    }

    public String requestPlayerName(int playerIndex) {
        System.out.print("Enter name for Player " + (playerIndex + 1) + ": ");
        return requestInput("").trim();
    }

    public void displayBoard(Board board, Player player0, Player player1) {
        System.out.println("\n=== Board State ===");

        // Column headers
        System.out.print("   ");
        for (char col = 'A'; col <= 'G'; col++) {
            System.out.print(" " + col + "  ");
        }
        System.out.println();

        // Top border
        System.out.print("  +");
        for (int i = 0; i < 7; i++) {
            System.out.print("----");
            if (i < 6) System.out.print("+");
        }
        System.out.println("+");

        // Board rows
        for (int row = 8; row >= 0; row--) {
            System.out.print(row + " |");
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                displayCell(board, pos, player0, player1);
                if (col < 6) System.out.print("|");
            }
            System.out.println("|");

            // Row separator
            if (row > 0) {
                System.out.print("  +");
                for (int i = 0; i < 7; i++) {
                    System.out.print("----");
                    if (i < 6) System.out.print("+");
                }
                System.out.println("+");
            }
        }

        // Bottom border
        System.out.print("  +");
        for (int i = 0; i < 7; i++) {
            System.out.print("----");
            if (i < 6) System.out.print("+");
        }
        System.out.println("+");

        // Column headers (bottom)
        System.out.print("   ");
        for (char col = 'A'; col <= 'G'; col++) {
            System.out.print(" " + col + "  ");
        }
        System.out.println("\n");

        displayLegend();
    }

    private void displayCell(Board board, Position pos, Player player0, Player player1) {
        Piece piece = board.getPieceAt(pos);
        String cellContent;
        String color = DEFAULT;

        // Determine terrain and piece
        if (board.isWater(pos)) {
            // Water with piece: ≈0R≈ or ≈1R≈
            if (piece != null) {
                int playerId = piece.getOwner().getPlayerId();
                color = (playerId == 0) ? RED : CYAN;
                cellContent = "≈" + playerId + piece.getSymbol() + "≈";
            } else {
                color = DEFAULT;
                cellContent = WATER;
            }
        } else if (board.isDen(pos, 0)) {
            // Player 0's den: #0R# or ####
            if (piece != null) {
                color = (piece.getOwner().getPlayerId() == 0) ? RED : CYAN;
                cellContent = "#" + piece.getOwner().getPlayerId() + piece.getSymbol() + "#";
            } else {
                color = RED;  // Player 0's den color
                cellContent = DEN;
            }
        } else if (board.isDen(pos, 1)) {
            // Player 1's den: #1R# or ####
            if (piece != null) {
                color = (piece.getOwner().getPlayerId() == 0) ? RED : CYAN;
                cellContent = "#" + piece.getOwner().getPlayerId() + piece.getSymbol() + "#";
            } else {
                color = CYAN;  // Player 1's den color
                cellContent = DEN;
            }
        } else if (board.isTrap(pos, 0)) {
            // Player 0's trap: x0Rx or xxxx
            if (piece != null) {
                color = (piece.getOwner().getPlayerId() == 0) ? RED : CYAN;
                cellContent = "x" + piece.getOwner().getPlayerId() + piece.getSymbol() + "x";
            } else {
                color = RED;  // Player 0's trap color
                cellContent = TRAP;
            }
        } else if (board.isTrap(pos, 1)) {
            // Player 1's trap: x1Rx or xxxx
            if (piece != null) {
                color = (piece.getOwner().getPlayerId() == 0) ? RED : CYAN;
                cellContent = "x" + piece.getOwner().getPlayerId() + piece.getSymbol() + "x";
            } else {
                color = CYAN;  // Player 1's trap color
                cellContent = TRAP;
            }
        } else {
            // Regular land
            if (piece != null) {
                int playerId = piece.getOwner().getPlayerId();
                color = (playerId == 0) ? RED : CYAN;
                cellContent = " " + playerId + piece.getSymbol() + " ";
            } else {
                cellContent = "    ";
            }
        }

        // Ensure exactly 4 characters
        if (cellContent.length() < 4) {
            cellContent = cellContent + " ".repeat(4 - cellContent.length());
        } else if (cellContent.length() > 4) {
            cellContent = cellContent.substring(0, 4);
        }

        System.out.print(color + cellContent + DEFAULT);
    }

    private void displayLegend() {
        System.out.println("Legend:");
        System.out.println(RED + "0X" + DEFAULT + " = Player 0's pieces (Red)   " +
                CYAN + "1X" + DEFAULT + " = Player 1's pieces (Cyan)");
        System.out.println(DEFAULT + "≈≈≈≈" + DEFAULT + " = Water   " +
                RED + "####" + DEFAULT + "/" + CYAN + "####" + DEFAULT + " = Dens");
        System.out.println(RED + "xxxx" + DEFAULT + "/" + CYAN + "xxxx" + DEFAULT + " = Traps");
        System.out.println("\nE=Elephant, L=Lion, T=Tiger, P=Leopard");
        System.out.println("W=Wolf, D=Dog, C=Cat, R=Rat");
        System.out.println();
    }

    public void displayGameStatus(String currentPlayer, int currentPlayerId, int moveCount,
                                  int player0UndoCount, int player1UndoCount) {
        System.out.println("====================");
        String playerColor = (currentPlayerId == 0) ? RED : CYAN;
        System.out.println("Current Turn: " + playerColor + BOLD + currentPlayer + DEFAULT);
        System.out.println("Move Count: " + moveCount);

        // Display only current player's undo count
        int remainingUndos = (currentPlayerId == 0) ?
                (3 - player0UndoCount) : (3 - player1UndoCount);
        System.out.println(playerColor + currentPlayer + "'s Remaining Undos: " +
                remainingUndos + "/3" + DEFAULT);
        System.out.println("====================");
    }

    public String getCommand() {
        System.out.print("\nEnter move (e.g., A0 A1) or command (help, save, undo, quit): ");
        return requestInput("").trim();
    }

    public void displayHelp() {
        System.out.println("\n=== JUNGLE GAME HELP ===");
        System.out.println("\n--- How to Play ---");
        System.out.println("• Move pieces using chess notation: A0 A1 (from A0 to A1)");
        System.out.println("• Capture opponent pieces by moving to their position");
        System.out.println("• Enter opponent's den to win!");
        System.out.println("\n--- Piece Ranks (higher captures lower) ---");
        System.out.println("8. Elephant - Strongest, but cannot capture Rat");
        System.out.println("7. Lion - Can jump over water horizontally/vertically");
        System.out.println("6. Tiger - Can jump over water horizontally/vertically");
        System.out.println("5. Leopard");
        System.out.println("4. Wolf");
        System.out.println("3. Dog");
        System.out.println("2. Cat");
        System.out.println("1. Rat - Can enter water, capture Elephant");
        System.out.println("\n--- Special Rules ---");
        System.out.println("• Only Rat can enter water squares (≈≈≈≈)");
        System.out.println("• Lion/Tiger can jump over water (blocked if Rat in water)");
        System.out.println("• Rat in water cannot capture Elephant");
        System.out.println("• Rat on land ↔ Rat in water cannot capture each other");
        System.out.println("• Pieces in opponent's trap (xxxx) lose all rank - any piece can capture");
        System.out.println("• Cannot enter own den (####)");
        System.out.println("\n--- Commands ---");
        System.out.println("help  - Show this help");
        System.out.println("save  - Save current game");
        System.out.println("undo  - Undo last move (max 3 per player per game)");
        System.out.println("quit  - Exit game");
        System.out.println();
    }

    public void displayWinner(Player winner) {
        System.out.println("\n================================");
        System.out.println("        GAME OVER!");
        System.out.println("================================");
        String winnerColor = (winner.getPlayerId() == 0) ? RED : CYAN;
        System.out.println(winnerColor + BOLD + "   " + winner.getName() + " WINS!" + DEFAULT);
        System.out.println("================================\n");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String error) {
        System.out.println(RED + "ERROR: " + error + DEFAULT);
    }

    public void displaySuccess(String message) {
        System.out.println(CYAN + "✓ " + message + DEFAULT);
    }

    public String requestInput(String prompt) {
        if (!prompt.isEmpty()) {
            System.out.print(prompt);
        }
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(System.in));
            return reader.readLine();
        } catch (java.io.IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            return "";
        }
    }
}
