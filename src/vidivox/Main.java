	package vidivox;
	import java.awt.Dimension;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import vidivox.frames.MainFrame;
import vidivox.tools.IOHandler;
	
	public class Main {
	
		/**
		 * Entry point to the vidivox application, calls the MainFrame gui
		 * to make it visible
		 * @param args
		 */
		public static void main(String[] args) {
			(new NativeDiscovery()).discover();
			IOHandler.CheckPaths(); //make sure all the needed directories are created before any logic is run
			MainFrame.mFrame = new MainFrame(); //create the main frame and make it visible.
			MainFrame.mFrame.setMinimumSize(new Dimension(MainFrame.mFrame.getWidth(), MainFrame.mFrame.getHeight())); // Set min dimension to current
			MainFrame.mFrame.setLocationRelativeTo(null); //put it in the center
			MainFrame.mFrame.setVisible(true);
			//before any proper logic is run.
		}
	
	}
