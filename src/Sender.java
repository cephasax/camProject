import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;

public class Sender{
	
	public static int HEADER_SIZE = 8;
	public static int MAX_PACKETS = 255;
	public static int SESSION_START = 128;
	public static int SESSION_END = 64;
	public static int DATAGRAM_MAX_SIZE = 65507 - HEADER_SIZE;
	public static int MAX_SESSION_NUMBER = 255;

	/*
	 * The absolute maximum datagram packet size is 65507, The maximum IP packet
	 * size of 65535 minus 20 bytes for the IP header and 8 bytes for the UDP
	 * header.
	 */
	public static String OUTPUT_FORMAT = "jpg";
	public static int COLOUR_OUTPUT = BufferedImage.TYPE_INT_RGB;

	/* Default parameters */
	public static double SCALING = 0.5;
	public static int SLEEP_MILLIS = 500;
	public static String IP_ADDRESS =  "225.4.5.6";
	public static int PORT = 4444;
	public static boolean SHOW_MOUSEPOINTER = false;
	private static FuncoesSender funcSend;
	
	public static void main(String[] args) throws Exception {
		
		int sessionNumber = 0;
		funcSend = new FuncoesSender();
		
		Dimension[] nonStandardResolutions = new Dimension[] {
			WebcamResolution.PAL.getSize(),
			WebcamResolution.HD720.getSize(),
			new Dimension(2000, 1000),
			new Dimension(1000, 500),
		};

		Webcam webcam = Webcam.getDefault();
		webcam.setCustomViewSizes(nonStandardResolutions);
		webcam.setViewSize(WebcamResolution.HD720.getSize());
		webcam.open();

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);
		
		JFrame window = new JFrame("SERVIDOR - WEBCAM - MULTICAST");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
		
		try {
			while(true){
			//BufferedImage image;
			
			byte[] imageByteArray = WebcamUtils.getImageBytes(webcam, OUTPUT_FORMAT);
			
			/* Scale image */
			//image = funcSend.shrink(image, SCALING);
			int packets = (int) Math.ceil(imageByteArray.length / (float)DATAGRAM_MAX_SIZE);

			/* If image has more than MAX_PACKETS slices -> error */
			if(funcSend.testeDeImagem(packets) != true) {
				continue;
			}
			
			/* Loop through slices */
			for(int i = 0; i <= packets; i++) {
				int flags = 0;
				flags = i == 0 ? flags | SESSION_START: flags;
				flags = (i + 1) * DATAGRAM_MAX_SIZE > imageByteArray.length ? flags | SESSION_END : flags;

				int size = (flags & SESSION_END) != SESSION_END ? DATAGRAM_MAX_SIZE : imageByteArray.length - i * DATAGRAM_MAX_SIZE;

				/* Set additional header */
				byte[] data = new byte[HEADER_SIZE + size];
				data[0] = (byte)flags;
				data[1] = (byte)sessionNumber;
				data[2] = (byte)packets;
				data[3] = (byte)(DATAGRAM_MAX_SIZE >> 8);
				data[4] = (byte)DATAGRAM_MAX_SIZE;
				data[5] = (byte)i;
				data[6] = (byte)(size >> 8);
				data[7] = (byte)size;

				/* Copy current slice to byte array */
				System.arraycopy(imageByteArray, i * DATAGRAM_MAX_SIZE, data, HEADER_SIZE, size);
				/* Send multicast packet */
				funcSend.sendImage(data, IP_ADDRESS, PORT);

				/* Leave loop if last slice has been sent */
				if((flags & SESSION_END) == SESSION_END) break;
			}
			
			/* Sleep */
			Thread.sleep(SLEEP_MILLIS);
			
			/* Increase session number */
			sessionNumber = sessionNumber < MAX_SESSION_NUMBER ? ++sessionNumber : 0;
			
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

