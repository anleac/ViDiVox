package vidivox.frames.mainframe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import vidivox.frames.MainFrame;
import vidivox.frames.ProgressBarFrame;
import vidivox.tools.FileTools;

/**
 * The action listener which supports the update of the gui
 * in MainGui.
 * @author andrew
 *
 */
public class GuiUpdaterActionListener implements ActionListener {

	/**
	 * This timer performs such to track the video 
	 * (if its in reverse/fastforward) and to update
	 * the current time on the MainGui
	 * @return
	 */

			@Override
			public void actionPerformed(ActionEvent ae) {
				boolean vL = MainFrame.mFrame.VProject().videoLoaded(); //is it loaded?
				MainFrame.mFrame.mnExport.setEnabled(vL);
				String title = MainFrame.mFrame.DEFAULT_NAME + "  -  " + MainFrame.mFrame.VProject().getName();
				if (MainFrame.mFrame.VProject().PendingSaves())
					title += " *";
				if (!MainFrame.mFrame.warning.equals(""))
					title += " (" + MainFrame.mFrame.warning + ")";
				MainFrame.mFrame.setTitle(title); // update the title frequently
				MainFrame.mFrame.btnPlay.setEnabled(vL);
				MainFrame.mFrame.btnStop.setEnabled(vL);
				MainFrame.mFrame.btnFastforward.setEnabled(vL);
				MainFrame.mFrame.btnReverse.setEnabled(vL);
				MainFrame.mFrame.btnAddCommentary.setEnabled(vL); //Make sure everything is disabled
				//that needs to be when a video isnt loaded
				MainFrame.mFrame.btnVolume.setEnabled(vL);
				MainFrame.mFrame.TimeSlider().setEnabled(vL);
				MainFrame.mFrame.VolSlider().setEnabled(vL && !MainFrame.mFrame.IsMuted());
				MainFrame.mFrame.warning = (vL) ? "" : "No video loaded";
				if (vL) { //If the video is loaded
					String t = "Playspeed: ";
					if (MainFrame.mFrame.reverse)
						t += '-';
					t += (int) (MainFrame.mFrame.videoPlayRate) + "x (Limit: 16x speed)";
					MainFrame.mFrame.LblPlaySpeed().setText(t); // Update the play speed label
					MainFrame.mFrame.CurrentTime().setText(FileTools.LongToTime(MainFrame.mFrame.Video().getTime()));
					int iPos = (int) (MainFrame.mFrame.Video().getTime() / 100);
					if (iPos + 5 >= MainFrame.mFrame.TimeSlider().getMaximum()) { 
						MainFrame.mFrame.Video().setTime(0);
						MainFrame.mFrame.btnPlay.doClick();
					}
					if (MainFrame.mFrame.Video().getMediaMeta().getLength() > 0) {
						MainFrame.mFrame.TimeSlider().setValue(iPos);
					}
					MainFrame.mFrame.LblVideo().setText("Video loaded: " + MainFrame.mFrame.VProject().getVideoName());
				}else{
					//video isnt loaded!
					MainFrame.mFrame.CurrentTime().setText("00:00"); //should always by this
					MainFrame.mFrame.LengthTime().setText("00:00"); //and 'no' length
					if (!ProgressBarFrame.pbFrame.isVisible()) MainFrame.mFrame.LblVideo().setText("Please load a video under the video menu above");
					//as if it is visible, it means the swing worker is in progress.
				}
				MainFrame.mFrame.LblVideo().setForeground((vL) ? Color.BLACK : Color.RED);
				if (MainFrame.mFrame.startVideo) {
					MainFrame.mFrame.VProject().Created();
					// theVideo.prepareMedia(project.getVideo()); //load new one
					MainFrame.mFrame.btnPlay.doClick();
					if (MainFrame.mFrame.VideoPlaying())
						MainFrame.mFrame.startVideo = false;
				}
			}
	
}
