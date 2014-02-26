package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		try {
		ServerSocket welcomeSocket = new ServerSocket(6789);
		System.out.println("Socket estabilished.");
		while(true) {
		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader (connectionSocket.getInputStream())); DataOutputStream outToClient = new DataOutputStream
		(connectionSocket.getOutputStream());
		String clientSentence = inFromClient.readLine(); System.out.println("Received: " + clientSentence);
		String capsSentence = clientSentence.toUpperCase() + '\n'; outToClient.writeBytes(capsSentence);
		}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

}
