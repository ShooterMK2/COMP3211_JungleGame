package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Cat extends Piece {
    public Cat(Player owner, Position position) {
        super(2, owner, position);
    }

    @Override
    public String getSymbol() {
        return "C";
    }

    @Override
    public String getName() {
        return "Cat";
    }
}
