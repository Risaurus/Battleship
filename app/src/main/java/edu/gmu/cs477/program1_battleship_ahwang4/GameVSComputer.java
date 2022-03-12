package edu.gmu.cs477.program1_battleship_ahwang4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.ArrayList;

public class GameVSComputer extends AppCompatActivity implements ClickAdapter.ItemClickListener{
    ArrayList<String> displayShips;
    ArrayList<String> displayAttacks;
    Battleship battleship;
    boolean gameEnd = false;

    RecyclerView shipGrid, attackGrid;
    DisplayAdapter displayAdapter;
    ClickAdapter clickAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_vscomputer);
        Log.i(MainActivity.APPNAME, "Started new game against Computer");

        battleship = new Battleship();
        displayShips = new ArrayList<>();
        battleship.getPlayerShipsAsArrayList(displayShips);
        displayAttacks = new ArrayList<>();
        battleship.getCompShipsAsArrayList(displayAttacks);

        if (displayShips == null || displayAttacks == null) {
            Log.e(MainActivity.APPNAME, "!!! issue occurred setting up Battleship game.");
        }

        shipGrid = (RecyclerView) findViewById(R.id.shipRV);
        shipGrid.setLayoutManager(new GridLayoutManager(this, Battleship.SIZE));
        shipGrid.setItemAnimator(null);
        displayAdapter = new DisplayAdapter(this, displayShips);
        shipGrid.setAdapter(displayAdapter);

        attackGrid = (RecyclerView) findViewById(R.id.attackRV);
        attackGrid.setLayoutManager(new GridLayoutManager(this, Battleship.SIZE));
        attackGrid.setItemAnimator(null);
        clickAdapter = new ClickAdapter(this, displayAttacks);
        clickAdapter.setClickListener(this);
        attackGrid.setAdapter(clickAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i(MainActivity.APPNAME,"Player attacked position " + position);

        // if there's a winner
        // winner = 1 IF player
        // winner = 2 IF computer
        int winner = 0;

        // handles the attackGrid first since player always goes first
        int row = position/Battleship.SIZE;
        int col = position%Battleship.SIZE;
        int result = battleship.shoot(row, col);
        if (result != 9) {
            if (result == 0) {
                displayAttacks.set(position, "2");
                System.out.println("\n\tPlayer MISS");
            } else {
                displayAttacks.set(position, "3");
                System.out.println("\n\tPlayer HIT");
            }

            clickAdapter.notifyItemChanged(position);

            if (result == 2) {
                gameEnd = true;
                winner = 1;
            }
        }

        // if player didn't win yet, handle computer turn
        if (!gameEnd) {
            int[] cturn = battleship.compShoot();

            Log.i(MainActivity.APPNAME,"Computer attacked position " + cturn[1]);

            if (cturn[0] == 0) {
                displayShips.set(cturn[1], "-2");
                System.out.println("\tComputer MISS\n");
            } else {
                displayShips.set(cturn[1], "-1");
                System.out.println("\tComputer HIT\n");
            }

            displayAdapter.notifyItemChanged(cturn[1]);

            if (cturn[0] == 2) {
                gameEnd = true;
                winner = 2;
            }
        }

        if (gameEnd) {
            endGame(winner);
        }

        System.out.println("finishing up onItemClick turn.");
    }


    /** endGame(int winner)
     *
     * asks player if they want to play a new game.
     *      if yes: calls method to generate new game
     *      if no: does nothing.
     *          *NOTE* with this setup, player must hit back button to leave screen.
     *
     * @param winner
     *      1 if player won
     *      2 if computer won
     */
    private void endGame(int winner) {
        Log.i(MainActivity.APPNAME, "Game has concluded.");

        if (winner == 1) {
            System.out.println("Player won game!");

            new AlertDialog.Builder(this)
                    .setTitle("You Win")
                    .setMessage("Would you like to start a new game?\nPress NO to return to menu.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("finishing up endGame()\n\tstarting new game...");
                            startGame();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("finishing up enGame()\n\treturning to menu.");
                            finish();
                        }
                    })
                    .show();
        }
        else {
            System.out.println("Computer won game.");

            new AlertDialog.Builder(this)
                    .setTitle("You Lost")
                    .setMessage("Would you like to start a new game?\nPress NO to return to menu.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("finishing up endGame()\n\tstarting new game...");
                            startGame();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("finishing up enGame()\n\treturning to menu.");
                            finish();
                        }
                    })
                    .show();
        }
    }

    /** startGame()
     *
     *  Starts new game against Computer
     */
    private void startGame() {
        Log.i(MainActivity.APPNAME, "Started new game against Computer");

        gameEnd = false;
        battleship.newGame();
        battleship.getPlayerShipsAsArrayList(displayShips);
        battleship.getCompShipsAsArrayList(displayAttacks);

        if (displayShips == null || displayAttacks == null) {
            Log.e(MainActivity.APPNAME, "!!! issue occurred setting up Battleship game.");
        }

        displayAdapter.notifyDataSetChanged();
        clickAdapter.notifyDataSetChanged();

        System.out.println("finishing up startGame()");
    }


    /**
     * onBackPressed()
     *  ask user to confirm they want to leave the game.
     *  if they do, go back to menu screen.
     */
    @Override
    public void onBackPressed() {
        Log.i(MainActivity.APPNAME,"Back Button Pressed");

        // confirm if user wants to leave the game since data will not be saved.
        new AlertDialog.Builder(this)
                .setTitle("Leaving Game")
                .setMessage("Are you sure you want to leave the game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("finishing up onBackPressed()\n\treturning to last activity...");
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();

        System.out.println("finishing up onBackPressed()\n\treturning to game.");
    }
}