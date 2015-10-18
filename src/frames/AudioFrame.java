package frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import tools.FileTools;
import videos.CustomAudio;

/**
 * A GUI which displays to the user what audio they have already added to the
 * existing project, and an option to add more / remove previous.
 * 
 * @author andrew
 *
 */
public class AudioFrame extends JFrame {

	private JPanel contentPane;
	private JTable audioTable;

	public static AudioFrame aFrame = new AudioFrame();
	private JLabel lblAudioAddedTo;
	
	private String selectedAudio = null; //keeps track of the row selected (to delete)

	/**
	 * Updates the jtable based on any added audio to the video
	 */
	public void updateAudio() {
		DefaultTableModel model = (DefaultTableModel) audioTable.getModel();
		model.setRowCount(0); // clear it
		for (CustomAudio a : MainFrame.mFrame.project.getAudio()) { // add the
																	// player
			// contents
			model.addRow(new Object[] { a.getText(), FileTools.LongToTime(a.getStart()), a.getDuration() });
		}
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
		//A visual representation of what audio has been added to the video.
		audioTable = new JTable();
		audioTable.setBounds(new Rectangle(100, 0, 100, 0));
		audioTable.setBorder(new LineBorder(new Color(0, 0, 0)));
		audioTable.setModel(new DefaultTableModel(new Object[][] { { null, null, null }, },
				new String[] { "Name", "Start Time", "Duration (s)" }));
		audioTable.setBounds(37, 74, 269, 279);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setBounds(37, 74, 269, 279);
		contentPane.add(scrollPane);
		final JButton btnRemoveAudio = new JButton("Remove audio");
		
		//handles the row selection listener, will need to enable/disable remove audio accordingly
		audioTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent event) {
		        if (audioTable.getSelectedRow() > -1) {
		            //extracts the name
		            selectedAudio = audioTable.getValueAt(audioTable.getSelectedRow(), 0).toString();
		            btnRemoveAudio.setEnabled(true);
		        }else{
		        	btnRemoveAudio.setEnabled(false);
		        }
		    }
		});

		scrollPane.setViewportView(audioTable);

		lblAudioAddedTo = new JLabel("Audio added to current video");
		lblAudioAddedTo.setBounds(66, 47, 240, 15);
		contentPane.add(lblAudioAddedTo);

		JButton btnAddAudio = new JButton("Add Audio");
		btnAddAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommentaryFrame.cmFrame.setLocationRelativeTo(null);
				CommentaryFrame.cmFrame.setVisible(true);
				//MainFrame.mFrame.setEnabled(false);
				//aFrame.setEnabled(false);
				MainFrame.mFrame.pauseVideo();
			}
		});
		btnAddAudio.setBounds(37, 365, 128, 25);
		contentPane.add(btnAddAudio);
		
		btnRemoveAudio.setEnabled(false);
		btnRemoveAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// confirm they want to delete
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this audio?",
						"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					MainFrame.mFrame.project.RemoveAudio(selectedAudio);
				}
			}
		});
		btnRemoveAudio.setBounds(164, 365, 147, 25);
		contentPane.add(btnRemoveAudio);
	}
}
