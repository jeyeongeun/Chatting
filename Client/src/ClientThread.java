import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

public class ClientThread extends Thread {
	
	private Socket sock;	// ����� ���� ����
	private DataInputStream din;	// dataInput
	private DataOutputStream dout;	// dataOutput
	private StringBuffer buff;	// ������ ������ �޽���
	private Thread thread;		// ������
	private User curUser;	// �α����� �����
	private User userInfo = new User();	// ����� �������� �� �� �����
	private loginDisplay loginD;	// �α��� display
	private mainDisplay mainD;		// ���� display 
	private Vector<ChatroomDisplay> chatD = new Vector<ChatroomDisplay> ();	 // ä�ù� display ���
	private userInfoDisplay infoD;	// ��������� display
	
	//������ ���´�.
	public Socket getSocket() {
		return sock;
	}
	public ClientThread() {
		try {
			
			// ���� ����� ���� �ʱ�ȭ����
			// ���� ����� ���ؼ��� �������ּҰ� �����Ǿ��־�� ������ �׽�Ʈ�� �ÿ� ���̹Ƿ� 127.0.0.1
			// ��Ʈ�� ���Ƿ� 5566���� ������
			sock = new Socket("127.0.0.1", 5566);
			din = new DataInputStream(sock.getInputStream());
			dout = new DataOutputStream(sock.getOutputStream());
			
			// ������ �޽����� ���� ���� ����
			buff = new StringBuffer(4096);
			// ������ ����
			thread = this;
			
			// �α���ȭ�� ���
			loginD = new loginDisplay(this);

			//�α��� ȭ���� ���̵��� �Ѵ�. 
			loginD.setVisible(true);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	// ���� �α����� ����� ����
	public User getUser() {
		return curUser;
	}
	
	// ������������⸦ ���� ��������� ����
	public User getUserInfo() {
		return userInfo;
	}
	
	// ���� ��� �κ�
	// �������� �޽����� �޾Ƽ� ó����
	@SuppressWarnings("deprecation")
	public void run() {
		try {

			boolean bfile = false;
			String filename = ""; // ���� 
			int fileLength = 0;
			byte[] content = new byte[fileLength];
			
			// ���� �����带 Ȯ���ؼ� �޽����� �ް� ������
			Thread th = Thread.currentThread(); 
			while(th == thread) {
				
				String recvData = din.readUTF();	// �������� �޽��� �޾ƿ�
				System.out.println("recv : "+ recvData);	// ���۹��� �޽��� Ȯ���� ���ؼ� �ܼ� â�� ����� 
				
				// ���۹��� �޽������� �ʿ��� ������� | ���� �����Ѵ�  - StringTokenizer
				// �޽����� ���� ó���� � �뵵�� �޽��������� �����ϴ� ��
				// �� ������ �� ������ ����
				
				if(bfile) {
					System.out.println("���� ���� : " + recvData);
					
					// �����̸��� ũ�⸦ �����մϴ�.
					StringTokenizer stk = new StringTokenizer(recvData, "|");
					filename = stk.nextToken();
					fileLength = Integer.valueOf(stk.nextToken());
					
					// data�� ������ ������ �Է¹޽��ϴ�.
					content = new byte[fileLength];
					try {
						// ������ ������ �����մϴ�.
						int size = 2048;
						int count = fileLength/size;
						int rest = fileLength%size;
						int flag = 1;
						
						if (count == 0) flag = 0;
						
						BufferedInputStream bin = new BufferedInputStream(din, 2048);
						
						for (int i=0; i<=count; i++) {
							// ���� ������ ���� ���
							if(i == count && flag == 0) {
								bin.read(content, 0, rest);
								break;
							}
							// ���� ������ ���� ���
							else if(i == count) {
								bin.read(content, i*size, rest);
								break;
							}
							// ������ �� �����ؾ��ϴ� ���
							else {
								bin.read(content, i*size, size);
							}
						}
					} catch (IOException e) {}
				/////////////////////////////////////////// ���� ���� �о���� ��
				
					System.out.println("�о���ⳡ");
				
				// ���丮 ���� ��ü���� : '���� ����' �̶�� ���丮�� ������ �����Ѵ�
				// ���� Ŭ���̾�Ʈ�� id�� �ڿ� �߰��Ͽ� ��� ������ ���� �������� Ȯ���� �� �ְ� �߽��ϴ�.
				File dir = new File("��������_" + curUser.getID() +"\\");
				// �ش� ���丮�� ���� ��� ���丮�� �����մϴ�.
				if (!dir.exists()) {
					dir.mkdir();
				}
				
				// 'dir' ���丮�� 'filename' �̶�� ���� ��ü�� �����Ѵ�.
				File file = new File(dir, filename);
				if(file.exists()) { // �̹� �����ϸ� filename�� re_�� �߰��մϴ�
					file = new File(dir, "re_" + filename);
				}
				else {	// ������ �������� ������ ������ ����
					if(!file.createNewFile()) {	// ���� ���� ����
						return ;
					}
				}
				
				// ���� �����ϱ����� FileOutputStream, BufferedOutputStream
				// ���ۿ� �� ������ ���Ͽ� ����
				FileOutputStream fout = new FileOutputStream(file);
				BufferedOutputStream bout = new BufferedOutputStream(fout, fileLength);
				bout.write(content, 0, fileLength);
				bout.flush();
				bfile = false;
				
				
			}
				
				StringTokenizer st = new StringTokenizer(recvData, "|");
				String info = st.nextToken();	// info = �޽��� �뵵
				String data = st.nextToken();	// data = �޽��� ���� ����
			
				
				// ������ ������ �޾ƿ� �� (������ �α����� �� ������ �޾ƿ� �� + ģ�� ���� ���� ������ ��)
				// id - �̸� - ������� - ��� - ����
				if(info.equals("UserInfo")) {
					// data = id
					String name = st.nextToken();
					
					// curUser�� ID�� -1�̴� == �α��ε� ������ ����
					// UserInfo�� �α��ε� ���� ������ �޾ƿ��� ���� ���̴�.
					if(curUser.getID() == "-1") {
						curUser.setID(data);
						curUser.setname(name);
						
						/*
						// db���� �ȵǼ� �׽�Ʈ������ ģ�� �߰���
						if(curUser.getID().equals("jye110"))
							this.requestAddFriend("eunji13");
						else if(curUser.getID().equals("eunji13"))
							this.requestAddFriend("jye110");
						// db���� �ȵǼ� �׽�Ʈ������ ģ�� �߰���
						*/
						
						loginD.dispose();
						
						// �α��� �Ǿ����Ƿ� mainâ ���
						mainD = new mainDisplay(this);
						mainD.setVisible(true);
					}
					else {
						User newUser = new User();
						newUser.setID(data);
						newUser.setname(name);
						
						infoD = new userInfoDisplay(this, newUser);
						infoD.setVisible(true);
					}
				}
				
				else if(info.equals("LoginError")) {
					loginD.setVisible(true);
					JOptionPane.showMessageDialog(loginD, "�߸� �Է��ϼ̽��ϴ�!");
				}
				
				// ����� �˻� �� �˻��� ���� �޾ƿ� ��
				// �˻��� ������� �� - �˻��� ID - �˻��� ID - .....
				else if(info.equals("SearchUser")) {
					int num = Integer.valueOf(data);
					Vector<String> list = new Vector<String> ();
					for(int i = 0; i<num; i++) {
						String id = st.nextToken();
						list.add(id);
					}
					// mainD�� �˻��� ����� ����Ʈ ui ������Ʈ
					mainD.setSearchList(list);
				}

				// �α��� ���Ŀ� "ģ�� ��� - ��� ���" �� �޾ƿ´�
				// �α��� �� ģ�� ����� �޾ƿ� ��
				// ģ�� ����� �� - ģ�� ID - ģ�� ID - ....
				else if(info.equals("FriendList")) {
					int num = Integer.valueOf(data);
					for(int i = 0; i<num; i++) {
						String ID = st.nextToken();
						curUser.addFriend(ID);
					}
				}
				// �α��� �� ģ�� ��� �ް� ��� ����� �޾ƿ� ��
				// ��� ����� �� - ��� ID - ��� ID - ....
				
				
				else if(info.equals("Paper")) {
					String id = st.nextToken();
					String msg = st.nextToken();
					
					ViewPaper vp = new ViewPaper(id, msg);
				}
				
				// �����Է� ä�ù濡�� ���۵� �޽����� �������� ��
				// �޽��� ������ ������� ID - ä�ù� ��ȣ - �޽���
				else if(info.equals("SendMessage")) {
					int nChatroom = Integer.valueOf(data);
					String ID = st.nextToken();
					String message = st.nextToken();
					
					Chatroom room = curUser.getChatroom(nChatroom); // ������� ä�ù��Ͽ��� id�� ��ġ�ϴ� ä�ù��� �޾ƿ�
					ChatroomDisplay chat;	// ä�ù��� �̹� ����� ������ �ִ� ���̹Ƿ� ����� ä�ù� ��Ͽ��� ��ġ�ϴ� ���� ã�´�.
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChatroom) {	// ��ġ�ϴ� ���� ã�����
							chat = chatD.get(i);
							chat.inMessage(ID, message);	// �޽��� ���۵Ǿ��ٴ� �� ���
						}
					}
				}
				
				// �����Է� ä�ù��� �����Ѵٴ� �޽����� ���� ��
				// ä�ù��ȣ - ���ξ��̵� - ������̵�
				else if(info.equals("MakeChat")) {
					// ���� ����ڿ��� ���ο� ä�ù� �����Ͽ� ������ ������Ʈ�Ͽ� ����
					Chatroom newChat = new Chatroom();
					newChat.setChatroomID(Integer.valueOf(data));
					String user = st.nextToken();
					newChat.addUser(curUser.getID());
					user = st.nextToken();
					newChat.addUser(user);
					curUser.addChatroom(newChat);
					
					// ä�ù� display ����
					ChatroomDisplay chat = new ChatroomDisplay(this, newChat);
					chat.setVisible(true);
					
					chatD.add(chat);	// ä�ù� display�� �߰�
					mainD.setChatroomList();	// ���� display�� ä�ù� ��� ������Ʈ
				}
				
				//SendFile - �������̵� - �� ��ȣ
				else if(info.equals("SendFile")) {
					bfile = true;
					int nChatroom = Integer.valueOf(st.nextToken());
					
					Chatroom room = curUser.getChatroom(nChatroom); // ������� ä�ù��Ͽ��� id�� ��ġ�ϴ� ä�ù��� �޾ƿ�
					ChatroomDisplay chat;	// ä�ù��� �̹� ����� ������ �ִ� ���̹Ƿ� ����� ä�ù� ��Ͽ��� ��ġ�ϴ� ���� ã�´�.
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChatroom) {	// ��ġ�ϴ� ���� ã�����
							chat = chatD.get(i);
							chat.inMessage(data, "SendFile");	// �޽��� ���۵Ǿ��ٴ� �� ���
						}
					}
				}
				
				// �ٸ� ����ڷκ��� ���� �����ҰŶ�� ��û�� ���� ��
				// ���� ������ �������� ������ ������
				// ������ ������ ID
			/*	else if(info.equals("SendFile")) {
					// ���������� ������ ������ �����ϴ� â ���
					int result = JOptionPane.showConfirmDialog(null, data + " ���� ������ �����մϴ�. �����Ͻðڽ��ϱ�?", 
							"���� ����", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					
					// Ȯ�ι�ư == ���۹޴´�
					if(result==0) {
						requestAcceptFile(data);
						new ReceiveFile();	// ���� ���۹��� �غ� ����
					}
					// ��ҹ�ư == ���۹��� �ʴ´�
					else {
					}
				}*/
				
				// �ٸ� ����ڵ鿡�� ���� �����Ѵٴ� �޽����� ������ �� �ڿ�
				// �ٸ� ����ڵ�κ��� ���������� �����Ѵٴ� �޽����� ���� ��
				// ip�ּ�
				/*
				else if(info.equals("AcceptFile")) {
					String addr = data;
					new SendFile(addr);	// ���� ���� ����
				}*/
				
				
				// ä�ù濡 ���ο� �����ڸ� �߰��Ѵٴ� �޽����� �޾��� ��
				// ä�ù��ȣ - ���ο������� ID
				else if(info.equals("JoinNewMember")) {
					int nChat = Integer.valueOf(data);
					String id = st.nextToken();
					
					// ä�ù� ��ȣ�� ä�ù��� �޾ƿͼ� ������ �߰�
					Chatroom tmp = curUser.getChatroom(nChat);
					tmp.addUser(id);
					
					mainD.setChatroomList();
					
					// ä�ù� display ��Ͽ��� ��ġ�ϴ� ä�ù� �޾ƿͼ�
					// ���List ������Ʈ �ϰ� â �ٽ� ���
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChat) {
							chatD.get(i).setMemberList();
							chatD.get(i).setVisible(true);
						}
					}
				}
				else if(info.equals("OutChatroom")) {
					int nChat = Integer.valueOf(data);
					String id = st.nextToken();

					curUser.getChatroom(nChat).deleteUser(id);
					

					mainD.setChatroomList();
					
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChat) {
							chatD.get(i).setMemberList();
						}
					}
				}
		
			} // while�� ��
		} catch (IOException e) {
			System.out.println(e);
		} // try - catch ��
	} // run �Լ� ��
	
	// �������Է� �޽��� ����
	// parameter : ������ �޽���
	public void sendMessage(String Message) {
		System.out.println(Message);	// �׽�Ʈ ������ ������ �޽��� ���
		try {
			dout.writeUTF(Message);
			dout.flush();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/////////////////////////////////////////////
	//// request~ �� �������Է� �޽��� �����ϴ� �Լ��̴� ////
	/////////////////////////////////////////////
	
	/*
	public void requestSendFile(int nChatroom, String ID) {
		sendMessage("SendFile|"+nChatroom+"|"+ID);
	}
	*/
	
	// ä�ù� �����ٴ� �޽��� ����
	// OutChatroom - ���� ���̵� - ä�ù� ���̵�
	public void requestOutChatroom(int chatroomID) {
		buff.setLength(0);
		buff.append("OutChatroom");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(chatroomID);
		sendMessage(buff.toString());
		
		// ���� ����ڿ��� chatroom �����
		// ä�ù� display���� ��� �����
		curUser.deleteChatroom(chatroomID);
		for(int i=0; i<chatD.size(); i++) {
			if(chatD.get(i).getChatroomID()==chatroomID) {
				chatD.remove(i);
			}
		}
		mainD.setChatroomList();
	}
	
	
	// ����� �ʴ��Ѵٴ� �޽��� ����
	// JoinUser - ä�ù� ���̵� - �߰��ϴ� ����� ���̵�
	public void requestJoinUser(String ID, int chatroomID) {
		buff.setLength(0);
		buff.append("JoinUser");
		buff.append("|");
		buff.append(chatroomID);
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());

		// ���� ����ڿ��� ä�ù� �޾ƿͼ� ����� �߰�
		// curUser.getChatroom(chatroomID).addUser(ID);
	}
	
	// �α����Ѵٴ� �޽��� ����
	// Login - ���̵� - ��й�ȣ
	public void requestLogin(String ID, String password) {
		curUser = new User();
		buff.setLength(0);
		buff.append("Login");
		buff.append("|");
		buff.append(ID);
		buff.append("|");
		buff.append(password);
		sendMessage(buff.toString());
	}
	
	// ȸ������ �޽��� ����
	// Register - ���̵� - ��й�ȣ - �̸� - ���� - ��� - ����
	public void requestRegister(String ID, String password, String name) {
		buff.setLength(0);
		buff.append("Register");
		buff.append("|");
		buff.append(ID);
		buff.append("|");
		buff.append(password);
		buff.append("|");
		buff.append(name);
		sendMessage(buff.toString());
	
		// �α��� â ���
		loginD.setVisible(true);
	}
	
	// ���� ���� ��û
	// SendFile - �����̵� - ä�ù� ���̵�
	public void requestSendFile(int nChatroom) {
		buff.setLength(0);
		buff.append("SendFile");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(nChatroom);
		sendMessage(buff.toString());
	}
	
	// CheckAnswer - ���ȣ - �����̵� - ����
	public void requestCheckAnswer(int roomNum, String answer) {
		buff.setLength(0);
		buff.append("CheckAnswer");
		buff.append("|");
		buff.append(roomNum);
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(answer);
		sendMessage(buff.toString());
	}
	
	// ���� ���� �޴´ٰ� �޽��� ����
	// AcceptFile - �����̵� - ���۹��� ����� ID - �� �������ּ�
	public void requestAcceptFile(String ID) {
		try {
			buff.setLength(0);
			buff.append("AcceptFile");
			buff.append("|");
			buff.append(ID);
			buff.append("|");
			buff.append(curUser.getID());
			buff.append("|");
			
			// �� ������ �ּҸ� �޾ƿ�
			StringTokenizer addr = new StringTokenizer(InetAddress.getLocalHost().toString(), "/");
			String hostname = "";
			String hostaddr = "";
			hostname = addr.nextToken();
			try {
				hostaddr = addr.nextToken();
			} catch(NoSuchElementException e) {
				hostaddr = hostname;
			}
			buff.append(hostaddr);
			sendMessage(buff.toString());
		} catch (Exception e)
		{ System.out.println(e); }
	}
	
	// ģ�� �߰��Ѵٴ� �޽��� ����
	// AddFriend - �����̵� - ģ���߰��� ���̵�
	public void requestAddFriend(String ID) {
		curUser.addFriend(ID);
		buff.setLength(0);
		buff.append("AddFriend");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// ģ�� �����Ѵٴ� �޽��� ����
	// DeleteFriend - �����̵� - ģ�������� ���̵�
	public void requestDeleteFriend(String ID) {
		curUser.deleteFriend(ID);
		buff.setLength(0);
		buff.append("DeleteFriend");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// ä�ù� ����ٴ� �޽��� ����
	// MakeChat - �����̵� - ä���� ����� ���̵�
	public void requestMakeChatroom(String ID) {
		buff.setLength(0);
		buff.append("MakeChat");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// ä�ù濡�� �޽��� �����Ѵٴ� �޽��� ����
	// SendMessage - �����̵� - ä�ù� ���̵� - ������ �޽���
	public void requestSendMessage(int nRoom, String message) {
		buff.setLength(0);
		buff.append("SendMessage");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(nRoom);
		buff.append("|");
		buff.append(message);
		sendMessage(buff.toString());
	}
	
	// ģ�� ��������
	// ShowFriendInfo - �����̵� - ������ ���̵�
	public void requestShowFriendInfo(String ID) {
		buff.setLength(0);
		buff.append("ShowFriendInfo");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// ����� �˻��ϱ�
	// SearchUser - �����̵� - �˻��� Ű����
	public void requestSearchUser(String keyword) {
		buff.setLength(0);
		buff.append("SearchUser");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(keyword);
		sendMessage(buff.toString());
	}
	
	// ����� ��������
	// GetUserInfo - �����̵� - ������ ���̵�
	public void requestUserInfo(String ID) {
		buff.setLength(0);
		buff.append("UserInfo");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	//���� ���
	public void requestSendPaper(String ID, String message) {
		buff.setLength(0);
		buff.append("Paper");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		buff.append("|");
		buff.append(message);
		sendMessage(buff.toString());
	}
	// ä�ù� display���� ��ġ�ϴ� �� return
	public ChatroomDisplay getChatroomD(int nChatroom) {
		for(int i =0; i<chatD.size(); i++) {
			if (chatD.get(i).getChatroomID() == nChatroom) {
				return chatD.get(i);
			}
		}
		// �˻��� ���� ���� ��� ���̵� -1�� �� ����
		Chatroom er = new Chatroom();
		er.setChatroomID(-1);
		ChatroomDisplay error = new ChatroomDisplay(this, er);
		return error;
	}
}
