package frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/**
 * A GUI which displays to the user what audio they have already added to the existing
 * project, and an option to add more / remove previous.
 * @author andrew
 *
 */
public class AudioFrame extends JFrame {

	private JPanel contentPane;
	private JTable audioTable;
	
	public static AudioFrame aFrame = new AudioFrame();
	private JLabel lblAudioAddedTo;
	
	/**
	 * Updates the jtable based on any added audio to the video
	 */
	public void updateAudio()
	{
		//check if file doesnt exist, if so, new project!
	}

	/**
	 * Create the frame.
	 */
	public AudioFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 352, 454);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAudioPanel = new JLabel("Audio Panel");
		lblAudioPanel.setFont(new Font("Dialog", Font.BOLD, 24));
		lblAudioPanel.setBounds(86, -12, 206, 63);
		contentPane.add(lblAudioPanel);
		
		audioTable = new JTable();
		audioTable.setBorder(new LineBorder(new Color(0, 0, 0)));
		audioTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Duration (s)", "Start Time", "Name"
			}
		));
		audioTable.setBounds(37, 74, 269, 279);
		contentPane.add(audioTable);
		
		lblAudioAddedTo = new JLabel("Audio added to current video");
		lblAudioAddedTo.setBounds(66, 47, 240, 15);
		contentPane.add(lblAudioAddedTo);
		
		JButton btnAddAudio = new JButton("Add Audio");
		btnAddAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommentaryFrame.cmFrame.setLocationRelativeTo(null);
				CommentaryFrame.cmFrame.setVisible(true);
			}
		});
		btnAddAudio.setBounds(37, 365, 128, 25);
		contentPane.add(btnAddAudio);
		
		JButton btnRemoveAudio = new JButton("Remove audio");
		btnRemoveAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//confirm they want to delete
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this audio?", "Confirm",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					MainFrame.mFrame.ChangesMade();
				}
			}
		});
		btnRemoveAudio.setBounds(164, 365, 147, 25);
		contentPane.add(btnRemoveAudio);
	}
}
