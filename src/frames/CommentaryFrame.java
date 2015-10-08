package frames;

import tools.BashTools;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tools.FileTools;

import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Label;

@SuppressWarnings("serial")
public class CommentaryFrame extends JFrame {
	/*
	 * 
	 * Frame used to add/create 'commentary' (text) into a wave file and add
	 * to the video loaded.
	 * This JFrame represents the GUI for it.
	 */
	private JPanel contentPane;
	public static CommentaryFrame cmFrame = new CommentaryFrame();
	private JTextArea textField;
	public static boolean loadNewVideoIsChecked = false;
	public static int currentFestID = 0;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Create the frame.
	 */
	public CommentaryFrame() {
		setResizable(false);
		setTitle("Add Commentary");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 413, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextArea();
		textField.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textField.setLineWrap(true);
		textField.setBounds(25, 39, 173, 121);
		contentPane.add(textField);

		// Preview button plays text through festival
		JButton btnPreview = new JButton("Preview");
		btnPreview.setBounds(25, 219, 102, 25);
		btnPreview.setBackground(Color.WHITE);
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Preview button clicked
				String textToPreview = textField.getText();
				//30 words is a good cap
				if (textToPreview.split(" ").length > 30) { // Too many words
					FileTools.displayError("Number of words should be less than 30");
				} else {
					if (textToPreview.equals("")){ //Error, they entered no text!
						FileTools.displayError("Please enter some valid text"); return;
					}
					currentFestID = BashTools.speakFestival(textToPreview);
				}

			}
		});
		contentPane.add(btnPreview);

		// Button returns to mainFrame without saving
		JButton btnBack = new JButton("Back");
		btnBack.setBounds(267, 219, 110, 25);
		btnBack.setBackground(Color.WHITE);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Back button clicked
				// MainFrame was never set to invisible
				// Simply hide this current frame
				textField.setText(""); //clear the text
				cmFrame.setVisible(false);
				MainFrame.mFrame.requestFocus();

			}
		});
		contentPane.add(btnBack);

		// Button for saving synth speech as MP3 file
		JButton btnSave = new JButton("Apply");
		btnSave.setBounds(139, 219, 116, 25);
		btnSave.setBackground(Color.WHITE);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save button clicked
				String textToSave = textField.getText();
				if (textToSave.equals("")){ //Error, they entered no text!
					FileTools.displayError("Please enter some valid text"); return;
				}
				BashTools.saveFestToMP3(textToSave);
			}
		});
		contentPane.add(btnSave);

		JLabel lblWriteTextBelow = new JLabel("Write text below to add to your video (30 word limit)");
		lblWriteTextBelow.setBounds(21, 12, 378, 15);
		contentPane.add(lblWriteTextBelow);
		
		JLabel lblVoiceSpeed = new JLabel("Voice Speed:");
		lblVoiceSpeed.setBounds(216, 80, 95, 15);
		contentPane.add(lblVoiceSpeed);
		
		JComboBox speedBox = new JComboBox();
		speedBox.setModel(new DefaultComboBoxModel(new String[] {"0.10x", "0.20x", "0.30x", "0.40x", "0.50x", "0.60x", "0.70x", "0.80x", "0.90x", "1.00x", "1.10x", "1.20x", "1.30x", "1.40x", "1.50x", "1.60x", "1.70x", "1.80x", "1.90x", "2.00x", "2.10x", "2.20x", "2.30x", "2.40x", "2.50x", "2.60x", "2.70x", "2.80x", "2.90x", "3.00x"}));
		speedBox.setSelectedIndex(9);
		speedBox.setBounds(314, 75, 63, 24);
		contentPane.add(speedBox);
		
		JLabel lblSpeakingOptions = new JLabel("Speaking Options");
		lblSpeakingOptions.setBounds(216, 41, 141, 15);
		contentPane.add(lblSpeakingOptions);
		
		JLabel lblVoicePitch = new JLabel("Pitch change:");
		lblVoicePitch.setBounds(209, 127, 102, 15);
		contentPane.add(lblVoicePitch);
		
		JComboBox pitchBox = new JComboBox();
		pitchBox.setModel(new DefaultComboBoxModel(new String[] {"-60Hz", "-50Hz", "-40Hz", "-30Hz", "-20Hz", "-10Hz", "0Hz", "10Hz", "20Hz", "30Hz", "40Hz", "50Hz", "60Hz"}));
		pitchBox.setSelectedIndex(6);
		pitchBox.setBounds(314, 122, 63, 24);
		contentPane.add(pitchBox);
		
		JLabel label = new JLabel("Time in video:");
		label.setToolTipText("Time to add the audio into the video");
		label.setBounds(25, 184, 105, 15);
		contentPane.add(label);
		
		textField_1 = new JTextField();
		textField_1.setToolTipText("Minutes in the video");
		textField_1.setText("0");
		textField_1.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_1.setColumns(10);
		textField_1.setBounds(130, 185, 22, 19);
		contentPane.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setToolTipText("Minutes in the video");
		textField_2.setText("0");
		textField_2.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_2.setColumns(10);
		textField_2.setBounds(169, 185, 23, 19);
		contentPane.add(textField_2);
		
		JButton button = new JButton("Current time");
		button.setToolTipText("Inserts the current time into the boxes to the left");
		button.setBackground(Color.WHITE);
		button.setBounds(216, 179, 161, 25);
		contentPane.add(button);
		
		Label label_1 = new Label("m");
		label_1.setBounds(152, 184, 15, 21);
		contentPane.add(label_1);
		
		Label label_2 = new Label("s");
		label_2.setBounds(192, 184, 19, 21);
		contentPane.add(label_2);
	}
}
