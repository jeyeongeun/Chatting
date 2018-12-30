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

// 회원 가입 display
public class registerDisplay extends JFrame {
	
	private JTextField textField_ID;	// 아이디
	private JTextField textField_Name;	// 이름
	private JPasswordField passwordField;	// 비밀번호 - 암호화로 출력되게
	
	private JLabel lblNewLabel_2;
	private JButton btn_Register;	// 회원가입 버튼
	private ClientThread thread;	// 현재 thread
	
	
	public registerDisplay(ClientThread th) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		// 사이즈 고정 변경 불가
		setSize(301, 245);
		setResizable(false);
		
		thread = th;

		getContentPane().setLayout(null);
		getContentPane().setBounds(5,10,460,215);
		
		JLabel lblNewLabel = new JLabel("\uC544\uC774\uB514");
		lblNewLabel.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel.setBounds(41, 40, 57, 21);
		getContentPane().add(lblNewLabel);
		
		// 아이디
		textField_ID = new JTextField();
		textField_ID.setBounds(113, 40, 116, 24);
		getContentPane().add(textField_ID);
		textField_ID.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\uBE44\uBC00\uBC88\uD638");
		lblNewLabel_1.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel_1.setBounds(41, 78, 57, 21);
		getContentPane().add(lblNewLabel_1);
		
		lblNewLabel_2 = new JLabel("\uC774\uB984");
		lblNewLabel_2.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel_2.setBounds(41, 115, 57, 21);
		getContentPane().add(lblNewLabel_2);
		
		// 이름
		textField_Name = new JTextField();
		textField_Name.setBounds(113, 115, 116, 24);
		getContentPane().add(textField_Name);
		textField_Name.setColumns(10);
		
		// 회원가입 버튼
		btn_Register = new JButton("\uD68C\uC6D0\uAC00\uC785");
		btn_Register.setFont(new Font("돋움", Font.PLAIN, 13));
		// 회원가입 버튼이 눌려지면
		btn_Register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btn_Register) {
					// 각 필드에서 정보받아와서 thread 에 회원가입 요청
					th.requestRegister(textField_ID.getText(), passwordField.getText(), 
							textField_Name.getText());
					// 회원가입 창 종료
					setVisible(false);
					dispose();
				}
			}
		});
		btn_Register.setBounds(87, 160, 116, 29);
		getContentPane().add(btn_Register);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("돋움", Font.PLAIN, 12));
		passwordField.setBounds(113, 78, 116, 24);
		getContentPane().add(passwordField);		
	}
}
