package tools;

import java.io.File;

import javax.swing.SwingWorker;

import frames.ProgressBarFrame;

/*
 * Swingworker extension for running an ffmpeg command in another thread outside the EDT
 */
public class AddAudio extends SwingWorker<Void, Void>{
	String mp3Path;
	String videoPath;
	String outVidPath;
	File selectedFile;
	
	//Constructor
	AddAudio(String mp3Path, String videoPath, File selectedFile){
		this.mp3Path = mp3Path;
		this.videoPath = videoPath;
		this.selectedFile = selectedFile;
		this.outVidPath = selectedFile.getAbsolutePath();
	}
	
	
	//Overriding the doinbackground method for ffmpeg command to do in background
	@Override
	protected Void doInBackground() throws Exception {
		
		if (!FileTools.hasExtension(outVidPath)){
			outVidPath += ".avi";
		}
		//This is the ffmpeg command used to apply selected audio to current video
		String cmd = "ffmpeg -i " + videoPath + " -i " + mp3Path + " -map 0:v -map 1:a " + outVidPath;
		
		
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

		try {
			
			//Running process
			Process process = pb.start();
			process.waitFor();
			//Waiting for process to end
			
		} catch (Exception e) {
			FileTools.displayError("Error adding audio to video");
		}
		return null;
	}
	protected void done(){
		//Close the "working..." frame 
		ProgressBarFrame.pbFrame.setVisible(false);
		//Displaying success and path in a pop up message upon completion
		FileTools.displayInfo("Video succesfully saved to\n" + outVidPath);
	}
	
	

}
