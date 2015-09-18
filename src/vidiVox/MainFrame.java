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
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Color;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private final EmbeddedMediaPlayer theVideo = Tools.getMediaPlayerComponent().getMediaPlayer();
	private boolean videoLoaded = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					NativeDiscovery nd = new NativeDiscovery();
					nd.discover();
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
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
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel bottomRowButtonsPanel = new JPanel();
		contentPane.add(bottomRowButtonsPanel, BorderLayout.SOUTH);
		bottomRowButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setBackground(Color.WHITE);
		btnPlay.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/play.jpg")));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Play button clicked
			
				if (videoLoaded){
				theVideo.play();
				theVideo.setRate(1.0f);
				} else {
					Tools.displayError("You need to open something to play first!");
				}
			}
		});
		bottomRowButtonsPanel.add(btnPlay);
		
		JButton btnPause = new JButton("Pause");
		btnPause.setBackground(Color.WHITE);
		btnPause.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/pause.jpg")));
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Pause button clicked
				theVideo.pause();
			}
		});
		bottomRowButtonsPanel.add(btnPause);
		
		JButton btnFastforward = new JButton("FastForward");
		btnFastforward.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/ff.jpg")));
		btnFastforward.setBackground(Color.WHITE);
		btnFastforward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Fast forward button clicked
				//Continuously? fast forward until play button clicked
				theVideo.setRate(3.0f);
			}
		});
		bottomRowButtonsPanel.add(btnFastforward);
		
		JButton btnReverse = new JButton("Reverse");
		btnReverse.setIcon(new ImageIcon(MainFrame.class.getResource("/vidiVox/rev.jpg")));
		btnReverse.setBackground(Color.WHITE);
		btnReverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Reverse button clicked
				
				
			}
		});
		
		bottomRowButtonsPanel.add(btnReverse);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBackground(Color.WHITE);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Stop button clicked 
				
				theVideo.stop();
			}
		});
		bottomRowButtonsPanel.add(btnStop);
		
		JPanel topRowButtonsPanel = new JPanel();
		contentPane.add(topRowButtonsPanel, BorderLayout.NORTH);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBackground(Color.WHITE);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Save button clicked
				
			}
		});
		
		JButton btnOpen = new JButton("Open");
		btnOpen.setBackground(Color.WHITE);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Open button clicked
				File chosenFile = Tools.openFile();
				if (chosenFile != null){
				String mediaPath = chosenFile.getAbsolutePath();
				theVideo.prepareMedia(mediaPath);
				videoLoaded = true;
				} 
				
			}
		});
		topRowButtonsPanel.add(btnOpen);
		topRowButtonsPanel.add(btnSave);
		
		JButton btnCommentary = new JButton("Commentary");
		btnCommentary.setBackground(Color.WHITE);
		btnCommentary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Commentary button clicked
				CommentaryFrame.cmFrame.setVisible(true);
			}
		});
		topRowButtonsPanel.add(btnCommentary);
		
		JButton btnAudio = new JButton("Add Audio");
		btnAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Audio button clicked
				File audioFile = Tools.openFile();
				String audioFilePath = audioFile.getAbsolutePath();
				
				
			}
		});
		btnAudio.setBackground(Color.WHITE);
		topRowButtonsPanel.add(btnAudio);
		contentPane.add(Tools.getMediaPlayerComponent(), BorderLayout.CENTER);
	}

}
