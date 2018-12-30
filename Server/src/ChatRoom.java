import java.util.Vector;

//채팅방과 관련된 클래스 - getter/setter로 구성되어있으며,
//서버 폴더 내 나머지 클래스들과 유사한 구조를 띔.
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
					System.out.println(newuser.getID() + "가 삭제되었습니다.");
					break;
				}
			}
		}
		
}
