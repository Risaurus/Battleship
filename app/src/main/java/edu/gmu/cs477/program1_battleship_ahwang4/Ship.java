package edu.gmu.cs477.program1_battleship_ahwang4;

import java.util.Random;

public class Ship {

    private final int HORIZONTAL = 1;
    private final int ROWS = 8;
    private final int COLS = 8;

    int SIZE, direction, NUM;
    int row, col;

    public Ship(int board[][], int size, int num) {
        SIZE = size; NUM = num;
        Random rand = new Random();
        int r, c;
        direction = rand.nextInt(2);

        Boolean placed = false;
        while(!placed) {
            if (direction == HORIZONTAL) {
                r = rand.nextInt(ROWS);
                c = rand.nextInt(COLS - size);
            }
            else {
                r = rand.nextInt(ROWS - size);
                c = rand.nextInt(COLS);
            }
            placed = true;

            // check to make sure all locations for the ship are free
            if (direction == HORIZONTAL) {
                for (int i = 0; i < size; i++)
                    if (board[r][c+i] != 0)
                        placed = false;
            }
            else {
                for (int i = 0; i < size; i++)
                    if (board[r+i][c] != 0)
                        placed = false;
            }

            // if all is ok, place the ship here
            if (placed) {
                if (direction == HORIZONTAL) {
                    for (int i = 0; i < size; i++)
                        board[r][c+i] = num;
                }
                else {
                    for (int i = 0; i < size; i++)
                        board[r+i][c] = num;
                }
                setRowCol(r, c);
            }
        }
    }


    public int getShipSize() {
        return SIZE;
    }

    public int getShipDirection() {
        return direction;
    }

    public int getShipNum() {
        return NUM;
    }

    public void setRowCol(int r, int c) {
        row = r; col = c;
    }

    public int getShipRow() {
        return row;
    }

    public int getShipCol() {
        return col;
    }
}
