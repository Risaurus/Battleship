package edu.gmu.cs477.program1_battleship_ahwang4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class GameVSPlayer extends AppCompatActivity implements ClickAdapter.ItemClickListener {
    DatabaseReference myDB;
    DatabaseReference match;

    ArrayList<String> displayShips;
    ArrayList<String> displayAttacks;
    Battleship battleship;
    int winner = 0;

    TextView waitMSG;
    RecyclerView shipGrid, attackGrid;
    DisplayAdapter displayAdapter;
    ClickAdapter clickAdapter;

    String playerID; String oppID;
    int pos;
    int player; int opponent;
    boolean turnCheck = false;
    boolean turnLock = false;
    boolean initial = true;
    boolean playable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_vsplayer);
        Log.i(MainActivity.APPNAME,"Started new game against Player");

        battleship = new Battleship();
        displayShips = new ArrayList<>();
        battleship.getPlayerShipsAsArrayList(displayShips);
        displayAttacks = new ArrayList<>();
        for(int i = 0; i < (Battleship.SIZE*Battleship.SIZE); i++)
            displayAttacks.add("0");

        if (displayShips == null) {
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

        waitMSG = (TextView) findViewById(R.id.wait);

        myDB = FirebaseDatabase.getInstance().getReference("BattleShip");

        // Create playerID and put your board on database
        getPlayerID();

        // Get opponent info
        getOpponent();
    }

    @Override
    public void onItemClick(View view,int position) {
        if (turnCheck && turnLock && playable) {
            Log.i(MainActivity.APPNAME,"Player attacked position " + position);

            pos = position;

            // check if player has tried hitting that spot yet or not.
            if (displayAttacks.get(position).equals("0")) {
                turnLock = false;
                Turn turn = new Turn(""+player,""+position,"requesting");
                String key = match.child("moves").push().getKey();
                match.child("moves").child(key).setValue(turn);

                if (initial) {
                    setListeners();
                }
            }
            turnCheck = false;
        }
    }

    public void getPlayerID() {
        Log.i(MainActivity.APPNAME,"Generating playerID and pushing board.");

        playerID = myDB.child("Players").push().getKey();
        /*DatabaseReference idRef = myDB.child("Players").child(playerID);
        for(String position: displayShips) {
            if (position.equals("0"))
                idRef.child("board").child("0").setValue(true);
            else
                idRef.child("board").child("1").setValue(true);
        }*/

        System.out.println("finishing up getPlayerID");
    }

    public void getOpponent() {
        Log.i(MainActivity.APPNAME,"Search for opponent board");

        myDB.child("Matches").child("Available").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    boolean result = (boolean) task.getResult().getValue();
                    if (result) {
                        myDB.child("Matches").child("Game").child("Player2").setValue(playerID);
                        match = myDB.child("Matches").child("Game");
                        player = 2; opponent = 1;
                        playable = true;
                        waitMSG.setText(R.string.waitmsg);
                        Toast.makeText(getApplicationContext(),"Game Starting", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        turnCheck = true;
                        myDB.child("Matches").child("Game").child("Player1").setValue(playerID);
                        match = myDB.child("Matches").child("Game");
                        match.child("Player2").setValue("None");
                        myDB.child("Matches").child("Winner").setValue("0");
                        myDB.child("Matches").child("Available").setValue(true);
                        match.child("Player2").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String val = String.valueOf(snapshot.getValue());
                                if (!val.equals("None")) {
                                    playable = true;
                                    waitMSG.setText(R.string.waitmsg);
                                    Toast.makeText(getApplicationContext(),"Game Starting", Toast.LENGTH_SHORT).show();
                                    checkTurn();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                        player = 1; opponent = 2;
                    }
                    myDB.child("Matches").child("Available").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean result = (boolean) snapshot.getValue();
                            if (!result) {
                                endGame(3);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {System.out.println("Database error");}
                    });
                    myDB.child("Matches").child("Winner").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!initial) {
                                winner = Integer.parseInt(String.valueOf(snapshot.getValue()));
                                if (winner == 1 || winner == 2 || winner == 3)
                                    endGame(winner);
                                else if (winner != 0)
                                    System.out.println("Something went wrong with database values");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    checkTurn();
                }
            }
        });
        System.out.println("finishing up getOpponent");
    }

    public void checkTurn() {
        if (playable) {
            if (turnCheck) {
                System.out.println("Player " + player + "'s turn.");
                waitMSG.setVisibility(View.INVISIBLE);
                turnLock = true;
            }
            else {
                System.out.println("Not Player " + player + "'s turn.");
                waitMSG.setVisibility(View.VISIBLE);
                if (initial) {
                    setListeners();
                }
            }
        }
    }

    public void setListeners() {
        match.child("moves").child("dummy").setValue(new Turn("3","3","fake"));
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(MainActivity.APPNAME,"onChildAdded:" + snapshot.getKey());

                // new turn has been made, check if it was made by player or opponent
                Turn turn = snapshot.getValue(Turn.class);
                if (Integer.parseInt(turn.player) != player && (turn.result).equals("requesting")) {
                    pos = Integer.parseInt(turn.position);
                    int row = pos / Battleship.SIZE;
                    int col = pos % Battleship.SIZE;
                    int result = battleship.opponentShoot(row, col);

                    if (result == 0) {
                        displayShips.set(pos, "-2");
                        System.out.println("\tOpponent MISS\n");
                    } else {
                        displayShips.set(pos, "-1");
                        System.out.println("\tOpponent HIT\n");
                    }
                    displayAdapter.notifyItemChanged(pos);
                    match.child("moves").child(Objects.requireNonNull(snapshot.getKey())).child("result")
                            .setValue(String.valueOf(result));

                    turnCheck = true;
                    checkTurn();

                    System.out.println("\tupdated result for opponent");
                }
                System.out.println("finishing up onChildAdded");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(MainActivity.APPNAME,"onChildChanged" + snapshot.getKey());

                // turn has been updated
                // ensure result has been properly changed and reflect on player's board
                Turn turn = snapshot.getValue(Turn.class);
                assert turn != null;
                if (Integer.parseInt(turn.player) == player) {
                    String result = turn.result;
                    pos = Integer.parseInt(turn.position);

                    if (result.equals("0")) {
                        displayAttacks.set(pos, "2");
                        System.out.println("\n\tPlayer MISS");
                    } else {
                        displayAttacks.set(pos, "3");
                        System.out.println("\n\tPlayer HIT");
                    }

                    clickAdapter.notifyItemChanged(pos);

                    if (result.equals("2")) {
                        myDB.child("Matches").child("Winner").setValue(player);
                    }
                    else {
                        turnCheck = false;
                        checkTurn();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("!!! Error in childEventListener");
            }
        };
        match.child("moves").addChildEventListener(childEventListener);
        initial = false;
    }

    /** endGame(int winner)
     *
     * informs player if they lost or won then prompts to return to menu.
     *
     * @param winner
     *      1 if player won
     *      2 if opponent won
     *      3 if opponent left
     */
    private void endGame(int winner) {
        Log.i(MainActivity.APPNAME,"Game has concluded.");

        String result;
        if (winner == player) {
            System.out.println("You have won the game!");
            result = "You Win";
        }
        else if (winner == 3) {
            System.out.println("Opponent left the game.");
            result = "Opponent has left";
        }
        else {
            System.out.println("Opponent won the game.");
            result = "You Lost";
        }

        new AlertDialog.Builder(this)
                .setTitle(result)
                .setMessage("Click back to return to menu.")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("Finishing up endGame()\n\t returning to menu...");
                        gameDBCleanup();
                        finish();
                    }
                })
                .show();
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
                        System.out.println("finishing up onBackPressed()" +
                                "\n\treturning to last activity...");
                        Log.i(MainActivity.APPNAME,
                                "Implement trigger to let opponent know a player quit so they won.");
                        myDB.child("Matches").child("Winner").setValue("3");
                        gameDBCleanup();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();

        System.out.println("finishing up onBackPressed()\n\treturning to game.");
    }

    private void gameDBCleanup() {
        myDB.child("Players").child(playerID).removeValue();
        myDB.child("Matches").child("Available").setValue(false);
        match.removeValue();
    }


}