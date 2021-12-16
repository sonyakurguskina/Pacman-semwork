package ru.itis.kurguskina;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.itis.kurguskina.GameLogic.CellValue;

public class PacManMapping extends Group {
    public final static double cell_width = 20.0;

    @FXML private int rowsCount;
    @FXML private int columnsCount;
    private ImageView[][] cellViews;
    private Image pacmanRightPic;
    private Image pacmanUpPic;
    private Image pacmanDownPic;
    private Image pacmanLeftPic;
    private Image ghost1Pic;
    private Image ghost2Pic;
    private Image blueGhostPic;
    private Image wallPic;
    private Image bigDotPic;
    private Image smallDotPic;

    public PacManMapping() {
        this.pacmanRightPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/pacmanRight.gif"));
        this.pacmanUpPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/pacmanUp.gif"));
        this.pacmanDownPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/pacmanDown.gif"));
        this.pacmanLeftPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/pacmanLeft.gif"));
        this.ghost1Pic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/ghost2.gif"));
        this.ghost2Pic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/redghost.gif"));
        this.blueGhostPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/blueghost.gif"));
        this.wallPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/wall.jpg"));
        this.bigDotPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/whitedot.png"));
        this.smallDotPic = new Image(getClass().getResourceAsStream("/ru/itis/kurguskina/resources/smalldot.png"));
    }


    private void initialize() {
        if (this.rowsCount > 0 && this.columnsCount > 0) {
            this.cellViews = new ImageView[this.rowsCount][this.columnsCount];
            for (int row = 0; row < this.rowsCount; row++) {
                for (int column = 0; column < this.columnsCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setY((double)row * cell_width);
                    imageView.setX((double)column * cell_width);
                    imageView.setFitHeight(cell_width);
                    imageView.setFitWidth(cell_width);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    public void update(GameLogic model) {
        assert model.getRowsCount() == this.rowsCount && model.getColumnsCount() == this.columnsCount;
        for (int row = 0; row < this.rowsCount; row++){
            for (int column = 0; column < this.columnsCount; column++){
                CellValue value = model.getCellValue(row, column);
                if (value == CellValue.WALL) {
                    this.cellViews[row][column].setImage(this.wallPic);
                }
                else if (value == CellValue.BIGDOT) {
                    this.cellViews[row][column].setImage(this.bigDotPic);
                }
                else if (value == CellValue.SMALLDOT) {
                    this.cellViews[row][column].setImage(this.smallDotPic);
                }
                else {
                    this.cellViews[row][column].setImage(null);
                }
                if (row == model.getPacmanCoordinates().getX() && column == model.getPacmanCoordinates().getY() && (GameLogic.getLastDirection() == GameLogic.Direction.RIGHT || GameLogic.getLastDirection() == GameLogic.Direction.NONE)) {
                    this.cellViews[row][column].setImage(this.pacmanRightPic);
                }

                else if (row == model.getPacmanCoordinates().getX() && column == model.getPacmanCoordinates().getY() && GameLogic.getLastDirection() == GameLogic.Direction.DOWN) {
                    this.cellViews[row][column].setImage(this.pacmanDownPic);
                }
                else if (row == model.getPacmanCoordinates().getX() && column == model.getPacmanCoordinates().getY() && GameLogic.getLastDirection() == GameLogic.Direction.LEFT) {
                    this.cellViews[row][column].setImage(this.pacmanLeftPic);
                }
                else if (row == model.getPacmanCoordinates().getX() && column == model.getPacmanCoordinates().getY() && GameLogic.getLastDirection() == GameLogic.Direction.UP) {
                    this.cellViews[row][column].setImage(this.pacmanUpPic);
                }

                if (GameLogic.isIsWeakGhostMode() && (Controller.getWeakGhostModeCounter() == 6 ||Controller.getWeakGhostModeCounter() == 4 || Controller.getWeakGhostModeCounter() == 2)) {
                    if (row == model.getGhost1Coordinates().getX() && column == model.getGhost1Coordinates().getY()) {
                        this.cellViews[row][column].setImage(this.ghost1Pic);
                    }
                    if (row == model.getGhost2Coordinates().getX() && column == model.getGhost2Coordinates().getY()) {
                        this.cellViews[row][column].setImage(this.ghost2Pic);
                    }
                }
                else if (GameLogic.isIsWeakGhostMode()) {
                    if (row == model.getGhost1Coordinates().getX() && column == model.getGhost1Coordinates().getY()) {
                        this.cellViews[row][column].setImage(this.blueGhostPic);
                    }
                    if (row == model.getGhost2Coordinates().getX() && column == model.getGhost2Coordinates().getY()) {
                        this.cellViews[row][column].setImage(this.blueGhostPic);
                    }
                }
                else {
                    if (row == model.getGhost1Coordinates().getX() && column == model.getGhost1Coordinates().getY()) {
                        this.cellViews[row][column].setImage(this.ghost1Pic);
                    }
                    if (row == model.getGhost2Coordinates().getX() && column == model.getGhost2Coordinates().getY()) {
                        this.cellViews[row][column].setImage(this.ghost2Pic);
                    }
                }
            }
        }
    }

    public int getRowsCount() {
        return this.rowsCount;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
        this.initialize();
    }

    public int getColumnsCount() {
        return this.columnsCount;
    }

    public void setColumnsCount(int columnsCount) {
        this.columnsCount = columnsCount;
        this.initialize();
    }
}
