import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int boardWidth = 900;
        int boardHeight = 600;

        JFrame frame = new JFrame("Snake");
        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Snakegame snakegame = new Snakegame(boardWidth,boardHeight);
        frame.add(snakegame);
        frame.setVisible(true);
        frame.pack();
        snakegame.requestFocusInWindow();
    }
}
