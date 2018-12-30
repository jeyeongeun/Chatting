import java.util.Vector;

// user 클래스
public class User {
	private String ID;
	private String name;
	
	private Vector<String> friendList;
	private Vector<Chatroom> chatroomList;
	
	// 생성자 모두 -1로 설정하고 Vector 초기화
	public User() {
		ID = "-1";
		name = "-1";
		friendList = new Vector<String>();
		chatroomList = new Vector<Chatroom>();
	}
	
	// 채팅방 목록 리턴
	public Vector<Chatroom> getChatroomList() {
		return chatroomList;
	}
	
	// 채팅방 아이디 입력받아 목록에서 채팅방 삭제
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
	
	// 사용자 아이디 설정
	public void setID(String _ID) {
		ID = _ID;
	}
	
	// 이름 설정
	public void setname(String _name) {
		name = _name;
	}
	
	// 친구 목록에 아이디 추가
	public void addFriend(String friend) {
		friendList.add(friend);
	}
	
	// 아이디 입력받아 친구 목록에서 제거
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
	
	// 아이디 리턴
	public String getID() {
		return ID;
	}
	
	// 이름 리턴
	public String getName() {
		return name;
	}
	
	// 채팅방아이디 받아 해당 채팅방 리턴
	public Chatroom getChatroom(int chatroomID) {
		for(int i=0; i<chatroomList.size(); i++) {
			Chatroom tmp = chatroomList.get(i);
			if(tmp.getChatroomID() == chatroomID)
			{
				return tmp;
			}
		}
		
		// 일치하는 것 없으면 아이디 -1인 채팅방 리턴
		Chatroom tm = new Chatroom();
		tm.setChatroomID(-1);
		return tm;
	}
	
	// 채팅방을 받아서 채팅방 목록에 추가
	public void addChatroom(Chatroom ch) {
		chatroomList.add(ch);
	}
	
	// 친구 목록 리턴
	public Vector<String> getFriend() {
		return friendList;
	}
}
