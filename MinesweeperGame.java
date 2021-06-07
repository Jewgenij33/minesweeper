package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;
import com.sun.tools.classfile.ConstantPool;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private static final String NOTFING = "\u0000";
    private int countFlags;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ANTIQUEWHITE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }


    private void countMineNeighbors() {
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

    private void openTile(int x, int y){

        boolean isOpen;
        int mines = 0;

        GameObject gameObject = gameField[y][x];

        if(gameObject.isMine) {
            setCellValue(gameObject.x, gameObject.y, MINE);

        }else {
            for (GameObject na : getNeighbors(gameObject)) {
                if (gameObject.countMineNeighbors == 0) {
                    if (!na.isOpen) {
                        gameObject.isOpen = true;
                        setCellValue(x, y, "\u0FD7");
                        openTile(x, y);
                    }
                } else {
                    setCellNumber(x, y, gameObject.countMineNeighbors);
                }
            }
        }

        setCellColor(x, y, Color.AZURE);
        gameObject.isOpen = true;

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        openTile(x, y);
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
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