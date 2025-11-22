package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Dog extends Piece {
    public Dog(Player owner, Position position) {
        super(3, owner, position);
    }

    @Override
    public String getSymbol() {
        return "D";
    }

    @Override
    public String getName() {
        return "Dog";
    }
}
