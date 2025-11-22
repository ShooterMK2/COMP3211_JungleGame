package Java.COMP3211_JungleGame.controller;

import Java.COMP3211_JungleGame.components.*;
import Java.COMP3211_JungleGame.components.Animals.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String GAME_EXTENSION = ".jungle";
    private static final String RECORD_EXTENSION = ".record";
    private static final String DEFAULT_SAVE_DIR = "resources/saves/";
    private static final String DEFAULT_RECORD_DIR = "resources/records/";

    public FileManager() {
        new File(DEFAULT_SAVE_DIR).mkdirs();
        new File(DEFAULT_RECORD_DIR).mkdirs();
    }

    public boolean saveGame(String filename, GameManager gameManager) {
        if (!filename.endsWith(GAME_EXTENSION)) {
            filename += GAME_EXTENSION;
        }

        String filepath = DEFAULT_SAVE_DIR + filename;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("CURRENT_PLAYER:" + gameManager.getCurrentPlayerIndex());
            writer.newLine();

            writer.write("PLAYER0:" + gameManager.getPlayer(0).getName());
            writer.newLine();
            writer.write("PLAYER1:" + gameManager.getPlayer(1).getName());
            writer.newLine();

            writer.write("BOARD_START");
            writer.newLine();

            Board board = gameManager.getBoard();
            for (int row = 0; row < board.getRows(); row++) {
                for (int col = 0; col < board.getCols(); col++) {
                    Piece piece = board.getPieceAt(new Position(row, col));
                    if (piece == null) {
                        writer.write("--");
                    } else {
                        String pieceCode = "P" + piece.getOwner().getPlayerId() +
                                piece.getSymbol();
                        writer.write(pieceCode);
                    }
                    if (col < board.getCols() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            writer.write("BOARD_END");
            writer.newLine();

            writer.write("MOVE_COUNT:" + gameManager.getGameRecord().getMoveCount());
            writer.newLine();

            return true;

        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            return false;
        }
    }

    public GameManager loadGame(String filename) {
        if (!filename.endsWith(GAME_EXTENSION)) {
            filename += GAME_EXTENSION;
        }

        String filepath = DEFAULT_SAVE_DIR + filename;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;

            line = reader.readLine();
            int currentPlayerIndex = Integer.parseInt(line.split(":")[1]);

            line = reader.readLine();
            String player0Name = line.split(":")[1];

            line = reader.readLine();
            String player1Name = line.split(":")[1];

            Player player0 = new Player(player0Name, 0);
            Player player1 = new Player(player1Name, 1);

            GameManager gameManager = new GameManager(player0, player1);

            Board board = gameManager.getBoard();
            for (int row = 0; row < board.getRows(); row++) {
                for (int col = 0; col < board.getCols(); col++) {
                    board.removePieceAt(new Position(row, col));
                }
            }

            player0.getPieces().clear();
            player1.getPieces().clear();

            line = reader.readLine();

            for (int row = 0; row < board.getRows(); row++) {
                line = reader.readLine();
                String[] cells = line.split(",");

                for (int col = 0; col < board.getCols(); col++) {
                    String cellData = cells[col].trim();

                    if (!cellData.equals("--")) {
                        int ownerIndex = Character.getNumericValue(cellData.charAt(1));
                        char pieceSymbol = cellData.charAt(2);

                        Player owner = (ownerIndex == 0) ? player0 : player1;
                        Position pos = new Position(row, col);

                        Piece piece = createPieceFromSymbol(pieceSymbol, owner, pos);
                        board.setPieceAt(pos, piece);
                        owner.addPiece(piece);
                    }
                }
            }

            reader.readLine();

            line = reader.readLine();
            int moveCount = Integer.parseInt(line.split(":")[1]);

            gameManager.setCurrentPlayerIndex(currentPlayerIndex);

            return gameManager;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    public boolean saveRecord(String filename, GameRecorder gameRecorder) {
        if (!filename.endsWith(RECORD_EXTENSION)) {
            filename += RECORD_EXTENSION;
        }

        String filepath = DEFAULT_RECORD_DIR + filename;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            List<Motions> moves = gameRecorder.getAllMoves();

            writer.write("# Jungle Game Record");
            writer.newLine();
            writer.write("# Format: Turn,Player,From,To,Piece,Captured");
            writer.newLine();

            for (int i = 0; i < moves.size(); i++) {
                Motions move = moves.get(i);

                StringBuilder line = new StringBuilder();
                line.append(i + 1).append(",");
                line.append("P").append(move.getPlayerIndex()).append(",");
                line.append(positionToString(move.getFrom())).append(",");
                line.append(positionToString(move.getTo())).append(",");
                line.append(move.getMovedPiece().getSymbol()).append(",");

                if (move.isCapture()) {
                    line.append(move.getCapturedPiece().getSymbol());
                } else {
                    line.append("-");
                }

                writer.write(line.toString());
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.err.println("Error saving record: " + e.getMessage());
            return false;
        }
    }

    public List<String> loadRecord(String filename) {
        if (!filename.endsWith(RECORD_EXTENSION)) {
            filename += RECORD_EXTENSION;
        }

        String filepath = DEFAULT_RECORD_DIR + filename;
        List<String> moves = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    moves.add(line);
                }
            }

            return moves;

        } catch (IOException e) {
            System.err.println("Error loading record: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> listSavedGames() {
        File saveDir = new File(DEFAULT_SAVE_DIR);
        List<String> games = new ArrayList<>();

        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(GAME_EXTENSION));
        if (files != null) {
            for (File file : files) {
                games.add(file.getName());
            }
        }

        return games;
    }

    public List<String> listRecords() {
        File recordDir = new File(DEFAULT_RECORD_DIR);
        List<String> records = new ArrayList<>();

        File[] files = recordDir.listFiles((dir, name) -> name.endsWith(RECORD_EXTENSION));
        if (files != null) {
            for (File file : files) {
                records.add(file.getName());
            }
        }

        return records;
    }

    private Piece createPieceFromSymbol(char symbol, Player owner, Position pos) {
        switch (symbol) {
            case 'E': return new Elephant(owner, pos);
            case 'L': return new Lion(owner, pos);
            case 'T': return new Tiger(owner, pos);
            case 'P': return new Leopard(owner, pos);
            case 'W': return new Wolf(owner, pos);
            case 'D': return new Dog(owner, pos);
            case 'C': return new Cat(owner, pos);
            case 'R': return new Rat(owner, pos);
            default: throw new IllegalArgumentException("Unknown piece symbol: " + symbol);
        }
    }

    private String positionToString(Position pos) {
        char column = (char)('A' + pos.getColumn());
        int row = pos.getRow();
        return "" + column + row;
    }
}
