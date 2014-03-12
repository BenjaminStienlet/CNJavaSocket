package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import support.Command;

/**
 * Class to execute the given HTTP command
 * 
 * @author 	Tom Stappaerts, Benjamin Stienlet
 */
@SuppressWarnings("deprecation")
public abstract class Http {

	protected String outputDir = "output";
	protected String outputFile = "received.html";

	protected String host;
	protected String resource;
	protected String ip;
	protected int port;
	protected Socket clientSocket;
	protected DataOutputStream outToServer;
	protected DataInputStream inFromServer;

	public Http(Command command, String host, String resource, String ip, int port) {
		try {
			// Create a new socket
			Socket socket = new Socket(ip, port);
			System.out.println("Socket estabilished: " + socket.toString() );
			initialize(command, host, resource, ip, port, socket);
		} catch (IOException e) {
			System.out.println(e.toString());
		} 
	}

	public Http(Command command, String host, String resource, String ip, int port, Socket socket) {
		initialize(command, host, resource, ip, port, socket);
	}

	private void initialize(Command command, String host, String resource, String ip, int port, Socket socket) {
		this.host = host;
		this.resource = resource;
		this.ip = ip;
		this.port = port;

		// Create the outputdir
		File file = new File(outputDir);
		if (! file.exists()) {
			file.mkdirs();
		}

		try {
			clientSocket = socket;
			if (clientSocket.isClosed()) {
				// Create a new socket
				clientSocket = new Socket(ip, port);
				System.out.println("Socket estabilished: " + clientSocket.toString() );
			}

			// Create the output- and inputstreams
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new DataInputStream(clientSocket.getInputStream());

			// Send the initial request line
			initialRequest(command);

			if (command.equals(Command.GET))
				get();
			else if(command.equals(Command.PUT))
				put();
			else if(command.equals(Command.POST))
				post();
			else if(command.equals(Command.HEAD))
				head();

			outToServer.close();
			inFromServer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 
	}

	/**
	 * Sends the initial request line
	 */
	protected abstract void initialRequest(Command command) throws IOException;
	
	protected abstract void put();
	
	protected abstract void get();
	
	protected abstract void head();
	
	protected abstract void post();
	
	protected abstract void executeGet(String host2, String resource2,String ip);

	/**
	 * Get-request for a html-file
	 */
	protected void getHtml(int contentLength) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		String sentence;
		String filename;

		if(contentLength > 0) {
			// Get the html file
			System.out.println("Getting HTML FILE");
			filename = outputDir + System.getProperty("file.separator") + outputFile;
			BufferedWriter outToFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));

			// Read bytes form server
			byte[] bytes = new byte[contentLength];
			inFromServer.readFully(bytes, 0, contentLength);
			sentence = new String(bytes, "UTF-8");
			
			// Write bytes to file
			outToFile.write(sentence);
			outToFile.close();
			System.out.println("Written to file: " + filename);

