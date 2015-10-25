package vidivox.frames.mainframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import vidivox.frames.MainFrame;
import vidivox.tools.FileTools;

/***
 * This returns the JMenuItem for the help menu.
 * This has been sepearted from the MainGUI class
 * as it does not interract with any other compnonents
 * and thus can be seperated. (static text)
 * @author andrew
 *
 */
public class HelpMenu {
	
	/**
	 * Returns the JMenu with all the components
	 * pre-added.
	 */
	public static JMenu getHelpMenu(){
		JMenu help = new JMenu("Help");
		
		JMenuItem about = new JMenuItem("About"), faq = new JMenuItem("FAQ");
		
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.mFrame.pauseVideo(); //pause the vid
				FileTools.displayInfo("<html>Vidivox, 2015<br>Version: 2.1<br><br>Built by Andrew Leach<br>SOFTENG206</html>");
				MainFrame.mFrame.btnPlay.doClick(); //resume afterwads
			}
		});
		
		faq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.mFrame.pauseVideo(); //pause the vid 
				//Shows a collection of frequently asked questions with answers.
				FileTools.displayInfo("<html><b>None of the buttons are enabled, what's wrong?</b><br>"
						+ "There is no video loaded, load one under the video tab<br>"
						+ "<br><b>It's only letting me save my project, what's wrong?</b><br>"
						+ "You can save the created video under video with Export Video<br>"
						+ "<br><b>The process of merging audio takes forever, what's wrong?</b><br>"
						+ "The process takes a long time, please be patient!<br><br>Built by Andrew Leach<br>SOFTENG206</html>");
				MainFrame.mFrame.btnPlay.doClick();
			}
		});
		
		help.add(faq);
		help.add(about); //add the two items
		
		return help;
	}

}
