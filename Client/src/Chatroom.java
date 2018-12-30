import java.util.Vector;

// ä�ù� ������ �������ִ� Ŭ����
public class Chatroom {
	private int chatroomID; // ä�ù� ���̵�
	private Vector<String> userIDList = new Vector<String> ();  // ä�ù濡 ������ ������� ���̵� ����� �����ϴ� Vector
	private String quiz;	// �ش� ä�ù��� ����(������)
	
	// ����� �߰�
	public void addUser(String userID) {
		userIDList.add(userID);
	}
	// ���� ���� ����
	public void setQuiz(String _quiz) {
		quiz = _quiz;
	}
	// ����� ���� (����ڰ� ä�ù濡�� ���� ���
	public void deleteUser(String userID) {
		userIDList.remove(userID);
	}
	// ä�ù� ���̵� ����
	public int getChatroomID() {
		return chatroomID;
	}
	// ���� ���� ����
	public String getQuiz() {
		return quiz;
	}
	// ����� ��� ����
	public Vector<String> getUserList() {
		return userIDList;
	}
	// ä�ù� ���̵� ����
	public void setChatroomID(int chatID) {
		chatroomID = chatID;
	}
}
