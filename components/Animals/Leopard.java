package Java.COMP3211_JungleGame.components.Animals;

import Java.COMP3211_JungleGame.components.Piece;
import Java.COMP3211_JungleGame.components.Player;
import Java.COMP3211_JungleGame.components.Position;

public class Leopard extends Piece {
    public Leopard(Player owner, Position position) {
        super(5, owner, position);
    }

    @Override
    public String getSymbol() {
        return "P";
    }

    @Override
    public String getName() {
        return "Leopard";
    }
}
