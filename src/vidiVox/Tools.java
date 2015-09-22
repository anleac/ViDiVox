package vidiVox;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

	public static void addCustomAudio(File audioFile, File videoFile) {
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
			System.out.println(cmd);
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

			try {
				ProgressBarFrame.pbFrame.setLocationRelativeTo(null);
				ProgressBarFrame.pbFrame.setVisible(true);
				Process process = pb.start();
				process.waitFor();
				ProgressBarFrame.pbFrame.setVisible(false);
				displayInfo("Done!");
			} catch (Exception e) {
				displayError("Error adding audio to video");
			}
		}

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

	public static void speakFestival(String textToSay) {

		String cmd = "echo " + "\"" + textToSay + "\" | festival --tts &";
		System.out.println(cmd);

		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

		try {
			Process process = pb.start();
			BufferedReader stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));
			System.out.println(stdin.readLine());
			// festID = stdin.readLine().split(" ")[1];
			// System.out.println(festID);
		} catch (Exception e) {
			displayError("Error using festival speech");
		}
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
		String mp3FullPath = IOHandler.Mp3Directory + "output" + numFiles + ".mp3";

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
		String vidPath = MainFrame.mFrame.chosenVideoPath;
		displayInfo("MP3 file saved to \n" + outputMP3.getAbsolutePath());
		if (CommentaryFrame.chckbxApplyThisSpeech.isSelected()) {
			// Checkbox is selected upon save button click
			if (vidPath != null) {
				File videoFile = new File(vidPath);
				addCustomAudio(outputMP3, videoFile);
			} else {
				displayError("You need a video opened first");
			}
		}
	}

	public static void killAllFestProc() {

		String cmd = "killall festival";

		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = pb.start();
			process.waitFor();
		} catch (Exception e) {
			displayError("Error killing festival process");
		}

	}

}
