package vidivox.frames.mainframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import vidivox.frames.MainFrame;
import vidivox.tools.FileTools;

/**
 * The action listener which supports the videotracking
 * in MainGui.
 * @author andrew
 *
 */
public class VideoTrackActionListener implements ActionListener {

	/**
	 * This timer performs such to track the video 
	 * (if its in reverse/fastforward) and to update
	 * the current time on the MainGui
	 * @return
	 */

			@Override
			public void actionPerformed(ActionEvent ae) {
				if (MainFrame.mFrame.VProject().videoLoaded()) {
					if (MainFrame.mFrame.Video().getMediaMeta().getLength() > 0) {
						MainFrame.mFrame.TimeSlider().setMaximum((int)(MainFrame.mFrame.Video().getMediaMeta()
								.getLength() / 100));
					}
					MainFrame.mFrame.LengthTime().setText(FileTools.LongToTime(MainFrame.mFrame.Video().getMediaMeta().getLength()));
					if (MainFrame.mFrame.videoPlayRate > 1f) {
						if (MainFrame.mFrame.reverse) { // This reverses or fast forwrads
							MainFrame.mFrame.Video().setTime(MainFrame.mFrame.Video().getTime()
									- (int) ((MainFrame.mFrame.videoPlayRate / (float) 5f) * 1000));
							if (MainFrame.mFrame.Video().getTime() <= 0) {
								MainFrame.mFrame.videoPlayRate = 1f;
								MainFrame.mFrame.reverse = false;
							}
						} else {
							MainFrame.mFrame.Video().setTime(MainFrame.mFrame.Video().getTime()
									+ (int) ((MainFrame.mFrame.videoPlayRate / (float) 5f) * 1000));
						}
					}
				}
			}
	
	
}
