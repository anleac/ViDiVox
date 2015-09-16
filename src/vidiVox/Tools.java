package vidiVox;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
	

}
