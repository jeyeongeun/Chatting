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

// 사용자 정보보기 출력 클래스
public class userInfoDisplay extends JFrame {
	private JTextField textField_ID;	// ID
	private JLabel lblNewLabel_2;
	private JTextField textField_Name;	// 이름
	private JButton btn_OK;		// 확인 버튼
	private ClientThread thread;	// 현재의 thread
	private String gender;		// 성별
	private String hobby;		// 취미 String
	
	// 생성자
	// Parameter : 현재 thread, 출력할 유저
	public userInfoDisplay(ClientThread th, User user) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// 사이즈 고정, 변경 불가
		setSize(301, 214);
		setResizable(false);
		
		// 쓰레드 설정
		thread = th;
		
		getContentPane().setLayout(null);
		getContentPane().setBounds(5,10,460,215);
		
		JLabel lblNewLabel = new JLabel("\uC544\uC774\uB514");
		lblNewLabel.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel.setBounds(41, 40, 57, 21);
		getContentPane().add(lblNewLabel);
		
		// ID
		textField_ID = new JTextField();
		textField_ID.setEditable(false);	// 수정 불가
		textField_ID.setBounds(113, 40, 116, 24);
		getContentPane().add(textField_ID);
		textField_ID.setColumns(10);
		textField_ID.setText(user.getID());
		
		lblNewLabel_2 = new JLabel("\uC774\uB984");
		lblNewLabel_2.setFont(new Font("돋움", Font.BOLD, 13));
		lblNewLabel_2.setBounds(41, 80, 57, 21);
		getContentPane().add(lblNewLabel_2);
		
		// 이름
		textField_Name = new JTextField();
		textField_Name.setEditable(false);	// 수정 불가
		textField_Name.setBounds(113, 80, 116, 24);
		getContentPane().add(textField_Name);
		textField_Name.setColumns(10);
		textField_Name.setText(user.getName());
		
		// 확인 버튼 선택시 창 종료
		btn_OK = new JButton("\uD655\uC778");
		btn_OK.setFont(new Font("돋움", Font.PLAIN, 13));
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

