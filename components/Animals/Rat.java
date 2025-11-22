package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Rat extends Piece {
    public Rat(Player owner, Position position) {
        super(1, owner, position);
    }

    @Override
    public String getSymbol() {
        return "R";
    }

    @Override
    public String getName() {
        return "Rat";
    }

    @Override
    public boolean canCapture(Piece target) {
        if (target instanceof Elephant) {
            return true;
        }
        return super.canCapture(target);
    }

    @Override
    public boolean canEnterWater() {
        return true;
    }
}
