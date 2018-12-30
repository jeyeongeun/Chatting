import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

// ä�ù� ���÷��� Ŭ����
// UI ��� �κи� ����Ѵ�.
public class ChatroomDisplay extends JFrame {
	private JTextField textField;	// ä�� ���� �Է��� textfield
	private ClientThread thread;	// ���� ����ǰ� �ִ� thread
	private Chatroom chat;		// �ش� UI�� ����� ä�ù� ����
	private String message;		// ����ڰ� �Է��� �޽��� ����
	private JList listMember = new JList();	// ä�� ������ ��� ���
	private String selectedMem;		// ä�� ������ ��� ����Ʈ���� ���õ� ����� ID ����
	private JTextField textField_curUser;	// ���� ����� ���̵� ���
	private joinUserDisplay jd ;		// joinUserDisplay Ŭ������ ���� �ν��Ͻ� - �ش� ȭ�� ǥ���� �� ��� ��.

	private JTextPane jp ;		// ä�� ������ ����� textpane
	private static StyledDocument doc; //JTextPane�� ��Ÿ�� ��Ŀ� ���� ������ ��� �ִ� �����Դϴ�.
	private Style s; // ��Ÿ�� ��� �� ������ ���� �����Դϴ�.
	
	// �ش� UI�� ä�ù� ���̵� ����
	public int getChatroomID() {
		return chat.getChatroomID(); //chat Ŭ������ getChatroomID() �޼��带 ���� �� ��ȣ�� ���´�.
	}
	// ���� �ʿ��� �޽����� �� ��, ȣ��Ǵ� �޼���μ�,
	// �ش� ä�ù����� �޽����� �������� �� �̸� TextArea�� ���
	// Parameter: �޽����� ������ ������� id�� �޽��� ����
	public void inMessage(String id, String message) {

		// message�� sendFile�� ��,
		if(message.equals("SendFile")) {
			try {
				doc.insertString(doc.getLength(), id+" ���� ������ �����߽��ϴ�.\n", null); // ������ ���۵Ǿ��ٴ� ���� �˸���. 
				setEndline(); // �� �ٲ�
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// message�� Imoticon ���õ� �޽����� ��,
		// ���� ���´� ��, ǥ���� ��, �̹��� �ּҸ� ���� �̸�Ƽ�� ǥ��
		else if(message.equals("shyImoticon")) {
			try {
				doc.insertString(doc.getLength(), id+" :   ", doc.getStyle("green"));
				setEndline();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			ImageIcon Icon = new ImageIcon("Imoticon/"+"shyImoticon"+".jpg");
			//jp�� ImageIcon�� �߰��մϴ�.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//���� ���ڸ� �����մϴ�.
			jp.replaceSelection("\n");
			setEndline();
		}
		else if(message.equals("smileImoticon")) {
			try {
				doc.insertString(doc.getLength(), id+" :   ", doc.getStyle("green"));
				setEndline();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			ImageIcon Icon = new ImageIcon("Imoticon/"+"smileImoticon"+".jpg");
			//jp�� ImageIcon�� �߰��մϴ�.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//���� ���ڸ� �����մϴ�.
			jp.replaceSelection("\n");
			setEndline();
		}
		else if(message.equals("teethImoticon")) {
			try {
				doc.insertString(doc.getLength(), id+" :   ", doc.getStyle("green"));
				setEndline();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			ImageIcon Icon = new ImageIcon("Imoticon/"+"teethImoticon"+".jpg");
			//jp�� ImageIcon�� �߰��մϴ�.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//���� ���ڸ� �����մϴ�.
			jp.replaceSelection("\n");
			setEndline();
		}
		else if(message.equals("wellImoticon")) {
			try {
				doc.insertString(doc.getLength(), id+" :   ", doc.getStyle("green"));
				setEndline();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			ImageIcon Icon = new ImageIcon("Imoticon/"+"wellImoticon"+".jpg");
			//jp�� ImageIcon�� �߰��մϴ�.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//���� ���ڸ� �����մϴ�.
			jp.replaceSelection("\n");
			setEndline();
		}
		else if(message.equals("cold_sweatImoticon")) {
			try {
				doc.insertString(doc.getLength(), id+" :   ", doc.getStyle("green"));
				setEndline();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			ImageIcon Icon = new ImageIcon("Imoticon/"+"cold_sweatImoticon"+".jpg");
			//jp�� ImageIcon�� �߰��մϴ�.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//���� ���ڸ� �����մϴ�.
			jp.replaceSelection("\n");
			setEndline();
		}
		// ���� ��쿡 ��� �ش���� ���� ��, ��, �ܼ��� �޽������� �ְ� ���� ��
		else {
			try {
				doc.insertString(doc.getLength(), id+" :   "+message +"\n", null);
				setEndline();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void setEndline() { 
		// TODO Auto-generated method stub
		jp.selectAll();
		//������ ���� ������ Ŀ�� �̵��ϰ� ����
		jp.setSelectionStart(jp.getSelectionEnd());
	}
	// ������ - ��� �ֿ� �κ� ���
	// Parameter: ���� �������� ������� �ش� UI�� ����� ä�ù��� ����
	public ChatroomDisplay(ClientThread th, Chatroom chatroom) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // x��ư ������ dispose
		
		// ������ ����
		setSize(459, 418);
		setResizable(false);
	
		// Parameter�� ���� �κ� ����
		thread = th;
		chat = chatroom;
		
 		getContentPane().setLayout(null);
		
		////////////////////////////////////////////////////////////////////////
		/////////////////////////////// ä��â ���� ////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		JPanel panel = new JPanel();
		panel.setToolTipText("");
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\uCC44\uD305\uCC3D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(12, 63, 316, 312);
		getContentPane().add(panel);
		panel.setLayout(null);

		jp = new JTextPane();
		jp.setContentType("text/html");
		doc = jp.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		DefaultStyledDocument d = new DefaultStyledDocument();
		
		s = doc.addStyle("green", def);
		s.addAttribute(StyleConstants.Foreground, Color.BLUE);
		
		jp.setBounds(12, 22, 292, 232);
		jp.setEnabled(false);
		
		// ä�� ���� ����ϴ� �κп� ScrollPane ����
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 21, 292, 217);
		scrollPane.setViewportView(jp); // �ش� �ؽ�Ʈ ������ ��ũ�� ���� ����� �����մϴ�.
		panel.add(scrollPane);
		// ��ũ�ѹٴ� ���� ���� ��� �ʿ��� ��쿡 ���⵵�� ����
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// ������ �޽��� �Է��ϴ� �κ�
		textField = new JTextField();
		textField.setFont(new Font("����", Font.PLAIN, 13));
		textField.setBounds(12, 248, 292, 21);
		panel.add(textField);
		textField.setColumns(10);
		
		// ���� ��ư
		JButton btnSend = new JButton("\uBA54\uC2DC\uC9C0 \uC804\uC1A1");
		btnSend.setBounds(22, 279, 129, 23);
		btnSend.setFont(new Font("����", Font.PLAIN, 12));
		// ���۹�ư�� ������ ���
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message = textField.getText(); // �Էµ� text�� String�������ϰ�
				textField.setText("");		// textField�� ����
				thread.requestSendMessage(chat.getChatroomID(), message);	// thread�� �̿��ؼ� �޽����� �����ϰ�
				textField.updateUI();		// textField ���� - textField�� �Էµ� ���� �����
			}
		});
		panel.add(btnSend);
		
		JButton btnImoticon = new JButton("\uC774\uBAA8\uD2F0\uCF58");
		btnImoticon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Imoticon imo = new Imoticon(thread, chat);
			}
		});
		
		btnImoticon.setFont(new Font("����", Font.PLAIN, 13));
		btnImoticon.setBounds(163, 279, 129, 23);
		panel.add(btnImoticon);
		

		////////////////////////////////////////////////////////////////////////
		///////////////////////////// ä�� ������ ���� //////////////////////////////
		////////////////////////////////////////////////////////////////////////
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\uCC44\uD305 \uCC38\uC5EC\uC790", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(335, 63, 110, 257);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 22, 86, 158);
		scrollPane_1.setViewportView(listMember); // �ش� �ؽ�Ʈ ������ ��ũ�� ���� ����� �����մϴ�.
		panel_1.add(scrollPane_1);
		
		// ä�� ������ ����� �޾Ƽ� JList listMember�� �Է���
		setMemberList();
		
		// ����� �ʴ��ϱ� ��ư
		JButton btnNewUser = new JButton("\uCD08\uB300\uD558\uAE30");
		// �ʴ��ϱ� ��ư�� ������ ���
		btnNewUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �ʴ��� ����ڸ� �޾ƿ��� ���� ���ο� â�� ����
				jd = new joinUserDisplay(thread, chat.getChatroomID());
				jd.setVisible(true);
			}
		});
		btnNewUser.setFont(new Font("����", Font.PLAIN, 13));
		btnNewUser.setBounds(12, 223, 86, 23);
		panel_1.add(btnNewUser);
		
