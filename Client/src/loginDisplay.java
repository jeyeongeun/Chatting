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
	private ClientThread thread;		// 쓰레드
	private registerDisplay regD;		// 회원가입 화면
	private JPasswordField passwordField;	// 비밀번호
	
	public loginDisplay(ClientThread th) {
		// 기본 설정 : 크기고정, x누르면 프로그램 종료
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(371, 300);
		setResizable(false);
	
		thread = th;
		getContentPane().setLayout(null);
		getContentPane().setBounds(5,10,460,215);
		
		// id 입력
		textField_ID = new JTextField();
		textField_ID.setForeground(Color.BLACK);
		textField_ID.setFont(new Font("돋움", Font.PLAIN, 12));
		textField_ID.setBounds(150, 55, 116, 29);
		getContentPane().add(textField_ID);
		textField_ID.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\uC544\uC774\uB514");
		lblNewLabel.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel.setBounds(84, 55, 54, 29);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\uBE44\uBC00\uBC88\uD638");
		lblNewLabel_1.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel_1.setBounds(84, 104, 57, 26);
		getContentPane().add(lblNewLabel_1);
		
		// 로그인 버튼
		JButton btnLogin = new JButton("\uB85C\uADF8\uC778");
		btnLogin.setFont(new Font("돋움", Font.PLAIN, 15));
		// 로그인 버튼이 눌려지면 로그인 요청하고 이 화면 끝
		// 아이디를 먼저 보내고 다음에 로그인 요청을 한다
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
		
		// 회원가입 버튼
		JButton btnRegister = new JButton("\uD68C\uC6D0\uAC00\uC785");
		btnRegister.setFont(new Font("돋움", Font.PLAIN, 15));
		// 회원가입 버튼이 눌려지면 회원가입 화면 띄우기
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
		
		// 비밀번호 
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("돋움", Font.PLAIN, 12));
		passwordField.setBounds(150, 101, 116, 29);
		getContentPane().add(passwordField);
	}
}
