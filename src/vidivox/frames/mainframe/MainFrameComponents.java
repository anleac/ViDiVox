package vidivox.frames.mainframe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import vidivox.frames.MainFrame;

/**
 * Essentially is a factory method class;
 * contains a static void which returns a slider for the MainFrame gui
 * depending on the given arguements. This helps to remove poor cohesion inside
 * MainFrame and reduce total code.
 * 
 * Any complex JCompontents (ie will lengthy action lisnters) will be placed here
 * for ease of location.
 * @param args
 */

public class MainFrameComponents {

	/**
	 * Creates a slider which will be used for the time of the video, AND the volume.
	 * We're creating it here to save code re-use, as it needs an extra overload
	 * to ensure that the slider will track to the location when clicked on.
	 * @return
	 * @isTime represents whether it will be the 'time' slider, as this is a special case.
	 * Therefore depending on whether its a time slider or not, will set up special properties 
	 * for both cases.
	 */
	public static JSlider CreateSlider(boolean isTime){
		final JSlider slider = new JSlider(JSlider.HORIZONTAL);
		slider.setUI(new MetalSliderUI() {
			protected void scrollDueToClickInTrack(int direction) {
				//ensures it will track where we click (overriding default behaviour)
				int value = slider.getValue();
				if (slider.getOrientation() == JSlider.HORIZONTAL) {
					value = this.valueForXPosition(slider.getMousePosition().x);
				} else if (slider.getOrientation() == JSlider.VERTICAL) {
					value = this.valueForYPosition(slider.getMousePosition().y);
				}
				slider.setValue(value);
			}
		});
		if (isTime){ //will be the time slider
			slider.setValue(0); //initially at the start
			slider.setPreferredSize(new Dimension(833 - 20, 20));
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					//Allows the slider to track properly when changing.
					JSlider timeSlider = (JSlider) evt.getSource();
					int value = timeSlider.getValue();
					if (timeSlider.getValueIsAdjusting()) {
						MainFrame.mFrame.Video().setTime(value * 100);
					}
				}
			});
		}else{ //it will be the volume slider, lets set up their icons
			slider.setMaximum(100);
			slider.setValue(100); //intiially max volume
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					JSlider timeSlider = (JSlider) evt.getSource();
					int value = timeSlider.getValue();
					if (value == 0) { //Show the 'mute' icon
						MainFrame.mFrame.btnVolume.setIcon(
								new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/muted.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else if (value < 50) { //Semi loud
						MainFrame.mFrame.btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/volumelow.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else { //Loud icon!
						MainFrame.mFrame.btnVolume.setIcon(
								new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/volume.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					}
					MainFrame.mFrame.Video().setVolume(value);
				}
			});
		}
		return slider;
	}
	
	/**
	 * JButton is a complex JComponent with dynamically changing icons.
	 * Moved into here to remove cluttered code.
	 * @return
	 */
	public static JButton createVolumeButton(){
		final JButton btnVolume = new JButton("");
		btnVolume.setBackground(Color.WHITE); //INitlize, set initial colour and icon
		btnVolume.setIcon(new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/volume.png"))).getImage())
				.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
		btnVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (MainFrame.mFrame.IsMuted()) {
					int value = MainFrame.mFrame.VolSlider().getValue();
					if (value == 0) { //Make sure it shows the correct icon
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/muted.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else if (value < 50) {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/volumelow.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/volume.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					}
					MainFrame.mFrame.setMute(false);
				} else {
					btnVolume.setIcon(
							new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/vidivox/icons/muted.png"))).getImage())
									.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					MainFrame.mFrame.setMute(true); // toggle the mute..
				}
				MainFrame.mFrame.VolSlider().setEnabled(MainFrame.mFrame.IsMuted()); MainFrame.mFrame.Video().mute(MainFrame.mFrame.IsMuted());
			}
		});
		return btnVolume;
	}

}
