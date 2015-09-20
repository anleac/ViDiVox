package vidiVox;

import java.io.File;
import java.io.IOException;

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
	public static File openFile() {
		
		JFileChooser jfc = new JFileChooser();
		if(lastDir != null){
		jfc.setCurrentDirectory(lastDir);
		}
		jfc.showOpenDialog(null);
		File f = jfc.getSelectedFile();
		if (f != null){
		lastDir = f.getParentFile();
		}
		return f;
		
	}
	
	public static EmbeddedMediaPlayerComponent getMediaPlayerComponent(){
		if (mediaPlayerComponent == null){
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		}
		return mediaPlayerComponent;
		
	}
	
	public static void displayInfo(String msg){
		JOptionPane.showMessageDialog(null, msg, "FYI", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void displayError(String msg){
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void addCustomAudio(File audioFile){
		//Is forced to be mp3 from file chooser
		//Brief says audio is to be automatically added to beginning of video
		
		
		
	}
	
	public static File openMP3File(){
		JFileChooser jfc = new JFileChooser();
		if(lastDir != null){
		jfc.setCurrentDirectory(lastDir);
		}
		//Same as other file chooser, but only allows mp3 files
		FileFilter ff = new FileNameExtensionFilter("MP3 File", "mp3");
		jfc.setFileFilter(ff);
		jfc.showOpenDialog(null);
		File f = jfc.getSelectedFile();
		if (f != null){
		lastDir = f.getParentFile();
		}
		return f;
		
	}
	
	public static void speakFestival(String textToSay){
		
		String cmd = "echo "+"\""+textToSay+"\" | festival --tts";
		System.out.println(cmd);
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = pb.start();
		} catch (IOException e) {
			displayError("Error using festival speech");
		}
		
		
	}
	

}
