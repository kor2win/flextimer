package flextimer.player;

public class Player {
    public final String name;
    public final int colorCode;

    public Player(String name, int colorCode) {
        this.name = name;
        this.colorCode = colorCode;
    }

    public boolean equals(Player p) {
        return name.equals(p.name) && colorCode == p.colorCode;
    }
}
