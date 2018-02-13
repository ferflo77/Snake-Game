package snake;
// Clase Board.java : Maneja el tablero y graficos sobre la pantalla
// Jose Fernando Flores

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    
    private int w= 700;
    private int h = 600;
    private int cw = 10;
    private String direction;
    private Food food;
    private Timer timer;
    private static int speed = 80;
    private Snake player1;
    private int playerScore;
    
    public Board() {
        playerScore = 0;
        addKeyListener(new Keys());
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(w, h));
        timer = new Timer(speed, this);
        timer.start();
        init();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintIt(g);
    }

    public void init() {
      direction = "right";
      create_snake();
      create_food();
    }

    public void paint_cell(int x, int y, Color color, Graphics g) {
        g.setColor(color);
        g.fillRect(x*cw, y*cw, cw, cw);
    }

    public void create_snake() {
       player1 = new Snake(0,Color.CYAN);
    }

    public void create_food() {
        int x = (int) Math.round(Math.random()*(w-cw)/cw);
        int y = (int) Math.round(Math.random()*(h-cw)/cw);
        food = new Food(x,y);
    }

    public boolean check_collision(int x, int y, ArrayList<ArrayList<Integer>> array) {
        for(int i = 0; i < array.size(); i++){
            if(array.get(i).get(0) == x && array.get(i).get(1) == y)
                return true;
        }
        return false;
    }

    public void endGame(Graphics g) {
        String message = "Fin del Juego";
        Font font = new Font("Calibri", Font.BOLD, 30);
        FontMetrics metrics = getFontMetrics(font);
        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(message, (w - metrics.stringWidth(message))/2,h/2);
    }

    public void paintIt(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        
        ArrayList<Integer> tail = new ArrayList();
        ArrayList<ArrayList<Integer>> snake_array = player1.getSnake_array();
        
        int nx = player1.getSnake_array().get(0).get(0);
        int ny = player1.getSnake_array().get(0).get(1);

        if("right".equals(direction)) nx++;
        else if("left".equals(direction)) nx--;
        else if("up".equals(direction)) ny--;
        else if("down".equals(direction)) ny++;

        if(nx == -1 || nx == w/cw || ny == -1 || ny == h/cw || check_collision(nx, ny,snake_array)) {
            System.out.println("GAME OVER!");
            init();
            return;
        }

        if(nx == food.getX() && ny == food.getY()) {                  
            tail.add(0, nx);
            tail.add(1, ny);
            playerScore+=1;
            System.out.println("SCORE: " + playerScore);
            create_food();
        }
        else {
            int tamanio = snake_array.size()-1;
            tail = snake_array.get(tamanio);
            tail.set(0, nx);
            tail.set(1, ny);    
            snake_array.remove(tamanio);                
        }

        snake_array.add(0, tail);

        System.out.println("coordinates: " + snake_array.toString());
        for(int i = 0; i < snake_array.size(); i++) {
            ArrayList<Integer> c = snake_array.get(i);
            paint_cell(c.get(0), c.get(1),player1.getColor(),g);
        }

        paint_cell(food.getX(), food.getY(), Color.MAGENTA,g);                    
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();      
    }

    private class Keys extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if(key == 37 && !"right".equals(direction)) direction = "left";
                    else if(key == 38 && !"down".equals(direction)) direction = "up";
                    else if(key == 39 && !"left".equals(direction)) direction = "right";
                    else if(key == 40 && !"up".equals(direction)) direction = "down";
        }
    }
}