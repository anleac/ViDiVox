package vidiVox;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.JSlider;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	public static MainFrame mFrame = new MainFrame();
	private final EmbeddedMediaPlayer theVideo = Tools.getMediaPlayerComponent().getMediaPlayer();
	private boolean videoLoaded = false;
	
	private boolean videoPlaying = false; // this is to toggle the pause/play button and keep track of state
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					NativeDiscovery nd = new NativeDiscovery();
					nd.discover();
					mFrame.setLocationRelativeTo(null); //centre screen
					mFrame.setVisible(true);
					IOHandler.CheckPaths();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * Button icons retrieved from: 
	 * http://www.tdcurran.com/sites/tdcurran/images/user/Icons-in-iOS-8/audio-controls.png
	 * on 15/09/15 at 11:41 a.m
	 */
	public MainFrame() {
		setTitle("ViDiVox");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 634, 444);	
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//Yo Nick, I've moved all your top button functionality into the menu items.
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		final JMenuItem mntmSaveCurrentVideo = new JMenuItem("Save current video");
		mntmSaveCurrentVideo.setEnabled(false); //no video loaded yet to save
		mntmSaveCurrentVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Save button clicked
				
			}
		});	
		
		JMenuItem mntmOpenAVideo = new JMenuItem("Open a video");
		mntmOpenAVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Open button clicked
				File chosenFile = Tools.openFile();
				if (chosenFile != null){
					String mediaPath = chosenFile.getAbsolutePath();
					theVideo.prepareMedia(mediaPath);
					videoLoaded = true;
					mntmSaveCurrentVideo.setEnabled(true); //can now save with a video loaded
				} 
			}
		});
		mnFile.add(mntmOpenAVideo);
		
		mnFile.add(mntmSaveCurrentVideo);
		
		JMenuItem mntmCloseProgram = new JMenuItem("Close program");
		mntmCloseProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmCloseProgram);
		
		JMenu mnAddAudioOverlay = new JMenu("Audio Overlay");
		menuBar.add(mnAddAudioOverlay);
		
		JMenuItem mntmAudio = new JMenuItem("Add Audio");
		mntmAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Audio button clicked
				File audioFile = Tools.openMP3File();
				if (audioFile != null){
				Tools.addCustomAudio(audioFile);
				}
			}
		});
		mnAddAudioOverlay.add(mntmAudio);
		
		JMenuItem mntmCommentary = new JMenuItem("Add Commentary");
		mntmCommentary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Commentary button clicked
				CommentaryFrame.cmFrame.setLocationRelativeTo(null);
				CommentaryFrame.cmFrame.setVisible(true);
			}
		});
		mnAddAudioOverlay.add(mntmCommentary);
		
		JMenuItem mntmClearAll = new JMenuItem("Clear All");
		mnAddAudioOverlay.add(mntmClearAll);

		contentPane.add(Tools.getMediaPlayerComponent(), BorderLayout.CENTER);
		
		JPanel bottomRowButtonsPanel = new JPanel();
		bottomRowButtonsPanel.setPreferredSize(new Dimension(this.getWidth(), 70));
		contentPane.add(bottomRowButtonsPanel, BorderLayout.SOUTH);
		bottomRowButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	
		
		final JSlider slider = new JSlider();
		slider.setValue(0);
		slider.setPreferredSize(new Dimension(this.getWidth() - 20, 20));
		bottomRowButtonsPanel.add(slider);
		
		addComponentListener(new ComponentAdapter() { //to keep the slider at full width when the window resizes
		    public void componentResized(ComponentEvent e) {
		    	slider.setPreferredSize(new Dimension(getWidth() - 20, 20));           
		    }
		});
	
		
		final JButton btnPlay = new JButton(""); //Removed text, testing.
		btnPlay.setHorizontalAlignment(SwingConstants.LEFT);
		btnPlay.setBackground(Color.WHITE);
		btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/play.jpg")));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Play/Pause button clicked
				if (!videoPlaying){ //was paused or stopped
					if (videoLoaded){
						theVideo.play();
						theVideo.setRate(1.0f);
						videoPlaying = true;
						btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/pause.jpg")));
					} else {
						Tools.displayError("You need to open something to play first!");
					}
				}else{ //was playing, so pause it
					theVideo.pause();
					videoPlaying = false; //paused;
					btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/play.jpg")));
				}
			}
		});
		
		bottomRowButtonsPanel.add(btnPlay);

		
		JButton btnStop = new JButton("Stop");
		btnStop.setBackground(Color.WHITE);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Stop button clicked 
				
				theVideo.stop();
			}
		});
		
		JButton btnReverse = new JButton("");
		btnReverse.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/rev.jpg")));
		btnReverse.setBackground(Color.WHITE);
		btnReverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Reverse button clicked
				
				
			}
		});
		
		bottomRowButtonsPanel.add(btnReverse);
		bottomRowButtonsPanel.add(btnStop);
		
		//btnPause has been removed, and merged into btnPlay.
		
		JButton btnFastforward = new JButton("");
		btnFastforward.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/ff.jpg")));
		btnFastforward.setBackground(Color.WHITE);
		btnFastforward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Fast forward button clicked
				//Continuously fast forward until play button clicked
				theVideo.setRate(3.0f);
			}
		});
		bottomRowButtonsPanel.add(btnFastforward);
		
	}

}
