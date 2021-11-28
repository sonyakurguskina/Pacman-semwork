package ru.itis.kurguskina;

import javafx.geometry.Point2D;

import javafx.fxml.FXML;
import java.io.*;

import java.util.*;

public class GameLogic {
    public enum CellValue {
        EMPTY, SMALLDOT, BIGDOT, WALL, GHOST1HOME, GHOST2HOME, PACMANHOME
    }
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }
    @FXML private int rowsCount;
    @FXML private int columnsCount;
    private CellValue[][] grid;
    private int scoreCount;
    private int levelNumber;
    private int dotsCount;
    private static boolean isGameOver;
    private static boolean isWon;
    private static boolean isWeakGhostMode;
    private Point2D pacmanCoordinates;
    private Point2D pacmanSpeed;
    private Point2D ghost1Coordinates;
    private Point2D ghost1Speed;
    private Point2D ghost2Coordinates;
    private Point2D ghost2Speed;
    private static Direction lastDirection;
    private static Direction currentDirection;

    public GameLogic() {
//        this.startNewGame();
    }

    public void initializeLevel(String fileName) {
        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                lineScanner.next();
                columnsCount++;
            }
            rowsCount++;
        }
        columnsCount = columnsCount / rowsCount;
        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid = new CellValue[rowsCount][columnsCount];
        int row = 0;
        int pacmanRow = 0;
        int pacmanColumn = 0;
        int ghost1Row = 0;
        int ghost1Column = 0;
        int ghost2Row = 0;
        int ghost2Column = 0;
        while(scanner2.hasNextLine()){
            int column = 0;
            String line= scanner2.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()){
                String value = lineScanner.next();
                CellValue thisValue;
                if (value.equals("W")){
                    thisValue = CellValue.WALL;
                }
                else if (value.equals("S")){
                    thisValue = CellValue.SMALLDOT;
                    dotsCount++;
                }
                else if (value.equals("B")){
                    thisValue = CellValue.BIGDOT;
                    dotsCount++;
                }
                else if (value.equals("1")){
                    thisValue = CellValue.GHOST1HOME;
                    ghost1Row = row;
                    ghost1Column = column;
                }
                else if (value.equals("2")){
                    thisValue = CellValue.GHOST2HOME;
                    ghost2Row = row;
                    ghost2Column = column;
                }
                else if (value.equals("P")){
                    thisValue = CellValue.PACMANHOME;
                    pacmanRow = row;
                    pacmanColumn = column;
                }
                else //(value.equals("E"))
                {
                    thisValue = CellValue.EMPTY;
                }
                grid[row][column] = thisValue;
                column++;
            }
            row++;
        }
        pacmanCoordinates = new Point2D(pacmanRow, pacmanColumn);
        pacmanSpeed = new Point2D(0,0);
        ghost1Coordinates = new Point2D(ghost1Row,ghost1Column);
        ghost1Speed = new Point2D(-1, 0);
        ghost2Coordinates = new Point2D(ghost2Row,ghost2Column);
        ghost2Speed = new Point2D(-1, 0);
        currentDirection = Direction.NONE;
        lastDirection = Direction.NONE;
    }

