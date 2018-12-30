import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class Imoticon extends JFrame{
	
	//�޴� ȭ���� �����ֱ� ���� ����ϴ� ��� �������Դϴ�.
	private JPanel contentPane; // ��ư�� �����ϱ� ���� ����ϴ� ����Ʈ ���Դϴ�.
	private JButton imoBtn1; // ä�� ����� ������ �� �������ϴ� ��ư�Դϴ�.
	private JButton imoBtn2; // ä�� ����� ������ �� �������ϴ� ��ư�Դϴ�.
	private JButton imoBtn3; // ä�� ����� ������ �� �������ϴ� ��ư�Դϴ�.
	private JButton imoBtn4; // ä�� ����� ������ �� �������ϴ� ��ư�Դϴ�.
	private JButton imoBtn5; // ä�� ����� ������ �� �������ϴ� ��ư�Դϴ�.
	
	private String imoticon = null;
	private Chatroom chat;
	private ClientThread thread;
	//�޴� Ŭ������ ���� �����ڷμ�, login_GUI Ŭ�������� �ν��Ͻ� ���� ��, ���޹��� ������ ��� ������ �ʱ�ȭ�մϴ�.
	public Imoticon(ClientThread clith, Chatroom chatroom){
		
		//�ʱ�ȭ �����Դϴ�.
		chat = chatroom;
		thread = clith;
		init();
		btnAction_Def();
	}
	
	
public void getImt(){
	Thread th = new Thread(new Runnable() { 

		@Override
		//�����尡 ����Ǹ�
		public void run() {
			while (true) {
			}//While ���� ������ �κ��Դϴ�.

		}//run�� ������ �κ��Դϴ�.
	});

//�� �κп� ���ؼ��� �����غ� �ʿ䰡 ����.
th.start();

	}
		
		
	public void init(){
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ����� "X" ��ư�� ����� Ȱ��ȭ�մϴ�.
		setBounds(100, 100, 277, 301); // â�� ������ �����մϴ�.
		setTitle("�̸�Ƽ��");
		contentPane = new JPanel(); // ����Ʈ ���� �����մϴ�.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //����Ʈ ���� ������ ������ �����մϴ�.
		setContentPane(contentPane);

		
		imoBtn1 = new JButton(); //�޽����� ������ ��, �������� ��ư�Դϴ�. 
		imoBtn1.setBounds(12, 10, 100, 100);
		ImageIcon Icon = new ImageIcon("Imoticon/"+"shyImoticon"+".jpg"); 
		contentPane.setLayout(null);
		imoBtn1.setIcon(Icon);
		contentPane.add(imoBtn1); //��ư�� ����Ʈ �ҿ� �����մϴ�.
		
		imoBtn2 = new JButton(); //�޽����� ������ ��, �������� ��ư�Դϴ�. 
		imoBtn2.setBounds(162, 10, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"cold_sweatImoticon"+".jpg"); 
		imoBtn2.setIcon(Icon);
		contentPane.add(imoBtn2); //��ư�� ����Ʈ �ҿ� �����մϴ�.
		
		imoBtn3 = new JButton(); //�޽����� ������ ��, �������� ��ư�Դϴ�. 
		imoBtn3.setBounds(12, 160, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"smileImoticon"+".jpg"); 
		imoBtn3.setIcon(Icon);
		contentPane.add(imoBtn3); //��ư�� ����Ʈ �ҿ� �����մϴ�.
		
		imoBtn4 = new JButton(); //�޽����� ������ ��, �������� ��ư�Դϴ�. 
		imoBtn4.setBounds(87, 85, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"teethImoticon"+".jpg"); 
		imoBtn4.setIcon(Icon);
		contentPane.add(imoBtn4); //��ư�� ����Ʈ �ҿ� �����մϴ�.
		
		imoBtn5 = new JButton(); //�޽����� ������ ��, �������� ��ư�Դϴ�. 
		imoBtn5.setBounds(162, 160, 100, 100);
		Icon = new ImageIcon("Imoticon/"+"wellImoticon"+".jpg"); 
		imoBtn5.setIcon(Icon);
		contentPane.add(imoBtn5); //��ư�� ����Ʈ �ҿ� �����մϴ�.

		setVisible(true); //ȭ���� ���������� �մϴ�.
	}
	
	public void btnAction_Def() { // ���� ��ư�� ����� �����մϴ�.

		imoBtn1.addActionListener(new Myaction()); // ActionListener�� ���� �����Ͽ�, ActionListener�� ��ư�� �߰��մϴ�.
		imoBtn2.addActionListener(new Myaction()); // ActionListener�� ���� �����Ͽ�, ActionListener�� ��ư�� �߰��մϴ�.
		imoBtn3.addActionListener(new Myaction()); // ActionListener�� ���� �����Ͽ�, ActionListener�� ��ư�� �߰��մϴ�.
		imoBtn4.addActionListener(new Myaction()); // ActionListener�� ���� �����Ͽ�, ActionListener�� ��ư�� �߰��մϴ�.
		imoBtn5.addActionListener(new Myaction()); // ActionListener�� ���� �����Ͽ�, ActionListener�� ��ư�� �߰��մϴ�.
		
	}

	class Myaction implements ActionListener // ���� ��ư�� ������ ��, ����Ǵ� ����� ������ Ŭ�����Դϴ�.
	{

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == imoBtn1) // �׼��̺�Ʈ�� ���۹�ư���� �߻��ߴٴ� ���̶��,
			{
				imoticon = "shyImoticon";
				
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread�� �̿��ؼ� �޽����� �����ϰ�
				
			}
			else if(e.getSource() == imoBtn2)
			{
				imoticon = "cold_sweatImoticon";

				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread�� �̿��ؼ� �޽����� �����ϰ�
			}
			else if(e.getSource() == imoBtn3)
			{
				imoticon = "smileImoticon";
				
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread�� �̿��ؼ� �޽����� ����
				
			}
			else if(e.getSource() == imoBtn4)
			{
				imoticon = "teethImoticon";
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread�� �̿��ؼ� �޽����� ����
			}
			else if(e.getSource() == imoBtn5)
			{
				imoticon = "wellImoticon";
				thread.requestSendMessage(chat.getChatroomID(), imoticon);	// thread�� �̿��ؼ� �޽����� ����
			}

		}

	}
	
}
