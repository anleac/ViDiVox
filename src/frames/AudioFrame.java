package frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;
import java.awt.Color;
/**
 * A GUI which displays to the user what audio they have already added to the existing
 * project, and an option to add more / remove previous.
 * @author andrew
 *
 */
public class AudioFrame extends JFrame {

	private JPanel contentPane;
	private JTable audioTable;

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
		audioTable.setBounds(54, 75, 238, 261);
		contentPane.add(audioTable);
	}
}
