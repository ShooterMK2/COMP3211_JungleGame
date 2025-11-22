package Java.COMP3211_JungleGame.controller;

import Java.COMP3211_JungleGame.components.*;
import Java.COMP3211_JungleGame.ui.UI;

public class GameController {
    private GameManager gameManager;
    private final UI ui;
    private final FileManager fileManager;
    private boolean gameRunning;

    public GameController() {
        this.ui = new UI();
        this.fileManager = new FileManager();
        this.gameRunning = false;
    }

    public void run() {
        ui.displayWelcomeMessage();
        boolean exitProgram = false;
        while (!exitProgram) {
            String menuChoice = ui.showMainMenu();
            switch (menuChoice.toLowerCase()) {
                case "1":
                case "new":
                    startNewGame();
                    break;
                case "2":
                case "load":
                    loadGame();
                    break;
                case "3":
                case "replay":
                    replayGame();
                    break;
                case "4":
                case "exit":
                    exitProgram = true;
                    ui.displayMessage("Thanks for playing Jungle!");
                    break;
                default:
                    ui.displayError("Invalid choice. Please try again.");
            }
        }
    }

    private void startNewGame() {
        String player0Name = ui.requestPlayerName(0);
        String player1Name = ui.requestPlayerName(1);
        Player player0 = new Player(player0Name, 0);
        Player player1 = new Player(player1Name, 1);
        gameManager = new GameManager(player0, player1);
        ui.displayMessage("\nGame started! " + player0Name + " vs " + player1Name);
        playGame();
    }

    private void loadGame() {
        java.util.List<String> savedGames = fileManager.listSavedGames();
        if (savedGames.isEmpty()) {
            ui.displayError("No saved games found.");
            return;
        }
        ui.displayMessage("\nSaved games:");
        for (int i = 0; i < savedGames.size(); i++) {
            ui.displayMessage((i + 1) + ". " + savedGames.get(i));
        }
        String filename = ui.requestInput("Enter filename to load (or 'cancel'): ");
        if (filename.equalsIgnoreCase("cancel")) {
            return;
        }
        gameManager = fileManager.loadGame(filename);
        if (gameManager != null) {
            ui.displayMessage("Game loaded successfully!");
            playGame();
        } else {
            ui.displayError("Failed to load game.");
        }
    }

    private void replayGame() {
        java.util.List<String> records = fileManager.listRecords();
        if (records.isEmpty()) {
            ui.displayError("No game records found.");
            return;
        }
        ui.displayMessage("\nAvailable records:");
        for (int i = 0; i < records.size(); i++) {
            ui.displayMessage((i + 1) + ". " + records.get(i));
        }
        String filename = ui.requestInput("Enter filename to replay (or 'cancel'): ");
        if (filename.equalsIgnoreCase("cancel")) {
            return;
        }
        java.util.List<String> moves = fileManager.loadRecord(filename);
        if (moves.isEmpty()) {
            ui.displayError("Failed to load record or record is empty.");
            return;
        }
        ui.displayMessage("\nReplaying game...");
        ui.displayMessage("Press Enter after each move to continue.");
        for (String moveData : moves) {
            ui.displayMessage("\n" + moveData);
            ui.requestInput("");
        }
        ui.displayMessage("\nReplay complete!");
    }

    private void playGame() {
        gameRunning = true;

        while (gameRunning && !gameManager.isGameOver()) {
            // Display board at START of each turn only
            ui.displayBoard(gameManager.getBoard(), gameManager.getPlayer(0), gameManager.getPlayer(1));
            ui.displayGameStatus(
                    gameManager.getCurrentPlayer().getName(),
                    gameManager.getGameRecord().getMoveCount(),
                    gameManager.getGameRecord().getRemainingUndos()
            );

            // Keep asking for valid command until turn ends
            boolean turnEnded = false;
            while (!turnEnded && gameRunning && !gameManager.isGameOver()) {
                String command = ui.getCommand();
                turnEnded = processCommand(command);
            }
        }

        if (gameManager.isGameOver()) {
            ui.displayBoard(gameManager.getBoard(), gameManager.getPlayer(0), gameManager.getPlayer(1));
            ui.displayWinner(gameManager.getWinner());
            String saveRecord = ui.requestInput("Save game record? (yes/no): ");
            if (saveRecord.equalsIgnoreCase("yes") || saveRecord.equalsIgnoreCase("y")) {
                String filename = ui.requestInput("Enter filename: ");
                if (fileManager.saveRecord(filename, gameManager.getGameRecord())) {
                    ui.displayMessage("Record saved successfully!");
                } else {
                    ui.displayError("Failed to save record.");
                }
            }
        }

        gameRunning = false;
    }

