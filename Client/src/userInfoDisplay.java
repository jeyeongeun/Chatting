import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

// ����� �������� ��� Ŭ����
public class userInfoDisplay extends JFrame {
	private JTextField textField_ID;	// ID
	private JLabel lblNewLabel_2;
	private JTextField textField_Name;	// �̸�
	private JButton btn_OK;		// Ȯ�� ��ư
	private ClientThread thread;	// ������ thread
	private String gender;		// ����
	private String hobby;		// ��� String
	
	// ������
	// Parameter : ���� thread, ����� ����
	public userInfoDisplay(ClientThread th, User user) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// ������ ����, ���� �Ұ�
		setSize(301, 214);
		setResizable(false);
		
		// ������ ����
		thread = th;
		
		getContentPane().setLayout(null);
		getContentPane().setBounds(5,10,460,215);
		
		JLabel lblNewLabel = new JLabel("\uC544\uC774\uB514");
		lblNewLabel.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel.setBounds(41, 40, 57, 21);
		getContentPane().add(lblNewLabel);
		
		// ID
		textField_ID = new JTextField();
		textField_ID.setEditable(false);	// ���� �Ұ�
		textField_ID.setBounds(113, 40, 116, 24);
		getContentPane().add(textField_ID);
		textField_ID.setColumns(10);
		textField_ID.setText(user.getID());
		
		lblNewLabel_2 = new JLabel("\uC774\uB984");
		lblNewLabel_2.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel_2.setBounds(41, 80, 57, 21);
		getContentPane().add(lblNewLabel_2);
		
		// �̸�
		textField_Name = new JTextField();
		textField_Name.setEditable(false);	// ���� �Ұ�
		textField_Name.setBounds(113, 80, 116, 24);
		getContentPane().add(textField_Name);
		textField_Name.setColumns(10);
		textField_Name.setText(user.getName());
		
		// Ȯ�� ��ư ���ý� â ����
		btn_OK = new JButton("\uD655\uC778");
		btn_OK.setFont(new Font("����", Font.PLAIN, 13));
		btn_OK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btn_OK) {
					dispose();
				}
			}
		});
		btn_OK.setBounds(86, 129, 116, 29);
		getContentPane().add(btn_OK);
	}
}

