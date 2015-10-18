package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import tools.FileTools;
import tools.IOHandler;
import tools.VlcTools;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import videos.VidProject;
/**
 * MainFrame represents the main GUI frame for this application.
 * It compasses the video player itself, along with all the GUI components
 * responsible for the video playing. This is seperate to the audio frame,
 * which holds everything related to added audio. (separation of guis, to avoid cluttered gui).
 * @author alea644
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	public static MainFrame mFrame;
	public final EmbeddedMediaPlayer theVideo = FileTools.getMediaPlayerComponent().getMediaPlayer();
	private float videoPlayRate = 1.0f, positiveCap = 4.0f; //play rate of the video, and its cap
	private final String DEFAULT_NAME = "ViDiVox"; // Default application name
	public String warning = "No video loaded"; // this is a warning which will

	private Component volalignment, timealignment, audioAlignment;
	// this is a flow-diagram hack to allow better alignments.

	//booleans for video playing functionality
	private boolean videoPlaying = false, isMuted = false, reverse = false;
	public final JSlider timeSlider = VlcTools.CreateSlider(true); //slider for the time of the video
	
	public final JButton btnPlay = new JButton(""), btnVolume = new JButton(""); //used to start/stop the video, and controlling the volume
	final JMenuItem mnExport = new JMenuItem("Export video"); //used to export the current video
	
	public VidProject project = new VidProject(IOHandler.GetNewName(), false); //the current project, default initially

	/**
	 * Launch the application. 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					(new NativeDiscovery()).discover();
					mFrame = new MainFrame(); mFrame.setLocationRelativeTo(null); mFrame.setVisible(true); //put it center screen
					mFrame.setMinimumSize(new Dimension(mFrame.getWidth(), mFrame.getHeight())); // Set min dimension to current
					IOHandler.CheckPaths(); //ensure all the paths are created which will be used.
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Pauses the video if its playing AND is loaded
	 */
	public void pauseVideo(){
		if (project.videoLoaded() && videoPlaying) btnPlay.doClick();
	}
	
	/**
	 * Resets the video, and the appriopriate
	 * GUI components to their initial states.
	 */
	public void resetVideo(){
		timeSlider.setValue(0);
		project.createVideo();
		if(project.videoLoaded()){ 
			loadVideo();
			if (videoPlaying) btnPlay.doClick();
		}else theVideo.release(); //remove any previous videos
		
	}
	
	/**
	 * Loads a given media file into 
	 */
	public void loadVideo(){ theVideo.prepareMedia(project.getCustomVideo()); }

	/**
	 * Create the frame. Button icons retrieved from:
	 * http://www.tdcurran.com/sites/tdcurran/images/user/Icons-in-iOS-8/audio-
	 * controls.png on 15/09/15 at 11:41 a.m 
	 * This is the constructor to MainFrame
	 */
	public MainFrame() {
		setTitle(DEFAULT_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 833, 609);
		createMenuBarGui(); setUpGui();
	}
	
	/**
	 * Creates all of the JMenuBar GUI componenets
	 * and adds it to the JFrame
	 */
	private void createMenuBarGui(){
		JMenuBar menuBar = new JMenuBar(); //create the menu bar
		menuBar.setMinimumSize(new Dimension(getWidth(), menuBar.getHeight()));
		setJMenuBar(menuBar); //create the headers
		JMenu mnFile = new JMenu("File"), mnVideo = new JMenu("Video"); 
		menuBar.add(mnFile); menuBar.add(mnVideo);
		
		//create items to go inside the JMenus
		JMenuItem mnOpen = new JMenuItem("Open a project"), mnNew = new JMenuItem("New project"), mnSave = new JMenuItem("Save Project");
		mnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //clicked on
				File chosenFile = FileTools.openFile("project");
				if (chosenFile != null) { //make sure file isnt null
					String projectPath = chosenFile.getAbsolutePath();
					VlcTools.CheckSaves();
					project = IOHandler.LoadProject(projectPath); //load the project
					if (!project.getVideo().equals("")){
						project.createVideo();
					}	
				}
			}
		});
		//Add their respective action listeners (being clicked on)
		mnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VlcTools.createNewProject();
			}
		});
		mnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				project.saveProject();
			}
		});
		mnFile.add(mnOpen); mnFile.add(mnNew); mnFile.add(mnSave); //Add them all
		
		//Now repeat for the 'video' menu bar
		JMenuItem mnLoad = new JMenuItem("Load a video");
		mnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File chosenFile = FileTools.openFile("video");
				if (chosenFile != null) {
					project = new VidProject(IOHandler.GetNewName(), false);
					//Check if they want to strip the current audio of the video
					int response = JOptionPane.showConfirmDialog(null, "Would you like to strip the current videos audio?", "Confirm",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.YES_OPTION) {
						project.StripAudio();
					}
					project.setVideo(chosenFile.getAbsolutePath()); //set the video path.
					resetVideo();
				}
			}
		});

		mnExport.setEnabled(false); //start not enabled as no video loaded yet to export
		mnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = FileTools.PickVideoSave(project.getName());
				File source = new File(project.getCustomVideo()), dest = new File(path);
				try { //make a copy of their video file to modify
					Files.copy(source.toPath(), dest.toPath());
				} catch (IOException ea) {
				    ea.printStackTrace();
				}
			}
		});
		mnVideo.add(mnLoad); mnVideo.add(mnExport); //add them
	}
	
	/**
	 * A method which contains auto-generated code from WindowBuilder
	 * which creates (and adds) all the GUI components to the JFrame
	 * (not including the menu!)
	 */
	private void setUpGui(){
		final JButton btnStop = new JButton(""), btnReverse = new JButton(""), //Create buttons to use for video playing
				btnFastforward = new JButton(""), btnAddCommentary = new JButton("Open Audio Panel");
		btnAddCommentary.setBackground(Color.WHITE);
		btnReverse.setBackground(Color.WHITE);
		btnStop.setBackground(Color.WHITE);
		btnFastforward.setBackground(Color.WHITE);
		btnAddCommentary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseVideo();
				AudioFrame.aFrame.updateAudio();
				AudioFrame.aFrame.setLocationRelativeTo(MainFrame.mFrame); 
				AudioFrame.aFrame.setLocation(
						AudioFrame.aFrame.getLocation().x + (MainFrame.mFrame.getSize().width / 2),
						AudioFrame.aFrame.getLocation().y);
				AudioFrame.aFrame.setVisible(true); //Attempt to show the audio frame to the right of the current window.
			}
		});
		btnFastforward.setEnabled(false); btnStop.setEnabled(false); btnReverse.setEnabled(false); btnPlay.setBackground(Color.WHITE);
 btnPlay.setEnabled(false);

		//create mainy display to spawn on
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		//labels for the feedback of the current time.
		final JLabel currentTime = new JLabel("00:00"), lengthTime = new JLabel("00:00"), lblPlayspeedx = new JLabel("Playspeed: 1.00x");
		btnPlay.setHorizontalAlignment(SwingConstants.LEFT);
		btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/play.jpg")));
		//btnPlay clicked on, we need to either resume or pause the video.
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!videoPlaying) { // was paused or stopped
					theVideo.play();
					theVideo.setRate(1.0f);
					videoPlaying = true;
					btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/pause.jpg")));
				} else { // was playing, so pause it
					theVideo.pause();
					videoPlaying = false; // paused;
					btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/play.jpg")));
				}
				reverse = false;
				videoPlayRate = 1;
			}
		});
		//Add the video component center screen
		contentPane.add(FileTools.getMediaPlayerComponent(), BorderLayout.CENTER);

		JPanel bottomRowButtonsPanel = new JPanel();
		bottomRowButtonsPanel.setPreferredSize(new Dimension(this.getWidth(), 85));
		contentPane.add(bottomRowButtonsPanel, BorderLayout.SOUTH);
		bottomRowButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		bottomRowButtonsPanel.add(timeSlider);

		// To keep the timeSlider at full width when the window resizes
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				volalignment.setPreferredSize(new Dimension(getWidth() - 775, 1));
				timealignment.setPreferredSize(new Dimension(getWidth() - 295, 1));
				timeSlider.setPreferredSize(new Dimension(getWidth() - 20, 20));
			}
		});

		//Add all the components created above.
		timealignment = Box.createHorizontalStrut(getWidth() - 295); 	
		bottomRowButtonsPanel.add(currentTime); bottomRowButtonsPanel.add(Box.createHorizontalStrut(54));  bottomRowButtonsPanel.add(lblPlayspeedx); 
		bottomRowButtonsPanel.add(timealignment); 	bottomRowButtonsPanel.add(lengthTime);
		bottomRowButtonsPanel.add(btnPlay); 	bottomRowButtonsPanel.add(Box.createHorizontalStrut(25));

		btnStop.setActionCommand("");
		btnStop.setIcon(new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/icons/stop.png"))).getImage())
				.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //Stop the video when this is clicked on.
				theVideo.setTime(0);
				btnPlay.doClick();
			}
		});

		btnReverse.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/rev.jpg")));
		btnReverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (videoPlaying) btnPlay.doClick();
				if (!reverse) videoPlayRate = 1f;
				videoPlayRate += 1f;
				reverse = true;
			}
		});

		btnFastforward.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/ff.jpg")));
		btnFastforward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Fast forward button clicked
				// Continuously fast forward until play button clicked
				if (videoPlaying) {
					btnPlay.doClick();
				}
				if (reverse) {
					reverse = false;
					videoPlayRate = 1f;
				}
				videoPlayRate += 1f;
			}
		}); 
		volalignment = Box.createHorizontalStrut(getWidth() - 775);
		btnVolume.setBackground(Color.WHITE);
		btnVolume.setIcon(new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/icons/volume.png"))).getImage())
				.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
		
		Component horizontalStrut = Box.createHorizontalStrut(25);  //Add the buttons created above again
		bottomRowButtonsPanel.add(btnFastforward); bottomRowButtonsPanel.add(btnStop);  bottomRowButtonsPanel.add(btnReverse); 	
		bottomRowButtonsPanel.add(horizontalStrut); bottomRowButtonsPanel.add(btnAddCommentary);
		bottomRowButtonsPanel.add(volalignment); bottomRowButtonsPanel.add(btnVolume);

		final JSlider volSlider = VlcTools.CreateSlider(false);
		bottomRowButtonsPanel.add(volSlider);
		theVideo.setVolume(100);

		btnVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMuted) {
					int value = volSlider.getValue();
					if (value == 0) { //Make sure it shows the correct icon
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/icons/muted.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else if (value < 50) {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/icons/volumelow.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/icons/volume.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					}
					volSlider.setEnabled(true); // cant change volume anymore
					isMuted = false;
				} else {
					btnVolume.setIcon(
							new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/icons/muted.png"))).getImage())
									.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					isMuted = true; // toggle the mute..
					volSlider.setEnabled(false); // can change again!
				}
				theVideo.mute(isMuted);
			}
		});

		Timer t = new Timer(25, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				mnExport.setEnabled(project.videoLoaded());
				String title = DEFAULT_NAME + "  -  " + project.getName();
				if (project.PendingSaves()) title += " *";
				if (!warning.equals("")) title += " (" + warning + ")";
				setTitle(title); // update the title frequently
				btnPlay.setEnabled(project.videoLoaded()); 	btnStop.setEnabled(project.videoLoaded());
				btnFastforward.setEnabled(project.videoLoaded()); 	btnReverse.setEnabled(project.videoLoaded());
				btnAddCommentary.setEnabled(project.videoLoaded()); btnVolume.setEnabled(project.videoLoaded());
				timeSlider.setEnabled(project.videoLoaded()); volSlider.setEnabled(project.videoLoaded());
				warning = (project.videoLoaded()) ? "" : "No video loaded";
				if (project.videoLoaded()) {
					DecimalFormat d = new DecimalFormat();
					d.setMinimumFractionDigits(2);
					String t = "Playspeed: ";
					if (reverse)
						t += '-';
					t += d.format(videoPlayRate) + "x";
					lblPlayspeedx.setText(t); // Update the play speed label
					currentTime.setText(FileTools.LongToTime(theVideo.getTime()));
					int iPos = (int) (theVideo.getTime() / 100);
					if (iPos + 5 >= timeSlider.getMaximum()) { // Loop back to the
															// start if video
															// has ended
						theVideo.setTime(0);
						btnPlay.doClick();
					}
					if (theVideo.getMediaMeta().getLength() > 0) {
						timeSlider.setValue(iPos);
					}
				}
			}
		});

		t.start();
		// Two different timers as I needed two different 'refresh' rates
		//this is due to as the code in here is quite intensive on the video,
		//and therefore updating too fast would cause video lag.
		Timer t2 = new Timer(200, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (project.videoLoaded()) {
					if (theVideo.getMediaMeta().getLength() > 0) {
						timeSlider.setMinimum(0);
						timeSlider.setMaximum((int) (theVideo.getMediaMeta().getLength() / 100));
					}
					lengthTime.setText(FileTools.LongToTime(theVideo.getMediaMeta().getLength()));
					if (videoPlayRate > 1f) {
						if (reverse) { // This reverses or fast forwrads
							theVideo.setTime(theVideo.getTime() - (int) ((videoPlayRate / (float) 5f) * 1000));
							if (theVideo.getTime() <= 0) {
								videoPlayRate = 1f;
								reverse = false;
							}
						} else {
							theVideo.setTime(theVideo.getTime() + (int) ((videoPlayRate / (float) 5f) * 1000));
						}
					}
				}
			}
		});
		t2.start();
	}

}
