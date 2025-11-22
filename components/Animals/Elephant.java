package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Elephant extends Piece {
    public Elephant(Player owner, Position position) {
        super(8, owner, position);
    }

    @Override
    public String getSymbol() {
        return "E";
    }

    @Override
    public String getName() {
        return "Elephant";
    }

    @Override
    public boolean canCapture(Piece target) {
        if (target instanceof Rat) {
            return false;
        }
        return super.canCapture(target);
    }
}

