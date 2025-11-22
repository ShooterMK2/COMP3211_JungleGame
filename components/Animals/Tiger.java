package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Tiger extends Piece {
    public Tiger(Player owner, Position position) {
        super(6, owner, position);
    }

    @Override
    public String getSymbol() {
        return "T";
    }

    @Override
    public String getName() {
        return "Tiger";
    }

    @Override
    public boolean canJumpWater() {
        return true;
    }
}
