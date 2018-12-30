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

// 채팅방 디스플레이 클래스
// UI 출력 부분만 담당한다.
public class ChatroomDisplay extends JFrame {
	private JTextField textField;	// 채팅 내용 입력할 textfield
	private ClientThread thread;	// 현재 실행되고 있는 thread
	private Chatroom chat;		// 해당 UI가 출력할 채팅방 정보
	private String message;		// 사용자가 입력한 메시지 저장
	private JList listMember = new JList();	// 채팅 참여자 목록 출력
	private String selectedMem;		// 채팅 참여자 목록 리스트에서 선택된 사용자 ID 저장
	private JTextField textField_curUser;	// 현재 사용자 아이디 출력
	private joinUserDisplay jd ;		// joinUserDisplay 클래스에 대한 인스턴스 - 해당 화면 표현할 때 사용 됌.

	private JTextPane jp ;		// 채팅 내용을 출력할 textpane
	private static StyledDocument doc; //JTextPane의 스타일 양식에 관한 정보를 담고 있는 변수입니다.
	private Style s; // 스타일 양식 내 정보에 관한 변수입니다.
	
	// 해당 UI의 채팅방 아이디 리턴
	public int getChatroomID() {
		return chat.getChatroomID(); //chat 클래스의 getChatroomID() 메서드를 통해 방 번호를 얻어온다.
	}
	// 서버 쪽에서 메시지가 올 때, 호출되는 메서드로서,
	// 해당 채팅방으로 메시지가 도착했을 때 이를 TextArea에 출력
	// Parameter: 메시지를 전송한 사용자의 id와 메시지 내용
	public void inMessage(String id, String message) {

		// message가 sendFile일 때,
		if(message.equals("SendFile")) {
			try {
				doc.insertString(doc.getLength(), id+" 님이 파일을 전송했습니다.\n", null); // 파일이 전송되었다는 것을 알린다. 
				setEndline(); // 줄 바꿈
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// message가 Imoticon 관련된 메시지일 때,
		// 누가 보냈는 지, 표시한 후, 이미지 주소를 통해 이모티콘 표시
		else if(message.equals("shyImoticon")) {
			try {
				doc.insertString(doc.getLength(), id+" :   ", doc.getStyle("green"));
				setEndline();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			ImageIcon Icon = new ImageIcon("Imoticon/"+"shyImoticon"+".jpg");
			//jp에 ImageIcon을 추가합니다.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//개행 문자를 삽입합니다.
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
			//jp에 ImageIcon을 추가합니다.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//개행 문자를 삽입합니다.
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
			//jp에 ImageIcon을 추가합니다.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//개행 문자를 삽입합니다.
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
			//jp에 ImageIcon을 추가합니다.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//개행 문자를 삽입합니다.
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
			//jp에 ImageIcon을 추가합니다.
			jp.insertComponent(new JLabel(Icon));
			setEndline();
			
			//개행 문자를 삽입합니다.
			jp.replaceSelection("\n");
			setEndline();
		}
		// 위의 경우에 모두 해당되지 않을 때, 즉, 단순히 메시지만을 주고 받을 때
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
		//문장의 끝에 무조건 커서 이동하게 설정
		jp.setSelectionStart(jp.getSelectionEnd());
	}
	// 생성자 - 출력 주요 부분 담당
	// Parameter: 현재 실행중인 쓰레드와 해당 UI가 출력할 채팅방의 정보
	public ChatroomDisplay(ClientThread th, Chatroom chatroom) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // x버튼 누르면 dispose
		
		// 사이즈 고정
		setSize(459, 418);
		setResizable(false);
	
		// Parameter로 받은 부분 설정
		thread = th;
		chat = chatroom;
		
 		getContentPane().setLayout(null);
		
		////////////////////////////////////////////////////////////////////////
		/////////////////////////////// 채팅창 구역 ////////////////////////////////
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
		
		// 채팅 내용 출력하는 부분에 ScrollPane 적용
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 21, 292, 217);
		scrollPane.setViewportView(jp); // 해당 텍스트 영역에 스크롤 팬의 기능을 설정합니다.
		panel.add(scrollPane);
		// 스크롤바는 가로 세로 모두 필요한 경우에 생기도록 적용
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// 전송할 메시지 입력하는 부분
		textField = new JTextField();
		textField.setFont(new Font("돋움", Font.PLAIN, 13));
		textField.setBounds(12, 248, 292, 21);
		panel.add(textField);
		textField.setColumns(10);
		
		// 전송 버튼
		JButton btnSend = new JButton("\uBA54\uC2DC\uC9C0 \uC804\uC1A1");
		btnSend.setBounds(22, 279, 129, 23);
		btnSend.setFont(new Font("돋움", Font.PLAIN, 12));
		// 전송버튼이 눌러진 경우
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message = textField.getText(); // 입력된 text를 String에저장하고
				textField.setText("");		// textField를 비운다
				thread.requestSendMessage(chat.getChatroomID(), message);	// thread를 이용해서 메시지를 전송하고
				textField.updateUI();		// textField 갱신 - textField에 입력된 값이 사라짐
			}
		});
		panel.add(btnSend);
		
		JButton btnImoticon = new JButton("\uC774\uBAA8\uD2F0\uCF58");
		btnImoticon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Imoticon imo = new Imoticon(thread, chat);
			}
		});
		
		btnImoticon.setFont(new Font("돋움", Font.PLAIN, 13));
		btnImoticon.setBounds(163, 279, 129, 23);
		panel.add(btnImoticon);
		

		////////////////////////////////////////////////////////////////////////
		///////////////////////////// 채팅 참여자 구역 //////////////////////////////
		////////////////////////////////////////////////////////////////////////
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\uCC44\uD305 \uCC38\uC5EC\uC790", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(335, 63, 110, 257);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 22, 86, 158);
		scrollPane_1.setViewportView(listMember); // 해당 텍스트 영역에 스크롤 팬의 기능을 설정합니다.
		panel_1.add(scrollPane_1);
		
		// 채팅 참여자 목록을 받아서 JList listMember에 입력함
		setMemberList();
		
		// 사용자 초대하기 버튼
		JButton btnNewUser = new JButton("\uCD08\uB300\uD558\uAE30");
		// 초대하기 버튼이 눌려진 경우
		btnNewUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 초대할 사용자를 받아오기 위해 새로운 창을 띄운다
				jd = new joinUserDisplay(thread, chat.getChatroomID());
				jd.setVisible(true);
			}
		});
		btnNewUser.setFont(new Font("돋움", Font.PLAIN, 13));
		btnNewUser.setBounds(12, 223, 86, 23);
		panel_1.add(btnNewUser);
		
		// listMember에서 값이 선택된 경우 selectedMem 변수에 저장함
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
		btnPaper.setFont(new Font("돋움", Font.PLAIN, 13));
		btnPaper.setBounds(12, 190, 86, 23);
		panel_1.add(btnPaper);
		
		// 나가기 버튼
		JButton btnOut = new JButton("\uB098\uAC00\uAE30");
		btnOut.setFont(new Font("돋움", Font.PLAIN, 13));
		// 나가기 버튼이 눌려진 경우 thread를 이용해서 채팅방에서 나간다는 메시지를 보내고 창을 끈다
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
		
		// 현재 로그인한 사용자의 정보를 출력하기 위한 textField
		textField_curUser = new JTextField();
		textField_curUser.setEditable(false);	// 사용자가 수정할 수 있는 textField가 아님
		textField_curUser.setFont(new Font("돋움", Font.PLAIN, 13));
		textField_curUser.setBounds(89, 12, 116, 21);
		getContentPane().add(textField_curUser);
		textField_curUser.setColumns(10);
		textField_curUser.setText(thread.getUser().getID());
		
		// 파일 전송 버튼
		// JList에서 선택한 사용자에게 파일을 전송한다.
		JButton btnSendFile = new JButton("\uD30C\uC77C\uC804\uC1A1");
		btnSendFile.setBounds(343, 325, 97, 23);
		getContentPane().add(btnSendFile);
		// 파일전송 버튼이 눌려진 경우 thread를 이용해서 파일 전송 요청 메시지를 보낸다
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				thread.requestSendFile(chat.getChatroomID());
				SendFile sf = new SendFile(thread);
			}
		});
		btnSendFile.setFont(new Font("돋움", Font.PLAIN, 13));
	}

	// listMember에 값을 넣는 부분
	public void setMemberList() {
		Vector memList = new Vector<String>();
		memList = chat.getUserList();	// 채팅방에서 사용자를 불러와서
		listMember.removeAll();			// 리스트를 비운다음
		listMember.setListData(memList);	// 값을 set한다
	}
}
