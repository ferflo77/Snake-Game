package snake;
// Clase Game.java : Instancia la clase Game, el JFrame
// Jose Fernando Flores
import java.awt.EventQueue;
import javax.swing.JFrame;

public class Game extends JFrame {

    public Game() {
        add(new Board());
        setResizable(false);
        pack();
        setTitle("Multiplayer Snake Game!");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new Game();
                frame.setVisible(true);
            }
        });
    }
}