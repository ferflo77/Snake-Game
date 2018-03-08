import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.sun.javafx.css.converters.ColorConverter;
import java.util.*;
import com.sun.prism.paint.Color;

public class ServerGame {
	static boolean[] jugadores = {false, false, false, false};
	static String[] coordenadas = {"","","",""};
	static Color[] colores = {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE};
	static Point food = new Point(100, 100);
	static private int w= 700;
	static private int h = 600;
	static private int cw = 10;
			
	public static void main(String[] args) throws IOException {
		System.out.println("Servidor encendido");
		int cont = 0;
		
		ServerSocket SSocket = new ServerSocket(2430);
		  while (cont !=3) {
			Socket socket = SSocket.accept();
			
		   if (!jugadores[0]) {
			   jugadores[0] = true;
				Thread thread1 = new Thread() {
				    public void run() {
				    	GameRequest(socket,1);
				    }
				};
				thread1.start();
			} else if (!jugadores[1]) {
				jugadores[1] = true;
				Thread thread2 = new Thread() {
				    public void run() {
				    	GameRequest(socket,2);
				    }
				};
				thread2.start();
			} else if (!jugadores[2]) { 
				jugadores[2] = true;
				Thread thread3 = new Thread() {
				    public void run() {
				    	GameRequest(socket,3);
				    }
				};
				thread3.start();
			} else if (!jugadores[3]) { 
				Thread thread4 = new Thread() {
				    public void run() {
				    	GameRequest(socket,4); 
				    }
				};
				thread4.start();
			}
		  }

	}



	public static void GameRequest(Socket socket, int player) {
		System.out.println("Jugador "+player+" conectado");
		BufferedReader entrada;
		int rgb1 = colores[0].getIntArgbPre();
		int rgb2 = colores[1].getIntArgbPre();
		int rgb3 = colores[2].getIntArgbPre();
		int rgb4 = colores[3].getIntArgbPre();
		while (true) {

			try {
				entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
				
				String textoEntrada = entrada.readLine().trim();
				if (textoEntrada.equals("Color")) { 
					
					int rgb = colores[player-1].getIntArgbPre();
					outToClient.writeBytes(rgb +" , "+player +"\n");
				} else if (textoEntrada.equals("New Food")) { 
					
					int x = (int) Math.round(Math.random()*(w-cw)/cw);
			        int y = (int) Math.round(Math.random()*(h-cw)/cw);
			        food.setLocation(x, y);
					outToClient.writeBytes(x+" , "+y+"\n");
				} else if (textoEntrada.equals("Food")) { 
					
					int x = (int) food.getX();
			        int y = (int) food.getY();
			        food.setLocation(x, y);
					outToClient.writeBytes(x+" , "+y+"\n");
				} else {
					coordenadas[player-1] = textoEntrada;
					//System.out.println("Player "+player+": "+textoEntrada);
					
					if (player ==1) {
						outToClient.writeBytes(rgb2 + " %  "+coordenadas[1].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb3 + " %  "+coordenadas[2].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb4 + " %  "+coordenadas[3].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
					} else if (player == 2){
						outToClient.writeBytes(rgb1 + " %  "+coordenadas[0].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb3 + " %  "+coordenadas[2].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb4 + " %  "+coordenadas[3].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
					} else if (player == 3){
						outToClient.writeBytes(rgb1 + " %  "+coordenadas[0].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb2 + " %  "+coordenadas[1].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb4 + " %  "+coordenadas[3].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
					} else if (player == 4){
						outToClient.writeBytes(rgb1 + " %  "+coordenadas[0].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb2 + " %  "+coordenadas[1].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
						outToClient.writeBytes(rgb3 + " %  "+coordenadas[2].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
					}
				}
				//outToClient.writeBytes("221 closing connection \n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				coordenadas[player-1] = "";
				jugadores[player-1] = false;
				//e.printStackTrace();
				break;
			}
		}
		
	}
}

