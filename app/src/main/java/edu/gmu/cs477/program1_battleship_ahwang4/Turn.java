package edu.gmu.cs477.program1_battleship_ahwang4;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Turn {
    public String player;
    public String position;
    public String result;

    public Turn() {
        // default constructor
    }

    public Turn(String player, String position, String result) {
        this.player = player;
        this.position = position;
        this.result = result;
    }
}
