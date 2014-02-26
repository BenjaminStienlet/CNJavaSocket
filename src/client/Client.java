package client;

import support.Command;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetAddress;
public class Client {


	public static void main(String[] args) {
		Boolean exit = false;
		try {
		while(exit != true) {
			System.out.println("Input (x for exit): ");
			BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
			String sentence = inFromUser.readLine();
			if(sentence.equalsIgnoreCase("x".trim())) {
				exit = true;
			} else {
				Client.command(sentence);
			}

		}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}







	/**
	 * 
	 * 
	 * @param input
	 * @throws IllegalArgumentException
	 */
	public static void command(String input) throws IllegalArgumentException {
		String commandInput = null;
		String versionNumber = null;
		String uri = null;
		int port = -1;
		try {
			String[] inputs = input.split(" ");
			commandInput = inputs[0];
			uri = inputs[1];
			port = Integer.parseInt(inputs[2]);
			versionNumber = inputs[3];
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

		//HTTP version
		String version;
		if(versionNumber.equals("1.0") || versionNumber.equalsIgnoreCase("http/1.0"))
			version ="HTTP/1.0";
		else if(versionNumber.equals("1.1") || versionNumber.equalsIgnoreCase("http/1.1"))
			version = "HTTP/1.1";
		else
			throw new IllegalArgumentException();
		//Command
		Command command = null;
		for(Command c : Command.values())
			if(commandInput.equalsIgnoreCase(c.toString())) command = c;
		if(command == null) throw new IllegalArgumentException();
		//Port
		if(port <= 0) throw new IllegalArgumentException();

		String host = "google.com";
		String resource = "/";
		try {
		String ip = InetAddress.getByName(host).getHostAddress();
		

		executeCommand(command,host,resource,ip,port,version);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}




	}







	private static void executeCommand(Command command, String host, String resource, String ip, int port, String version) {
		try {
			Socket clientSocket = new Socket(ip, port); 
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Socket estabilished: " + clientSocket.toString() );
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String outputSentence = command + " " + resource + " " + version;
			if(version.equals("HTTP/1.1")) {
				outputSentence += "\n HOST: " + host;
			}
			System.out.println("Input: " + outputSentence);
			outToServer.writeBytes(outputSentence + "\n\n");
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

}


