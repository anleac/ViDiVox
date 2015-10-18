package tools;

import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
/*
 * A class for arbitrary methods.
 * Keeping button logic clean and simple
 * This contains methods especially related to both files (JDialog)
 * and text processing.
 */
public class FileTools {

	static EmbeddedMediaPlayerComponent mediaPlayerComponent = null;
	static File lastDir = null;
	
	/**
	 * Simply opens and returns a file choosen by the user.
	 * @ext represents the extension to limit it by
	 * @return
	 */
	public static File openFile(String type) {
		JFileChooser jfc = FileTools.ReturnConfirmationChooser(null);
		if (type == null);
		else if (type.equals("project")){
			jfc.setSelectedFile(new File(IOHandler.ProjectDirectory + File.separator + " "));
			jfc.setFileFilter(new FileNameExtensionFilter("Project file", new String[] {"pro"}));
		}
		else{//video
			jfc.setFileFilter(new FileNameExtensionFilter("Video file", new String[] {"avi", "mp4", "mkv"}));
		}
		
		if (lastDir != null) {
			jfc.setCurrentDirectory(lastDir);
		}
		jfc.showOpenDialog(null);
		File f = jfc.getSelectedFile();
		if (f != null) {
			lastDir = f.getParentFile();
			if (type.equals("video")){ //ask if they want to strip audio here.
				
			}
		}
		return f;
	}
	
	/**
	 * A method which allows the user to pick where to save their project to.
	 * @return
	 */
	public static String PickProjectSave(String name){
		JFileChooser jfc = FileTools.ReturnConfirmationChooser(null);
		jfc.setSelectedFile(new File(IOHandler.ProjectDirectory + name));
		//Limit to only project
		jfc.setFileFilter(new FileNameExtensionFilter("Project File", "pro"));
		if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return null; //they cancelled!
		String ret = jfc.getSelectedFile().getAbsolutePath();
		if (!FileTools.hasExtension(ret)){ //make sure it has an extension
			ret += IOHandler.ProjectExtension;
		}
		return ret;
	}
	
	/**
	 * A method which allows the user to pick where to save their video to.
	 * @return
	 */
	public static String PickVideoSave(String name){
		JFileChooser jfc = FileTools.ReturnConfirmationChooser(null);
		jfc.setSelectedFile(new File(IOHandler.VideoDirectory + name.replace(".pro", ".avi")));
		//Limit to only project
		jfc.setFileFilter(new FileNameExtensionFilter("Video File", ".avi"));
		if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) return null; //they cancelled!
		String ret = jfc.getSelectedFile().getAbsolutePath();
		if (!FileTools.hasExtension(ret)){ //make sure it has an extension
			ret += ".avi";
		}
		return ret;
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
	
	// Converts back
	public static Long TimeToLong(String time) {
		int seconds = (int) (Integer.parseInt(time.split(":")[0]) * 60) + Integer.parseInt(time.split(":")[1]);
		return (long) (seconds * 1000);
	}

	/**
	 * Returns the inbuilt media player vlcj has
	 * @return
	 */
	public static EmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		if (mediaPlayerComponent == null) {
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		}
		return mediaPlayerComponent;

	}

	/**
	 * Displays a message
	 * @param msg
	 */
	public static void displayInfo(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Important", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays an error to the user
	 * @param msg
	 */
	public static void displayError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	

	/**
	 * opens a given mp3 file (that the user selects)
	 * and returns it
	 * @return
	 */
	public static File openMP3File() {
		JFileChooser jfc = FileTools.ReturnConfirmationChooser(null);
		jfc.setSelectedFile(new File(IOHandler.Mp3Directory + "Audio"));
		if (lastDir != null) {
			jfc.setCurrentDirectory(lastDir);
		}
		//Limit to only mp3 now
		jfc.setFileFilter(new FileNameExtensionFilter("MP3 File", "mp3"));
		if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) return null; //they cancelled!
		if (jfc.getSelectedFile() != null) {
			lastDir = jfc.getSelectedFile().getParentFile();
		}
		return jfc.getSelectedFile();
	}
	/*
	 * Code for overwrite confirmation within JFileChooser. Retrieved from:
	 * http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
	 * On 23/9/15
	 */
	public static JFileChooser ReturnConfirmationChooser(Boolean isVideo){
		JFileChooser jfc = new JFileChooser(){
		    @Override
		    public void approveSelection(){
		        File f = getSelectedFile();
		        if(f.exists() && getDialogType() == SAVE_DIALOG){
		            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }
		        super.approveSelection();
		    }        
		};
		String saveTo = "";
		if (isVideo == null) return jfc; //generic file no extension
		else if (isVideo){ //saving a video file, thus give it a default name
			saveTo = IOHandler.VideoDirectory + "myVideo" + new File(IOHandler.VideoDirectory).listFiles().length + ".avi";
		} else { // mp3
			saveTo = IOHandler.Mp3Directory + "myAudio" + new File(IOHandler.Mp3Directory).listFiles().length + ".mp3";
		}
		jfc.setSelectedFile(new File(saveTo));
		return jfc;
		
	}
	
	/**
	 * A simple method which writes text to a file
	 * @param text
	 * @param fileName
	 */
	public static void writeTextToFile(String text, String fileName) {
		PrintWriter pw = null;
		try {
			(pw = new PrintWriter(IOHandler.TmpDirectory + fileName)).write(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pw.close();
	}
	

	/**
	 * Takes in a string s and determines whether of not it contains an extension
	 * ie test.avi (based on assumping the filename doesnt contain an . already)
	 * @param s
	 * @return
	 */
	public static boolean hasExtension(String s){
		return (s.split(File.separator)[s.split(File.separator).length - 1].contains("."));
	}
	
	/**
	 * A simple JDialog to put up, with a message 'msg' with a yes/no option!
	 * @param msg
	 * @return
	 */
	public static boolean doYesNoDialog(String msg){
		return (JOptionPane.showConfirmDialog(null, msg, "Warning!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}
}
