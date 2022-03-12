package edu.gmu.cs477.program1_battleship_ahwang4;


import java.util.ArrayList;
import java.util.Random;

public class Battleship {
    public static final int SIZE = 8;
    private int[][] playerShips, compShips;
    private ArrayList<Integer> availableCells;
    private int php, chp;
    private Ship pship1, pship2, pship3, pship4, pship5;
    private Ship cship1, cship2, cship3, cship4, cship5;
    private boolean fresh = true;

    public Battleship() {
        newGame();
    }

    /**
     *
     * @param row
     * @param col
     * @return
     *  1 if HIT
     *  0 if MISS
     *  2 if PLAYER WON
     *  9 if nothing happens
     */
    public int shoot(int row, int col) {
        fresh = false;

        if (compShips[row][col] == 11) {
            // already hit this place
            return 9;
        }
        else if (compShips[row][col] == 1) {
            // ship is hit
            chp--;
            if (chp == 0)
                return 2;
            compShips[row][col] = 11;
            return 1;
        }

        compShips[row][col] = 11;
        return 0;
    }

    /**
     *
     * @return
     *  ret[0] =
         *  1 if HIT
         *  0 if MISS
         *  2 if PLAYER LOST
     *  ret[1] = position
     */
    public int[] compShoot() {
        fresh = false;

        int[] ret = new int[2];

        Random rand = new Random();
        int shoot = rand.nextInt(availableCells.size()-1);
        ret[1] = availableCells.get(shoot);
        //System.out.println("hitting position " + ret[1]);
        availableCells.remove(shoot);

        int cellR = ret[1] / SIZE;
        int cellC = ret[1] % SIZE;

        //System.out.println("spots in playerShips = " + playerShips[cellR][cellC]);
        if(playerShips[cellR][cellC] >= 1 && playerShips[cellR][cellC] <= 5) {
            ret[0] = 1;
            php--;
            if (php == 0)
                ret[0] = 2;
        }

        return ret;
    }

    public int opponentShoot(int row, int col) {
        fresh = false;

        int ret = 0;
        if (playerShips[row][col] >= 1 && playerShips[row][col] <= 5) {
            ret = 1;
            php--;
            if (php == 0)
                ret = 2;
        }
        return ret;
    }

    public void newGame() {
        playerShips = new int[SIZE][SIZE];
        compShips = new int[SIZE][SIZE];
        php = 17; chp = 17;

        // generate board for player ships
        playerShips = new int[8][8];
        pship1 = new Ship(playerShips, 5, 1);
        pship2 = new Ship(playerShips, 4, 2);
        pship3 = new Ship(playerShips, 3, 3);
        pship4 = new Ship(playerShips, 3, 4);
        pship5 = new Ship(playerShips, 2, 5);

        // generate board for computer ships
        compShips = new int[8][8];
        cship1 = new Ship(compShips, 5, 1);
        cship2 = new Ship(compShips, 4, 1);
        cship3 = new Ship(compShips, 3, 1);
        cship4 = new Ship(compShips, 3, 1);
        cship5 = new Ship(compShips, 2, 1);

        availableCells = new ArrayList<>();
        for(int i = 0; i < (SIZE * SIZE); i++) {
            availableCells.add(i);
        }

        fresh=true;
    }

    public void getPlayerShipsAsArrayList(ArrayList<String> board) {
        if (fresh) {
            board.clear();

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    int val = playerShips[row][col];
                    board.add(Integer.toString(val));
                }
            }
        }
    }

    public void getCompShipsAsArrayList(ArrayList<String> board) {
        if (fresh) {
            board.clear();

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    int val = compShips[row][col];
                    board.add(Integer.toString(val));
                }
            }
        }
    }
}
