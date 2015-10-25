package vidivox.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import vidivox.frames.mainframe.GuiUpdaterActionListener;
import vidivox.frames.mainframe.MainFrameComponents;
import vidivox.frames.mainframe.VideoTrackActionListener;
import vidivox.projects.VidProject;
import vidivox.tools.FileTools;
import vidivox.tools.IOHandler;

/**
 * MainFrame represents the main GUI frame for this application. It compasses
 * the video player itself, along with all the GUI components responsible for
 * the video playing. This is seperate to the audio frame, which holds
 * everything related to added audio. (separation of guis, to avoid cluttered
 * gui).
 * 
 * @author alea644
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	public static MainFrame mFrame;
	private final EmbeddedMediaPlayer theVideo = FileTools.getMediaPlayerComponent().getMediaPlayer();
	public EmbeddedMediaPlayer Video() {return theVideo;}

	public float videoPlayRate = 1.0f, positiveCap = 16.0f; // play rate of the video/cap
	public final String DEFAULT_NAME = "Vidivox"; // Default application name
	public String warning = "No video loaded"; // this is a warning which will

	private Component volalignment, timealignment;
	// this is a flow-diagram hack to allow better alignments.

	// booleans for video playing functionality
	private boolean videoPlaying = false;
	public boolean VideoPlaying() {return videoPlaying;}
	private boolean isMuted = false;
	public boolean IsMuted() {return isMuted;}
	public void setMute(boolean set) {isMuted = set;}
	public boolean reverse = false, startVideo = false;
	
	private final JSlider timeSlider = MainFrameComponents.CreateSlider(true), volSlider = MainFrameComponents.CreateSlider(false);
	public JSlider TimeSlider() {return timeSlider; }
	public JSlider VolSlider() {return volSlider; }
	//slider for the video and time

	public final JButton btnPlay = new JButton(""), btnVolume = MainFrameComponents.createVolumeButton();
	// used to start/stop the video, and controlling the volume
	
	public final JMenuItem mnExport = new JMenuItem("Export video"), mnNew = new JMenuItem("New project");
	private VidProject project = new VidProject(IOHandler.GetNewName(), false);
	public VidProject VProject() {return project;}
	//the project used
	
	//these labels show in the GUI the current time in the video and total length
	private final JLabel currentTime = new JLabel("00:00"), lengthTime = new JLabel("00:00"), lblPlayspeedx = new JLabel("Playspeed: 1x  (Limit: 16x speed)");
	public JLabel CurrentTime() {return currentTime;}; public JLabel LengthTime() {return lengthTime;}
	public JLabel LblPlaySpeed() {return lblPlayspeedx;}
	//gui buttons, need to be accesible as used in other classes
	public final JButton btnStop = new JButton(""), btnReverse = new JButton(""),
	btnFastforward = new JButton(""), btnAddCommentary = new JButton("Open Audio Panel");
	private final JLabel lblVideoLoaded = new JLabel("Video Loaded:");
	//Displays the video information to the user.
	public JLabel LblVideo() {return lblVideoLoaded;}

	/**
	 * Pauses the video if its playing AND is loaded
	 */
	public void pauseVideo() {
		if (project.videoLoaded() && videoPlaying)
			btnPlay.doClick();
	}

	/**
	 * Resets the video, and the appriopriate GUI components to their initial
	 * states.
	 */
	public void resetVideo() {
		if (videoPlaying)
			btnPlay.doClick(); //make sure nothings 'playing'
		if (!project.getVideo().equals("")) {
			timeSlider.setValue(0);
			mFrame.setEnabled(false); // disable this until its completely finished
			project.createVideo();
		} else {
			theVideo.release(); // reset everything
			timeSlider.setValue(0);
		}
	}

	/**
	 * Called when swingworker is complete
	 */
	public void createdVideo() {
		mFrame.setEnabled(true);
		project.Created();
		if (project.videoLoaded()) {
			loadVideo();
			if (videoPlaying)
				btnPlay.doClick();
		} else {
			theVideo.release(); // remove any previous videos
		}
	}
	
	/**
	 * Creates a new project, and resets the appropriate
	 * GUI components in MainFrame to their initial values.
	 */
	public void createNewProject(){
		CheckSaves();
		project = new VidProject(IOHandler.GetNewName(), false);
		resetVideo();
		mnNew.setEnabled(false); //can not re-new a new project
	}
	
	/**
	 * Checks if there are any saves pending, and will 
	 * inform the user to see if they want to save
	 */
	public void CheckSaves(){
		if (project.PendingSaves()) { //Check if they'd like to save first
			int response = JOptionPane.showConfirmDialog(null, "Would you like to save your changes first?", "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION){ //they want to save
				project.saveProject(); //save it
			}
		}
	}

	/**
	 * Loads a given media file, and plays it
	 */
	public void loadVideo() {
		theVideo.prepareMedia(project.getCustomVideo());
		startVideo = true;
	}

	/**
	 * Create the frame. Button icons retrieved from:
	 * http://www.tdcurran.com/sites/tdcurran/images/user/Icons-in-iOS-8/audio-
	 * controls.png on 15/09/15 at 11:41 a.m This is the constructor to
	 * MainFrame
	 */
	public MainFrame() {
		setTitle(DEFAULT_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 833, 609);
		createMenuBarGui();
		setUpGui();
	}

	/**
	 * Creates all of the JMenuBar GUI componenets and adds it to the JFrame
	 */
	private void createMenuBarGui() {
		JMenuBar menuBar = new JMenuBar(); // create the menu bar
		menuBar.setMinimumSize(new Dimension(getWidth(), menuBar.getHeight()));
		setJMenuBar(menuBar); // create the headers
		JMenu mnFile = new JMenu("File"), mnVideo = new JMenu("Video");
		menuBar.add(mnFile);	menuBar.add(mnVideo);
		mnNew.setEnabled(false);
		// create items to go inside the JMenus
		JMenuItem mnOpen = new JMenuItem("Open project"), mnSave = new JMenuItem("Save Project");
		mnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // clicked on
				File chosenFile = FileTools.openFile("project");
				if (chosenFile != null) { // make sure file isnt null
					String projectPath = chosenFile.getAbsolutePath();
					CheckSaves();
					project = IOHandler.LoadProject(projectPath); //load in the project
					if (!project.getVideo().equals("")) {
						project.createVideo();
					}
					project.ChangesUndo();
				}
			}
		});
		// Add their respective action listeners (being clicked on)
		mnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewProject();
			}
		});
		mnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				project.saveProject();
			}
		});
		mnFile.add(mnNew);	mnFile.add(mnOpen);	mnFile.add(mnSave); // Add them all

		// Now repeat for the 'video' menu bar
		JMenuItem mnLoad = new JMenuItem("Load a video");
		mnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File chosenFile = FileTools.openFile("video");
				if (chosenFile != null) {
					project = new VidProject(IOHandler.GetNewName(), false);
					// Check if they want to strip the current audio of the
					// video
					if (FileTools.doYesNoDialog("Would you like to strip the current videos audio?")) {
						project.StripAudio();
					}
					project.ChangesMade(); // new video
					project.setVideo(chosenFile.getAbsolutePath());
					resetVideo();
				}
			}
		});

		mnExport.setEnabled(false); // start not enabled as no video loaded yet
		mnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = FileTools.PickVideoSave(project.getName());
				File source = new File(project.getCustomVideo()), dest = new File(path);
				try { // make a copy of their video file to modify
					Files.copy(source.toPath(), dest.toPath());
					FileTools.displayInfo("Succesfully exported!");
				} catch (IOException ea) {
					ea.printStackTrace();
					FileTools.displayInfo("An error occured during the export, please try again");
				}
			}
		});
		mnVideo.add(mnLoad);
		mnVideo.add(mnExport); // add them
	}

	/**
	 * A method which contains auto-generated code from WindowBuilder which
	 * creates (and adds) all the GUI components to the JFrame (not including
	 * the menu!)
	 */
	private void setUpGui() {
		btnAddCommentary.setBackground(Color.WHITE);
		btnReverse.setBackground(Color.WHITE);
		btnStop.setBackground(Color.WHITE); // Set them to white
		btnFastforward.setBackground(Color.WHITE);
		btnAddCommentary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseVideo(); // Add commentrary has bene clicked, show the
								// audio frame
				AudioFrame.aFrame.updateAudio();
				AudioFrame.aFrame.setLocationRelativeTo(MainFrame.mFrame);
				AudioFrame.aFrame.setLocation(
						// Attempt to position it to the right of the current
						// screen
						AudioFrame.aFrame.getLocation().x
								+ (MainFrame.mFrame.getSize().width / 2),
						AudioFrame.aFrame.getLocation().y);
				AudioFrame.aFrame.setVisible(true); // Attempt to show the audio
													// frame to the right of the
													// current window.
			}
		});
		btnFastforward.setEnabled(false); btnStop.setEnabled(false);
		btnReverse.setEnabled(false);	btnPlay.setBackground(Color.WHITE);
		btnPlay.setEnabled(false);

		// create mainy display to spawn on
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		// labels for the feedback of the current time.
		btnPlay.setHorizontalAlignment(SwingConstants.LEFT);
		btnPlay.setIcon(new ImageIcon(MainFrame.class
				.getResource("/vidivox/icons/play.jpg")));
		// btnPlay clicked on, we need to either resume or pause the video.
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!videoPlaying) { // was paused or stopped
					theVideo.play();
					theVideo.setRate(1.0f);
					videoPlaying = true;
					btnPlay.setIcon(new ImageIcon(MainFrame.class
							.getResource("/vidivox/icons/pause.jpg")));
				} else { // was playing, so pause it
					theVideo.pause();
					videoPlaying = false; // paused;
					btnPlay.setIcon(new ImageIcon(MainFrame.class
							.getResource("/vidivox/icons/play.jpg")));
				}
				reverse = false;
				videoPlayRate = 1;
			}
		});
		// Add the video component center screen
		contentPane.add(FileTools.getMediaPlayerComponent(),
				BorderLayout.CENTER);

		JPanel bottomRowButtonsPanel = new JPanel();
		bottomRowButtonsPanel.setPreferredSize(new Dimension(this.getWidth(),
				85));
		contentPane.add(bottomRowButtonsPanel, BorderLayout.SOUTH);
		bottomRowButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		bottomRowButtonsPanel.add(timeSlider);

		// To keep the timeSlider at full width when the window resizes
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				volalignment.setPreferredSize(new Dimension(getWidth() - 775, 1));
				timealignment.setPreferredSize(new Dimension(getWidth() - 405, 1));
				timeSlider.setPreferredSize(new Dimension(getWidth() - 20, 20));
			}
		});

		// Add all the components created above.
		timealignment = Box.createHorizontalStrut(getWidth() - 405);
		bottomRowButtonsPanel.add(currentTime); bottomRowButtonsPanel.add(Box.createHorizontalStrut(54));
		bottomRowButtonsPanel.add(lblPlayspeedx); 	bottomRowButtonsPanel.add(timealignment);
		bottomRowButtonsPanel.add(lengthTime); 	bottomRowButtonsPanel.add(btnPlay);
		bottomRowButtonsPanel.add(Box.createHorizontalStrut(25));

		btnStop.setActionCommand("");
		btnStop.setIcon(new ImageIcon(((new ImageIcon(MainFrame.class
				.getResource("/vidivox/icons/stop.png"))).getImage())
				.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Stop the video clicked
				theVideo.setTime(0);
				btnPlay.doClick();
			}
		});

		btnReverse.setIcon(new ImageIcon(MainFrame.class.getResource("/vidivox/icons/rev.jpg")));
		btnReverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (videoPlaying)
					btnPlay.doClick();
				if (!reverse)
					videoPlayRate = 1f;
				videoPlayRate *= 2f;
				reverse = true; // check the limits.
				if (videoPlayRate > positiveCap)
					videoPlayRate = positiveCap;
			}
		});

		btnFastforward.setIcon(new ImageIcon(MainFrame.class
				.getResource("/vidivox/icons/ff.jpg")));
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
				videoPlayRate *= 2f; // Always keep the playback cap within the
										// limit
				if (videoPlayRate > positiveCap)
					videoPlayRate = positiveCap;
			}
		});
		volalignment = Box.createHorizontalStrut(getWidth() - 775);

		Component horizontalStrut = Box.createHorizontalStrut(25);//Created to space out GUI nicer
		bottomRowButtonsPanel.add(btnReverse);	bottomRowButtonsPanel.add(btnStop);
		bottomRowButtonsPanel.add(btnFastforward); 	bottomRowButtonsPanel.add(horizontalStrut);
		bottomRowButtonsPanel.add(btnAddCommentary);	bottomRowButtonsPanel.add(volalignment);
		bottomRowButtonsPanel.add(btnVolume);	bottomRowButtonsPanel.add(volSlider);
	
	contentPane.add(lblVideoLoaded, BorderLayout.NORTH);
		theVideo.setVolume(100);

		Timer t = new Timer(25, new GuiUpdaterActionListener());
		t.start();
		// Two different timers as I needed two different 'refresh' rates
		// this is due to as the code in here is quite intensive on the video,
		// and therefore updating too fast would cause video lag.
		Timer t2 = new Timer(200, new VideoTrackActionListener());
		t2.start();
	}

}
