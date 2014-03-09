package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import support.Command;

@SuppressWarnings("deprecation")
public class Http11 extends Http {


	public Http11(Command command, String host, String resource, String ip, int port) {
		super(command, host, resource, ip, port);
	}

	public Http11(Command command, String host, String resource, String ip,int port, Socket socket) {
		super(command, host, resource, ip, port, socket);
	}

	@Override
	public String toString() {
		return "HTTP/1.1";
	}

	@Override
	protected void initialRequest(Command command) throws IOException {
		String outputSentence = command + " " + resource + " " + toString();
		outputSentence += "\n" + "Host: " + host;
		System.out.println("Input: " + outputSentence);
		outToServer.writeBytes(outputSentence + "\n");
	}

	@Override
	protected void put() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void get() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			String status = inFromServer.readLine();
			while (status.isEmpty() || status.trim().endsWith("100 Continue")) {
				status = inFromServer.readLine();
			}
			System.out.println("Status:  " + status);
			System.out.println("Headers: \n");
			String sentence;
			String contentType = null;
			String newLocation = null;
			int contentLength = 0;
			//Getting headers.
			while((sentence = inFromServer.readLine()) != null && !sentence.trim().isEmpty()) {
				if(sentence.startsWith("Content-Type:")) {
					contentType = sentence.split("Content-Type: ")[1].trim();
				}
				if(sentence.startsWith("Content-Length:")) {
					contentLength = Integer.parseInt(sentence.split("Content-Length: ")[1].trim());
				}
				if(sentence.startsWith("Location: ")) {
					newLocation = sentence.split("Location: ")[1].trim();
				}

				System.out.println(sentence);
			}
			//Getting file information. 2 possibilities: html or image. Others are not supported.

			if(status.contains("200") && contentType.startsWith("text/html")) {
				getHtml(contentLength);
			}
			else if(status.contains("200") && contentType.startsWith("image")) {
				getImage(contentType, contentLength);
			} else if((status.contains("301") || status.contains("302") ||status.contains("307") ||status.contains("308")) && newLocation != null ) {
				getRedirection(newLocation,contentLength);
			} else  {
				if (status.contains("200"))
					System.out.println("Content-type: " + contentType + " Not implemented.");
				else
					System.out.println("Status not ok: " + status);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void post() {
		// TODO Auto-generated method stub

	}




}
