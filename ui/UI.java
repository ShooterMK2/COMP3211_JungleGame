package Java.COMP3211_JungleGame.ui;

import Java.COMP3211_JungleGame.components.*;
import Java.COMP3211_JungleGame.components.Animals.*;

public class UI {

    private static final String WATER = "≈≈";
    private static final String TRAP = "XX";
    private static final String DEN = "##";

    public void displayWelcomeMessage() {
        System.out.println("WELCOME TO JUNGLE GAME!\n");
    }

    public String showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. New Game");
        System.out.println("2. Load Game");
        System.out.println("3. Replay Game Record");
        System.out.println("4. Exit");
        System.out.print("\nEnter choice (1-4): ");

        return requestInput("").trim();
    }

    public String requestPlayerName(int playerIndex) {
        System.out.print("Enter name for Player " + (playerIndex + 1)+ ": ");
        return requestInput("").trim();
    }

    public void displayBoard(Board board, Player player0, Player player1) {
        System.out.println("\nCurrent Board State:\n");

        System.out.print("    ");
        for (char col = 'A'; col <= 'G'; col++) {
            System.out.print("  " + col + "  ");
        }
        System.out.println(" ");

        System.out.print("   ╔");
        for (int i = 0; i < 7; i++) {
            System.out.print("════");
            if (i < 6) System.out.print("╦");
        }
        System.out.println("╗");

        for (int row = 8; row >= 0; row--) {
            System.out.print(" " + row + " ║");

            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                displayCell(board, pos, player0, player1);

                if (col < 6) System.out.print("║");
            }
            System.out.println("║");

            if (row > 0) {
                System.out.print("   ╠");
                for (int i = 0; i < 7; i++) {
                    System.out.print("════");
                    if (i < 6) System.out.print("╬");
                }
                System.out.println("╣");
            }
        }

        System.out.print("   ╚");
        for (int i = 0; i < 7; i++) {
            System.out.print("════");
            if (i < 6) System.out.print("╩");
        }
        System.out.println("╝");

        System.out.print("    ");
        for (char col = 'A'; col <= 'G'; col++) {
            System.out.print("  " + col + "  ");
        }
        System.out.println(" \n");

        displayLegend();
    }

    private void displayCell(Board board, Position pos, Player player0, Player player1) {
        Piece piece = board.getPieceAt(pos);
        String cellContent;

        if (board.isWater(pos)) {
            cellContent = " "+WATER;
        } else if (board.isDen(pos, 0) || board.isDen(pos, 1)) {
            cellContent = " "+DEN;
        } else if (board.isTrap(pos, 0) || board.isTrap(pos, 1)) {
            cellContent = " "+TRAP;
        } else if (piece != null) {
            cellContent = piece.getOwner().getPlayerId() + piece.getSymbol() + "  ";
        } else {
            cellContent = "    ";
        }

        if (cellContent.length() < 4) {
            cellContent = cellContent + " ".repeat(4 - cellContent.length());
        } else if (cellContent.length() > 4) {
            cellContent = cellContent.substring(0, 4);
        }

        System.out.print(cellContent);
    }

    private void displayLegend() {
        System.out.println("Legend:");
        System.out.println("0X = Player 0's pieces  1X = Player 1's pieces");
        System.out.println("≈≈ = Water             ## = Dens");
        System.out.println("XX = Traps");
        System.out.println("\nPieces: E=Elephant, L=Lion, T=Tiger, P=Leopard");
        System.out.println("        W=Wolf, D=Dog, C=Cat, R=Rat");
    }

    public void displayGameStatus(String currentPlayer, int moveCount, int remainingUndos) {
        System.out.println("═══════════════════════════════════");
        System.out.println("Current Turn: " + currentPlayer);
        System.out.println("Move Count: " + moveCount);
        System.out.println("Remaining Undos: " + remainingUndos + "/3");
        System.out.println("═══════════════════════════════════");
    }

    public String getCommand() {
        System.out.print("\nEnter move (e.g., 'A0 A1') or command ('help', 'save', 'quit'): ");
        return requestInput("").trim();
    }

    public void displayHelp() {
        System.out.println("\n=== JUNGLE GAME HELP ===");
        System.out.println("\nHow to Play:");
        System.out.println("• Move pieces using chess notation: 'A0 A1' (from A0 to A1)");
        System.out.println("• Capture opponent pieces by moving to their position");
        System.out.println("• Enter opponent's den to win!");

        System.out.println("\nPiece Ranks (higher captures lower):");
        System.out.println("  8. Elephant - Strongest, but cannot capture Rat");
        System.out.println("  7. Lion - Can jump over water horizontally/vertically");
        System.out.println("  6. Tiger - Can jump over water horizontally/vertically");
        System.out.println("  5. Leopard");
        System.out.println("  4. Wolf");
        System.out.println("  3. Dog");
        System.out.println("  2. Cat");
        System.out.println("  1. Rat - Can enter water, capture Elephant");

        System.out.println("\nSpecial Rules:");
        System.out.println("• Only Rat can enter water squares");
        System.out.println("• Lion/Tiger can jump over water (blocked if Rat in water)");
        System.out.println("• Rat in water cannot capture Elephant");
        System.out.println("• Rat on land ≠ Rat in water (cannot capture each other)");
        System.out.println("• Pieces in opponent's trap lose all rank (any piece can capture)");
        System.out.println("• Cannot enter own den");

        System.out.println("\nCommands:");
        System.out.println("  help  - Show this help");
        System.out.println("  save  - Save current game");
        System.out.println("  undo  - Undo last move (max 3 per turn)");
        System.out.println("  quit  - Exit game");
        System.out.println();
    }

    public void displayWinner(Player winner) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║          GAME OVER!                ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\n*** " + winner.getName() + " WINS! ***\n");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String error) {
        System.out.println("ERROR: " + error);
    }

    public void displaySuccess(String message) {
        System.out.println("OK: " + message);
    }

    public String requestInput(String prompt) {
        if (!prompt.isEmpty()) {
            System.out.print(prompt);
        }

        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(System.in)
            );
            return reader.readLine();
        } catch (java.io.IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            return "";
        }
    }

    public void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}

