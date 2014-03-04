package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[] args) {
		try {
			ServerSocket welcomeSocket = new ServerSocket(6789);
			System.out.println("Socket estabilished.");
			while(true) {
				Socket connectionSocket = welcomeSocket.accept();
				if (connectionSocket != null) {
					Handler h = new Handler(connectionSocket);
					Thread thread = new Thread(h);
					thread.start();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	

}
