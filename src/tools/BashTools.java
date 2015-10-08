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
 * This contains methods especially bash, and bash scripting.
 */
public class BashTools {

	static String festID = null;

	/**
	 * Adds a audio file to a video file.
	 * @param audioFile
	 * @param videoFile
	 * @return
	 */
	public static String addCustomAudio(File audioFile, File videoFile) {
		String mp3Path = audioFile.getAbsolutePath();
		String videoPath = videoFile.getAbsolutePath();

		JFileChooser jfc = FileTools.ReturnConfirmationChooser(true);
		FileTools.displayInfo("Choose somewhere to save your new video");
		
		if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return null; //they cancelled.
		
		File f = jfc.getSelectedFile(); //the file they selected
		if (f != null) {
			ProgressBarFrame.pbFrame.setLocationRelativeTo(null);
			ProgressBarFrame.pbFrame.setVisible(true); //show progress bar to the user
			
			(new AddAudio(mp3Path, videoPath, f)).execute(); //start the swingworker class to add it
			String outVidPath = f.getAbsolutePath();
			if (!FileTools.hasExtension(outVidPath)){
				outVidPath += ".avi";
			}
			return outVidPath;
		}
		return null;
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
			FileTools.displayError("Error using festival speech");
		}
		return pid;
	}
	
	/**
	 * This message takes in same text, and saves that text 
	 * as a spoken mp3 file, with the magic of festival and ffmpeg
	 * @param textToSave
	 */
	public static void saveFestToMP3(String textToSave) {
		String wavFullPath = IOHandler.TmpDirectory + "output.wav";
		String tmpTxtFullPath = IOHandler.TmpDirectory + "txtTmp.txt"; //get the save paths
		JFileChooser jfc = FileTools.ReturnConfirmationChooser(false);
		FileTools.displayInfo("Choose where to save the mp3 file");

		if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return;
		String mp3FullPath = jfc.getSelectedFile().getAbsolutePath(); //path of file selected
		
		if (!FileTools.hasExtension(mp3FullPath)){ //make sure it has an extension
			mp3FullPath += ".mp3";
		}

		FileTools.writeTextToFile(textToSave, "txtTmp.txt"); //save text to a file for use later in bash

		//Write the bash code
		ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c", "text2wave -o " + wavFullPath + " " + tmpTxtFullPath);
		ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + wavFullPath + " " + mp3FullPath);

		try {
			pb1.start().waitFor(); pb2.start(); //run the first process before the 2nd!
		} catch (Exception e) {
			FileTools.displayError("Error saving speech to MP3");
		}
		// deleting intermediate files (output.wav and txtTmp)
		String newVidPath = null; //for later processing
		FileTools.displayInfo("MP3 file saved to \n" + mp3FullPath); //mp3 saved, display where to

		if (CommentaryFrame.cmFrame.loadNewVideoIsChecked) {
			// Checkbox for auto load new video is checked
			if (newVidPath != null) {
				MainFrame.mFrame.theVideo.stop();
				MainFrame.mFrame.theVideo.prepareMedia(MainFrame.mFrame.chosenVideoPath = newVidPath);
			}
		}
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
}
