package vidivox.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;

import vidivox.frames.MainFrame;
import vidivox.tools.workers.AudioWorker;

/*
 * A class for arbitrary methods.
 * Keeping button logic clean and simple
 * This contains methods especially bash, and bash scripting.
 */
public class BashTools {

	static String festID = null;

	/**
	 * Adds a audio file to a video file.
	 * 
	 * @param audioFile
	 * @param videoFile
	 * @return
	 */
	public static void createVideo() {
		(new AudioWorker(MainFrame.mFrame.VProject())).execute();
	}

	/**
	 * Method which will speak text (with the use of festival) and bash
	 * 
	 * @param textToSay
	 * @return
	 */
	public static void speakFestival(String textToSay, String voice) {
		saveFestToMP3(textToSay, voice, true); //save it first
		speakMp3(IOHandler.TmpDirectory() + "output.wav"); //speak it
	}
	
	/**
	 * Method which will play an mp3 file through bash
	 * 
	 * @param textToSay
	 * @return
	 */
	public static void speakMp3(String filePath) {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "cvlc " + filePath);
		System.out.println("cvlc " + filePath);
		try {
			pb.start();
		} catch (Exception e) {
			FileTools.displayError("Error playing the mp3 file");
		}
	}


	/**
	 * This message takes in same text, and saves that text as a spoken mp3
	 * file, with the magic of festival and ffmpeg
	 * The voice is an option which the user can choose.
	 * 
	 * tmp represents whether or not it should be saved, or just created to preview
	 * @param textToSave
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static double saveFestToMP3(String textToSave, String voice, boolean tmp) {
		String wavFullPath = IOHandler.TmpDirectory() + "output.wav";
		String tmpTxtFullPath = IOHandler.TmpDirectory() + "txtTmp.txt"; //tmp directories
		String mp3FullPath = "";
		if (!tmp){ //if its not a tmp file, let the user pick where to save it
			JFileChooser jfc = FileTools.ReturnConfirmationChooser(false);
			FileTools.displayInfo("Choose where to save the mp3 file");
	
			if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
				return -1;
			mp3FullPath = jfc.getSelectedFile().getAbsolutePath(); 
			if (!FileTools.hasExtension(mp3FullPath)) { // make sure it has an	extension
				mp3FullPath += ".mp3";
			}
		}
		FileTools.writeTextToFile(textToSave, "txtTmp.txt");

		// Write the bash code
		ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c",
				"text2wave -o " + wavFullPath + " " + tmpTxtFullPath + " -eval \"" + voice + "\"");
		ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + wavFullPath + " " + mp3FullPath);

		try {
			pb1.start().waitFor();
			if (tmp) return 0; //can end here as we can play the wav file
			pb2.start(); // run the first process before the 2nd!
		} catch (Exception e) {
			FileTools.displayError("Error saving speech to MP3");
		}

		File f = new File(wavFullPath); // get the length in (s) of the wav file
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(f);
		} catch (Exception e) {
		}
		AudioFormat format = audioInputStream.getFormat();
		long frames = audioInputStream.getFrameLength();
		double ret = (frames + 0.0) / format.getFrameRate();
		try {
			audioInputStream.close(); //try to close it
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Even a process ID, this will kill the script running via bash.
	 * 
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

				while ((line = br.readLine()) != null)
					s += line; // read in the output

				String sub = s.substring(s.indexOf("play")); // "play"
																// corresponds
																// to the
																// festival id
				String result = "";
				for (int i = 0; i < sub.length(); i++) {
					Character character = sub.charAt(i);
					if (character.toString().equals(")"))
						break; // loop till end of process id bracket is found
					if (Character.isDigit(character)) {
						result += character;
					}
				}
				(new ProcessBuilder("/bin/bash", "-c", "kill -9 " + result)).start(); // Kill
																						// the
																						// id!

			} catch (IOException e) {
				System.out.println("Error killing process: " + pid);
			}
		}

	}
}
