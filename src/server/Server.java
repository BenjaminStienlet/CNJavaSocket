package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[] args) {
		try {
			boolean exit = false;
			// Create socket
			ServerSocket welcomeSocket = new ServerSocket(6789);
			System.out.println("Socket estabilished.");
			while(!exit) {
				Socket connectionSocket = welcomeSocket.accept();
				if (connectionSocket != null) {
					// Create a thread for the handler
					Handler h = new Handler(connectionSocket);
					Thread thread = new Thread(h);
					thread.start();
				}
			}
			welcomeSocket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	

}
