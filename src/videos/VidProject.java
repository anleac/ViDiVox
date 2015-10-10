package videos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.FileUtils;

import tools.BashTools;
import tools.FileTools;
import tools.IOHandler;

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
	
	String videoPath = ""; //the path in which the projects ORIGINAL video is saved
	
	boolean audioStripped = false; //whether or not the original audio of the video should be removed.
	
	public VidProject(String name, boolean isSaved){
		addedAudio = new ArrayList<CustomAudio>();
		this.name = name;
		this.isSaved = isSaved;
	}
	
	/**
	 * Adds audio to the video.
	 * @param a
	 */
	public void AddAudio(CustomAudio a){
		addedAudio.add(a);
	}
	
	/**
	 * Removes audio file from the video
	 */
	public void RemoveAudio(){
		
	}
	
	public void StripAudio() { audioStripped = true; }
	public boolean isStripped() {return audioStripped; }
	
	/**
	 * Creates a video, in the tmp directory.
	 */
	public void createVideo(){
		if (videoLoaded()){ //make sure there is one loaded
			File source = new File(videoPath), dest = new File(getCustomVideo() + "tmp");
			try {
				Files.copy(source.toPath(), dest.toPath());
			} catch (IOException e) {
			    e.printStackTrace();
			}
			BashTools.createVideo();
		}
	}
	
	/**
	 * Returns the path to the custom video path.
	 * @return
	 */
	public String getCustomVideo(){
		return IOHandler.TmpDirectory + name.replace(".pro", ".avi");
	}
	
	public boolean IsSaved() { return isSaved; }
	/**
	 * Sets the path of the save, thus must be saved now.
	 * Name is set to whatever the name of the path was.
	 * @param p
	 */
	public void SetPath(String p) {path = p; this.name = (p.split(File.separator)[p.split(File.separator).length - 1]); isSaved = true;}
	public void changeName(String name) { this.name = name; }
	public void setVideo(String path) {videoPath = path;}
	public String getVideo() {return videoPath;}
	public boolean videoLoaded() {return !videoPath.equals(""); }
	public String getName() {return name;}
	public String getPath() {return path;}
	public List<CustomAudio> getAudio() {return addedAudio;}
}
