package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import support.Command;

/**
 * Class to execute the given HTTP/1.1 command
 * 
 * @author 	Tom Stappaerts, Benjamin Stienlet
 */
@SuppressWarnings("deprecation")
public class Http11 extends Http {


	public Http11(Command command, String host, String resource, String ip, int port) {
		super(command, host, resource, ip, port);
	}

	public Http11(Command command, String host, String resource, String ip,int port, Socket socket) {
		super(command, host, resource, ip, port, socket);
	}

	/**
	 * String description
	 */
	@Override
	public String toString() {
		return "HTTP/1.1";
	}

	/**
	 * InitialRequestLine
	 */
	@Override
	protected void initialRequest(Command command) throws IOException {
		String outputSentence = command + " " + resource + " " + toString();
		//Required HOST header
		outputSentence += "\n" + "Host: " + host;
		System.out.println("Input: " + outputSentence);
		outToServer.writeBytes(outputSentence + "\n");
	}

	@Override
	protected void put() {
		try{
			executePut();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void get() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			//Read status, ignore 100 lines
			String status = inFromServer.readLine();
			while (status.isEmpty() || status.trim().endsWith("100 Continue")) {
				System.out.println("Status: " + status);
				status = inFromServer.readLine();
			}
			System.out.println("Status:  " + status);
			System.out.println("Headers:");
			executeGet(status);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * executes a new get request with same socket
	 */
	@Override
	protected void executeGet(String hostImg, String resourceImg, String ipImg) {
		if (!ipImg.equals(ip)) {
			try {
				System.out.println("Closing socket: New ip.");
				clientSocket.close();
				outToServer.close();
				inFromServer.close();

				clientSocket = new Socket(ipImg, port);
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new DataInputStream(clientSocket.getInputStream());
				System.out.println("Socket estabilished: " + clientSocket.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (clientSocket.isClosed()) {
					clientSocket = new Socket(ip, port);
					System.out.println("Socket estabilished: " + clientSocket.toString() );
					outToServer = new DataOutputStream(clientSocket.getOutputStream());
					inFromServer = new DataInputStream(clientSocket.getInputStream());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ip = ipImg;
		host = hostImg;
		resource = resourceImg;
		try {
			initialRequest(Command.GET);
		} catch (IOException e) {
			e.printStackTrace();
		}
		get();
	}

	@Override
	protected void head() {
		try{
			executeHead();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void post() {
		//Post executes put method
		this.put();
	}

}
