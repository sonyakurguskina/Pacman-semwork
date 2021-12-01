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

    @FXML
    private int rowsCount;
    @FXML
    private int columnsCount;
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
        this.startNewGame();
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
        while (scanner2.hasNextLine()) {
            int column = 0;
            String line = scanner2.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                String value = lineScanner.next();
                CellValue thisValue;
                if (value.equals("W")) {
                    thisValue = CellValue.WALL;
                } else if (value.equals("S")) {
                    thisValue = CellValue.SMALLDOT;
                    dotsCount++;
                } else if (value.equals("B")) {
                    thisValue = CellValue.BIGDOT;
                    dotsCount++;
                } else if (value.equals("1")) {
                    thisValue = CellValue.GHOST1HOME;
                    ghost1Row = row;
                    ghost1Column = column;
                } else if (value.equals("2")) {
                    thisValue = CellValue.GHOST2HOME;
                    ghost2Row = row;
                    ghost2Column = column;
                } else if (value.equals("P")) {
                    thisValue = CellValue.PACMANHOME;
                    pacmanRow = row;
                    pacmanColumn = column;
                } else //(value.equals("E"))
                {
                    thisValue = CellValue.EMPTY;
                }
                grid[row][column] = thisValue;
                column++;
            }
            row++;
        }
        pacmanCoordinates = new Point2D(pacmanRow, pacmanColumn);
        pacmanSpeed = new Point2D(0, 0);
        ghost1Coordinates = new Point2D(ghost1Row, ghost1Column);
        ghost1Speed = new Point2D(-1, 0);
        ghost2Coordinates = new Point2D(ghost2Row, ghost2Column);
        ghost2Speed = new Point2D(-1, 0);
        currentDirection = Direction.NONE;
        lastDirection = Direction.NONE;
    }

    public void startNewGame() {
        this.isGameOver = false;
        this.isWon = false;
        this.isWeakGhostMode = false;
        dotsCount = 0;
        rowsCount = 0;
        columnsCount = 0;
        this.scoreCount = 0;
        this.levelNumber = 1;
        this.initializeLevel(Controller.getLevelFile(0));
    }

    public void startNextLevel() {
        if (this.isLevelComplete()) {
            this.levelNumber++;
            rowsCount = 0;
            columnsCount = 0;
            isWon = false;
            isWeakGhostMode = false;
            try {
                this.initializeLevel(Controller.getLevelFile(levelNumber - 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                isWon = true;
                isGameOver = true;
                levelNumber--;
            }
        }
    }

    public void movePacman(Direction direction) {
        Point2D potentialPacmanVelocity = changeVelocity(direction);
        Point2D potentialPacmanLocation = pacmanCoordinates.add(potentialPacmanVelocity);
        potentialPacmanLocation = setGoingOffscreenNewLocation(potentialPacmanLocation);
        if (direction.equals(lastDirection)) {
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                pacmanSpeed = changeVelocity(Direction.NONE);
                setLastDirection(Direction.NONE);
            } else {
                pacmanSpeed = potentialPacmanVelocity;
                pacmanCoordinates = potentialPacmanLocation;
            }
        } else {
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                potentialPacmanVelocity = changeVelocity(lastDirection);
                potentialPacmanLocation = pacmanCoordinates.add(potentialPacmanVelocity);
                if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                    pacmanSpeed = changeVelocity(Direction.NONE);
                    setLastDirection(Direction.NONE);
                } else {
                    pacmanSpeed = changeVelocity(lastDirection);
                    pacmanCoordinates = pacmanCoordinates.add(pacmanSpeed);
                }
            } else {
                pacmanSpeed = potentialPacmanVelocity;
                pacmanCoordinates = potentialPacmanLocation;
                setLastDirection(direction);
            }
        }
    }

    public void moveGhosts() {
        Point2D[] ghost1Data = moveAGhost(ghost1Speed, ghost1Coordinates);
        Point2D[] ghost2Data = moveAGhost(ghost2Speed, ghost2Coordinates);
        ghost1Speed = ghost1Data[0];
        ghost1Coordinates = ghost1Data[1];
        ghost2Speed = ghost2Data[0];
        ghost2Coordinates = ghost2Data[1];

    }

    public Point2D[] moveAGhost(Point2D velocity, Point2D location) {
        Random generator = new Random();
        if (!isWeakGhostMode) {
            if (location.getY() == pacmanCoordinates.getY()) {
                if (location.getX() > pacmanCoordinates.getX()) {
                    velocity = changeVelocity(Direction.UP);
                } else {
                    velocity = changeVelocity(Direction.DOWN);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else if (location.getX() == pacmanCoordinates.getX()) {
                if (location.getY() > pacmanCoordinates.getY()) {
                    velocity = changeVelocity(Direction.LEFT);
                } else {
                    velocity = changeVelocity(Direction.RIGHT);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else {
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            }
        }
        if (isWeakGhostMode) {
            if (location.getY() == pacmanCoordinates.getY()) {
                if (location.getX() > pacmanCoordinates.getX()) {
                    velocity = changeVelocity(Direction.DOWN);
                } else {
                    velocity = changeVelocity(Direction.UP);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else if (location.getX() == pacmanCoordinates.getX()) {
                if (location.getY() > pacmanCoordinates.getY()) {
                    velocity = changeVelocity(Direction.RIGHT);
                } else {
                    velocity = changeVelocity(Direction.LEFT);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else {
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            }
        }
        Point2D[] data = {velocity, location};
        return data;

    }

    public Point2D setGoingOffscreenNewLocation(Point2D objectLocation) {
        //if object goes offscreen on the right
        if (objectLocation.getY() >= columnsCount) {
            objectLocation = new Point2D(objectLocation.getX(), 0);
        }
        if (objectLocation.getY() < 0) {
            objectLocation = new Point2D(objectLocation.getX(), columnsCount - 1);
        }
        return objectLocation;
    }

    public Direction intToDirection(int x) {
        if (x == 0) {
            return Direction.LEFT;
        } else if (x == 1) {
            return Direction.RIGHT;
        } else if (x == 2) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }
}