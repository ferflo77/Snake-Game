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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    
    private int w= 700;
    private int h = 600;
    private int cw = 10;
    private String direction;
    private Food food;
    private Timer timer;
    private static int speed = 100;
    private Snake player1;
    private int playerScore;
	private Socket socket;
	private BufferedReader br;
	private DataOutputStream serverStream;
	private BufferedReader serverBuffer;
    
    public Board() throws UnknownHostException, IOException {
        playerScore = 0;
        addKeyListener(new Keys());
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(w, h));
        timer = new Timer(speed, this);
        timer.start();
        socket = new Socket("localhost", 2430);
        br = new BufferedReader(new InputStreamReader(System.in));
		serverStream = new DataOutputStream(socket.getOutputStream());
		serverBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
        init();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
			paintIt(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void init() {
      direction = "right";
      create_snake();
      create_food(true);
    }

    public void paint_cell(int x, int y, Color color, Graphics g) {
        g.setColor(color);
        g.fillRect(x*cw, y*cw, cw, cw);
    }

    public void create_snake() {
    	try {
			serverStream.writeBytes("Color\n");
			String textoDevuelto = serverBuffer.readLine();
			int colorNum = Integer.parseInt(textoDevuelto.split(Pattern.quote(" , "))[0]);
			int player = Integer.parseInt(textoDevuelto.split(Pattern.quote(" , "))[1]);
			Color color = new Color(colorNum);
		    player1 = new Snake(player * 10,color);
		    playerScore = 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public void create_food(boolean newFood) {
    	try {
	    	if (newFood) {
				serverStream.writeBytes("New Food\n");
				
	    	} else {
	    		serverStream.writeBytes("Food\n");
	    	}
    	
    	
	    	String textoDevuelto = serverBuffer.readLine();
			String[] xy = textoDevuelto.split(Pattern.quote(" , "));
	        int x = Integer.parseInt(xy[0]);
	        int y = Integer.parseInt(xy[1]);
	        food = new Food(x,y);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
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

    public void paintIt(Graphics g) throws IOException {
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

        if(nx == -1 || nx == w/cw || ny == -1 || ny == h/cw) {
            //System.out.println("GAME OVER!");
            //create_snake();
        	if (nx==-1) nx = w/cw-1; 
        	if (ny==-1) ny = h/cw-1; 
        	if (nx==w/cw) nx = 0; 
        	if (ny==h/cw) ny = 0; 
            //direction = "right";
            //return;
        }
        
        if (check_collision(nx, ny,snake_array)) {
        	create_snake();
        	direction = "right";
        	return ;
        }

        if(nx == food.getX() && ny == food.getY()) {                  
            tail.add(0, nx);
            tail.add(1, ny);
            playerScore+=1;
            System.out.println("SCORE: " + playerScore);
            create_food(true);
        }
        else {
            int tamanio = snake_array.size()-1;
            tail = snake_array.get(tamanio);
            tail.set(0, nx);
            tail.set(1, ny);    
            snake_array.remove(tamanio);                
        }

        snake_array.add(0, tail);

        //System.out.println("coordinates: " + snake_array.toString());
        serverStream.writeBytes(snake_array.toString() + '\n');
        String textoDevuelto = serverBuffer.readLine();
        //System.out.println(textoDevuelto);
        for(int i = 0; i < snake_array.size(); i++) {
            ArrayList<Integer> c = snake_array.get(i);
            paint_cell(c.get(0), c.get(1),player1.getColor(),g);
        }
       
        String[] snakesJugadores = textoDevuelto.split(Pattern.quote(" | "));
        for(int j = 0; j < snakesJugadores.length; j++) {
        	String color = snakesJugadores[j].split(Pattern.quote(" % "))[0];
        	String jugadorAjeno = snakesJugadores[j].split(Pattern.quote(" % "))[1];
        	if (!jugadorAjeno.equals(" ")) {
	        	ArrayList<ArrayList<Integer>> otherPlayerSnake = convertToSnake(jugadorAjeno);
		        for(int i = 0; i < otherPlayerSnake.size(); i++) {
		            ArrayList<Integer> c = otherPlayerSnake.get(i);
		            paint_cell(c.get(0), c.get(1),new Color(Integer.parseInt(color)),g);
		        }
		        
		        if (check_collision(nx, ny,otherPlayerSnake)) {
		        	create_snake();
		        	direction = "right";
		        	return ;
		        }

	        }
        }
        create_food(false);
        paint_cell(food.getX(), food.getY(), Color.MAGENTA,g);                    
        Toolkit.getDefaultToolkit().sync();
    }
    
    public ArrayList<ArrayList<Integer>> convertToSnake(String snakeString) {
    	ArrayList<ArrayList<Integer>> snake = new ArrayList<ArrayList<Integer>>();
    	String snakeS = snakeString.substring(2, snakeString.length()-1);
    	//System.out.println(snakeS);
    	String[] coordinates = snakeS.split(" ; ");
    	
    	for(int i = 0; i < coordinates.length; i++) {
    	 
            ArrayList<Integer> c = new ArrayList<Integer>();
            String[] xy = coordinates[i].substring(1, coordinates[i].length()-1).split(", ");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            
            c.add(x);
            c.add(y);
            snake.add(c);
        }
        
    	return snake;
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
