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

// ȸ�� ���� display
public class registerDisplay extends JFrame {
	
	private JTextField textField_ID;	// ���̵�
	private JTextField textField_Name;	// �̸�
	private JPasswordField passwordField;	// ��й�ȣ - ��ȣȭ�� ��µǰ�
	
	private JLabel lblNewLabel_2;
	private JButton btn_Register;	// ȸ������ ��ư
	private ClientThread thread;	// ���� thread
	
	
	public registerDisplay(ClientThread th) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		// ������ ���� ���� �Ұ�
		setSize(301, 245);
		setResizable(false);
		
		thread = th;

		getContentPane().setLayout(null);
		getContentPane().setBounds(5,10,460,215);
		
		JLabel lblNewLabel = new JLabel("\uC544\uC774\uB514");
		lblNewLabel.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel.setBounds(41, 40, 57, 21);
		getContentPane().add(lblNewLabel);
		
		// ���̵�
		textField_ID = new JTextField();
		textField_ID.setBounds(113, 40, 116, 24);
		getContentPane().add(textField_ID);
		textField_ID.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\uBE44\uBC00\uBC88\uD638");
		lblNewLabel_1.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel_1.setBounds(41, 78, 57, 21);
		getContentPane().add(lblNewLabel_1);
		
		lblNewLabel_2 = new JLabel("\uC774\uB984");
		lblNewLabel_2.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel_2.setBounds(41, 115, 57, 21);
		getContentPane().add(lblNewLabel_2);
		
		// �̸�
		textField_Name = new JTextField();
		textField_Name.setBounds(113, 115, 116, 24);
		getContentPane().add(textField_Name);
		textField_Name.setColumns(10);
		
		// ȸ������ ��ư
		btn_Register = new JButton("\uD68C\uC6D0\uAC00\uC785");
		btn_Register.setFont(new Font("����", Font.PLAIN, 13));
		// ȸ������ ��ư�� ��������
		btn_Register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btn_Register) {
					// �� �ʵ忡�� �����޾ƿͼ� thread �� ȸ������ ��û
					th.requestRegister(textField_ID.getText(), passwordField.getText(), 
							textField_Name.getText());
					// ȸ������ â ����
					setVisible(false);
					dispose();
				}
			}
		});
		btn_Register.setBounds(87, 160, 116, 29);
		getContentPane().add(btn_Register);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("����", Font.PLAIN, 12));
		passwordField.setBounds(113, 78, 116, 24);
		getContentPane().add(passwordField);		
	}
}
