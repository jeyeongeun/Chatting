import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Paper extends JFrame {

	
	//메뉴 화면을 보여주기 위해 사용하는 멤버 변수들입니다.
	private JPanel contentPane; // 버튼을 부착하기 위해 사용하는 컨텐트 팬입니다.
	private JButton sendBtn;
	private JTextField tf_ID;
	private JTextField msg;
	
	private String paper_arlm = null;
	
	private ClientThread thread;
	private String IdTo;
	
	//메뉴 클래스에 대한 생성자로서, login_GUI 클래스에서 인스턴스 생성 시, 전달받은 값으로 멤버 변수를 초기화합니다.
	public Paper(ClientThread clith, String ID){
		IdTo = ID;
		thread = clith;
		init();
		btnAction_Def();
	}
	
	
public void getImt(){
	Thread th = new Thread(new Runnable() { 

		@Override
		//쓰레드가 실행되면
		public void run() {
			while (true) {
				//imt[0] = imoticon;
			}//While 문이 닫히는 부분입니다.

		}//run이 닫히는 부분입니다.
	});

//이 부분에 대해서는 조사해볼 필요가 있음.
th.start();

	}
		
		
	public void init(){
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 상단의 "X" 버튼의 기능을 활성화합니다.
		setBounds(100, 100, 342, 182); // 창의 영역을 정의합니다.
		setTitle("쪽지");
		contentPane = new JPanel(); // 컨텐트 팬을 정의합니다.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //컨텐트 팬의 여백의 영역을 정의합니다.
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_id = new JLabel("ID");
		lblNewLabel_id.setBounds(12, 5, 30, 40);
		contentPane.add(lblNewLabel_id); //컨텐트팬에 라벨을 부착합니다.
		
		JLabel lblNewLabel_msg = new JLabel("MSG");
		lblNewLabel_msg.setBounds(12, 55, 30, 40);
		contentPane.add(lblNewLabel_msg); //컨텐트팬에 라벨을 부착합니다.
		
		tf_ID = new JTextField();
		tf_ID.setBounds(54, 10, 100, 33);
		tf_ID.setEditable(false);
		contentPane.add(tf_ID);
		tf_ID.setColumns(10); //보여질 행의 길이를 설정합니다.
		tf_ID.setText(IdTo);
		
		msg = new JTextField();
		msg.setBounds(52, 59, 270, 33);
		contentPane.add(msg);
		msg.setColumns(10); //보여질 행의 길이를 설정합니다.
		
		sendBtn = new JButton("보내기");
		sendBtn.setBounds(67, 106, 192, 33);
		contentPane.add(sendBtn);
		
		setVisible(true); //화면이 보여지도록 합니다.
	}
	
	public void btnAction_Def() { // 전송 버튼의 기능을 정의합니다.

		sendBtn.addActionListener(new btnAction()); // ActionListener를 따로 정의하여, ActionListener를 버튼에 추가합니다.
		
	}

	class btnAction implements ActionListener // 실제 버튼을 눌렀을 때, 실행되는 기능을 정의한 클래스입니다.
	{

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == sendBtn) // 액션이벤트가 전송버튼에서 발생했다는 것이라면,
			{
				paper_arlm = msg.getText();
				thread.requestSendPaper(IdTo, paper_arlm);
				dispose();
			}
			

		}

	}
	
}
