package vidivox.projects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import vidivox.frames.AudioFrame;
import vidivox.tools.BashTools;
import vidivox.tools.FileTools;
import vidivox.tools.IOHandler;

/**
 * This is a class which holds everything related to the video project.
 * It holds audio files, video paths etc.
 * @author andrew
 *
 */
public class VidProject {
	List<CustomAudio> addedAudio;
	//a list of the added audio to a video.
	String name, path = "";
	boolean isSaved = false; //has the project been saved to a specific path yet?
	String videoPath = ""; //the path in which the projects ORIGINAL video is saved
	
	boolean created = false;
	boolean pendingSaves = false; //to track whether all saves have been changed
	public void ChangesMade() {pendingSaves = true;} // changes made
	public void ChangesUndo() {pendingSaves = false;} //force no changes
	public boolean PendingSaves() {return pendingSaves;}
	public void Created() {created = true;}
	
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
		pendingSaves = true; //something changed!
		addedAudio.add(a);
	}
	
	/**
	 * Removes audio file from the video
	 */
	public void RemoveAudio(String remove){
		pendingSaves = true; //something changed!
		for (int i = 0; i < addedAudio.size(); i++){
			if (addedAudio.get(i).text.equals(remove)){
				addedAudio.remove(i);
				break; //remove the selected audio
			}
		}
		AudioFrame.aFrame.updateAudio(); //update the audio
	}
	
	public void StripAudio() { audioStripped = true; }
	public boolean isStripped() {return audioStripped; }
	
	/**
	 * Creates a video, in the tmp directory.
	 */
	public void createVideo(){
		if (videoPath.equals("") == false){ //make sure there is one loaded
			created = false;
			File source = new File(videoPath), dest = new File(getCustomVideo() + "tmp");
			if (dest.exists()) dest.delete();
			try {
				Files.copy(source.toPath(), dest.toPath());
			} catch (IOException e) {
			    e.printStackTrace();
			}
			BashTools.createVideo();
		}
	}
	
	/**
	 * Saves the current project
	 */
	public void saveProject() {
		if (!IsSaved()){
			SetPath(FileTools.PickProjectSave(getName()));
		}
		IOHandler.SaveProject(this);
	}
	
	/**
	 * Called when it is succesfully saved.
	 */
	public void Saved(){
		pendingSaves = false; //no more pending changes.
	}
	
	/**
	 * Returns the path to the custom video path.
	 * @return
	 */
	public String getCustomVideo(){
		return IOHandler.TmpDirectory() + name.replace(".pro", ".avi");
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
	public boolean videoLoaded() {return !videoPath.equals("") && created; }
	public String getName() {return name;}
	public String getPath() {return path;}
	public List<CustomAudio> getAudio() {return addedAudio;}
	//getters/setters for variables above.
}
