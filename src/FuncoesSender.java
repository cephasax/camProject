import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class FuncoesSender {

	public static int MAX_PACKETS = 255;

	/**
	 * Sends a byte array via multicast
	 * Multicast addresses are IP addresses in the range of 224.0.0.0 to
	 * 239.255.255.255.
	 * 
	 * @param imageData Byte array
	 * @param multicastAddress IP multicast address
	 * @param port Port
	 * @return <code>true</code> on success otherwise <code>false</code>
	 */
	public boolean sendImage(byte[] imageData, String multicastAddress,
			int port) {
		InetAddress ia;

		boolean ret = false;
		int ttl = 2;

		try {
			ia = InetAddress.getByName(multicastAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return ret;
		}

		MulticastSocket ms = null;

		try {
			ms = new MulticastSocket();
			ms.setTimeToLive(ttl);
			DatagramPacket dp = new DatagramPacket(imageData, imageData.length, ia, port);
			ms.send(dp);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (ms != null) {
					ms.close();
			}
		}
		return ret;
	}
	
	public boolean testeDeImagem(int packets){
		if(packets > MAX_PACKETS) {
			System.out.println("Image is too large to be transmitted!");
			return false;
		}
		else
			return true;
	}
}
