package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import support.Command;

@SuppressWarnings("deprecation")
public class Http10 extends Http{


	/**
	 * @param command
	 * @param host
	 * @param resource
	 * @param ip
	 * @param port
	 */
	public Http10(Command command, String host, String resource, String ip, int port) {
		super(command, host, resource, ip, port);
	}


	@Override
	public String toString() {
		return "HTTP/1.0";
	}

	@Override
	protected void initialRequest(Command command) throws IOException {
		String outputSentence = command + " " + resource + " " + toString();
		System.out.println("Input: " + outputSentence);
		outToServer.writeBytes(outputSentence + "\n");
	}

	@Override
	public void put() {
		try{
			System.out.println("What do you want to send? (terminate with enter)");
			BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
			String input = "";
			String inputString;
			while(!(inputString = inFromUser.readLine()).equals("")) {
				input += " " + inputString;
			}
			input = input.trim();
			outToServer.writeBytes("Content-Length: " + input.getBytes().length + "\n\n");
			outToServer.writeBytes(input+"\n");

			System.out.println("Written output");
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			String sentence1;
			while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
				System.out.println(sentence1);
			}
			if(!clientSocket.isClosed()) {
				System.out.println("closing socket");
				clientSocket.close();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public void get() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			String status = inFromServer.readLine();
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
			if(contentType.startsWith("text/html") && status.contains("200")) {
				this.getHtml(contentLength);
			} else if(contentType.startsWith("image") && status.contains("200")) {
				this.getImage(contentType, contentLength);
			} else if((status.contains("301") || status.contains("302") ||status.contains("307") ||status.contains("308")) && newLocation != null ) {
				this.getRedirection(newLocation,contentLength);

			} else {
				if (status.contains("200"))
					System.out.println("Content-type: " + contentType + " Not implemented.");
				else
					System.out.println("Status not ok: " + status);
			}
			
			if(!clientSocket.isClosed()) {
				System.out.println("closing socket");
				clientSocket.close();
			}



		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}


	@Override
	public void head() {
		try{
			outToServer.writeBytes("\n");
			System.out.println("Written output");
			String status = inFromServer.readLine();
			System.out.println("Status:  " + status);
			System.out.println("Header: \n");
			String sentence1;
			while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
				System.out.println(sentence1);
			}

			System.out.println("closing socket");
			clientSocket.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public void post() {
		put();
	}


	@Override
	protected void executeGet(String host2, String resource2, String ip2) {
		try {
			clientSocket.close();
			System.out.println("Socket closed");
		} catch (IOException e) {
		}
		new Http10(Command.GET,host2,resource2,ip2,this.port);
	}



}
