package vidivox.projects;

/**
 * A very simple class which holds a path, and the
 * value of the text. 
 * @author andrew
 *
 */
public class CustomAudio {

	String text;
	long startTime; //end seconds
	double duration;
	boolean file = false;
	String path = ""; //path of the mp3 file (if applicable)
	/**
	 * Constructor.
	 * s contains all the valuable information, split via |
	 * @param s
	 */
	public CustomAudio(String s){
		String[] data = s.split("~~");
		text = data[0]; 
		startTime = Long.parseLong(data[1]); 
		duration = Double.parseDouble(data[2]);
		path = data[3];
		if (data[3].contains(".mp3")) // its a file!
			file = true;
	}
	
	/**
	 * Constructor.
	 * Made in program
	 * @param s
	 */
	public CustomAudio(String text, long start, double duration, String path){
		this.text = text; this.startTime = start; this.duration = duration;
		if (path.contains(".mp3")) //its a file!
			file = true;
		this.path = path;
	}
	
	public String getText() {return text;}
	public String getPath() {return path;}
	public boolean isFile() {return file;}
	public Long getStart() {return startTime;}
	public Double getDuration(){return duration;}
	
	/**
	 * Returns the data to write to a file.
	 * @return
	 */
	public String getData(){
		return text + "~~" + startTime + "~~" + getDuration() + "~~" + path;
	}
}
