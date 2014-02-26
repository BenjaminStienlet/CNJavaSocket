
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Test {

	public static void main(String[] args) throws IllegalArgumentException, UnknownHostException {
		
		String uri = "http://www.tweakers.net/index.html";
		//host = tweakers.net
		//resource = /index.html
		String[] result = parseURI(uri);
		System.out.println("Host: " + result[0] + "\nResource: " + result[1] + "\nIP: " + result[2]);
	}
	
	/**
	 * result[0] = host
	 * result[1] = resource
	 * result[2] = ip
	 */
	public static String[] parseURI(String uri) throws IllegalArgumentException, UnknownHostException {
		String[] result = new String[3];
		String ipRegex = "^((\\d{1,3}\\.){3}\\d{1,3})(/\\S*)?$";
		String hostRegex1 = "^(http:\\/\\/)?(www\\.)?([^\\/]*)$";
		String hostRegex2 = "^(http:\\/\\/)?(www\\.)?([^\\/]*)(/\\S*)$";
		
		if(uri.matches(ipRegex)) {
			result[2] = uri.replaceAll(ipRegex, "$1");
			result[1] = uri.replaceAll(ipRegex, "$3");
			result[0] = result[2];
		}
		else if(uri.matches(hostRegex1)) {
			result[0] = uri.replaceAll(hostRegex1, "$3");
			result[1] = "/";
			result[2] = InetAddress.getByName(result[0]).getHostAddress();
		}
		else if(uri.matches(hostRegex2)) {
			result[0] = uri.replaceAll(hostRegex2, "$3");
			result[1] = uri.replaceAll(hostRegex2, "$4");
			result[2] = InetAddress.getByName(result[0]).getHostAddress();
		}
		else {
			throw new IllegalArgumentException();
		}
		return result;
	}
 	
}
