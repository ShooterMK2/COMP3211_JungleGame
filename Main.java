package Java.COMP3211_JungleGame;
import Java.COMP3211_JungleGame.controller.GameController;

public class Main {

    public static void main(String[] args) {
        try {
            GameController controller = new GameController();
            controller.run();

        } catch (Exception e) {
            System.err.println("\nFatal error occurred:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\nGoodbye!");
        System.exit(0);
    }
}

