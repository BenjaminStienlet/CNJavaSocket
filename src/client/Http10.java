package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import support.Command;

public class Http10 extends Http{

	public void executeCommand(Command command, String host, String resource, String ip, int port) {
		try {
			Socket clientSocket = new Socket(ip, port); 
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Socket estabilished: " + clientSocket.toString() );
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String outputSentence = command + " " + resource + " " + toString();
			
		
			System.out.println("Input: " + outputSentence);
			outToServer.writeBytes(outputSentence + "\n");

			if (command.needsInput()) {
				BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
//				ArrayList<String> input = new ArrayList<String>();
				String input = "";
				String inputString;
				while(!(inputString = inFromUser.readLine()).equals("")) {
					input += " " + inputString;
				}
				input.trim();
				outToServer.writeBytes("Content-Length: " + input.getBytes().length + "\n\n");
				outToServer.writeBytes(input);
			}
			else {
				outToServer.writeBytes("\n");
			}
			
			System.out.println("Written output");
			String sentence1;
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
				System.out.println(sentence1);
			}
			System.out.println("Data: \n");
			while((sentence1 = inFromServer.readLine()) != null) {
				System.out.println(sentence1);
			}

			clientSocket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "HTTP/1.0";
	}
	
}
