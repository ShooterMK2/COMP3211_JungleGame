package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Lion extends Piece {
    public Lion(Player owner, Position position) {
        super(7, owner, position);
    }

    @Override
    public String getSymbol() {
        return "L";
    }

    @Override
    public String getName() {
        return "Lion";
    }

    @Override
    public boolean canJumpWater() {
        return true;
    }
}
