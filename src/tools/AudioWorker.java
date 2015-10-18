package tools;

import java.io.File;

import javax.swing.SwingWorker;

import frames.ProgressBarFrame;
import videos.CustomAudio;
import videos.VidProject;

/*
 * Swingworker extension for running an ffmpeg command in another thread outside the EDT
 * This process can be quite intensive, so multi-threading it ensures the GUI keeps
 * responsive during this time of execution.
 */
public class AudioWorker extends SwingWorker<Void, Void>{
	VidProject project;
	String videoPath, newVideoPath, tmpAudioPath, audioPath = IOHandler.TmpDirectory + "toAdd.mp3";
	//Constructor
	AudioWorker(VidProject project){
		this.project = project;
		videoPath = project.getCustomVideo() + "tmp";
		newVideoPath = project.getCustomVideo();
		tmpAudioPath = IOHandler.TmpDirectory + "tmp.mp3";
	}
	
	
	//Overriding the doinbackground method for ffmpeg command to do in background
	@Override
	protected Void doInBackground() throws Exception {
		
		//This is the ffmpeg command used to apply selected audio to current video

		//strip the initial audio
		try {
			//Running process
			ProcessBuilder pb;
			String cmd;
			if (project.isStripped()){ //first loop
				cmd = "ffmpeg -i "+videoPath+" -an -vcodec copy "+newVideoPath;
				pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				Process process = pb.start();
				process.waitFor();
			}else{
				(new File(videoPath)).renameTo(new File(newVideoPath));
			}
			for(CustomAudio a : project.getAudio()){
				String wavFullPath = IOHandler.TmpDirectory + "output.wav";
				String tmpTxtFullPath = IOHandler.TmpDirectory + "txtTmp.txt"; 		
				FileTools.writeTextToFile(a.getText(), "txtTmp.txt");
				
				//Create the required mp3 files.
				ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c", "text2wave -o " + wavFullPath + " " + tmpTxtFullPath);
				ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + wavFullPath + " " + audioPath);
				pb1.start().waitFor();
				pb2.start().waitFor(); 
				//Apply it to the video recursively
				cmd = "ffmpeg -i "+newVideoPath+" "+tmpAudioPath;
				pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				System.out.println(cmd);
				Process process = pb.start();
				process.waitFor();
				cmd = "ffmpeg -i "+newVideoPath+" -an -vcodec copy "+videoPath + ".avi";
				System.out.println(cmd);
				pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				process = pb.start(); //strip audio
				process.waitFor();			
				(new File(newVideoPath)).delete(); (new File(videoPath + ".avi")).renameTo(new File(newVideoPath));
			    String audioOffset = "00:"+FileTools.LongToTime(a.getStart());
			    if (true) break;
				while (true){
					cmd = "ffmpeg -y -i "+newVideoPath+" -itsoffset "+audioOffset+" -i "+audioPath+" -map 0:0 -map 1:0 "
							+ "-c:v copy -preset ultrafast -async 1 "+newVideoPath + ".avi";
					pb = new ProcessBuilder("/bin/bash", "-c", cmd);
					process = pb.start(); //ffmpeg -i newProject3.avi tmp.mp3
					process.waitFor();
					if ((new File(newVideoPath + ".avi")).exists()) break;
					cmd = "ffmpeg -y -i "+newVideoPath+" -itsoffset "+audioOffset+" -i "+audioPath+" -map 0:0 -map 0:1 "
							+ "-c:v copy -preset ultrafast -async 1 "+newVideoPath + ".avi";
					pb = new ProcessBuilder("/bin/bash", "-c", cmd);
					process = pb.start(); //ffmpeg -i newProject3.avi tmp.mp3
					process.waitFor();
				}
				(new File(newVideoPath)).delete();
				cmd = "ffmpeg -y -i "+newVideoPath + ".avi"+" -i " + tmpAudioPath + " -filter_complex amix=inputs=2 "+newVideoPath;
				pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				process = pb.start();
				process.waitFor();
				File[] files = new File(IOHandler.TmpDirectory).listFiles();
			    for (File file : files) {
			        if (!file.getAbsolutePath().equals(newVideoPath)) file.delete();        
			    }
			}
			
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
