import support.*;
import client.*;

public class Test {

	public static void main(String[] args) {
		String uri = "tweakers.net";
		int port = 80;
		Socket clientSocket = new Socket(uri,port);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes("GET / HTTP/1.0");
	}
	
}
