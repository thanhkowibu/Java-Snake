import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Snakegame extends JPanel implements ActionListener, KeyListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // no need
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_A && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_S && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_D && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // no need
    }

    private static class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private Image appleImage;


    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Apple
    Tile apple;
    Random random;

    // Game logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    Snakegame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth,this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        appleImage = new ImageIcon("src/assets/apple.png").getImage();

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        apple = new Tile(10, 10);
        random = new Random();
        placeApple();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid
//        for (int i = 0; i < boardWidth/tileSize; i++) {
//            // (x1, x2, y1, y2)
//            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
//            g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
//        }

        // Apple
        g.drawImage(appleImage, apple.x * tileSize, apple.y * tileSize, tileSize, tileSize, this);
        // Snake head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Snake body
        for (Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Game over screen
        if (gameOver) {
            g.setColor(Color.red);
            String gameOverText = "Game Over";
            String scoreText = "Score: " + snakeBody.size();
            String restartText = "Press R to restart";

            // Set font for Game Over text
            g.setFont(new Font("Arial", Font.BOLD, 36));
            FontMetrics metrics = g.getFontMetrics(g.getFont());

            // Calculate positions for the text
            int xGameOver = (boardWidth - metrics.stringWidth(gameOverText)) / 2;
            int yGameOver = boardHeight / 2;

            // Draw the Game Over text
            g.drawString(gameOverText, xGameOver, yGameOver);

            // Set font and color for Score text
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.gray);
            metrics = g.getFontMetrics(g.getFont());
            int xScore = (boardWidth - metrics.stringWidth(scoreText)) / 2;
            int yScore = yGameOver + metrics.getHeight();

            // Draw the Score text
            g.drawString(scoreText, xScore, yScore);

            // Set font and color for Restart text
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.gray);
            metrics = g.getFontMetrics(g.getFont());
            int xRestart = (boardWidth - metrics.stringWidth(restartText)) / 2;
            int yRestart = yScore + metrics.getHeight();

            // Draw the Restart text
            g.drawString(restartText, xRestart, yRestart);
        }
        else {
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }

    }

    public void placeApple() {
        apple.x = random.nextInt(boardWidth / tileSize);
        apple.y = random.nextInt(boardHeight / tileSize);
    }

    public void move() {
        if (gameOver) {
            return; // Don't move if the game is over
        }
        // Eat apple
        if (collision(snakeHead,apple)) {
            snakeBody.add(new Tile(apple.x, apple.y));
            placeApple();
        }

        // Move snake body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevPart = snakeBody.get(i-1);
                snakePart.x = prevPart.x;
                snakePart.y = prevPart.y;
            }
        }

        // Move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Game over conditions

        // collide with own body
        for (Tile snakePart: snakeBody) {
            if (collision(snakePart, snakeHead)) {
                gameOver = true;
            }
        }

        // collide with wall
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth/tileSize
         || snakeHead.y < 0 || snakeHead.y >= boardHeight/tileSize) {
            gameOver = true;
        }

    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void resetGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        placeApple();
        velocityX = 0;
        velocityY = 0;
        gameOver = false;
        gameLoop.start();
        repaint();
        requestFocusInWindow();
    }
}
