package com.example.gomoku_dianyang_javafx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.io.IOException;

/**
 * Constructs game Gomoku (five in a row). JavaFX version.
 * See gameplay at https://en.wikipedia.org/wiki/Gomoku
 *
 * @author Dian Yang
 * @version 1.0
 */
public class Gomoku extends Application {
    private static final int SIDE = 15;
    private static final int LENGTH = 750;
    enum State {
        running, black, white
    }
    private State gameState = State.running;
    private final Color chessboardColor = Color.SADDLEBROWN;
    private Piece[][] chessboard = new Piece[SIDE][SIDE];
    private boolean blackTurn = true;
    private Scene scene;
    private Pane pane;

    /**
     * Constructor. Sets features of the window, creates a chessboard.
     * For every mouse click, get its position on the window and add a piece at the corresponding position in the
     * chessboard.
     */
    public Gomoku() {
        pane = new Pane();
        Rectangle board = new Rectangle(50, 50, LENGTH, LENGTH);
        board.setFill(chessboardColor);
        pane.getChildren().add(board);

        for (int i = 0; i < SIDE; i++) {
            Line horizontalLine = new Line(50, 50 + i * LENGTH / SIDE, 50 + LENGTH, 50 + i * LENGTH / SIDE);
            Line verticalLine = new Line(50 + i * LENGTH / SIDE, 50, 50 + i * LENGTH / SIDE, 50 + LENGTH);
            pane.getChildren().addAll(horizontalLine, verticalLine);
        }

        board.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int[] coordinates = addPiece((int) (mouseEvent.getX()), (int) (mouseEvent.getY()));
                if (coordinates != null)
                    draw(pane, coordinates[0], coordinates[1]);
            }
        });

        scene = new Scene(pane, 850, 850);
    }

    /**
     * Method for drawing chess pieces on the chessboard and display result if possible.
     *
     * @param pane pane object with nodes such as chessboard and pieces on it
     * @param row row position of the piece to be added
     * @param col col position of the piece to be added
     */
    void draw(Pane pane, int row, int col) {
        Color c = chessboard[row][col].getColor() ? Color.BLACK : Color.WHITE;
        Circle piece = new Circle();
        piece.setCenterX(col * 50);
        piece.setCenterY(row * 50);
        piece.setRadius(20);
        piece.setFill(c);
        pane.getChildren().add(piece);
        if (gameState != State.running) {
            String result = gameState == State.black ? "Black won!" : "White won!";
            c = gameState == State.black ? Color.BLACK : Color.WHITE;
            Label l = new Label(result);
            l.setFont(Font.font("SansSerif", FontWeight.BOLD, 55));
            l.setTextFill(c);
            l.setTranslateX(300);
            l.setTranslateY(400);
            pane.getChildren().add(l);
        }
    }

    /**
     * Converts the coordinate of mouse click into position in the chessboard and adds a chess piece in that position.
     *
     * @param x the x coordinate of mouse click
     * @param y the y coordinate of mouse click
     */
    int[] addPiece(int x, int y) {
        if (gameState != State.running)
            return null;

        double xPos = x * 1.0 / (LENGTH / SIDE);
        double yPos = y * 1.0 / (LENGTH / SIDE);
        int row = -1, col = -1;
        if (yPos - (y / (LENGTH / SIDE)) >= 0.8) {
            row = y / (LENGTH / SIDE) + 1;
        } else if (yPos - (y / (LENGTH / SIDE)) <= 0.2) {
            row = y / (LENGTH / SIDE);
        }
        if (xPos - (x / (LENGTH / SIDE)) >= 0.8) {
            col = x / (LENGTH / SIDE) + 1;
        } else if (xPos - (x / (LENGTH / SIDE)) <= 0.2) {
            col = x / (LENGTH / SIDE);
        }

        if (row >= 0 && col >= 0 && chessboard[row][col] == null) {
            chessboard[row][col] = new Piece(blackTurn);
            blackTurn = !blackTurn;
            checkIfEnded(row, col);
            return new int[] {row, col};
        }
        return null;
    }

    /**
     * Checks if the game is ended by checking if a horizontal, vertical, or diagonally chain of five pieces
     * in the same color is formed.
     *
     * @param row the row of the last piece added.
     * @param col the col of the last piece added.
     */
    void checkIfEnded(int row, int col) {
        Piece piece = chessboard[row][col];
        int left = -1, right = 1;
        int chain = 1;

        // check horizontal chain
        while (col + left >= 0 && chessboard[row][col + left] != null
                && chessboard[row][col + left].getColor() == piece.getColor()) {
            chain++;
            left--;
        }
        while (col + right < SIDE && chessboard[row][col + right] != null
                && chessboard[row][col + right].getColor() == piece.getColor()) {
            chain++;
            right++;
        }
        if (chain >= 5) {
            if (blackTurn)
                gameState = State.white;
            else
                gameState = State.black;
            return;
        }
        // check vertical chain
        left = -1;
        right = 1;
        chain = 1;
        while (row + left >= 0 && chessboard[row + left][col] != null
                && chessboard[row + left][col].getColor() == piece.getColor()) {
            chain++;
            left--;
        }
        while (row + right < SIDE && chessboard[row + right][col] != null
                && chessboard[row + right][col].getColor() == piece.getColor()) {
            chain++;
            right++;
        }
        if (chain >= 5) {
            if (blackTurn)
                gameState = State.white;
            else
                gameState = State.black;
            return;
        }
        //check diagonal upward chain
        left = -1;
        right = 1;
        chain = 1;
        while (row + left >= 0 && col - left < SIDE && chessboard[row + left][col - left] != null
                && chessboard[row + left][col - left].getColor() == piece.getColor()) {
            chain++;
            left--;
        }
        while (row + right < SIDE && col - right >= 0 && chessboard[row + right][col - right] != null
                && chessboard[row + right][col - right].getColor() == piece.getColor()) {
            chain++;
            right++;
        }
        if (chain >= 5) {
            if (blackTurn)
                gameState = State.white;
            else
                gameState = State.black;
            return;
        }
        //check diagonal downward chain
        left = -1;
        right = 1;
        chain = 1;
        while (row + right < SIDE && col + right < SIDE && chessboard[row + right][col + right] != null
                && chessboard[row + right][col + right].getColor() == piece.getColor()) {
            chain++;
            right++;
        }
        while (row + left >= 0 && col + left >= 0 && chessboard[row + left][col + left] != null
                && chessboard[row + left][col + left].getColor() == piece.getColor()) {
            chain++;
            left--;
        }
        if (chain >= 5) {
            if (blackTurn)
                gameState = State.white;
            else
                gameState = State.black;
            return;
        }
    }

    public void start(Stage stage) throws IOException {
        stage.setScene(scene);
        stage.setTitle("Gomoku");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

/**
 * Piece class used for creating chess pieces on the chessboard.
 */
class Piece {
    private boolean isBlack;
    boolean getColor() {
        return isBlack;
    }
    Piece(boolean isBlack) {
        this.isBlack = isBlack;
    }
}
