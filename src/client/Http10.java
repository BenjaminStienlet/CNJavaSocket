package client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import support.Command;

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

			System.out.println("closing socket");
			clientSocket.close();

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
			String filename = null;
			String contentType = null;
			String contentLength = null;
			//Getting headers.
			while((sentence = inFromServer.readLine()) != null && !sentence.trim().isEmpty()) {
				if(sentence.startsWith("Content-Type:")) {
					contentType = sentence.split("Content-Type: ")[1].trim();
				}
				if(sentence.startsWith("Content-Length:")) {
					contentLength = sentence.split("Content-Length: ")[1].trim();
				}
				System.out.println(sentence);
			}
			//Getting file information. 2 possibilities: html or image. Others are not supported.
			if(contentType.startsWith("text/html")) {
				System.out.println("Getting HTML FILE");
				filename = "received.html";
				BufferedWriter outToFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
				while((sentence = inFromServer.readLine()) != null) {
					outToFile.write(sentence);
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
					new Http10(Command.GET, hostImg, resourceImg, ipImg, this.port);
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
//					byte[] bytes = receivedFileString.getBytes(Charset.forName("UTF-8"));
//					BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
//					FileOutputStream fos = new FileOutputStream(filename);
//					fos.write(bytes);
//					fos.close();
//					System.out.println("Image written to: " + filename);
					System.out.println("\nImage type: " + imageType);
					System.out.println("Image length: " + contentLength);
//					System.out.println("Byte-array length: " + bytes.length);
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



}
