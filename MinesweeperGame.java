package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {                                 // set side
            for (int x = 0; x < SIDE; x++) {                             // playing field
                boolean isMine = getRandomNumber(10) < 1;           // max count MINE
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ANTIQUEWHITE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }


    private void countMineNeighbors() {                                   //method add mine neighbors for count
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine)
                    for (GameObject a : getNeighbors(gameField[y][x])) {
                        if (a.isMine)
                            gameField[y][x].countMineNeighbors++;
                    }
            }
        }
    }


    @Override
    public void onMouseLeftClick(int x, int y) {                           //extended method which clicked mouse
            super.onMouseLeftClick(x, y);
        if(isGameStopped)
            restart();
        else
            openTile(x, y);

        }

    private void openTile(int x, int y) {                                  //method which opening cell
        GameObject gameObject = gameField[y][x];

        if (!gameObject.isOpen && !gameObject.isFlag && !isGameStopped) {
            gameObject.isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.AQUAMARINE);

            if (countClosedTiles > countMinesOnField) {                           //works until the number of closed cells is more than the number of mines
                if (gameObject.isMine) {                                          //method changed: when opening a cell with a mine, the method is called gameOver();
                    setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
                    gameOver();
                    countClosedTiles++;

                } else if (gameObject.countMineNeighbors == 0) {                    // if there are no mines nearby, recursion is called
                    setCellValue(gameObject.x, gameObject.y, "");
                    List<GameObject> neighbors = getNeighbors(gameObject);
                    for (GameObject neighbor : neighbors) {
                        if (!neighbor.isOpen) {
                            openTile(neighbor.x, neighbor.y);
                            score += 5;
                        }
                    }
                } else {
                    setCellNumber(gameObject.x, gameObject.y, gameObject.countMineNeighbors);   //shows the number of mines in the adjacent cell
                    score += 5;
                }
                setScore(score);
            } else if (countClosedTiles == countMinesOnField && !gameObject.isMine)             //if the number of mines is comparable to the number of closed cells,
                win();                                                                          // the game is stopped and the winner is declared
        }
    }


    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    private void markTile(int x, int y) {
        GameObject gameObject = gameField[y][x];

        if (!isGameStopped) {
            if (countFlags != 0 && !gameObject.isOpen) {
                if (!gameObject.isFlag) {
                    setCellValue(gameObject.x, gameObject.y, FLAG);
                    setCellColor(gameObject.x, gameObject.y, Color.RED);
                    gameObject.isFlag = true;
                    countFlags--;
                } else {
                    setCellValue(gameObject.x, gameObject.y, "");
                    gameObject.isFlag = false;
                    setCellColor(gameObject.x, gameObject.y, Color.ANTIQUEWHITE);
                    countFlags++;
                }
            }
        }
    }

    private void gameOver(){                                    //added a new method that ends the game and sets the flag "isGameStopped" to true
        isGameStopped = true;
        showMessageDialog(Color.RED, "Game failed!", Color.BROWN, 100);
    }

    private void win(){                                         // method that stops the game if you win
        isGameStopped = true;
        showMessageDialog(Color.GREEN, "congratulations".toUpperCase(Locale.ROOT), Color.WHITE, 40);
    }

    private void restart(){                                     //added restart method and conditions for its execution
        isGameStopped = false;
        countFlags = 0;
        score = 0;
        countMinesOnField = 0;
        countClosedTiles = SIDE * SIDE;
        setScore(score);
        createGame();

    }

    private List<GameObject> getNeighbors (GameObject gameObject) {        //add neighbors cell on field
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}