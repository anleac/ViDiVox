package vidivox.tools.workers;

import java.io.File;

import javax.swing.SwingWorker;

import vidivox.frames.MainFrame;
import vidivox.frames.ProgressBarFrame;
import vidivox.projects.CustomAudio;
import vidivox.projects.VidProject;
import vidivox.tools.FileTools;
import vidivox.tools.IOHandler;

/*
 * Swingworker extension for running an ffmpeg command in another thread outside the EDT
 * This process can be quite intensive, so multi-threading it ensures the GUI keeps
 * responsive during this time of execution.
 */
public class AudioWorker extends SwingWorker<Void, Integer[]>{
	VidProject project;
	String videoPath, newVideoPath, tmpAudioPath, audioPath = IOHandler.TmpDirectory() + "toAdd.mp3";
	//Constructor
	public AudioWorker(VidProject project){
		this.project = project;
		videoPath = project.getCustomVideo() + "tmp";
		newVideoPath = project.getCustomVideo();
		tmpAudioPath = IOHandler.TmpDirectory() + "tmp.mp3";
		if (project.getAudio().size() > 0 || project.isStripped()){ //if its zero, it wont take any time
			//therefore no need for a frame to breifly pop up
			ProgressBarFrame.pbFrame.setLocationRelativeTo(null);
			ProgressBarFrame.pbFrame.setVisible(true);
		}
	}
	
	
	//Overriding the doinbackground method for ffmpeg command to do in background
	//this will merge all of the audios into the video that the user has specifid
	@Override
	protected Void doInBackground() throws Exception {
		
		//This is the ffmpeg command used to apply selected audio to current video

		//strip the initial audio
		try {
			//Running process
			ProcessBuilder pb;
			String cmd;
			if (project.isStripped()){ //first loop
				cmd = "ffmpeg -y -i "+videoPath+" -an -vcodec copy "+newVideoPath;
				System.out.println(cmd);
				publish(new Integer[] {0, 0});
				pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				Process process = pb.start();
				process.waitFor();
				publish(new Integer[] {1, 20});
			}else{
				(new File(videoPath)).renameTo(new File(newVideoPath));
			}

			String audioInfo = ""; //to concat all the audio information together
			int audioCounter = 0; //keep all the names unique
			for(CustomAudio a : project.getAudio()){
				audioCounter++;
				String wavFullPath = IOHandler.TmpDirectory() + audioCounter +"output.wav";
				String tmpTxtFullPath = IOHandler.TmpDirectory() + audioCounter +"txtTmp.txt"; 	
				FileTools.writeTextToFile(a.getText(), audioCounter + "txtTmp.txt");
				//Create the required mp3 files.
				if (!a.isFile()){ //only if its text to create
					ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c",
							"text2wave -o " + wavFullPath + " " + tmpTxtFullPath + " -eval \"" + a.getPath() + "\"");
					ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -i " + wavFullPath + " " + audioPath.replace("toAdd", "toAdd" + audioCounter));
					pb1.start().waitFor();
					pb2.start().waitFor(); 
				}
				audioInfo += " -itsoffset " + (a.getStart()/1000) + " -i " + ((a.isFile())? a.getPath() : audioPath.replace("toAdd", "toAdd" + audioCounter));
				publish(new Integer[] {1, 20 + (30 - ((30 / project.getAudio().size()) * (project.getAudio().size() - audioCounter)))});
			}
			if (audioCounter > 0){
				publish(new Integer[] {1, 50});
				//FFMPEG commands to split audio from video, combine the two audios and re attache the audio and video  
				String tmpDir = IOHandler.TmpDirectory() + "tempmp3.mp3";
				cmd = "ffmpeg -y -i " + newVideoPath + audioInfo + " -filter_complex amix=inputs=" + (project.getAudio().size() + (project.isStripped()? 0 : 1)) +":duration=first -preset ultrafast -async 1 " + tmpDir;
				ProcessBuilder splitter = new ProcessBuilder("/bin/bash", "-c", cmd);
				ProcessBuilder combiner = new ProcessBuilder("/bin/bash", "-c", (cmd = "ffmpeg -i "+tmpDir+" -i " + newVideoPath + " -map 0:a -map 1:v -preset ultrafast " + newVideoPath + ".avi"));
				Process split = splitter.start();
				publish(new Integer[] {1, 55});
				split.waitFor();
				publish(new Integer[] {2, 70});
				Process combine = combiner.start();
				publish(new Integer[] {2, 75});
				combine.waitFor();
				(new File(newVideoPath)).delete(); //replace the new file
				(new File(newVideoPath + ".avi")).renameTo(new File(newVideoPath));
				publish(new Integer[] {2, 80});
			}
			publish(new Integer[] {3, 95});
			File[] files = new File(IOHandler.TmpDirectory()).listFiles();
		    for (File file : files) {
		        if (!file.getAbsolutePath().equals(newVideoPath)) file.delete();        
		    }
		} catch (Exception e) {
			FileTools.displayError("Error adding audio to video");
		}
		if (ProgressBarFrame.pbFrame.isVisible()) Thread.sleep(600); //let them read it for a tad
		publish(new Integer[] {3, 100}); //done!
		return null;
	}
	
	/**
	 * Takes in an array of 2 long, first integer
	 * will be the 'code' (see progress bar frame)
	 * and 2nd will be the progress in the video
	 * @param chunks
	 */
	protected void publish(Integer[] chunks) {
       ProgressBarFrame.pbFrame.Update(chunks[0], chunks[1]);
       //update the progress bar
    }
	
	/**
	 * Called once the worker has finished.
	 * It will remove the progress bar and load the new
	 * video into the jframe
	 */
	@Override
	protected void done(){
		ProgressBarFrame.pbFrame.setVisible(false);
		MainFrame.mFrame.createdVideo();
	}
}
