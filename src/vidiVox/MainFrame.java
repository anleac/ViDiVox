package vidiVox;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JSlider;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	public static MainFrame mFrame;
	private final EmbeddedMediaPlayer theVideo = Tools.getMediaPlayerComponent().getMediaPlayer();
	private boolean videoLoaded = false;
	private float videoPlayRate = 1.0f;
	public String chosenVideoPath = null;

	//This is a 'hack' for flow layouts which pushes the volume button towards the right
	private Component volalignment, timealignment; 

	//For toggling pause/play button
	private boolean videoPlaying = false; 
	
	//For the audio controller
	private boolean isMuted = false;
	private boolean reverse = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					NativeDiscovery nd = new NativeDiscovery();
					nd.discover();
					mFrame = new MainFrame();
					mFrame.setLocationRelativeTo(null); // centre screen
					mFrame.setVisible(true);
					//Set min dimension to current
					mFrame.setMinimumSize(new Dimension(mFrame.getWidth(), mFrame.getHeight()));
					IOHandler.CheckPaths();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame. Button icons retrieved from:
	 * http://www.tdcurran.com/sites/tdcurran/images/user/Icons-in-iOS-8/audio-
	 * controls.png on 15/09/15 at 11:41 a.m
	 */
	public MainFrame() {
		setTitle("ViDiVox");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 705, 496);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		JMenuBar menuBar = new JMenuBar();
		//Set min dimension to current
		menuBar.setMinimumSize(new Dimension(getWidth(), menuBar.getHeight())); 
		setJMenuBar(menuBar);

		final JLabel currentTime = new JLabel("00:00"), lengthTime = new JLabel("00:00");
		final JLabel lblPlayspeedx = new JLabel("Playspeed: 1.00x");
		
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		final JMenuItem mntmSaveCurrentVideo = new JMenuItem("Save current video");
		mntmSaveCurrentVideo.setEnabled(false); // no video loaded yet to save
		mntmSaveCurrentVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save button clicked

			}
		});

		final JButton btnPlay = new JButton(""); // Removed text, testing.
		btnPlay.setHorizontalAlignment(SwingConstants.LEFT);
		btnPlay.setBackground(Color.WHITE);
		btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/play.jpg")));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Play/Pause button clicked
				if (!videoPlaying) { // was paused or stopped
					if (videoLoaded) {
						theVideo.play();
						theVideo.setRate(1.0f);
						videoPlaying = true;
						btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/pause.jpg")));
					} else {
						Tools.displayError("You need to open something to play first!");
					}
				} else { // was playing, so pause it
					theVideo.pause();
					videoPlaying = false; // paused;
					btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/play.jpg")));
				}
				reverse = false;
				videoPlayRate = 1;
			}
		});

		JMenuItem mntmOpenAVideo = new JMenuItem("Open a video");
		mntmOpenAVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Open button clicked
				File chosenFile = Tools.openFile();
				if (chosenFile != null) {
					String mediaPath = chosenFile.getAbsolutePath();
					chosenVideoPath = mediaPath;
					theVideo.prepareMedia(mediaPath);
					videoLoaded = true;
					
					if (!videoPlaying)
						btnPlay.doClick();
					//Can now click save button as video is loaded
					mntmSaveCurrentVideo.setEnabled(true); 
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
				// Audio button clicked
				File audioFile = Tools.openMP3File();
				File videoFile = new File(chosenVideoPath);
				if (audioFile != null) {
					Tools.addCustomAudio(audioFile, videoFile);
				}
			}
		});
		mnAddAudioOverlay.add(mntmAudio);

		JMenuItem mntmCommentary = new JMenuItem("Add Commentary");
		mntmCommentary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Commentary button clicked
				CommentaryFrame.cmFrame.setLocationRelativeTo(null);
				CommentaryFrame.cmFrame.setVisible(true);
			}
		});
		mnAddAudioOverlay.add(mntmCommentary);

		JMenuItem mntmClearAll = new JMenuItem("Clear All");
		mnAddAudioOverlay.add(mntmClearAll);

		JPanel videoPanel = new JPanel();
		videoPanel.add(Tools.getMediaPlayerComponent());

		contentPane.add(Tools.getMediaPlayerComponent(), BorderLayout.CENTER);

		JPanel northPanel = new JPanel();
		contentPane.add(northPanel, BorderLayout.NORTH);

		JPanel bottomRowButtonsPanel = new JPanel();
		bottomRowButtonsPanel.setPreferredSize(new Dimension(this.getWidth(), 85));
		contentPane.add(bottomRowButtonsPanel, BorderLayout.SOUTH);
		bottomRowButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		final JSlider slider = new JSlider();
		slider.setValue(0);
		slider.setPreferredSize(new Dimension(this.getWidth() - 20, 20));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				int value = slider.getValue();
				if (slider.getValueIsAdjusting()){
					theVideo.setTime(value * 1000);	
				}
			}
		});
		bottomRowButtonsPanel.add(slider);

		//To keep the slider at full width when the window resizes
		addComponentListener(new ComponentAdapter() { 
			public void componentResized(ComponentEvent e) {
				volalignment.setPreferredSize(new Dimension(getWidth() - 580, 1));
				timealignment.setPreferredSize(new Dimension(getWidth() - 295, 1));
				slider.setPreferredSize(new Dimension(getWidth() - 20, 20));
			}
		});

		bottomRowButtonsPanel.add(currentTime);

		timealignment = Box.createHorizontalStrut(getWidth() - 295);
		bottomRowButtonsPanel.add(Box.createHorizontalStrut(54));
	
		bottomRowButtonsPanel.add(lblPlayspeedx);
		
		bottomRowButtonsPanel.add(timealignment);
		bottomRowButtonsPanel.add(lengthTime);

		bottomRowButtonsPanel.add(btnPlay);
		bottomRowButtonsPanel.add(Box.createHorizontalStrut(25));

		JButton btnStop = new JButton("");
		btnStop.setActionCommand("");
		btnStop.setIcon(new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/vidiVox/stop.png"))).getImage())
				.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
		btnStop.setBackground(Color.WHITE);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				theVideo.setTime(0);
				btnPlay.doClick();
			}
		});

		JButton btnReverse = new JButton("");
		btnReverse.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/rev.jpg")));
		btnReverse.setBackground(Color.WHITE);
		btnReverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (videoPlaying){
					btnPlay.doClick();
				}
				if (!reverse){
					videoPlayRate = 1f;
				}
				videoPlayRate += 1f;
				reverse = true;
			}
		});

		bottomRowButtonsPanel.add(btnReverse);
		bottomRowButtonsPanel.add(btnStop);

		// btnPause has been removed, and merged into btnPlay.

		JButton btnFastforward = new JButton("");
		btnFastforward.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/ff.jpg")));
		btnFastforward.setBackground(Color.WHITE);
		btnFastforward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Fast forward button clicked
				// Continuously fast forward until play button clicked
				if (videoPlaying){
					btnPlay.doClick();
				}
				if (reverse){
					reverse = false;
					videoPlayRate = 1f;
				}
				videoPlayRate += 1f;
			}
		}); //
		bottomRowButtonsPanel.add(btnFastforward);
		volalignment = Box.createHorizontalStrut(getWidth() - 580);
		final JButton btnVolume = new JButton("");
		btnVolume.setBackground(Color.WHITE);
		btnVolume.setIcon(new ImageIcon(((new ImageIcon(MainFrame.class.getResource("/vidiVox/volume.png"))).getImage())
				.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
		bottomRowButtonsPanel.add(volalignment);
		bottomRowButtonsPanel.add(btnVolume);

		final JSlider volSlider = new JSlider();
		volSlider.setValue(100);
		theVideo.setVolume(100);
		volSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				int value = slider.getValue();
				// value here
				if (value == 0) {
					btnVolume.setIcon(new ImageIcon(
							((new ImageIcon(MainFrame.class.getResource("/vidiVox/muted.png"))).getImage())
									.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
				} else if (value < 50) {
					btnVolume.setIcon(new ImageIcon(
							((new ImageIcon(MainFrame.class.getResource("/vidiVox/volumelow.png"))).getImage())
									.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
				} else {
					btnVolume.setIcon(new ImageIcon(
							((new ImageIcon(MainFrame.class.getResource("/vidiVox/volume.png"))).getImage())
									.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
				}
				theVideo.setVolume(value);
			}
		});

		btnVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMuted) {
					int value = volSlider.getValue();
					if (value == 0) {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidiVox/muted.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else if (value < 50) {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidiVox/volumelow.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					} else {
						btnVolume.setIcon(new ImageIcon(
								((new ImageIcon(MainFrame.class.getResource("/vidiVox/volume.png"))).getImage())
										.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					}
					volSlider.setEnabled(true); // cant change volume anymore
					isMuted = false;
				} else {
					btnVolume.setIcon(new ImageIcon(
							((new ImageIcon(MainFrame.class.getResource("/vidiVox/muted.png"))).getImage())
									.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
					isMuted = true; // toggle the mute..
					volSlider.setEnabled(false); // can change again!
				}
				theVideo.mute(isMuted);
			}
		});

		bottomRowButtonsPanel.add(volSlider);

		Timer t = new Timer(250, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (videoLoaded) {
					DecimalFormat d = new DecimalFormat();
					d.setMinimumFractionDigits(2);
					String t = "Playspeed: ";
					if (reverse) t += '-';
					t += d.format(videoPlayRate) + "x";
					lblPlayspeedx.setText(t);
					theVideo.setRate(videoPlayRate);
					currentTime.setText(Tools.LongToTime(theVideo.getTime()));
					lengthTime.setText(Tools.LongToTime(theVideo.getMediaMeta().getLength()));
					int iPos = (int)(theVideo.getTime() / 1000);
					if (iPos + 1 == slider.getMaximum()){
						theVideo.setTime(0);
						btnPlay.doClick();
					}
					if (theVideo.getMediaMeta().getLength() > 0) {
						slider.setValue(iPos);
						slider.setMinimum(0);
						slider.setMaximum((int)(theVideo.getMediaMeta().getLength() / 1000));
					}
					if (videoPlayRate > 1f){
						if (reverse){
							theVideo.setTime(theVideo.getTime() - (int)((videoPlayRate / (float)4f) * 1000));
							if (theVideo.getTime() <= 0){
								videoPlayRate = 1f;
								reverse = false;
							}
						}else{
							theVideo.setTime(theVideo.getTime() + (int)((videoPlayRate / (float)4f) * 1000));
						}
					}
				}
			}
		});

		t.start();

	}

}
