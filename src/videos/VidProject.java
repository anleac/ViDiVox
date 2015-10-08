package videos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tools.FileTools;

/**
 * This is a class which holds everything related to the video project.
 * It holds audio files, video paths etc.
 * @author andrew
 *
 */
public class VidProject {
	List<CustomAudio> addedAudio;
	//a list of the added audio to a video.
	String name, path;
	boolean isSaved = false; //has the project been saved to a specific path yet?
	
	public VidProject(String name, boolean isSaved){
		addedAudio = new ArrayList<CustomAudio>();
		this.name = name;
		this.isSaved = isSaved;
	}
	
	public boolean IsSaved() { return isSaved; }
	/**
	 * Sets the path of the save, thus must be saved now.
	 * Name is set to whatever the name of the path was.
	 * @param p
	 */
	public void SetPath(String p) {path = p; this.name = (p.split(File.separator)[p.split(File.separator).length - 1]); isSaved = true;}
	public void changeName(String name) { this.name = name; }
	public String getName() {return name;}
}
