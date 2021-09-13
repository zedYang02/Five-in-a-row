import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Constructs game Gomoku (five in a row).
 * See gameplay at https://en.wikipedia.org/wiki/Gomoku
 *
 * @author Dian Yang
 * @version 1.0
 */
public class Gomoku extends JPanel {
    private static final int SIDE = 15;
    private static final int LENGTH = 750;
    enum State {
        running, black, white
    }
    State gameState = State.running;
    private final Color chessboardColor = new Color(0xFF8B4513, true);
    private final Color black = Color.BLACK;
    private final Color white = Color.WHITE;
    private Piece[][] chessboard = new Piece[SIDE][SIDE];
    private boolean blackTurn = true;

    /**
     * Constructor. Sets features of the window.
     * For every mouse click, get its position on the window and add a piece at the corresponding position in the
     * chessboard, then repaint the window.
     */
    public Gomoku() {
        setPreferredSize(new Dimension(850, 850));
        setBackground(Color.WHITE);
        setFocusable(true);
        repaint();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                addPiece(x, y);
                repaint();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        draw(g2);
    }

    /**
     * Method for drawing the chessboard and chess pieces.
     *
     * @param g2 the Graphics2D object to be modified.
     */
    void draw(Graphics2D g2) {
        g2.setColor(chessboardColor);
        g2.fillRect(50, 50, 750, 750);
        g2.setColor(Color.BLACK);
        for (int i = 0; i < SIDE; i++) {
            g2.drawLine(50, 50 + i * LENGTH / SIDE, 50 + LENGTH, 50 + i * LENGTH / SIDE);
            g2.drawLine(50 + i * LENGTH / SIDE, 50, 50 + i * LENGTH / SIDE, 50 + LENGTH);
        }
        if (gameState == State.running) {
            for (int i = 0; i < SIDE; i++) {
                for (int j = 0; j < SIDE; j++) {
                    if (chessboard[i][j] != null) {
                        Color c = chessboard[i][j].getColor() ? Color.BLACK : Color.WHITE;
                        g2.setColor(c);
                        g2.fillOval(j * 50 - 20, i * 50 - 20, 40, 40);
                    }
                }
            }
        } else {
            Color c = gameState == State.black ? Color.BLACK : Color.WHITE;
            g2.setColor(c);
            g2.setFont(new Font("SansSerif", Font.BOLD, 55));
            String res = gameState == State.black ? "Black won" : "White won";
            g2.drawString(res, 300, 300);
        }
    }

    /**
     * Converts the coordinate of mouse click into position in the chessboard and adds a chess piece in that position.
     *
     * @param x the x coordinate of mouse click
     * @param y the y coordinate of mouse click
     */
    void addPiece(int x, int y) {
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
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Gomoku");
            f.setResizable(true);
            f.add(new Gomoku(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
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
