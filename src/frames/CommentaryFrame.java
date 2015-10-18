package frames;

import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tools.BashTools;
import tools.FileTools;
import videos.CustomAudio;
import javax.swing.JTabbedPane;
import java.awt.Font;

/**
 * A JFrame which handles the GUI components of adding mp3s to the video (this
 * supports generation of wav files, and adding pre-existing mp3s).
 * 
 * @author alea644
 *
 */
@SuppressWarnings("serial")
public class CommentaryFrame extends JFrame {
	/*
	 * 
	 * Frame used to add/create 'commentary' (text) into a wave file and add to
	 * the video loaded. This JFrame represents the GUI for it.
	 */
	private JPanel contentPane;
	public static CommentaryFrame cmFrame = new CommentaryFrame();
	private JTextArea textField;
	public static boolean loadNewVideoIsChecked = false;
	public static int currentFestID = 0;
	private final JTextField mField, sField;
	
	private boolean showWarning = false; //whether or not to show the warning message.
	private String warningMessage = ""; //shows an error to the user
	private String selectedFile = "";
	
	

	/**
	 * Create the frame.
	 */
	public CommentaryFrame() {
		setResizable(false);
		setTitle("Add Audio");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 414, 381);
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
		final JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BashTools.speakFestival(textField.getText()); //speak the preview
			}
		});
		btnPreview.setEnabled(false);
		btnPreview.setBounds(25, 303, 102, 25);
		btnPreview.setBackground(Color.WHITE);
		contentPane.add(btnPreview);

		// Button returns to mainFrame without saving
		JButton btnBack = new JButton("Back");
		btnBack.setBounds(267, 303, 110, 25);
		btnBack.setBackground(Color.WHITE);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Back button clicked
				// MainFrame was never set to invisible
				// Simply hide this current frame
				textField.setText(""); // clear the text
				cmFrame.setVisible(false);
				MainFrame.mFrame.requestFocus();

			}
		});
		contentPane.add(btnBack);

		// Button for saving synth speech as MP3 file
		final JButton btnSave = new JButton("Apply");
		btnSave.setEnabled(false);
		btnSave.setBounds(139, 303, 116, 25);
		btnSave.setBackground(Color.WHITE);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save button clicked
				String textToSave = textField.getText();
				if (textToSave.equals("")) { // Error, they entered no text!
					FileTools.displayError("Please enter some valid text");
					return;
				}
				double length = BashTools.saveFestToMP3(textToSave);
				if (length > 0) {
					MainFrame.mFrame.project.AddAudio(new CustomAudio(textToSave,
							FileTools.TimeToLong(mField.getText() + ":" + sField.getText()), length));
					MainFrame.mFrame.resetVideo();
					AudioFrame.aFrame.updateAudio();
				}
				setVisible(false);
			}
		});
		contentPane.add(btnSave);

		JLabel lblVoiceSpeed = new JLabel("Voice Speed:"), lblSpeakingOptions = new JLabel("Speaking Options"),
				lblWriteTextBelow = new JLabel("Write text below to add to your video (30 word limit)");
		lblWriteTextBelow.setBounds(21, 12, 378, 15);
		contentPane.add(lblWriteTextBelow);

		lblVoiceSpeed.setEnabled(false);
		lblVoiceSpeed.setBounds(216, 80, 95, 15);
		contentPane.add(lblVoiceSpeed);

		JComboBox speedBox = new JComboBox();
		speedBox.setEnabled(false); // Contains all the options for speed,
									// pre-determined options
		speedBox.setModel(new DefaultComboBoxModel(
				new String[] { "0.10x", "0.20x", "0.30x", "0.40x", "0.50x", "0.60x", "0.70x", "0.80x", "0.90x", "1.00x",
						"1.10x", "1.20x", "1.30x", "1.40x", "1.50x", "1.60x", "1.70x", "1.80x", "1.90x", "2.00x",
						"2.10x", "2.20x", "2.30x", "2.40x", "2.50x", "2.60x", "2.70x", "2.80x", "2.90x", "3.00x" }));
		speedBox.setSelectedIndex(9);
		speedBox.setBounds(314, 75, 63, 24);
		contentPane.add(speedBox);

		lblSpeakingOptions.setBounds(216, 41, 141, 15);
		contentPane.add(lblSpeakingOptions);

		JLabel lblVoicePitch = new JLabel("Pitch change:");
		lblVoicePitch.setEnabled(false);
		lblVoicePitch.setBounds(209, 127, 102, 15);
		contentPane.add(lblVoicePitch);

		JComboBox pitchBox = new JComboBox();
		pitchBox.setEnabled(false);
		pitchBox.setModel(new DefaultComboBoxModel(new String[] { "-60Hz", "-50Hz", "-40Hz", "-30Hz", "-20Hz", "-10Hz",
				"0Hz", "10Hz", "20Hz", "30Hz", "40Hz", "50Hz", "60Hz" }));
		pitchBox.setSelectedIndex(6);
		pitchBox.setBounds(314, 122, 63, 24);
		contentPane.add(pitchBox);

		JLabel label = new JLabel("Time in video:");
		label.setToolTipText("Time to add the audio into the video");
		label.setBounds(25, 268, 105, 15);
		contentPane.add(label);

		// These hold the minutes and seconds in the video
		mField = new JTextField();
		mField.setToolTipText("Minutes in the video");
		mField.setText("0");
		mField.setHorizontalAlignment(SwingConstants.RIGHT);
		mField.setColumns(10);
		mField.setBounds(130, 269, 22, 19);
		contentPane.add(mField);

		sField = new JTextField();
		sField.setToolTipText("Seconds in the video");
		sField.setText("0");
		sField.setHorizontalAlignment(SwingConstants.RIGHT);
		sField.setColumns(10);
		sField.setBounds(169, 269, 23, 19);
		contentPane.add(sField);

		JButton button = new JButton("Current time");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] currentTime = FileTools.LongToTime(MainFrame.mFrame.timeSlider.getValue() * 100).split(":");
				sField.setText(currentTime[1]); // Get the current time of the
												// video
				mField.setText(currentTime[0]); // Then split it into
												// minutes/seconds and applt to
												// GUI
			}
		});
		button.setToolTipText("Inserts the current time into the boxes to the left");
		button.setBackground(Color.WHITE);
		button.setBounds(216, 263, 161, 25);
		contentPane.add(button);

		Label mLabel = new Label("m"), sLabel = new Label("s");
		mLabel.setBounds(152, 268, 15, 21);
		sLabel.setBounds(192, 268, 19, 21);
		contentPane.add(mLabel);
		contentPane.add(sLabel);

		JLabel lblOrAddAn = new JLabel("Or add an existing file (takes priority)");
		lblOrAddAn.setBounds(25, 201, 286, 15);
		contentPane.add(lblOrAddAn);
		final JLabel lblName = new JLabel("N/A");
		final JLabel lblWarning = new JLabel("");
		lblWarning.setForeground(Color.RED);
		// to select an MP3 file to add
		JButton btnSelectFile = new JButton("Select file:");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File mp3 = FileTools.openMP3File(); // select an mp3 file to
													// add.
				if (mp3 != null){ //make sure isnt null!
					lblName.setText(mp3.getAbsolutePath().split(File.separator)[mp3.getAbsolutePath().split(File.separator).length -1]);
					//update name
					btnPreview.setEnabled(true);
					btnSave.setEnabled(true);
					lblWarning.setVisible(false);
				}
			}
		});
		btnSelectFile.setToolTipText("Inserts the current time into the boxes to the left");
		btnSelectFile.setBackground(Color.WHITE);
		btnSelectFile.setBounds(25, 221, 161, 25);
		contentPane.add(btnSelectFile);
		lblName.setBounds(192, 228, 185, 15);
		contentPane.add(lblName);
		lblWarning.setFont(new Font("Dialog", Font.BOLD, 12));
		lblWarning.setBounds(25, 172, 352, 15);
		contentPane.add(lblWarning);
		//an action listener which is called upon new text
		//nbeing added, we can check here for adequate text input.
		textField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				    warn();
				  }
				  public void removeUpdate(DocumentEvent e) {
				    warn();
				  }
				  public void insertUpdate(DocumentEvent e) {
				    warn();
				  }

				  public void warn() {
				     //check here if its adequate data
					  String[] data = textField.getText().trim().split("\\s+",-1);
					  if (data.length > 30 && lblName.getText().equals("N\\A")){ //bad data
						  showWarning = true;
						  warningMessage = "Please no more than 30 words.";
						  btnPreview.setEnabled(false); btnSave.setEnabled(false);
					  }
					  else if (data.length <= 1 && data[0].trim().equals("") && lblName.getText().equals("N/A")){
						  showWarning = true;
						  warningMessage = "Please enter some words";
						  btnPreview.setEnabled(false); btnSave.setEnabled(false);
					  }else{
						  showWarning = false;
						  warningMessage = "";
						  btnPreview.setEnabled(true); btnSave.setEnabled(true);
					  }
					  lblWarning.setText(warningMessage);
					  lblWarning.setVisible(showWarning);
				  }
		});

	}
}
