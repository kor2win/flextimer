package flextimer.turnFlow;

public class Player {
    public final String name;
    public final int colorCode;
    private final int hashCode;

    public Player(String name, int colorCode) {
        this.name = name;
        this.colorCode = colorCode;
        hashCode = calculateHashCode();
    }

    public boolean equals(Player p) {
        return name.equals(p.name) && colorCode == p.colorCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player
                ? this.equals((Player) obj)
                : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return (name.hashCode() ^ 0xf0f0f0f0) | (colorCode ^ 0x0f0f0f0f);
    }
}
