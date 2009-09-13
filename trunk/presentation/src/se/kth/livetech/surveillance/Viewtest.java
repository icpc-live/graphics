package se.kth.livetech.surveillance;
import java.io.IOException;


public class Viewtest {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VncCamViewer viewer = null;
		try {
			viewer = new VncCamViewer();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i = 0; i < 2; ++i) {
			switch(i % 2) {
			case 0:
				viewer.startVnc("130.237.223.184", 5900);
				break;
			case 1:
				viewer.startWebcam("130.237.223.184", 8080);
				break;
			}
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		viewer.stop();
	}

}
