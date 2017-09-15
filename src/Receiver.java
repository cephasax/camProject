import javax.swing.JFrame;
import javax.swing.JWindow;

public class Receiver{
	
	/* Flags and sizes */
	public static int HEADER_SIZE = 8;
	public static int SESSION_START = 128;
	public static int SESSION_END = 64;
	/* Default values */
	public static String IP_ADDRESS = "225.4.5.6";
	public static int PORT = 4444;

	JFrame frame;
	boolean fullscreen = false;
	JWindow fullscreenWindow = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/* Handle command line arguments */
		switch (args.length) {
		case 2:
			IP_ADDRESS = args[1];
		case 1:
			PORT = Integer.parseInt(args[0]);
		}

		FuncoesReceiver funcRec = new FuncoesReceiver();
		funcRec.receiveImages(IP_ADDRESS, PORT);
	}
}