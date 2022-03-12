package edu.gmu.cs477.program1_battleship_ahwang4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String APPNAME="Program1-Battleship-ahwang4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * handles when @+id/buttonPlayComputer is clicked.
     * starts new activity that sets up Battleship game against AI
     */
    public void onPlayComputerClicked(View v) {
        Log.i(APPNAME,"Menu Play Computer Button clicked "+v.getId());

        System.out.println("starting activity GameVSComputer.java...");
        Intent intent = new Intent(this, GameVSComputer.class);
        startActivity(intent);

        System.out.println("finishing up onPlayComputerClicked()");
    }


    /**
     * handles when @+id/buttonPlayPlayer is clicked.
     * starts new activity that sets up Battleship game against
     *      another person on the same device.
     */
    public void onPlayPlayerClicked(View v) {
        Log.i(APPNAME,"Menu Play Player Button clicked "+v.getId());

        System.out.println("starting activity GameVSPlayer.java...");
        Intent intent = new Intent(this, GameVSPlayer.class);
        startActivity(intent);

        System.out.println("finishing up onPlayPlayerClicked()");
    }
}