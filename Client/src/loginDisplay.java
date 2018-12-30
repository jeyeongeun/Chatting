import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class loginDisplay extends JFrame {
	private JTextField textField_ID;	// id
	private ClientThread thread;		// ������
	private registerDisplay regD;		// ȸ������ ȭ��
	private JPasswordField passwordField;	// ��й�ȣ
	
	public loginDisplay(ClientThread th) {
		// �⺻ ���� : ũ�����, x������ ���α׷� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(371, 300);
		setResizable(false);
	
		thread = th;
		getContentPane().setLayout(null);
		getContentPane().setBounds(5,10,460,215);
		
		// id �Է�
		textField_ID = new JTextField();
		textField_ID.setForeground(Color.BLACK);
		textField_ID.setFont(new Font("����", Font.PLAIN, 12));
		textField_ID.setBounds(150, 55, 116, 29);
		getContentPane().add(textField_ID);
		textField_ID.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\uC544\uC774\uB514");
		lblNewLabel.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel.setBounds(84, 55, 54, 29);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\uBE44\uBC00\uBC88\uD638");
		lblNewLabel_1.setFont(new Font("����", Font.BOLD, 13));
		lblNewLabel_1.setBounds(84, 104, 57, 26);
		getContentPane().add(lblNewLabel_1);
		
		// �α��� ��ư
		JButton btnLogin = new JButton("\uB85C\uADF8\uC778");
		btnLogin.setFont(new Font("����", Font.PLAIN, 15));
		// �α��� ��ư�� �������� �α��� ��û�ϰ� �� ȭ�� ��
		// ���̵� ���� ������ ������ �α��� ��û�� �Ѵ�
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btnLogin) {
					th.sendMessage(textField_ID.getText());
					th.requestLogin(textField_ID.getText(), passwordField.getText());
					setVisible(false);
					//dispose();
				}
			}
		});
		btnLogin.setBounds(104, 161, 151, 26);
		getContentPane().add(btnLogin);
		
		// ȸ������ ��ư
		JButton btnRegister = new JButton("\uD68C\uC6D0\uAC00\uC785");
		btnRegister.setFont(new Font("����", Font.PLAIN, 15));
		// ȸ������ ��ư�� �������� ȸ������ ȭ�� ����
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btnRegister) {
					regD = new registerDisplay(th);
					regD.setVisible(true);
					setVisible(false);
				}
			}
		});
		btnRegister.setBounds(104, 201, 151, 26);
		getContentPane().add(btnRegister);
		
		// ��й�ȣ 
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("����", Font.PLAIN, 12));
		passwordField.setBounds(150, 101, 116, 29);
		getContentPane().add(passwordField);
	}
}
