package vidiVox;

import java.io.File;

import javax.swing.JFileChooser;

/*
 * A class for arbitrary methods.
 * Keeping button logic clean and simple
 */
public class Tools {
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
	
	
	

}
