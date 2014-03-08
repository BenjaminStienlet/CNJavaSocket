package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import support.Command;

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
		outputSentence += "\n" + "Host: " + host + ":" + port;
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
			String filename = null;
			String contentType = null;
			int contentLength = -1;
			//Getting headers.
			while((sentence = inFromServer.readLine()) != null && !sentence.trim().isEmpty()) {
				if(sentence.startsWith("Content-Type:")) {
					contentType = sentence.split("Content-Type: ")[1].trim();
				}
				if(sentence.startsWith("Content-Length:")) {
					contentLength = Integer.parseInt(sentence.split("Content-Length: ")[1].trim());
				}
				System.out.println(sentence);
			}
			//Getting file information. 2 possibilities: html or image. Others are not supported.
			if(contentType.startsWith("text/html")) {
				System.out.println("Getting HTML FILE");
				filename = "received.html";
				BufferedWriter outToFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
				while((sentence = inFromServer.readLine()) != null && sentence.getBytes().length <= contentLength) {
					outToFile.write(sentence);
					contentLength -= sentence.getBytes().length;
				}
				outToFile.close();
				System.out.println("Written to file: " + filename);
				System.out.println("closing socket");
				clientSocket.close();

				//Getting the images out of the html.
				File input = new File(filename);
				Document doc = Jsoup.parse(input, "UTF-8");
				Elements imgs = doc.select("img");
				for (int i = 0; i < imgs.size(); i++) {
					Element el = imgs.get(i);
					String uri = el.attr("src");
					System.out.println("Found image: " + uri);
					String hostImg;
					String resourceImg;
					String ipImg;
					try {
						String[] parsed = Client.parseURI(uri);
						hostImg = parsed[0];
						resourceImg = parsed[1];
						ipImg = parsed[2];
					} catch(Exception e) {
						hostImg = host;
						resourceImg = uri;
						if(!resourceImg.startsWith("/")) {
							resourceImg = "/"+resourceImg;
						}
						ipImg = ip;
					}
					//Getting images.
					executeGet(hostImg, resourceImg, ipImg);
				}

			} else if(contentType.startsWith("image")) {
				String imageType = contentType.split("/")[1].trim();
				if(imageType.equals("jpeg") || imageType.equals("png") || imageType.equals("gif")) {
					//making new unexisting filename for the image. Limit on 100 for safety purposes.
//					for(int i = 0;i<100;i++) {
//						filename = "received"+i+"."+imageType;
//						File f = new File(filename);
//						if(!f.exists() && !f.isDirectory()) {
//							break;
//						}
//					}
//					String receivedFileString = "";
//					while((sentence = inFromServer.readLine()) != null) {
//						receivedFileString += sentence;
//					}
//					byte[] bytes = receivedFileString.getBytes(Charset.forName("ISO-8859-15"));
//					BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
//					FileOutputStream fos = new FileOutputStream(filename);
//					fos.write(bytes);
//					fos.close();
//					System.out.println("Image written to: " + filename);
					System.out.println("\nImage type: " + imageType);
					System.out.println("Image length: " + contentLength);
				} else {
					System.out.println("This client does not support the image type: "+imageType);
				}
				System.out.println("closing socket");
				clientSocket.close();

				
			} else  {
				System.out.println("Content-type: " + contentType + " Not implemented.");
				clientSocket.close();
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	private void executeGet(String hostImg, String resourceImg, String ipImg) {
		if (!ipImg.equals(ip)) {
			try {
				clientSocket.close();
				clientSocket = new Socket(ipImg, port);
				ip = ipImg;
				host = hostImg;
				resource = resourceImg;
				System.out.println("Socket estabilished: " + clientSocket.toString());
				initialRequest(Command.GET);
				get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void head() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void post() {
		// TODO Auto-generated method stub
		
	}

	
	

//	if(toString().equals("HTTP/1.1")) {
//		outputSentence += "\n HOST: " + host;
//	}
}
