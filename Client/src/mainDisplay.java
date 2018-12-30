import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;

// 메인 창 출력
public class mainDisplay extends JFrame {
	private User curUser;		// 현재 로그인한 사용자
	private ClientThread thread;	// 현재 쓰레드
	private String selectedFriend;	// 친구 탭에 선택된 사용자
	private String selectedSearch;	// 검색 탭에 선택된 사용자
	
	private JList list_friend = new JList();	// 친구 탭의 리스트
	private JList list_chatroom = new JList();	// 채팅방 탭의 리스트
	private JList list_search = new JList();	// 검색 탭의 리스트
	
	private ChatroomDisplay chatD;	// 채팅방 디스플레이
	private int selectedChat;		// 선택된 채팅방
	private JTextField textField_search;	// 검색할 키워드 입력할 textField
	
	// 검색 탭의 리스트 설정 
	public void setSearchList(Vector<String> searchList) {
		list_search.removeAll();
		list_search.setListData(searchList);
	}
	
	// 친구목록 탭의 리스트 설정
	public void setFriendList() {
		Vector friendList = new Vector<String>();
		friendList = curUser.getFriend();
		list_friend.removeAll();
		list_friend.setListData(friendList);
	}
	
	// 채팅방 목록 탭의 리스트 설정
	public void setChatroomList() {
		Vector chatroomList = new Vector<String>();
		chatroomList = curUser.getChatroomList();
		// 채팅방 목록의 받아와서 받아온 정보로 출력할 모양으로 가공해서 이용할 것!
		// v3에 가공한 정보목록을 저장하고 buff는 가공한 정보를 임시저장
		Vector vc3 = new Vector<String>();
		StringBuffer buff = new StringBuffer(100);
		for(int i=0; i<chatroomList.size(); i++) {
			buff.setLength(0);
			Chatroom t = (Chatroom) chatroomList.get(i);
			buff.append(t.getChatroomID());
			buff.append("번 방: ");
			int size = t.getUserList().size();
			for(int j=0; j<size; j++) {
				buff.append(t.getUserList().get(j));
				if(j != size -1) {
					buff.append(", "); }
			}
			vc3.addElement(buff.toString());
		}
		list_chatroom.removeAll();
		list_chatroom.setListData(vc3);
	}
	
	private JPanel panel_friend;	// 친구 탭
	private JPanel panel_search;	// 검색 탭
	private JPanel panel_chatroom;	// 채팅방 목록 탭
	private JTextField textField_curUser;	// 현재 사용자 출력용 textField
	
	// 생성자
	public mainDisplay(ClientThread th) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 쓰레드 및 현재 사용자 설정
		thread = th;
		curUser = thread.getUser();
		
		// 사이즈 고정, 변경불가
		setSize(383, 421);
		setResizable(false);
		getContentPane().setLayout(null);
		
