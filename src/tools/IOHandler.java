package tools;

import java.io.*;

/**
 * A simple class which is used to handle all the file IO.
 * This will keep variables such as where to save files.
 * @author andrew
 *
 */

public class IOHandler {
	
	private static String _uniqueName = "VidiVoxNA"; // vidivox nick andrew, should be unique enough..
	
	public final static String SaveDirectory = GetSavePath();
	public final static String AutoSaveDirectory = GetSavePath() + "Autosaves" + File.separator;
	public final static String VideoDirectory = GetSavePath() + "Videos" + File.separator;
	public final static String TmpDirectory = GetSavePath() + "Tmp" + File.separator;
	public final static String Mp3Directory = GetSavePath() + "Audio" + File.separator; //for the audio files generated
	
	private static String GetSavePath(){
		
		return System.getProperty("user.home") + File.separator + _uniqueName + File.separator;
		//Will return the directory to save the file to.
	}
	
	/**
	 * A simple method which checks the directories used for this program exist at runtime.
	 * If not; creates it for us.
	 * This is called in the constructor of MainFrame();
	 */
	public static void CheckPaths(){
		File mainDir = new File(SaveDirectory);
		File autoDir = new File(AutoSaveDirectory);
		File mp3Dir = new File(Mp3Directory);
		File tmpDir = new File(TmpDirectory);
		File videoDir = new File(VideoDirectory);
		// if the directory does not exist, create it
		if (!mainDir.exists()) {
		   mainDir.mkdir();
		}
		if (!autoDir.exists()) {
		   autoDir.mkdir();
		}
		if (!mp3Dir.exists()) {
			mp3Dir.mkdir();
		}
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		if (!videoDir.exists()) {
			videoDir.mkdir();
		}
	}
	
}