			// Getting the images out of the html.
			File input = new File(filename);
			Document doc = Jsoup.parse(input, "UTF-8");
			Elements imgs = doc.select("img");
			for (int i = 0; i < imgs.size(); i++) {
				// Get the uri of the image
				Element el = imgs.get(i);
				String uri = el.attr("src");
				System.out.println("Found image: " + uri);
				String hostImg;
				String resourceImg;
				String ipImg;
				try {
					// Get the host, resource and ip
					String[] parsed = Client.parseURI(uri);
					hostImg = parsed[0];
					resourceImg = parsed[1];
					ipImg = parsed[2];
				} catch(Exception e) {
					hostImg = host;
					resourceImg = uri;
					if(!resourceImg.startsWith("/")) {
						resourceImg = System.getProperty("file.separator") + resourceImg;
					}
					ipImg = ip;
				}
				//Getting images.
				executeGet(hostImg, resourceImg, ipImg);
			}


		} else {
			System.out.println("NO CONTENT LENGTH GIVEN, BAD SERVER.");
			//BufferSize not specified:
			@SuppressWarnings("unused")
			String sentence2;
			while((sentence2 = inFromServer.readLine()) != null) {
				//discard sentence
			}
		}

	}

	/**
	 * Get-request for an image
	 */
	protected void getImage(String contentType, int contentLength) {
		String filename;
		String imageType = contentType.split("/")[1].trim();
		if(imageType.trim().contains("jpeg") || imageType.trim().contains("png") || imageType.trim().contains("gif")) {

			filename = System.getProperty("user.dir") + System.getProperty("file.separator") + outputDir + resource;

			byte[] bytes = new byte[contentLength];

			try {
				// Read bytes
				inFromServer.readFully(bytes, 0, contentLength);
				File file = new File(filename);
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				// Write to bytes
				FileOutputStream writer = new FileOutputStream(file);
				writer.write(bytes);					
				writer.close();
				System.out.println("Image written to: " + filename);
			} catch(IOException e) {
				System.out.println(e.getMessage());
			}

			System.out.println("\nImage type: " + imageType);
			System.out.println("Image length: " + contentLength);
		} else {
			System.out.println("This client does not support the image type: "+imageType);
		}
	}

	/**
	 * Get-request for a redirection
	 */
	protected void getRedirection(String newLocation, int contentLength) {
		String host2;
		String resource2;
		String ip2;
		System.out.println("REDIRECTING \n");
		try {
			// Parse the uri
			String[] parsed = Client.parseURI(newLocation);
			host2 = parsed[0];
			resource2 = parsed[1];
			ip2 = parsed[2];
		} catch(Exception e) {
			host2 = host;
			resource2 = newLocation;
			if(!resource2.startsWith("/")) {
				resource2 = System.getProperty("file.separator") + resource2;
			}
			ip2 = ip;
		}
		//READ BUFFER for content-length:
		if(contentLength >0) {
			byte[] bytes = new byte[1024];
			try {
				inFromServer.readFully(bytes, 0, contentLength);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else {
			//BufferSize not specified:
			@SuppressWarnings("unused")
			String sentence;
			try {
				while((sentence = inFromServer.readLine()) != null) {
					//discard sentence
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		executeGet(host2, resource2, ip2);
	}

	/**
	 * Executes the head command
	 */
	protected void executeHead() throws IOException {
		//Write end of headers: \n
		outToServer.writeBytes("\n");
		System.out.println("Written output");
		//Getting status from client. If: 100 continue: continue
		String status = inFromServer.readLine();
		while (status.isEmpty() || status.trim().endsWith("100 Continue")) {
			if (!status.trim().isEmpty())
				System.out.println("Status: " + status);
			status = inFromServer.readLine();
		}
		

		System.out.println("Status:  " + status);
		System.out.println("Headers: \n");
		String sentence1;
		boolean close = false;
		//READING HEADERS
		while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
			System.out.println(sentence1);
			if (sentence1.trim().equalsIgnoreCase("Connection: close")) {
				close = true;
			}
		}
		//Close the socket if the client asks.
		if (close) {
			System.out.println("Socket closed by server");
			clientSocket.close();
		}
	}

	/**
	 * Handles the put request
	 * @throws IOException
	 */
	protected void executePut() throws IOException {
		//First: input the DATA that needs to be transmitted
		System.out.println("What do you want to send? (terminate with enter)");
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
		String input = "";
		String inputString;
		while(!(inputString = inFromUser.readLine()).equals("")) {
			input += " " + inputString;
		}
		input = input.trim();
		//Calculate headers
		outToServer.writeBytes("Content-Length: " + input.getBytes("utf-8").length + "\n\n");
		outToServer.writeBytes(input+"\n");
		System.out.println("Written output");
		
		//Read status from the server.
		String status = inFromServer.readLine();
		System.out.println("Status:  " + status);
		while (status.isEmpty() || status.trim().endsWith("100 Continue")) {
			System.out.println("Status: " + status);
			status = inFromServer.readLine();
		}
		//print headers
		System.out.println("Header: \n");
		String sentence1;
		boolean close = false;
		while((sentence1 = inFromServer.readLine()) != null && !sentence1.trim().isEmpty()) {
			System.out.println(sentence1);
			if (sentence1.trim().equalsIgnoreCase("Connection: close")) {
				close = true;
			}
		}
		//close if necessary
		if (close) {
			System.out.println("Socket closed by server");
			clientSocket.close();
		}
	}

	/**
	 * Handles a get request
	 * @param status
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	protected void executeGet(String status) throws IOException, UnsupportedEncodingException, FileNotFoundException {
				String sentence;
				String contentType = null;
				String newLocation = null;
				int contentLength = 0;
				boolean close = false;
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
					if (sentence.trim().equalsIgnoreCase("Connection: close")) {
						close = true;
					}
					System.out.println(sentence);
				}
				//Getting file information. 2 possibilities: html or image. Others are not supported.
				if(contentType.startsWith("text/html") && status.contains("200")) {
					this.getHtml(contentLength);
				//image
				} else if(contentType.startsWith("image") && status.contains("200")) {
					this.getImage(contentType, contentLength);
				//redirect
				} else if((status.contains("301") || status.contains("302") ||status.contains("307") ||status.contains("308")) && newLocation != null ) {
					this.getRedirection(newLocation,contentLength);
				//Handle other status codes / content types
				} else {
					//Status is ok: content-type was not supported
					if (status.contains("200"))
						System.out.println("Content-type: " + contentType + " Not implemented.");
					else
						//The status was not ok.
						System.out.println("Status not ok: " + status);
				}
				if (close) {
					System.out.println("Socket closed by server");
					clientSocket.close();
				}
			}
}
