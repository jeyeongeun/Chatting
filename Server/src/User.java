import java.util.Vector;

// 유저에 대한 클래스
public class User {
	//유저 정보를 담을 때 사용해야 될 멤버 변수
	private String id;
	private String password;
	private String name;
	int random_num;
	////////////////////////////////////////////

	private Vector<User> friendList = new Vector<User>(); //친구 리스트를 벡터형식으로 
	private Vector<ChatRoom> chatroomList = new Vector<ChatRoom>();  //채팅방 리스트를 벡터형식으로 생성
	private int chatroom_num = chatroomList.size(); // 채팅방 리스트의 개수를 저장
	
	public User(){ //생성자를 통한 초기화
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
	public int getChatroomNum(){ // 채팅방 번호 값 추출
		return chatroom_num;
	}
	
	public Vector<User> getFriendList(){ // 친구 리스트 추출
		return friendList;
	}
	
	public Vector<ChatRoom> getChatList(){ // 채팅방 리스트 추출
		return chatroomList;
	}
	
	public String getID(){ // ID 추출
		return id;
	}
	
	public String getPassword(){ // PW 추출
		return password;
	}
	
	public String getName(){ // 이름 추출
		return name;
	}
		
	public void setUserInfo(String _id, String _password, String _name){ // 유저 정보 설정
		id = _id;
		password = _password;
		name = _name;
	}
		
	public void addFriend(User newuser){ // 친구 추가
		friendList.add(newuser);
	}
	
	public void deleteFriend(User newuser){ // 친구 삭제
		for(int i = 0; i < friendList.size(); i++){
			User alluser = friendList.elementAt(i);
			if(alluser.getID().equals(newuser.getID())){
				friendList.removeElementAt(i);
				System.out.println(newuser.getID() + "가 친구 목록에 추가되었습니다.");
				break;
			}
		}
	}
	
	public void addChatRoom(ChatRoom newchat){ // 채팅방 추가
		chatroomList.addElement(newchat);
	}
	
	public void deleteChatRoom(ChatRoom newchat){ // 채팅방 삭제
		for(int i = 0; i < chatroomList.size(); i++){
			ChatRoom allchat = chatroomList.elementAt(i);
			if(allchat.getChatroomID() == newchat.getChatroomID()){
				chatroomList.removeElementAt(i);
				System.out.println(allchat.getChatroomID() + "가 채팅 목록에서 삭제되었습니다.");
				break;
			}
		}
	}
}
