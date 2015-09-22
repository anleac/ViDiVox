package vidiVox;

import java.io.File;

import javax.swing.SwingWorker;

public class addAudio extends SwingWorker<Void, Void>{
	String mp3Path;
	String videoPath;
	String outVidPath;
	File selectedFile;
	
	//Constructor
	addAudio(String mp3Path, String videoPath, File selectedFile){
		this.mp3Path = mp3Path;
		this.videoPath = videoPath;
		this.selectedFile = selectedFile;
		this.outVidPath = selectedFile.getAbsolutePath();
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		String outVidPath = selectedFile.getAbsolutePath();
		if (!Tools.hasExtension(outVidPath)){
			outVidPath += ".avi";
		}
		String cmd = "ffmpeg -i " + videoPath + " -i " + mp3Path + " -map 0:v -map 1:a " + outVidPath;
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

		try {
			
			
			Process process = pb.start();
			process.waitFor();
			
			
		} catch (Exception e) {
			Tools.displayError("Error adding audio to video");
		}
		return null;
	}
	protected void done(){
		ProgressBarFrame.pbFrame.setVisible(false);
		Tools.displayInfo("Video succesfully saved to\n" + outVidPath);
	}
	
	

}
