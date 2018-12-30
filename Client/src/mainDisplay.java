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

// ���� â ���
public class mainDisplay extends JFrame {
	private User curUser;		// ���� �α����� �����
	private ClientThread thread;	// ���� ������
	private String selectedFriend;	// ģ�� �ǿ� ���õ� �����
	private String selectedSearch;	// �˻� �ǿ� ���õ� �����
	
	private JList list_friend = new JList();	// ģ�� ���� ����Ʈ
	private JList list_chatroom = new JList();	// ä�ù� ���� ����Ʈ
	private JList list_search = new JList();	// �˻� ���� ����Ʈ
	
	private ChatroomDisplay chatD;	// ä�ù� ���÷���
	private int selectedChat;		// ���õ� ä�ù�
	private JTextField textField_search;	// �˻��� Ű���� �Է��� textField
	
	// �˻� ���� ����Ʈ ���� 
	public void setSearchList(Vector<String> searchList) {
		list_search.removeAll();
		list_search.setListData(searchList);
	}
	
	// ģ����� ���� ����Ʈ ����
	public void setFriendList() {
		Vector friendList = new Vector<String>();
		friendList = curUser.getFriend();
		list_friend.removeAll();
		list_friend.setListData(friendList);
	}
	
	// ä�ù� ��� ���� ����Ʈ ����
	public void setChatroomList() {
		Vector chatroomList = new Vector<String>();
		chatroomList = curUser.getChatroomList();
		// ä�ù� ����� �޾ƿͼ� �޾ƿ� ������ ����� ������� �����ؼ� �̿��� ��!
		// v3�� ������ ��������� �����ϰ� buff�� ������ ������ �ӽ�����
		Vector vc3 = new Vector<String>();
		StringBuffer buff = new StringBuffer(100);
		for(int i=0; i<chatroomList.size(); i++) {
			buff.setLength(0);
			Chatroom t = (Chatroom) chatroomList.get(i);
			buff.append(t.getChatroomID());
			buff.append("�� ��: ");
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
	
	private JPanel panel_friend;	// ģ�� ��
	private JPanel panel_search;	// �˻� ��
	private JPanel panel_chatroom;	// ä�ù� ��� ��
	private JTextField textField_curUser;	// ���� ����� ��¿� textField
	
	// ������
	public mainDisplay(ClientThread th) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// ������ �� ���� ����� ����
		thread = th;
		curUser = thread.getUser();
		
		// ������ ����, ����Ұ�
		setSize(383, 421);
		setResizable(false);
		getContentPane().setLayout(null);
		
		// ����ȭ���� �������� ���� ������
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 38, 353, 345);
		getContentPane().add(tabbedPane);

		///////////////////////////////////////////////////////////////////////
		////////////////////// ù��° ��: ģ����� /////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		panel_friend = new JPanel();
		tabbedPane.addTab("\uCE5C\uAD6C\uBAA9\uB85D", null, panel_friend, null);
		panel_friend.setLayout(null);
	
		// ģ�� list ����
		setFriendList();
		// list���� �� ���� ���õǸ� �ش� ���� �޾ƿ´�
		list_friend.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedFriend = (String) ((JList) e.getSource()).getSelectedValue();
			}
		});
		list_friend.setBounds(0, 0, 348, 266);
		list_friend.setFont(new Font("����", Font.PLAIN, 13));
		list_friend.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel_friend.add(list_friend);
		
		// ä���ϱ� ��ư
		JButton btnChat = new JButton("\uBC29\uB9CC\uB4E4\uAE30");
		btnChat.setBounds(125, 276, 97, 30);
		// ä���ϱ� ��ư�� �������� �����忡  ä�� ��û�ϰ�
		// ä�ø�� �缳��
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestMakeChatroom(selectedFriend);
				setChatroomList();
				panel_chatroom.updateUI();
			}
		});
		btnChat.setFont(new Font("����", Font.PLAIN, 13));
		panel_friend.add(btnChat);
		
		// �����ϱ� ��ư
		JButton btnDelete = new JButton("\uC0AD\uC81C\uD558\uAE30");
		btnDelete.setBounds(241, 276, 97, 30);
		// �����ϱ� ��ư�� �������� �����忡 ģ������ ��û�ϰ� ��Ͽ��� ����
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestDeleteFriend(selectedFriend);
				list_friend.clearSelection();
				selectedFriend = "";
				setFriendList();
				panel_friend.updateUI();
			}
		});
		btnDelete.setFont(new Font("����", Font.PLAIN, 13));
		panel_friend.add(btnDelete);
		
		// ģ���������� ��ư
		JButton btnInfo = new JButton("\uC815\uBCF4\uBCF4\uAE30");
		btnInfo.setBounds(12, 276, 97, 30);
		// �������� ��ư�� ��������
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestShowFriendInfo(selectedFriend);
			}
		});
		btnInfo.setFont(new Font("����", Font.PLAIN, 13));
		panel_friend.add(btnInfo);
		
		//////////////////////////////////////////////////////////////////////////////////
		////////////////////////// ����° �� : ä�ù� ��� //////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////
		panel_chatroom = new JPanel();
		tabbedPane.addTab("\uCC44\uD305\uBC29\uBAA9\uB85D", null, panel_chatroom, null);
		panel_chatroom.setLayout(null);
		panel_chatroom.add(list_chatroom);
		
		// ä�ù� ��� ����
		setChatroomList();
		// ä�ù� ��Ͽ��� ���� ���õǸ� ���õ� ���� �� ��ȣ�� selectedChat�� ������
		list_chatroom.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String ch = (String) ((JList) e.getSource()).getSelectedValue();
				StringTokenizer st = new StringTokenizer(ch, "�� ��: ");
				selectedChat = Integer.valueOf(st.nextToken());
			}
		});
		list_chatroom.setBounds(0, 0, 348, 265);
		list_chatroom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// ���� ��ư
		JButton btnEnter = new JButton("\uB4E4\uC5B4\uAC00\uAE30");
		btnEnter.setBounds(37, 275, 112, 31);
		// ���� ��ư�� �������� �����带 ���� �ش� ���÷��̸� �ҷ����� ui ���
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatroomDisplay chatroom = thread.getChatroomD(selectedChat);
				if (chatroom.getChatroomID() != -1)
					chatroom.setVisible(true);
			}
		});
		btnEnter.setFont(new Font("����", Font.PLAIN, 13));
		panel_chatroom.add(btnEnter);
		
		// ������ ��ư
		JButton btnOut = new JButton("\uB098\uAC00\uAE30");
		btnOut.setBounds(207, 275, 111, 30);
		// ������ ��ư�� �������� ������� ä�ù濡�� �����ٰ� �˸��� ��� �缳��
		btnOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestOutChatroom(selectedChat);
				// ���õ� �� �ʱ�ȭ
				selectedChat = -1;
				list_chatroom.clearSelection();
				// ui �缳�� 
				setChatroomList();
				panel_chatroom.updateUI();
			}
		});
		btnOut.setFont(new Font("����", Font.PLAIN, 13));
		panel_chatroom.add(btnOut);
		
		///////////////////////////////////////////////////////////////////////////
		////////////////////////�׹�°��/////////////////////////////////////////////
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
		
		// �˻��� Ű���� �Է��ϴ� textField
		textField_search = new JTextField();
		textField_search.setBounds(12, 10, 215, 30);
		textField_search.setFont(new Font("����", Font.PLAIN, 13));
		textField_search.setColumns(10);
		panel_search.add(textField_search);
		
		// �˻��ϱ� ��ư
		JButton btnSearch = new JButton("\uAC80\uC0C9");
		btnSearch.setBounds(239, 10, 97, 30);
		btnSearch.setFont(new Font("����", Font.PLAIN, 13));
		// �˻��ϱ� ��ư�� �������� �����带 ���� ����� �˻� ��û
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestSearchUser(textField_search.getText());
			}
		});
		panel_search.add(btnSearch);
		
		list_search = new JList();
		list_search.setBounds(12, 59, 324, 207);
		// �˻��� ����Ʈ���� ���� ���õǸ� selectedSearch �� �Է�
		list_search.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedSearch = (String) ((JList) e.getSource()).getSelectedValue();
			}
		});
		panel_search.add(list_search);
		
		// �������� ��ư
		JButton btnInfo_search = new JButton("\uC815\uBCF4\uBCF4\uAE30");
		btnInfo_search.setBounds(39, 276, 111, 30);
		// �������� ��ư�� �������� �����带 ���� ������ ������� ���� ��û
		btnInfo_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestUserInfo(selectedSearch);
				
			}
		});
		btnInfo_search.setFont(new Font("����", Font.PLAIN, 13));
		panel_search.add(btnInfo_search);
		
		// ģ�� �߰� ��ư
		JButton btnAdd_search = new JButton("\uCE5C\uAD6C\uCD94\uAC00");
		btnAdd_search.setBounds(201, 276, 111, 30);
		// ģ�� �߰� ��ư�� �������� �����带 ���� ģ�� �߰� ��û
		btnAdd_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestAddFriend(selectedSearch);
				setFriendList();
				panel_friend.repaint();
			}
		});
		btnAdd_search.setFont(new Font("����", Font.PLAIN, 13));
		panel_search.add(btnAdd_search);
		
		JLabel lblNewLabel = new JLabel("\uD604\uC7AC \uC0AC\uC6A9\uC790 : ");
		lblNewLabel.setFont(new Font("����", Font.PLAIN, 13));
		lblNewLabel.setBounds(12, 13, 83, 15);
		getContentPane().add(lblNewLabel);
		
		// ���� �α����� ����� ������ ����ϴ� �κ�
		textField_curUser = new JTextField();
		textField_curUser.setEditable(false);
		textField_curUser.setBounds(96, 10, 116, 21);
		getContentPane().add(textField_curUser);
		textField_curUser.setColumns(10);
		textField_curUser.setText(thread.getUser().getID());
	}
}
