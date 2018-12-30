import java.util.Vector;

// 채팅방 정보를 가지고있는 클래스
public class Chatroom {
	private int chatroomID; // 채팅방 아이디
	private Vector<String> userIDList = new Vector<String> ();  // 채팅방에 참여한 사용자의 아이디 목록을 저장하는 Vector
	private String quiz;	// 해당 채팅방의 퀴즈(문제만)
	
	// 사용자 추가
	public void addUser(String userID) {
		userIDList.add(userID);
	}
	// 퀴즈 문제 설정
	public void setQuiz(String _quiz) {
		quiz = _quiz;
	}
	// 사용자 삭제 (사용자가 채팅방에서 나갈 경우
	public void deleteUser(String userID) {
		userIDList.remove(userID);
	}
	// 채팅방 아이디 리턴
	public int getChatroomID() {
		return chatroomID;
	}
	// 퀴즈 문제 리턴
	public String getQuiz() {
		return quiz;
	}
	// 사용자 목록 리턴
	public Vector<String> getUserList() {
		return userIDList;
	}
	// 채팅방 아이디 설정
	public void setChatroomID(int chatID) {
		chatroomID = chatID;
	}
}
