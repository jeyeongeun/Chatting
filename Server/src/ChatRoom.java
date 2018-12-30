import java.util.Vector;

//ä�ù�� ���õ� Ŭ���� - getter/setter�� �����Ǿ�������,
//���� ���� �� ������ Ŭ������� ������ ������ ��.
public class ChatRoom {
		private int chatroomID;
		private Vector<User> userList = new Vector<User>();
		
		public int getChatroomID(){
			return chatroomID;
		}
		
		public Vector<User> getChatUserList(){
			return userList;
		}
		
		public void setChatroomID(int _id){
			chatroomID = _id;
		}
		
		public void addUser(User newuser){
			//for(int i = 0; i < userList.size(); i++){
				userList.add(newuser);
			//}
		}
		
		public void deleteUser(User newuser){
			for(int i = 0; i < userList.size(); i++){
				User alluser = userList.elementAt(i);
				if(alluser.getID().equals(newuser.getID())){
					userList.removeElementAt(i);
					System.out.println(newuser.getID() + "�� �����Ǿ����ϴ�.");
					break;
				}
			}
		}
		
}
