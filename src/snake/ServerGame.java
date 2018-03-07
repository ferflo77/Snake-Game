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

public class ServerGame {
	static boolean[] jugadores = {false, false, false, false};
	static String[] coordenadas = {"","","",""};
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
		while (true) {

			try {
				entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
				
				String textoEntrada = entrada.readLine().trim();
				coordenadas[player-1] = textoEntrada;
				//System.out.println("Player "+player+": "+textoEntrada);
				
				if (player ==1) {
					outToClient.writeBytes(coordenadas[1].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[2].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[3].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
				} else if (player == 2){
					outToClient.writeBytes(coordenadas[0].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[2].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[3].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
				} else if (player == 3){
					outToClient.writeBytes(coordenadas[0].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[1].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[3].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
				} else if (player == 4){
					outToClient.writeBytes(coordenadas[0].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[1].replaceAll(Pattern.quote("], ["), "] ; [")+" | ");
					outToClient.writeBytes(coordenadas[2].replaceAll(Pattern.quote("], ["), "] ; [")+"\n");
				}
				
				//outToClient.writeBytes("221 closing connection \n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				coordenadas[player-1] = "";
				jugadores[player-1] = false;
				e.printStackTrace();
				break;
			}
		}
		
	}
}
