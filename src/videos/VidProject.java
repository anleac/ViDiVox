package videos;

import java.util.ArrayList;
import java.util.List;

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
	
	public VidProject(){
		addedAudio = new ArrayList<CustomAudio>();
	}
	
	public void changeName(String name) { this.name = name; }
	public String getName() {return name;}
}