		// listMember���� ���� ���õ� ��� selectedMem ������ ������
		listMember.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedMem = (String) ((JList) e.getSource()).getSelectedValue();
			}
		});
		listMember.setBounds(12, 22, 86, 176);
		
		JButton btnPaper = new JButton("\uCABD\uC9C0\uC804\uC1A1");
		btnPaper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Paper pa = new Paper(thread, selectedMem);
			}
		});
		btnPaper.setFont(new Font("����", Font.PLAIN, 13));
		btnPaper.setBounds(12, 190, 86, 23);
		panel_1.add(btnPaper);
		
		// ������ ��ư
		JButton btnOut = new JButton("\uB098\uAC00\uAE30");
		btnOut.setFont(new Font("����", Font.PLAIN, 13));
		// ������ ��ư�� ������ ��� thread�� �̿��ؼ� ä�ù濡�� �����ٴ� �޽����� ������ â�� ����
		btnOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestOutChatroom(chat.getChatroomID());
				dispose();
			}
		});
		btnOut.setBounds(343, 356, 97, 23);
		getContentPane().add(btnOut);
		
		JLabel lblNewLabel = new JLabel("\uD604\uC7AC \uC0AC\uC6A9\uC790 : ");
		lblNewLabel.setBounds(12, 15, 86, 15);
		getContentPane().add(lblNewLabel);
		
		// ���� �α����� ������� ������ ����ϱ� ���� textField
		textField_curUser = new JTextField();
		textField_curUser.setEditable(false);	// ����ڰ� ������ �� �ִ� textField�� �ƴ�
		textField_curUser.setFont(new Font("����", Font.PLAIN, 13));
		textField_curUser.setBounds(89, 12, 116, 21);
		getContentPane().add(textField_curUser);
		textField_curUser.setColumns(10);
		textField_curUser.setText(thread.getUser().getID());
		
		// ���� ���� ��ư
		// JList���� ������ ����ڿ��� ������ �����Ѵ�.
		JButton btnSendFile = new JButton("\uD30C\uC77C\uC804\uC1A1");
		btnSendFile.setBounds(343, 325, 97, 23);
		getContentPane().add(btnSendFile);
		// �������� ��ư�� ������ ��� thread�� �̿��ؼ� ���� ���� ��û �޽����� ������
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				thread.requestSendFile(chat.getChatroomID());
				SendFile sf = new SendFile(thread);
			}
		});
		btnSendFile.setFont(new Font("����", Font.PLAIN, 13));
	}

	// listMember�� ���� �ִ� �κ�
	public void setMemberList() {
		Vector memList = new Vector<String>();
		memList = chat.getUserList();	// ä�ù濡�� ����ڸ� �ҷ��ͼ�
		listMember.removeAll();			// ����Ʈ�� ������
		listMember.setListData(memList);	// ���� set�Ѵ�
	}
}
