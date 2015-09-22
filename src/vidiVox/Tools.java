package vidiVox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/*
 * A class for arbitrary methods.
 * Keeping button logic clean and simple
 */
public class Tools {

	static EmbeddedMediaPlayerComponent mediaPlayerComponent = null;
	static File lastDir = null;
	static String festID = null;

	public static File openFile() {

		JFileChooser jfc = new JFileChooser();
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

	public static EmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		if (mediaPlayerComponent == null) {
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		}
		return mediaPlayerComponent;

	}

	public static void displayInfo(String msg) {
		JOptionPane.showMessageDialog(null, msg, "FYI", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void displayError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static String addCustomAudio(File audioFile, File videoFile) {
		// Is forced to be mp3 from file chooser
		// Brief says audio is to be automatically added to beginning of video

		String mp3Path = audioFile.getAbsolutePath();
		String videoPath = videoFile.getAbsolutePath();

		JFileChooser jfc = new JFileChooser();
		displayInfo("Choose somewhere to save your new video");
		jfc.showSaveDialog(null);

		File f = jfc.getSelectedFile();

		if (f != null) {
			String outVidPath = f.getAbsolutePath();
			String cmd = "ffmpeg -i " + videoPath + " -i " + mp3Path + " -map 0:v -map 1:a " + outVidPath;
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

			try {
				ProgressBarFrame.pbFrame.setLocationRelativeTo(null);
				ProgressBarFrame.pbFrame.setVisible(true);
				Process process = pb.start();
				process.waitFor();
				ProgressBarFrame.pbFrame.setVisible(false);
				displayInfo("Done!");
				return outVidPath;
			} catch (Exception e) {
				displayError("Error adding audio to video");
			}
			
		}
		return null;
	}

	public static File openMP3File() {
		JFileChooser jfc = new JFileChooser();
		if (lastDir != null) {
			jfc.setCurrentDirectory(lastDir);
		}
		// Same as other file chooser, but only allows mp3 files
		FileFilter ff = new FileNameExtensionFilter("MP3 File", "mp3");

		jfc.setFileFilter(ff);
		jfc.showOpenDialog(null);

		File f = jfc.getSelectedFile();
		if (f != null) {
			lastDir = f.getParentFile();
		}
		return f;

	}

	public static int speakFestival(String textToSay) {

		String cmd = "echo " + "\"" + textToSay + "\" | festival --tts";

		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		int pid = 0;
		try {

			Process p1 = pb.start();
			Field f = p1.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			pid = f.getInt(p1);
			

		} catch (Exception e) {
			displayError("Error using festival speech");
			return pid;
		}
		return pid;
	}

	public static void writeTextToFile(String text, String fileName) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(IOHandler.TmpDirectory + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pw.write(text);
		pw.close();

	}

	public static void saveFestToMP3(String textToSave) {
		IOHandler.CheckPaths();

		String wavFullPath = IOHandler.TmpDirectory + "output.wav";
		String tmpTxtFullPath = IOHandler.TmpDirectory + "txtTmp.txt";
		int numFiles = new File(IOHandler.Mp3Directory).listFiles().length;
		JFileChooser jfc = new JFileChooser();
		displayInfo("Choose where to save the mp3 file");
		jfc.showSaveDialog(null);
		File mp3 = jfc.getSelectedFile();
		if (mp3 == null){
			return;
		}
		String mp3FullPath = mp3.getAbsolutePath();

		writeTextToFile(textToSave, "txtTmp.txt");

		String cmd1 = "text2wave -o " + wavFullPath + " " + tmpTxtFullPath;
		String cmd2 = "ffmpeg -i " + wavFullPath + " " + mp3FullPath;

		ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c", cmd1);
		ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", cmd2);

		try {
			Process process1 = pb1.start();
			process1.waitFor();
			pb2.start();
		} catch (Exception e) {
			displayError("Error saving speech to MP3");
		}
		// deleting intermediate files (output.wav and txtTmp)
		File outputMP3 = new File(mp3FullPath);
		String newVidPath = null;
		displayInfo("MP3 file saved to \n" + outputMP3.getAbsolutePath());
		if (CommentaryFrame.chckbxApplyThisSpeech.isSelected()) {
			String vidPath = MainFrame.mFrame.chosenVideoPath;
			// Checkbox is selected upon save button click
			if (vidPath != null) {
				File videoFile = new File(vidPath);
				newVidPath = addCustomAudio(outputMP3, videoFile);
			} else {
				displayError("You need a video opened first to add speech to it");
			}
		}
		
		if (CommentaryFrame.chckbxLoadNewVideo.isSelected()){
			//Checkbox for auto load new video is checked
			if (newVidPath != null){
			MainFrame.mFrame.theVideo.prepareMedia(newVidPath);
			}
			
		}
	}

	public static void killAllFestProc(int pid) {

		if (pid != 0){
			String cmd = "pstree -p " + pid;
	/*
	 * Using the pstree bash command, we find the process named "aplay" associated with festival 
	 * which is what makes the sound. The pid of this process is found and killed using the "kill -9" command
	 */
			ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", cmd);
			String line = null;
			String s = "";

			try {
				Process p2 = pb2.start();
				InputStream stdout = p2.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while ((line = br.readLine()) != null) {
					s += line;
				}
				int x = s.indexOf("play");
				String sub = s.substring(x);
				int length = sub.length();
				String result = "";
				for (int i = 0; i < length; i++) {
					Character character = sub.charAt(i);
					String s2 = character.toString();
					if (s2.equals(")")) {
						break;
					}
					if (Character.isDigit(character)) {
						result += character;
					}
				}
				cmd = "kill -9 " + result;
				ProcessBuilder pb3 = new ProcessBuilder("/bin/bash", "-c", cmd);
				pb3.start();

			} catch (IOException e) {
				System.out.println("Error killing process: " + pid);
			}
			}

	}
}
