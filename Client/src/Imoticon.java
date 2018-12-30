import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class Imoticon extends JFrame{
	
	//메뉴 화면을 보여주기 위해 사용하는 멤버 변수들입니다.
	private JPanel contentPane; // 버튼을 부착하기 위해 사용하는 컨텐트 팬입니다.
	private JButton imoBtn1; // 채팅 기능을 실행할 때 눌러야하는 버튼입니다.
	private JButton imoBtn2; // 채팅 기능을 실행할 때 눌러야하는 버튼입니다.
	private JButton imoBtn3; // 채팅 기능을 실행할 때 눌러야하는 버튼입니다.
	private JButton imoBtn4; // 채팅 기능을 실행할 때 눌러야하는 버튼입니다.
	private JButton imoBtn5; // 채팅 기능을 실행할 때 눌러야하는 버튼입니다.
	
	private String imoticon = null;
	private Chatroom chat;
	private ClientThread thread;
	//메뉴 클래스에 대한 생성자로서, login_GUI 클래스에서 인스턴스 생성 시, 전달받은 값으로 멤버 변수를 초기화합니다.
	public Imoticon(ClientThread clith, Chatroom chatroom){
		
		//초기화 과정입니다.
		chat = chatroom;
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
			}//While 문이 닫히는 부분입니다.

		}//run이 닫히는 부분입니다.
	});

//이 부분에 대해서는 조사해볼 필요가 있음.
th.start();

	}
		
		
	public void init(){
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 상단의 "X" 버튼의 기능을 활성화합니다.
		setBounds(100, 100, 277, 301); // 창의 영역을 정의합니다.
		setTitle("이모티콘");
		contentPane = new JPanel(); // 컨텐트 팬을 정의합니다.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //컨텐트 팬의 여백의 영역을 정의합니다.
		setContentPane(contentPane);

		
		imoBtn1 = new JButton(); //메시지를 전송할 때, 눌러야할 버튼입니다. 
		imoBtn1.setBounds(12, 10, 100, 100);
		ImageIcon Icon = new ImageIcon("Imoticon/"+"shyImoticon"+".jpg"); 
		contentPane.setLayout(null);
		imoBtn1.setIcon(Icon);
		contentPane.add(imoBtn1); //버튼을 컨텐트 팬에 부착합니다.
		
		imoBtn2 = new JButton(); //메시지를 전송할 때, 눌러야할 버튼입니다. 
		imoBtn2.setBounds(162, 10, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"cold_sweatImoticon"+".jpg"); 
		imoBtn2.setIcon(Icon);
		contentPane.add(imoBtn2); //버튼을 컨텐트 팬에 부착합니다.
		
		imoBtn3 = new JButton(); //메시지를 전송할 때, 눌러야할 버튼입니다. 
		imoBtn3.setBounds(12, 160, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"smileImoticon"+".jpg"); 
		imoBtn3.setIcon(Icon);
		contentPane.add(imoBtn3); //버튼을 컨텐트 팬에 부착합니다.
		
		imoBtn4 = new JButton(); //메시지를 전송할 때, 눌러야할 버튼입니다. 
		imoBtn4.setBounds(87, 85, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"teethImoticon"+".jpg"); 
		imoBtn4.setIcon(Icon);
		contentPane.add(imoBtn4); //버튼을 컨텐트 팬에 부착합니다.
		
		imoBtn5 = new JButton(); //메시지를 전송할 때, 눌러야할 버튼입니다. 
		imoBtn5.setBounds(162, 160, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"wellImoticon"+".jpg"); 
		imoBtn5.setIcon(Icon);
		contentPane.add(imoBtn5); //버튼을 컨텐트 팬에 부착합니다.

		setVisible(true); //화면이 보여지도록 합니다.
	}
	
	public void btnAction_Def() { // 전송 버튼의 기능을 정의합니다.

		imoBtn1.addActionListener(new Myaction()); // ActionListener를 따로 정의하여, ActionListener를 버튼에 추가합니다.
		imoBtn2.addActionListener(new Myaction()); // ActionListener를 따로 정의하여, ActionListener를 버튼에 추가합니다.
		imoBtn3.addActionListener(new Myaction()); // ActionListener를 따로 정의하여, ActionListener를 버튼에 추가합니다.
		imoBtn4.addActionListener(new Myaction()); // ActionListener를 따로 정의하여, ActionListener를 버튼에 추가합니다.
		imoBtn5.addActionListener(new Myaction()); // ActionListener를 따로 정의하여, ActionListener를 버튼에 추가합니다.
		
	}

	class Myaction implements ActionListener // 실제 버튼을 눌렀을 때, 실행되는 기능을 정의한 클래스입니다.
	{

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == imoBtn1) // 액션이벤트가 전송버튼에서 발생했다는 것이라면,
			{
				imoticon = "shyImoticon";
				
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread를 이용해서 메시지를 전송하고
				
			}
			else if(e.getSource() == imoBtn2)
			{
				imoticon = "cold_sweatImoticon";

				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread를 이용해서 메시지를 전송하고
			}
			else if(e.getSource() == imoBtn3)
			{
				imoticon = "smileImoticon";
				
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread를 이용해서 메시지를 전송
				
			}
			else if(e.getSource() == imoBtn4)
			{
				imoticon = "teethImoticon";
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread를 이용해서 메시지를 전송
			}
			else if(e.getSource() == imoBtn5)
			{
				imoticon = "wellImoticon";
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread를 이용해서 메시지를 전송
			}

		}

	}
	
}
