import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.proteanit.sql.DbUtils;

public class Notizen {

	private JFrame frame;
	private JTextField txtFldNotiz;
	private JTable table;
	private JTextField textFldSuchen;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private Connection con = null;
	private PreparedStatement pst;
	private ResultSet rs;

	/**
	 * Create the application.
	 */
	public Notizen() {
		initialize();
		connect();
		loadList();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Notizen window = new Notizen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

// connecting the db
	public void connect() {

		try {

			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dvdrental","postgres","1988");
		} catch (Exception e) {
			e.printStackTrace();;
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.getContentPane().setForeground(new Color(205, 133, 63));
		frame.getContentPane().setBackground(new Color(233, 150, 122));
		frame.setTitle("Notizbuch");
		frame.setBounds(100, 100, 1165, 518);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Notizbuch");
		lblNewLabel.setFont(new Font("Poor Richard", Font.BOLD, 39));
		lblNewLabel.setBounds(312, 53, 214, 34);
		frame.getContentPane().add(lblNewLabel);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(233, 150, 122));
		panel.setBorder(new TitledBorder(null, "Eingabe Bereich", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(32, 111, 525, 301);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Titel Notiz");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(10, 32, 104, 14);
		panel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("detaillierte Beschreibung");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_2.setBounds(10, 72, 209, 30);
		panel.add(lblNewLabel_2);

		textArea = new JTextArea();
		textArea.setBorder(BorderFactory.createEtchedBorder());
		textArea.setBounds(229, 77, 286, 106);
		panel.add(textArea);

		txtFldNotiz = new JTextField();
		txtFldNotiz.setBounds(228, 29, 287, 24);
		panel.add(txtFldNotiz);
		txtFldNotiz.setColumns(10);

//		saving data into the database
		JButton btnSave = new JButton("SPEICHERN");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String titel, titDetail, datum;
				titel = txtFldNotiz.getText();
				titDetail = textArea.getText();
//				datum=txtFldDatum.getText();
				LocalDate heute = LocalDate.now();
				Date a = Date.valueOf(heute);

				try {
					pst = con.prepareStatement("insert into notizen(notiz,notBesch,datum) values (?,?,?)");
					pst.setString(1, titel);
					pst.setString(2, titDetail);
					pst.setDate(3, a);
					pst.executeUpdate();
					JOptionPane.showMessageDialog(null, "Record Added!");
					loadList();
					txtFldNotiz.setText("");
					textArea.setText("");
					txtFldNotiz.requestFocus();

				} catch (SQLException i) {
					i.printStackTrace();
				}

			}
		});
		btnSave.setBounds(10, 219, 121, 20);
		panel.add(btnSave);

//		exit the application
		JButton btnExit = new JButton("VERLASSEN");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.exit(0);

			}
		});
		btnExit.setBounds(141, 270, 178, 20);
		panel.add(btnExit);

//		empty fields
		JButton btnClear = new JButton("FELDER LEEREN");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				txtFldNotiz.setText("");
				textArea.setText("");

			}
		});
		btnClear.setBounds(10, 270, 121, 20);
		panel.add(btnClear);

//		the whole list will be deleted
		JButton btnDeleteAll = new JButton("ALLE EINTR\u00C4GE L\u00D6SCHEN");
		btnDeleteAll.setBounds(141, 246, 178, 20);
		panel.add(btnDeleteAll);
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					pst = con.prepareStatement("delete from notizen ");
					pst.executeUpdate();
					JOptionPane.showMessageDialog(null, "Alle Einträge wurde gelöscht");
					loadList();
					txtFldNotiz.setText("");
					textArea.setText("");
					txtFldNotiz.requestFocus();

				} catch (SQLException i) {
					i.printStackTrace();
				}

			}
		});

		JButton btnDelete = new JButton("EINTRAG L\u00D6SCHEN");
		btnDelete.setBounds(141, 219, 178, 20);
		panel.add(btnDelete);

//		selected record will be deleted
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int notId;
				notId = Integer.parseInt(textFldSuchen.getText());

				try {
					pst = con.prepareStatement("delete from notizen where notid=? ");
					pst.setInt(1, notId);
					pst.executeUpdate();
					JOptionPane.showMessageDialog(null, "Der Notiz mit der NotizId: " + notId + " wurde gelöscht");
					loadList();
					txtFldNotiz.setText("");
					textArea.setText("");
					txtFldNotiz.requestFocus();

				} catch (SQLException i) {
					i.printStackTrace();
				}

			}
		});

		JButton btnUpdate = new JButton("AKTUALISIEREN");
		btnUpdate.setBounds(10, 246, 121, 20);
		panel.add(btnUpdate);

//		selected record will be updated
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String titel, titDetail, datum;
				int notId;
				notId = Integer.parseInt(textFldSuchen.getText());
				titel = txtFldNotiz.getText();
				titDetail = textArea.getText();

				LocalDate heute = LocalDate.now();
				Date a = Date.valueOf(heute);

				try {
					pst = con.prepareStatement("update notizen set notiz=?,notbesch=?,datum=? where notid=? ");
					pst.setString(1, titel);
					pst.setString(2, titDetail);
					pst.setDate(3, a);
					pst.setInt(4, notId);
					pst.executeUpdate();
					JOptionPane.showMessageDialog(null, "Anpassungen wurden umgesetzt!");
					loadList();
					txtFldNotiz.setText("");
					textArea.setText("");
					txtFldNotiz.requestFocus();

				} catch (SQLException i) {
					i.printStackTrace();
				}

			}
		});

		scrollPane = new JScrollPane();
		scrollPane.setBounds(567, 118, 559, 294);
		frame.getContentPane().add(scrollPane);
		table = new JTable();
		scrollPane.setViewportView(table);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(233, 150, 122));
		panel_1.setBorder(new TitledBorder(null, "Suchen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(32, 41, 151, 59);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel lblSuchen = new JLabel("Notiz ID");
		lblSuchen.setFont(new Font("Tahoma", Font.ITALIC, 14));
		lblSuchen.setBounds(10, 25, 61, 23);
		panel_1.add(lblSuchen);

		textFldSuchen = new JTextField();

//		searches information in the database for the matching id
		textFldSuchen.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {

				try {
					int notId = Integer.parseInt(textFldSuchen.getText());
					pst = con.prepareStatement("select notiz, notbesch from notizen where notid=?");
					pst.setInt(1, notId);
					ResultSet rs = pst.executeQuery();

					if (rs.next() == true) {
						String notiz = rs.getString(1);
						String notBe = rs.getString(2);

						txtFldNotiz.setText(notiz);
						textArea.setText(notBe);

					} else {
						txtFldNotiz.setText("");
						textArea.setText("");
					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		textFldSuchen.setBounds(81, 28, 38, 20);
		panel_1.add(textFldSuchen);
		textFldSuchen.setColumns(10);
	}

//	loads the note list from database
	public void loadList() {
		try {
			pst = con.prepareStatement("select * from notizen");
			rs = pst.executeQuery();
			table.setModel(DbUtils.resultSetToTableModel(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
