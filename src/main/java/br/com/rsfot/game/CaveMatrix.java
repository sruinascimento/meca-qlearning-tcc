package br.com.rsfot.game;

public enum CaveMatrix {

    FIRST_CAVE(new String[][] {
            {"", "", "", "PIT"},
            {"PIT", "", "WUMPUS", ""},
            {"", "GOLD", "", ""},
            {"PIT", "", "", "PIT"}
    }),

    SECOND_CAVE(new String[][] {
            {"", "", "", ""},
            {"PIT", "PIT", "PIT", "WUMPUS"},
            {"", "", "", ""},
            {"GOLD", "", "", ""}
    });

    private final String[][] cave;

    CaveMatrix(String[][] cave) {
        this.cave = cave;
    }

    public String[][] getCave() {
        return cave;
    }
}