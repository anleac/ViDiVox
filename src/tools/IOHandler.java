package tools;

import java.io.*;
import java.util.Scanner;

import com.sun.jna.platform.FileUtils;

import videos.CustomAudio;
import videos.VidProject;

/**
 * A simple class which is used to handle all the file IO. This will keep
 * variables such as where to save files.
 * 
 * @author andrew
 *
 */

public class IOHandler {

	private static String _uniqueName = "VidiVoxNAA"; // vidivox nick andrew
														// andrew, should be
														// unique enough..

	public final static String SaveDirectory = GetSavePath();
	public final static String AutoSaveDirectory = GetSavePath() + "Autosaves" + File.separator;
	public final static String VideoDirectory = GetSavePath() + "Videos" + File.separator;
	public final static String TmpDirectory = GetSavePath() + "Tmp" + File.separator;
	public final static String Mp3Directory = GetSavePath() + "Audio" + File.separator; // for
																						// the
																						// audio
																						// files
																						// generated
	public final static String ProjectDirectory = GetSavePath() + "Project" + File.separator; // to
																								// save
																								// the
																								// projects
																								// to

	public final static String ProjectExtension = ".pro";

	/**
	 * Gets the default save path, which is located in the home.
	 * 
	 * @return
	 */
	private static String GetSavePath() {

		return System.getProperty("user.home") + File.separator + _uniqueName + File.separator;
		// Will return the directory to save the file to.
	}

	/**
	 * Returns a default new project name, and make sure it is unique.
	 */
	public static String GetNewName() {
		CheckPaths(); //ensure the path exists.
		return "newProject" + (new File(IOHandler.ProjectDirectory).listFiles().length + 1) + ProjectExtension;
	}

	/**
	 * A simple method which checks the directories used for this program exist
	 * at runtime. If not; creates it for us. This is called in the constructor
	 * of MainFrame();
	 */
	public static void CheckPaths() {
		File mainDir = new File(SaveDirectory);
		File autoDir = new File(AutoSaveDirectory);
		File mp3Dir = new File(Mp3Directory);
		File tmpDir = new File(TmpDirectory);
		File videoDir = new File(VideoDirectory);
		File proDir = new File(ProjectDirectory);

		// if the directory does not exist, create it
		if (!mainDir.exists())
			mainDir.mkdir();
		if (!autoDir.exists())
			autoDir.mkdir();
		if (!mp3Dir.exists())
			mp3Dir.mkdir();
		if (!tmpDir.exists())
			tmpDir.mkdir();
		if (!videoDir.exists())
			videoDir.mkdir();
		if (!proDir.exists())
			proDir.mkdir();
		
		for(File f: tmpDir.listFiles()) 
			  f.delete();  //clean the tmp directory.

	}

	/**
	 * Saves the project to a specified path, already inside vidproject
	 * 
	 * @param project
	 */
	public static void SaveProject(VidProject project) {
		try {
			File f = new File(project.getPath());
			if (!f.exists())
				f.createNewFile();
			FileWriter fr = new FileWriter(f);
			BufferedWriter br = new BufferedWriter(fr);
			br.write(project.getVideo());
			String strip = (project.isStripped()) ? "1" : "0";
			br.newLine();
			br.write(strip);
			br.newLine();
			for (CustomAudio a : project.getAudio()){
				br.write(a.getData());
				br.newLine();
			}
			br.close();
		} catch (Exception e) {
			FileTools.displayError("Error occured during file save, please try again.");
		}
	}

	/**
	 * Loads a video project at a specified path.
	 * 
	 * @param path
	 * @return
	 */
	public static VidProject LoadProject(String path) {
		VidProject p = new VidProject("", true);
		p.SetPath(path);
		File f = new File(path);
		FileReader fr;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			p.setVideo(br.readLine());
			if (br.readLine().equals("1")) p.StripAudio();
			while ((s = br.readLine()) != null) {
				p.AddAudio(new CustomAudio(s));
			}
			br.close();
		} catch (Exception e) {
			FileTools.displayError("An error occuring during the loading, please try again.");
		}
		return p;
	}

}
