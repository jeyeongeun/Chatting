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

	
	//�޴� ȭ���� �����ֱ� ���� ����ϴ� ��� �������Դϴ�.
	private JPanel contentPane; // ��ư�� �����ϱ� ���� ����ϴ� ����Ʈ ���Դϴ�.
	private JButton sendBtn;
	private JTextField tf_ID;
	private JTextField msg;
	
	private String paper_arlm = null;
	
	private ClientThread thread;
	private String IdTo;
	
	//�޴� Ŭ������ ���� �����ڷμ�, login_GUI Ŭ�������� �ν��Ͻ� ���� ��, ���޹��� ������ ��� ������ �ʱ�ȭ�մϴ�.
	public Paper(ClientThread clith, String ID){
		IdTo = ID;
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
				//imt[0] = imoticon;
			}//While ���� ������ �κ��Դϴ�.

		}//run�� ������ �κ��Դϴ�.
	});

//�� �κп� ���ؼ��� �����غ� �ʿ䰡 ����.
th.start();

	}
		
		
	public void init(){
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ����� "X" ��ư�� ����� Ȱ��ȭ�մϴ�.
		setBounds(100, 100, 342, 182); // â�� ������ �����մϴ�.
		setTitle("����");
		contentPane = new JPanel(); // ����Ʈ ���� �����մϴ�.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); //����Ʈ ���� ������ ������ �����մϴ�.
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_id = new JLabel("ID");
		lblNewLabel_id.setBounds(12, 5, 30, 40);
		contentPane.add(lblNewLabel_id); //����Ʈ�ҿ� ���� �����մϴ�.
		
		JLabel lblNewLabel_msg = new JLabel("MSG");
		lblNewLabel_msg.setBounds(12, 55, 30, 40);
		contentPane.add(lblNewLabel_msg); //����Ʈ�ҿ� ���� �����մϴ�.
		
		tf_ID = new JTextField();
		tf_ID.setBounds(54, 10, 100, 33);
		tf_ID.setEditable(false);
		contentPane.add(tf_ID);
		tf_ID.setColumns(10); //������ ���� ���̸� �����մϴ�.
		tf_ID.setText(IdTo);
		
		msg = new JTextField();
		msg.setBounds(52, 59, 270, 33);
		contentPane.add(msg);
		msg.setColumns(10); //������ ���� ���̸� �����մϴ�.
		
		sendBtn = new JButton("������");
		sendBtn.setBounds(67, 106, 192, 33);
		contentPane.add(sendBtn);
		
		setVisible(true); //ȭ���� ���������� �մϴ�.
	}
	
	public void btnAction_Def() { // ���� ��ư�� ����� �����մϴ�.

		sendBtn.addActionListener(new btnAction()); // ActionListener�� ���� �����Ͽ�, ActionListener�� ��ư�� �߰��մϴ�.
		
	}

	class btnAction implements ActionListener // ���� ��ư�� ������ ��, ����Ǵ� ����� ������ Ŭ�����Դϴ�.
	{

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == sendBtn) // �׼��̺�Ʈ�� ���۹�ư���� �߻��ߴٴ� ���̶��,
			{
				paper_arlm = msg.getText();
				thread.requestSendPaper(IdTo, paper_arlm);
				dispose();
			}
			

		}

	}
	
}