		// 메인화면은 여러개의 탭을 가진다
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 38, 353, 345);
		getContentPane().add(tabbedPane);

		///////////////////////////////////////////////////////////////////////
		////////////////////// 첫번째 탭: 친구목록 /////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		panel_friend = new JPanel();
		tabbedPane.addTab("\uCE5C\uAD6C\uBAA9\uB85D", null, panel_friend, null);
		panel_friend.setLayout(null);
	
		// 친구 list 설정
		setFriendList();
		// list에서 한 값이 선택되면 해당 값을 받아온다
		list_friend.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedFriend = (String) ((JList) e.getSource()).getSelectedValue();
			}
		});
		list_friend.setBounds(0, 0, 348, 266);
		list_friend.setFont(new Font("돋움", Font.PLAIN, 13));
		list_friend.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel_friend.add(list_friend);
		
		// 채팅하기 버튼
		JButton btnChat = new JButton("\uBC29\uB9CC\uB4E4\uAE30");
		btnChat.setBounds(125, 276, 97, 30);
		// 채팅하기 버튼이 눌려지면 쓰레드에  채팅 요청하고
		// 채팅목록 재설정
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestMakeChatroom(selectedFriend);
				setChatroomList();
				panel_chatroom.updateUI();
			}
		});
		btnChat.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_friend.add(btnChat);
		
		// 삭제하기 버튼
		JButton btnDelete = new JButton("\uC0AD\uC81C\uD558\uAE30");
		btnDelete.setBounds(241, 276, 97, 30);
		// 삭제하기 버튼이 눌려지면 쓰레드에 친구삭제 요청하고 목록에서 지움
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestDeleteFriend(selectedFriend);
				list_friend.clearSelection();
				selectedFriend = "";
				setFriendList();
				panel_friend.updateUI();
			}
		});
		btnDelete.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_friend.add(btnDelete);
		
		// 친구정보보기 버튼
		JButton btnInfo = new JButton("\uC815\uBCF4\uBCF4\uAE30");
		btnInfo.setBounds(12, 276, 97, 30);
		// 정보보기 버튼이 눌려지면
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestShowFriendInfo(selectedFriend);
			}
		});
		btnInfo.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_friend.add(btnInfo);
		
		//////////////////////////////////////////////////////////////////////////////////
		////////////////////////// 세번째 탭 : 채팅방 목록 //////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////
		panel_chatroom = new JPanel();
		tabbedPane.addTab("\uCC44\uD305\uBC29\uBAA9\uB85D", null, panel_chatroom, null);
		panel_chatroom.setLayout(null);
		panel_chatroom.add(list_chatroom);
		
		// 채팅방 목록 설정
		setChatroomList();
		// 채팅방 목록에서 값이 선택되면 선택된 값의 방 번호를 selectedChat에 저장함
		list_chatroom.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String ch = (String) ((JList) e.getSource()).getSelectedValue();
				StringTokenizer st = new StringTokenizer(ch, "번 방: ");
				selectedChat = Integer.valueOf(st.nextToken());
			}
		});
		list_chatroom.setBounds(0, 0, 348, 265);
		list_chatroom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// 들어가기 버튼
		JButton btnEnter = new JButton("\uB4E4\uC5B4\uAC00\uAE30");
		btnEnter.setBounds(37, 275, 112, 31);
		// 들어가기 버튼이 눌려지면 쓰레드를 통해 해당 디스플레이를 불러오고 ui 출력
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatroomDisplay chatroom = thread.getChatroomD(selectedChat);
				if (chatroom.getChatroomID() != -1)
					chatroom.setVisible(true);
			}
		});
		btnEnter.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_chatroom.add(btnEnter);
		
		// 나가기 버튼
		JButton btnOut = new JButton("\uB098\uAC00\uAE30");
		btnOut.setBounds(207, 275, 111, 30);
		// 나가기 버튼이 눌려지면 쓰레드로 채팅방에서 나간다고 알리고 목록 재설정
		btnOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestOutChatroom(selectedChat);
				// 선택된 값 초기화
				selectedChat = -1;
				list_chatroom.clearSelection();
				// ui 재설정 
				setChatroomList();
				panel_chatroom.updateUI();
			}
		});
		btnOut.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_chatroom.add(btnOut);
		
		///////////////////////////////////////////////////////////////////////////
		////////////////////////네번째탭/////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		panel_search = new JPanel();
		panel_search.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				list_search.removeAll();
			}
		});
		tabbedPane.addTab("\uCE5C\uAD6C\uCC3E\uAE30", null, panel_search, null);
		panel_search.setLayout(null);
		
		// 검색할 키워드 입력하는 textField
		textField_search = new JTextField();
		textField_search.setBounds(12, 10, 215, 30);
		textField_search.setFont(new Font("돋움", Font.PLAIN, 13));
		textField_search.setColumns(10);
		panel_search.add(textField_search);
		
		// 검색하기 버튼
		JButton btnSearch = new JButton("\uAC80\uC0C9");
		btnSearch.setBounds(239, 10, 97, 30);
		btnSearch.setFont(new Font("돋움", Font.PLAIN, 13));
		// 검색하기 버튼이 눌려지면 쓰레드를 통해 사용자 검색 요청
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestSearchUser(textField_search.getText());
			}
		});
		panel_search.add(btnSearch);
		
		list_search = new JList();
		list_search.setBounds(12, 59, 324, 207);
		// 검색된 리스트에서 값이 선택되면 selectedSearch 에 입력
		list_search.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedSearch = (String) ((JList) e.getSource()).getSelectedValue();
			}
		});
		panel_search.add(list_search);
		
		// 정보보기 버튼
		JButton btnInfo_search = new JButton("\uC815\uBCF4\uBCF4\uAE30");
		btnInfo_search.setBounds(39, 276, 111, 30);
		// 정보보기 버튼이 눌려지면 쓰레드를 통해 선택한 사용자의 정보 요청
		btnInfo_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestUserInfo(selectedSearch);
				
			}
		});
		btnInfo_search.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_search.add(btnInfo_search);
		
		// 친구 추가 버튼
		JButton btnAdd_search = new JButton("\uCE5C\uAD6C\uCD94\uAC00");
		btnAdd_search.setBounds(201, 276, 111, 30);
		// 친구 추가 버튼이 눌려지면 쓰레드를 통해 친구 추가 요청
		btnAdd_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestAddFriend(selectedSearch);
				setFriendList();
				panel_friend.repaint();
			}
		});
		btnAdd_search.setFont(new Font("돋움", Font.PLAIN, 13));
		panel_search.add(btnAdd_search);
		
		JLabel lblNewLabel = new JLabel("\uD604\uC7AC \uC0AC\uC6A9\uC790 : ");
		lblNewLabel.setFont(new Font("돋움", Font.PLAIN, 13));
		lblNewLabel.setBounds(12, 13, 83, 15);
		getContentPane().add(lblNewLabel);
		
		// 현재 로그인한 사용자 정보를 출력하는 부분
		textField_curUser = new JTextField();
		textField_curUser.setEditable(false);
		textField_curUser.setBounds(96, 10, 116, 21);
		getContentPane().add(textField_curUser);
		textField_curUser.setColumns(10);
		textField_curUser.setText(thread.getUser().getID());
	}
}