    /**
     * Processes user commands.
     * Returns true if turn should end (move confirmed), false otherwise.
     */
    private boolean processCommand(String command) {
        command = command.trim().toLowerCase();

        switch (command) {
            case "help" -> {
                ui.displayHelp();
                return false;
            }
            case "save" -> {
                saveCurrentGame();
                return false;
            }
            case "quit", "exit" -> {
                String confirm = ui.requestInput("Are you sure you want to quit? (yes/no): ");
                if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                    gameRunning = false;
                }
                return false;
            }
            default -> {
                try {
                    String cleanCommand = command.replace("move", "").replace("to", "").trim();
                    String[] parts = cleanCommand.split("\\s+");
                    if (parts.length >= 2) {
                        Position from = parsePosition(parts[0]);
                        Position to = parsePosition(parts[1]);
                        return executeMove(from, to);
                    } else {
                        ui.displayError("Invalid move format. Use: A0 D3 (e.g., 'A2 A3')");
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    ui.displayError(e.getMessage());
                    return false;
                } catch (Exception e) {
                    ui.displayError("Error: " + e.getMessage());
                    return false;
                }
            }
        }
    }

    /**
     * Executes a move and handles confirmation/undo.
     * Returns true if turn ends (confirmed), false if undone.
     */
    private boolean executeMove(Position from, Position to) {
        try {
            gameManager.executeMove(from, to);
            ui.displaySuccess("Move executed successfully!");

            GameRecorder recorder = gameManager.getGameRecord();
            if (recorder.getMoveCount() > 0) {
                Motions lastMove = recorder.peekLastMove();
                if (lastMove != null && lastMove.isCapture()) {
                    ui.displayMessage("Captured " + lastMove.getCapturedPiece().getName() + "!");
                }
            }

            // Show board after move execution
            ui.displayBoard(gameManager.getBoard(), gameManager.getPlayer(0), gameManager.getPlayer(1));

            // Ask for confirmation/undo
            return confirmMoveOrUndo();

        } catch (IllegalArgumentException e) {
            // Invalid move - just show error, no board print
            ui.displayError(e.getMessage());
            return false;
        }
    }

    /**
     * Asks player to confirm or undo the move.
     * Returns true if confirmed (end turn), false if undone (stay in turn).
     */
    private boolean confirmMoveOrUndo() {
        while (true) {
            int remaining = gameManager.getGameRecord().getRemainingUndos();
            String prompt = String.format("Need Undo? (y/n - %d remaining for entire game): ", remaining);
            String response = ui.requestInput(prompt).trim().toLowerCase();

            if (response.equals("n") || response.equals("no") || response.isEmpty()) {
                // Confirmed - turn ends
                return true;
            } else if (response.equals("y") || response.equals("yes")) {
                try {
                    gameManager.undoMove();
                    ui.displaySuccess("Move undone! Try again. (" +
                            gameManager.getGameRecord().getRemainingUndos() + " undos remaining for entire game)");
                    ui.displayBoard(gameManager.getBoard(), gameManager.getPlayer(0), gameManager.getPlayer(1));
                    return false;
                } catch (IllegalStateException e) {
                    ui.displayError(e.getMessage());
                    ui.displayMessage("Move confirmed automatically.");
                    // No undos left - turn ends
                    return true;
                }
            } else {
                ui.displayError("Invalid input. Type 'y' for undo or 'n' to confirm.");
                // Invalid input - stay in confirmation loop
            }
        }
    }

    private void saveCurrentGame() {
        String filename = ui.requestInput("Enter filename to save: ");
        if (fileManager.saveGame(filename, gameManager)) {
            ui.displayMessage("Game saved successfully!");
        } else {
            ui.displayError("Failed to save game.");
        }
    }

    private Position parsePosition(String posStr) {
        posStr = posStr.trim().toUpperCase();
        if (posStr.length() < 2) {
            throw new IllegalArgumentException("Position must be in format: A0-G8");
        }
        char columnChar = posStr.charAt(0);
        int column = columnChar - 'A';
        if (column < 0 || column > 6) {
            throw new IllegalArgumentException("Column must be A-G, got: " + columnChar);
        }
        String rowStr = posStr.substring(1);
        int row;
        try {
            row = Integer.parseInt(rowStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Row must be a number 0-8, got: " + rowStr);
        }
        if (row < 0 || row > 8) {
            throw new IllegalArgumentException("Row must be 0-8, got: " + row);
        }
        return new Position(row, column);
    }
}
