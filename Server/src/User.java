import java.util.Vector;

// ������ ���� Ŭ����
public class User {
	//���� ������ ���� �� ����ؾ� �� ��� ����
	private String id;
	private String password;
	private String name;
	int random_num;
	////////////////////////////////////////////

	private Vector<User> friendList = new Vector<User>(); //ģ�� ����Ʈ�� ������������ 
	private Vector<ChatRoom> chatroomList = new Vector<ChatRoom>();  //ä�ù� ����Ʈ�� ������������ ����
	private int chatroom_num = chatroomList.size(); // ä�ù� ����Ʈ�� ������ ����
	
	public User(){ //�����ڸ� ���� �ʱ�ȭ
		id = "";
		password = "";
		name = "";
		random_num = 0;
	}
	
	public int getRandNum(){
		return random_num;
	}
	
	public void setRandNum(int _num){
		for(int i = 0; i < _num; i++){
			random_num++;
		}
	}
	public int getChatroomNum(){ // ä�ù� ��ȣ �� ����
		return chatroom_num;
	}
	
	public Vector<User> getFriendList(){ // ģ�� ����Ʈ ����
		return friendList;
	}
	
	public Vector<ChatRoom> getChatList(){ // ä�ù� ����Ʈ ����
		return chatroomList;
	}
	
	public String getID(){ // ID ����
		return id;
	}
	
	public String getPassword(){ // PW ����
		return password;
	}
	
	public String getName(){ // �̸� ����
		return name;
	}
		
	public void setUserInfo(String _id, String _password, String _name){ // ���� ���� ����
		id = _id;
		password = _password;
		name = _name;
	}
		
	public void addFriend(User newuser){ // ģ�� �߰�
		friendList.add(newuser);
	}
	
	public void deleteFriend(User newuser){ // ģ�� ����
		for(int i = 0; i < friendList.size(); i++){
			User alluser = friendList.elementAt(i);
			if(alluser.getID().equals(newuser.getID())){
				friendList.removeElementAt(i);
				System.out.println(newuser.getID() + "�� ģ�� ��Ͽ� �߰��Ǿ����ϴ�.");
				break;
			}
		}
	}
	
	public void addChatRoom(ChatRoom newchat){ // ä�ù� �߰�
		chatroomList.addElement(newchat);
	}
	
	public void deleteChatRoom(ChatRoom newchat){ // ä�ù� ����
		for(int i = 0; i < chatroomList.size(); i++){
			ChatRoom allchat = chatroomList.elementAt(i);
			if(allchat.getChatroomID() == newchat.getChatroomID()){
				chatroomList.removeElementAt(i);
				System.out.println(allchat.getChatroomID() + "�� ä�� ��Ͽ��� �����Ǿ����ϴ�.");
				break;
			}
		}
	}
}
