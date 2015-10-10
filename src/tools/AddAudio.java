package tools;

import java.io.File;

import javax.swing.SwingWorker;

import frames.ProgressBarFrame;
import videos.CustomAudio;
import videos.VidProject;

/*
 * Swingworker extension for running an ffmpeg command in another thread outside the EDT
 */
public class AddAudio extends SwingWorker<Void, Void>{
	VidProject project;
	String videoPath, newVideoPath, tmpAudioPath;
	//Constructor
	AddAudio(VidProject project){
		this.project = project;
		videoPath = project.getCustomVideo();
		newVideoPath = videoPath + "new";
		tmpAudioPath = IOHandler.TmpDirectory + "tmp.mp3";
	}
	
	
	//Overriding the doinbackground method for ffmpeg command to do in background
	@Override
	protected Void doInBackground() throws Exception {
		
		//This is the ffmpeg command used to apply selected audio to current video
		String cmd = "ffmpeg -i "+videoPath+" "+tmpAudioPath;
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		int counter = 0;
		//strip the initial audio
		try {
			//Running process
			Process process = pb.start();
			process.waitFor();
			if (project.isStripped() && counter++ == 0){ //first loop
				
			}else{
				cmd = "ffmpeg -y -i " + tmpVidDir + " -i " + tmpMp3Dir + " -filter_complex amix=inputs=2 " + outVidPath;
			}
			
			for(CustomAudio a : project.getAudio()){
				
			}
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
		//reload the video here maybe
	}
	
	

}
