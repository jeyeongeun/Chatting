import java.util.Vector;

// user Ŭ����
public class User {
	private String ID;
	private String name;
	
	private Vector<String> friendList;
	private Vector<Chatroom> chatroomList;
	
	// ������ ��� -1�� �����ϰ� Vector �ʱ�ȭ
	public User() {
		ID = "-1";
		name = "-1";
		friendList = new Vector<String>();
		chatroomList = new Vector<Chatroom>();
	}
	
	// ä�ù� ��� ����
	public Vector<Chatroom> getChatroomList() {
		return chatroomList;
	}
	
	// ä�ù� ���̵� �Է¹޾� ��Ͽ��� ä�ù� ����
	public void deleteChatroom(int chatroomID) {
		for(int i=0; i<chatroomList.size(); i++) {
			Chatroom tmp = chatroomList.get(i);
			if(tmp.getChatroomID() == chatroomID)
			{
				chatroomList.remove(i);
				return ;
			}
		}
	}
	
	// ����� ���̵� ����
	public void setID(String _ID) {
		ID = _ID;
	}
	
	// �̸� ����
	public void setname(String _name) {
		name = _name;
	}
	
	// ģ�� ��Ͽ� ���̵� �߰�
	public void addFriend(String friend) {
		friendList.add(friend);
	}
	
	// ���̵� �Է¹޾� ģ�� ��Ͽ��� ����
	public void deleteFriend(String id) {
		for(int i=0; i<friendList.size(); i++) {
			String tmp = friendList.get(i);
			if(tmp == id)
			{
				friendList.remove(i);
				return ;
			}
		}
		
	}
	
	// ���̵� ����
	public String getID() {
		return ID;
	}
	
	// �̸� ����
	public String getName() {
		return name;
	}
	
	// ä�ù���̵� �޾� �ش� ä�ù� ����
	public Chatroom getChatroom(int chatroomID) {
		for(int i=0; i<chatroomList.size(); i++) {
			Chatroom tmp = chatroomList.get(i);
			if(tmp.getChatroomID() == chatroomID)
			{
				return tmp;
			}
		}
		
		// ��ġ�ϴ� �� ������ ���̵� -1�� ä�ù� ����
		Chatroom tm = new Chatroom();
		tm.setChatroomID(-1);
		return tm;
	}
	
	// ä�ù��� �޾Ƽ� ä�ù� ��Ͽ� �߰�
	public void addChatroom(Chatroom ch) {
		chatroomList.add(ch);
	}
	
	// ģ�� ��� ����
	public Vector<String> getFriend() {
		return friendList;
	}
}
