package vidivox.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import vidivox.projects.CustomAudio;
import vidivox.projects.VidProject;

/**
 * A simple class which is used to handle all the file IO. This will keep
 * variables such as where to save files.
 * This class holds the default directory for where everything will be saved,
 * and makes sure all the required files are created when needed.
 * @author andrew
 *
 */

public class IOHandler {

	private static String _uniqueName = "ViDiVox"; //projects name
	private static String workPath = ""; //this needs to be defined by the user

	//below defines directories to be created for the application when in use
	public static String SaveDirectory() {return workPath;}
	public static String VideoDirectory() {return workPath + "Videos" + File.separator; }
	public static String TmpDirectory() {return workPath + "Tmp" + File.separator; }
	public static String Mp3Directory() {return workPath + "Audio" + File.separator;}
	public static String ProjectDirectory() {return workPath + "Project" + File.separator; }
	
	//a default workspace directory incase the user somehow doesnt choose one.
	private final static String defaultWorkspace = System.getProperty("user.home") + File.separator + _uniqueName + File.separator;
	private final static String hiddenDirectory = System.getProperty("user.home") + File.separator + ".vidivoxaws";
	//Above is a hidden directory, which will contain the workspace the user wanted
	//if this file does not exist, it means the workspace has not been defined yet, and thus 
	//must prompt and ask

	public final static String ProjectExtension = ".pro";

	/**
	 * Returns a default new project name, and make sure it is unique.
	 */
	public static String GetNewName() {
		CheckPaths(); //ensure the path exists.
		return "newProject" + (new File(IOHandler.ProjectDirectory()).listFiles().length + 1) + ProjectExtension;
	}
	
	/**
	 * Checks to see if the user has already specified a workspace,
	 * if not, prompt and ask for one.
	 */
	private static void WorkSpaceChosen(){
		File wsDir = new File(hiddenDirectory);
		if (wsDir.exists()){ //already chosen a workspace
			BufferedReader brTest = null;
		    try { //read in the workpath
				brTest = new BufferedReader(new FileReader(wsDir));
				workPath = brTest.readLine();
				brTest.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{ //ask for one
			//prompt user to where theyd like to save their work
			String newWs = defaultWorkspace;
			if (FileTools.doYesNoDialog("<html>Would you like to select your default workspace directory?<br> If not, it" +
					" will default to your current home under ViDiVox</html>")){
				newWs = FileTools.PickWorkspace(); //let them pick
			}
			if (newWs == "") newWs = defaultWorkspace; //incase they didnt choose one.	
			else{
				newWs += File.separator;
			}
			(new File(newWs)).mkdir(); //create the directorys
			workPath = newWs; //set the new work path.
			try {
				wsDir.createNewFile();
				//write the new path for future reference
				Files.write(wsDir.toPath(), newWs.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //create it
		}
		workPath = workPath.replaceAll(File.separator + File.separator, File.separator);
	}

	/**
	 * A simple method which checks the directories used for this program exist
	 * at runtime. If not; creates it for us. This is called in the constructor
	 * of MainFrame();
	 */
	public static void CheckPaths() {
		WorkSpaceChosen(); //check if the workspace is set, this has to be.
		File mainDir = new File(SaveDirectory());
		File mp3Dir = new File(Mp3Directory());
		File tmpDir = new File(TmpDirectory());
		File videoDir = new File(VideoDirectory());
		File proDir = new File(ProjectDirectory());

		// if the directory does not exist, create it
		if (!mainDir.exists())
			mainDir.mkdir();
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
			br.newLine(); //Save all the relevant data to the text file
			br.write(strip);
			br.newLine();
			for (CustomAudio a : project.getAudio()){
				br.write(a.getData());
				br.newLine();
			}
			br.close();
			project.Saved();
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
