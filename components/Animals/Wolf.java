package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Wolf extends Piece {
    public Wolf(Player owner, Position position) {
        super(4, owner, position);
    }

    @Override
    public String getSymbol() {
        return "W";
    }

    @Override
    public String getName() {
        return "Wolf";
    }
}
