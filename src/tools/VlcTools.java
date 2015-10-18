package tools;

import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalSliderUI;

import frames.MainFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import videos.VidProject;
/*
 * A class for arbitrary methods related purely to the handling
 * of videos in the VLCj component in an attempt to keep
 * the main GUI's code more cohesive.
 * 
 * The main intention of this class was to remove any code possible from MainFrame
 * in order to maximum it's cohesive and minimise long chunks of code.
 */
public class VlcTools {
	/**
	 * Creates a new project, and resets the appropriate
	 * GUI components in MainFrame to their initial values.
	 */
	public static void createNewProject(){
		CheckSaves();
		MainFrame.mFrame.project = new VidProject(IOHandler.GetNewName(), false);
		MainFrame.mFrame.resetVideo();
	}
	
	/**
	 * Checks if there are any saves pending, and will 
	 * inform the user to see if they want to save
	 */
	public static void CheckSaves(){
		if (MainFrame.mFrame.project.PendingSaves()) { //Check if they'd like to save first
			int response = JOptionPane.showConfirmDialog(null, "Would you like to save your changes first?", "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION){ //they want to save
				MainFrame.mFrame.project.saveProject(); //save it
			}
		}
	}
	
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
						MainFrame.mFrame.theVideo.setTime(value * 100);
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
								new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/icons/muted.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else if (value < 50) { //Semi loud
						MainFrame.mFrame.btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/icons/volumelow.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else { //Loud icon!
						MainFrame.mFrame.btnVolume.setIcon(
								new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/icons/volume.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					}
					MainFrame.mFrame.theVideo.setVolume(value);
				}
			});
		}
		return slider;
	}
}
