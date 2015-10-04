package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import frames.CommentaryFrame;
import frames.MainFrame;
import frames.ProgressBarFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/*
 * A class for arbitrary methods.
 * Keeping button logic clean and simple
 */
public class Tools {

	static EmbeddedMediaPlayerComponent mediaPlayerComponent = null;
	static File lastDir = null;
	static String festID = null;

	/**
	 * Simply opens and returns a file choosen by the user.
	 * @return
	 */
	public static File openFile() {
		JFileChooser jfc = Tools.ReturnConfirmationChooser(null);
		if (lastDir != null) {
			jfc.setCurrentDirectory(lastDir);
		}
		jfc.showOpenDialog(null);
		File f = jfc.getSelectedFile();
		if (f != null) {
			lastDir = f.getParentFile();
		}
		return f;
	}

	// displays the time in a nice format, no in milliseconds
	public static String LongToTime(long length) {
		length = length / 1000; // as was in ms, get it down to seconds
		String toReturn = "";
		int minutes = (int) (length / 60);
		int seconds = (int) (length % 60);
		if (minutes - 10 < 0)
			toReturn += "0";
		toReturn += minutes + ":";
		if (seconds - 10 < 0)
			toReturn += "0";
		toReturn += seconds;
		return toReturn;
	}

	/**
	 * Returns the inbuilt media player vlcj has
	 * @return
	 */
	public static EmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		if (mediaPlayerComponent == null) {
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		}
		return mediaPlayerComponent;

	}

	/**
	 * Displays a message
	 * @param msg
	 */
	public static void displayInfo(String msg) {
		JOptionPane.showMessageDialog(null, msg, "FYI", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays an error to the user
	 * @param msg
	 */
	public static void displayError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Adds a audio file to a video file.
	 * @param audioFile
	 * @param videoFile
	 * @return
	 */
	public static String addCustomAudio(File audioFile, File videoFile) {
		String mp3Path = audioFile.getAbsolutePath();
		String videoPath = videoFile.getAbsolutePath();

		JFileChooser jfc = Tools.ReturnConfirmationChooser(true);
		displayInfo("Choose somewhere to save your new video");
		
		if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return null; //they cancelled.
		
		File f = jfc.getSelectedFile(); //the file they selected
		if (f != null) {
			ProgressBarFrame.pbFrame.setLocationRelativeTo(null);
			ProgressBarFrame.pbFrame.setVisible(true); //show progress bar to the user
			
			(new addAudio(mp3Path, videoPath, f)).execute(); //start the swingworker class to add it
			String outVidPath = f.getAbsolutePath();
			if (!Tools.hasExtension(outVidPath)){
				outVidPath += ".avi";
			}
			return outVidPath;
		}
		return null;
	}

	/**
	 * opens a given mp3 file (that the user selects)
	 * and returns it
	 * @return
	 */
	public static File openMP3File() {
		JFileChooser jfc = Tools.ReturnConfirmationChooser(null);
		jfc.setSelectedFile(new File(IOHandler.Mp3Directory + "Audio"));
		if (lastDir != null) {
			jfc.setCurrentDirectory(lastDir);
		}
		//Limit to only mp3 now
		jfc.setFileFilter(new FileNameExtensionFilter("MP3 File", "mp3"));
		if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) return null; //they cancelled!
		if (jfc.getSelectedFile() != null) {
			lastDir = jfc.getSelectedFile().getParentFile();
		}
		return jfc.getSelectedFile();
	}

	/**
	 * Method which will speak text (with the use of festival)
	 * and bash
	 * @param textToSay
	 * @return
	 */
	public static int speakFestival(String textToSay) {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "echo " + "\"" + textToSay + "\" | festival --tts");
		int pid = 0;
		try {
			Process p1 = pb.start();
			Field f = p1.getClass().getDeclaredField("pid");
			f.setAccessible(true); //Using reflection to get the id
			pid = f.getInt(p1);
		} catch (Exception e) {
			displayError("Error using festival speech");
		}
		return pid;
	}

	/*
	 * Code for overwrite confirmation within JFileChooser. Retrieved from:
	 * http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
	 * On 23/9/15
	 */
	public static JFileChooser ReturnConfirmationChooser(Boolean isVideo){
		JFileChooser jfc = new JFileChooser(){
		    @Override
		    public void approveSelection(){
		        File f = getSelectedFile();
		        if(f.exists() && getDialogType() == SAVE_DIALOG){
		            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }
		        super.approveSelection();
		    }        
		};
		String saveTo = "";
		if (isVideo == null) return jfc; //generic file no extension
		else if (isVideo){ //saving a video file, thus give it a default name
			saveTo = IOHandler.VideoDirectory + "myVideo" + new File(IOHandler.VideoDirectory).listFiles().length + ".avi";
		} else { // mp3
			saveTo = IOHandler.Mp3Directory + "myAudio" + new File(IOHandler.Mp3Directory).listFiles().length + ".mp3";
		}
		jfc.setSelectedFile(new File(saveTo));
		return jfc;
		
	}
	
	/**
	 * A simple method which writes text to a file
	 * @param text
	 * @param fileName
	 */
	public static void writeTextToFile(String text, String fileName) {
		PrintWriter pw = null;
		try {
			(pw = new PrintWriter(IOHandler.TmpDirectory + fileName)).write(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pw.close();
	}
	
	/**
	 * This message takes in same text, and saves that text 
	 * as a spoken mp3 file, with the magic of festival and ffmpeg
	 * @param textToSave
	 */
	public static void saveFestToMP3(String textToSave) {
		String wavFullPath = IOHandler.TmpDirectory + "output.wav";
		String tmpTxtFullPath = IOHandler.TmpDirectory + "txtTmp.txt"; //get the save paths
		JFileChooser jfc = Tools.ReturnConfirmationChooser(false);
		displayInfo("Choose where to save the mp3 file");

		if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return;
		String mp3FullPath = jfc.getSelectedFile().getAbsolutePath(); //path of file selected
		
		if (!hasExtension(mp3FullPath)){ //make sure it has an extension
			mp3FullPath += ".mp3";
		}

		writeTextToFile(textToSave, "txtTmp.txt"); //save text to a file for use later in bash

		//Write the bash code
		ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c", "text2wave -o " + wavFullPath + " " + tmpTxtFullPath);
		ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + wavFullPath + " " + mp3FullPath);

		try {
			pb1.start().waitFor(); pb2.start(); //run the first process before the 2nd!
		} catch (Exception e) {
			displayError("Error saving speech to MP3");
		}
		// deleting intermediate files (output.wav and txtTmp)
		String newVidPath = null; //for later processing
		displayInfo("MP3 file saved to \n" + mp3FullPath); //mp3 saved, display where to
		if (CommentaryFrame.chckbxApplyThisSpeech.isSelected()) {
			// Checkbox is selected upon save button click
			if (MainFrame.mFrame.chosenVideoPath != null) {
				newVidPath = addCustomAudio(new File(mp3FullPath), new File(MainFrame.mFrame.chosenVideoPath));
			} else {
				displayError("You need a video opened first to add speech to it");
			}
		}

		if (CommentaryFrame.cmFrame.loadNewVideoIsChecked) {
			// Checkbox for auto load new video is checked
			if (newVidPath != null) {
				MainFrame.mFrame.theVideo.stop();
				MainFrame.mFrame.theVideo.prepareMedia(MainFrame.mFrame.chosenVideoPath = newVidPath);
			}
		}
	}
	
	/**
	 * Takes in a string s and determines whether of not it contains an extension
	 * ie test.avi (based on assumping the filename doesnt contain an . already)
	 * @param s
	 * @return
	 */
	public static boolean hasExtension(String s){
		return (s.split(File.separator)[s.split(File.separator).length - 1].contains("."));
	}

	/**
	 * Even a process ID, this will kill the script running via bash.
	 * @param pid
	 */
	public static void killAllFestProc(int pid) {

		if (pid != 0) {
			String cmd = "pstree -p " + pid;
			/*
			 * Using the pstree bash command, we find the process named "aplay"
			 * associated with festival which is what makes the sound. The pid
			 * of this process is found and killed using the "kill -9" command
			 */
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
			String line = null;
			String s = "";

			try {
				Process p = pb.start();
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				while ((line = br.readLine()) != null) s += line; //read in the output
				
				String sub = s.substring(s.indexOf("play")); //"play" corresponds to the festival id
				String result = "";
				for (int i = 0; i < sub.length(); i++) {
					Character character = sub.charAt(i);
					if (character.toString().equals(")")) break; //loop till end of process id bracket is found
					if (Character.isDigit(character)) {
						result += character;
					}
				}
				(new ProcessBuilder("/bin/bash", "-c", "kill -9 " + result)).start(); //Kill the id!

			} catch (IOException e) {
				System.out.println("Error killing process: " + pid);
			}
		}

	}
	
	/**
	 * A simple JDialog to put up, with a message 'msg' with a yes/no option!
	 * @param msg
	 * @return
	 */
	public static boolean doYesNoDialog(String msg){
		return (JOptionPane.showConfirmDialog(null, msg, "Warning!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}
}
