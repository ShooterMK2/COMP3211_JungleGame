package Java.COMP3211_JungleGame.components;

public abstract class Piece {
    protected final int rank;
    protected final Player owner;
    protected Position position;
    protected boolean captured;

    public Piece(int rank, Player owner, Position position) {
        this.rank = rank;
        this.owner = owner;
        this.position = position;
        this.captured = false;
    }

    public int getRank() {
        return rank;
    }

    public Player getOwner() {
        return owner;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public abstract String getSymbol();

    public abstract String getName();

    public boolean canCapture(Piece target) {
        if (target.getOwner().equals(this.owner)) {
            return false;
        }
        return this.rank >= target.rank;
    }

    public boolean canEnterWater() {
        return false;
    }

    public boolean canJumpWater() {
        return false;
    }

    @Override
    public String toString() {
        return owner.getName() + "'s " + getName() + " at " + position;
    }
}


