import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

//ChatServer : 포트를 여는 클래스.
//이 클래스 내부 함수에 ServerThead클래스가 있음.
public class Server {
	
	private int port = 5566;	// 포트 5566으로 고정
	private ServerSocket server_socket; // 서버 소켓
	private Socket socket; // 서버 클라이언트 간에 accept 과정을 마친 클라이언트에 대한 소켓
	
	private Vector<ServerThread> all_user = new Vector<ServerThread>();		//현재 로그인한 상태인 USER들.
	private Vector<ChatRoom> all_chatroom = new Vector<ChatRoom>();		//모든 채팅방
	private Vector<User> actual_user = new Vector<User>();				//모든 사용자들
	
	private Connection conn = null;		// DB연동
	private Statement stmt = null;		// DB연동
	
	public Server(){
		initiate();			//저장된 사용자 정보를 불러오는 함수
		startNetwork();		//연결 시작
	}
	
	/*
	 * DB 생성 쿼리문 (오라클 DB를 기준으로 작성하였습니다.)
	 * 
	 * CREATE TABLE chatfriend(ID VARCHAR2(15), FRIEND_ID VARCHAR2(15), STATE VARCHAR2(8))
	 * CREATE TABLE "CHATUSER" ("ID" VARCHAR2(15), "PASSWORD" NUMBER(10,0), "NAME" VARCHAR2(15))
	 */
	
	//미리 저장된 DB들을 불러온다.
	public void initiate(){
		
		try{
			// JDBC 연동으로 오라클 DB 사용 설정
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("드라이버 로딩성공");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE","hr","hr"); // (오라클 DB에 접속할 경로와, 포트번호, 데이터베이스 그리고 아이디와 패스워드)
			System.out.println("DB연결 성공");
			
			stmt = conn.createStatement(); //질의문 생성
			ResultSet rs = stmt.executeQuery("select id, password, name from chatuser"); // 유저 정보를 추출하는 질의문을 실행하여, 결과를 반환
			System.out.println("<<사용자DB>>");
			try{
				//질의문에 대한 정보의 끝까지
				while(rs.next()){
					//가져온 유저 정보를 모아 추가한다.
					String id = rs.getString("id"); 
					String password = rs.getString("password");
					String name = rs.getString("name");
					
					User newuser = new User();
					
					//불러온 사용자들을 all_user에 하나하나 저장한다.
					newuser.setUserInfo(id, password, name);
					actual_user.add(newuser);
					
					System.out.println(id + "   " + password + "   " + name);
					
				}
			} catch(Exception ex){}			//여기까지가 사용자DB 불러오기
			
			ResultSet rs2 = stmt.executeQuery("select id, friend_id, state from chatfriend"); //유저 친구에 대한 정보를 추출하는 질의문을 실행하여, 결과를 반환.
			System.out.println("<<친구DB>>");
			try{
				//위 과정과 동일
				while(rs2.next()){
					String id = rs2.getString("id");
					String friend_id = rs2.getString("friend_id");
					String state = rs2.getString("state");
					
					User newuser2 = new User();
					for(int i = 0; i < actual_user.size(); i++){
						if(actual_user.elementAt(i).getID().equals(id)){
							
							for(int j = 0; j < actual_user.size(); j++){
								if(actual_user.elementAt(j).getID().equals(friend_id)){
									newuser2 = actual_user.elementAt(j);
								}
							}		//친구 유저 정보를 찾고 나서.
							
							if(state.equals("친구")){
								actual_user.elementAt(i).addFriend(newuser2);
							}
						}
					}
					
				}
			} catch(Exception ex){}		//여기까지 친구 DB 불러오기.


		} catch(ClassNotFoundException cnfe){
			System.out.println("해당 클래스를 찾을 수 없습니다." + cnfe.getMessage());
		} catch(SQLException se){
			System.out.println(se.getMessage());
		}
	}
	
	// 서버 open
	private void startNetwork(){
		try{
			server_socket = new ServerSocket(port); //지정한 포트번호로 소켓을 생성한다.
			System.out.println("서버를 시작합니다...");
			connect();
		} catch(IOException e){
			System.out.println("이미 사용 중인 포트입니다.");
		} catch(Exception e) {
			System.out.println("잘못 입력하였습니다.");
		}
	}
	
	// 통신 시작
	private void connect(){
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try{
						//클라이언트 쪽에서 연결이 되어야 제대로 작동.
						System.out.println("사용자의 접속을 기다립니다..\n");
						socket = server_socket.accept(); 
						System.out.println("success");
						ServerThread servThread = new ServerThread(socket);
						all_user.add(servThread);
						servThread.start();
					}catch(IOException e){
						System.out.println("서버 중지!! 다시 시도하세요.");
					}
				}
			}
		});
		th.start();	
	}
	
	//test용으로 모든 user, 친구 리스트 확인
	public void printUser(){
		System.out.println("<<All userList>>");
		User newuser = new User();
		Vector<User> friend = new Vector<User>();
		
		for(int i = 0; i < actual_user.size(); i++){
			newuser = actual_user.elementAt(i);
			friend = actual_user.elementAt(i).getFriendList();
			
			System.out.println("id : " + newuser.getID());
			System.out.println("<<FriendList>>");
			for(int j = 0; j < friend.size(); j++){
				System.out.println(friend.elementAt(j).getID());
			}
		}
	}
	
	//쓰레드 함수이다. 각 user마다 쓰레드가 돌면서 메세지를 전달받아 기능을 수행한다.
	class ServerThread extends Thread{

			// 데이터를 주고 받기 위해 사용
			private InputStream is;
			private OutputStream os;
			private DataInputStream dis;
			private DataOutputStream dos;
			
			private BufferedInputStream bin;
			private BufferedOutputStream bout ;
			///////////////////////////////////
			private Socket thread_socket;
			Thread thread;
			
			private int all_room_number = 0;		//방의 수
			User myUser = new User();				//현재 이 쓰레드의 user.
			
			private StringBuffer buf = new StringBuffer(4096);			//메세지를 보낼 때 사용

			// 생성자 관련
			public ServerThread(Socket newsock)
			{
				this.thread_socket = newsock;
				thread = this;
				setStream();
			}
			
			public ServerThread(){
				this.thread_socket = new Socket();
				thread = this;
				setStream();
			}
			///////////////////////////////////////

			// 유저 정보 관련
			public void setUser(User _user){
				myUser = _user;
			}
			
			public User getUser(){
				return myUser;
			}
			
			//로그인을 실제로 수행하는 함수 - 클라이언트 쪽에서 아이디와 패스워드 값을 받았을 경우.
			public User login(String id, String password){
				User newuser = new User();
				for(int i = 0; i < actual_user.size(); i++){
					String a = actual_user.elementAt(i).getID();
					newuser = actual_user.elementAt(i);
					if(newuser.getID().equals(id)){
						if(newuser.getPassword().equals(password)){
							setUser(newuser);
							return newuser;
						}
						else{
							System.out.println("비밀번호가 다릅니다. 다시 시도하세요.\n");
							return null;
						}
					}
				}
				
				System.out.println("없는 계정입니다.\n");
				return null;
			}
			
			// 클라이언트 쪽 회원 가입 폼에서 양식에 값들을 채워넣었을 때,
			public void registerUser(String id, String password, String name){
				User newuser = new User();
				newuser.setUserInfo(id, password, name);
				actual_user.add(newuser);
				
				System.out.println("회원가입 완료");
				
				
				try{
					String query = "insert into chatuser values('" + id + "', " + password + ", '" + name + "')"; // 질의문을 통해 chatuser 테이블에 삽입
	
					//기존 정보 갱신하는 부분
					int count = stmt.executeUpdate(query);
					ResultSet rs = stmt.executeQuery("select id, password, name from chatuser"); 

					try{
						while(rs.next()){
							String _id = rs.getString("id");
							int _password = rs.getInt("password");
							String _name = rs.getString("name");
							
							System.out.println(_id + "   " + _password + "   " + _name);
						}
					} catch(Exception ex){
					}
				} catch(SQLException se){
					System.out.println(se.getMessage());
				}
			}
			
			//필요한 STREAM을 정의.
			private void setStream(){
				try{
					is = thread_socket.getInputStream();
					dis = new DataInputStream(is);
					os = thread_socket.getOutputStream();
					dos = new DataOutputStream(os);
					bout =  new BufferedOutputStream(dos, 2048);
					bin = new BufferedInputStream(dis, 2048);
				} catch(IOException e){
					System.out.println("Stream 설정 에러!\n");
				}
				
			}
			
			
			public void run(){
				try{
					Thread th2 = Thread.currentThread();
					
					while(th2 == thread){
						String msg = dis.readUTF(); // 클라이언트 쪽의 sendMessage 메서드를 통해 메세지를 받아와서 읽고 이를 inmessage에서 처리
						System.out.println("received message : " + msg);
						
						inmessage(msg);
					}
				} catch(IOException e){
					System.out.println("Fail");
				}
			}
			
			//ID와 패스워드를 추출하여, 정보를 검사함.		
			public void getLogIn(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				
				String id = t.nextToken();
				String password = t.nextToken();
			
				User login_user = login(id, password);
				if(login_user != null) {
					for(int i = 0 ; i <all_user.size(); i++){
						if(all_user.elementAt(i).getUser().getID().equals(login_user.getID()))
						{
							ServerThread rr = all_user.elementAt(i);
							buf.setLength(0);
							buf.append("UserInfo");
							buf.append("|");
							buf.append(login_user.getID());
							buf.append("|");
							buf.append(login_user.getName());
							rr.sendMessage(buf.toString());
						}
					}
					
					//broadCast(buf.toString(), r);
					getFriendList("FriendList|"+login_user.getID());
					System.out.println("Send Message from server : "+ buf.toString());
				}
				else if (login_user == null) {
					sendMessage("LoginError|-1");
					System.out.println("Send Message from server : LoginError|-1");
				}
			}
			
			//등록시 입력받은 정보 추출
			public void getRegister(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String password = t.nextToken();
				String name = t.nextToken();
				
				registerUser(id, password, name);	
			}
			
			//친구 추가 시 사용
			public void getAddFriend(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String add_id = t.nextToken();
				
				User newuser = new User();
				//친구 유저를 먼저 찾음
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(add_id)){
						newuser = actual_user.elementAt(i);
					}
				}
				
				if(newuser.getID() == ""){
					//추가할 유저를 찾을 수 없습니다.
				}
				else{
					for(int i = 0; i < actual_user.size(); i++){
						if(actual_user.elementAt(i).getID().equals(id)){
							actual_user.elementAt(i).addFriend(newuser);
						}
					}
				}
				
				try{
					String query = "insert into chatfriend values('" + id + "', '" + add_id + "', '친구')";

					int count = stmt.executeUpdate(query);
					/*ResultSet rs = stmt.executeQuery("select id, password, name from chatuser");

					try{
						while(rs.next()){
							String _id = rs.getString("id");
							int _password = rs.getInt("password");
							String _name = rs.getString("name");
							
							System.out.println(_id + "   " + _password + "   " + _name);
						}
					} catch(Exception ex){
					}*/
				} catch(SQLException se){
					System.out.println(se.getMessage());
				}
				
			}
			
			//친구 삭제 시 사용
			public void getDeleteFriend(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String delete_id = t.nextToken();
				
				User newuser = new User();
				//친구 유저를 먼저 찾음
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(delete_id)){
						newuser = actual_user.elementAt(i);
					}
				}
				
				if(newuser.getID() == ""){
					//추가할 유저를 찾을 수 없습니다.
				}
				else{
					for(int i = 0; i < actual_user.size(); i++){
						if(actual_user.elementAt(i).getID().equals(id)){
							actual_user.elementAt(i).deleteFriend(newuser);
						}
					}
				}
				try{
					String query = "delete from chatfriend where ID = '" + id + "' AND FRIEND_ID = '" + delete_id +"'";

					int count = stmt.executeUpdate(query);
					/*ResultSet rs = stmt.executeQuery("select id, password, name from chatuser");

					try{
						while(rs.next()){
							String _id = rs.getString("id");
							int _password = rs.getInt("password");
							String _name = rs.getString("name");
							
							System.out.println(_id + "   " + _password + "   " + _name);
						}
					} catch(Exception ex){
					}*/
				} catch(SQLException se){
					System.out.println(se.getMessage());
				}
				
			}
			
			
			//메세지를 보내는 기능
			public void getSendMessage(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id, message;
				int roomNumber;
				User roomUser = new User();
				Vector<User> newuser = new Vector<User>();
				
				id = t.nextToken();
				roomNumber =  Integer.valueOf(t.nextToken()).intValue();
				message = t.nextToken();
				
				buf.setLength(0);
				buf.append("SendMessage");
				buf.append("|");
				buf.append(roomNumber);
				buf.append("|");
				buf.append(id);
				buf.append("|");
				buf.append(message);
				//해당 사용자가 가지고 있는 방 번호를 찾아 그 방에 있는 모든 사람에게 그 메세지를 보냄.
				for(int i = 0; i < all_chatroom.size(); i++)
				{
					if(all_chatroom.elementAt(i).getChatroomID() == roomNumber){		//방 번호를 찾으면
						newuser = all_chatroom.elementAt(i).getChatUserList();			//그 방에 있는 모든 사용자들을 불러옴.
						int a = newuser.size();
						for(int j = 0; j < newuser.size(); j++){						//모든 사용자에게 메세지를 보낸다.
					
									for(int p = 0; p < all_user.size(); p++){
											String b = all_user.elementAt(p).getUser().getID();
											if(all_user.elementAt(p).getUser().getID().equals(newuser.elementAt(j).getID())){
												ServerThread r = all_user.elementAt(p);
												r.sendMessage(buf.toString());
											}
										}
									}
									
								}		
					}
				}
			 //방을 만드는 기능
			public void getMakeRoom(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String send_id = t.nextToken();
				ChatRoom newchat = new ChatRoom();
				newchat.setChatroomID(all_room_number);
				
				User newuser = new User();
				User newuser2 = new User();
				
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(id)){
						newuser = actual_user.elementAt(i);
					}
					if(actual_user.elementAt(i).getID().equals(send_id)){
						newuser2 = actual_user.elementAt(i);
					}
				}
				
				boolean thread_check = false;
				boolean thread_check2 = false;
				
				ServerThread r = new ServerThread();
				ServerThread r2 = new ServerThread();
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(id)){
						r = all_user.elementAt(i);
						thread_check = true;
						break;
					}
				}
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(send_id)){
						r2 = all_user.elementAt(i);
						thread_check2 = true;
						break;
					}
				}
				
				if(thread_check == false || thread_check2 == false){
					System.out.println("방을 만들 수 없습니다.");
				}
				else
				{
					newchat.addUser(newuser);
					newchat.addUser(newuser2);
				
					all_chatroom.add(newchat);
					
					buf.setLength(0);
					buf.append("MakeChat");
					buf.append("|");
					buf.append(all_room_number);
					buf.append("|");
					buf.append(r.getUser().getID());
					buf.append("|");
					buf.append(r2.getUser().getID());
					r.sendMessage(buf.toString());
					System.out.println("send1 : " + buf.toString());
				
					buf.setLength(0);
					buf.append("MakeChat");
					buf.append("|");
					buf.append(all_room_number);
					buf.append("|");
					buf.append(r2.getUser().getID());
					buf.append("|");
					buf.append(r.getUser().getID());
					r2.sendMessage(buf.toString());
					all_room_number++;
				}
			}
			
/*
			public void getAcceptFile(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String send_id = t.nextToken();
				String id = t.nextToken();
				String ip = t.nextToken();
				
				User senduser = new User();
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(send_id)){
						senduser = actual_user.elementAt(i);
					}
				}
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(senduser.getID())){
						buf.setLength(0);
						buf.append("AcceptFile");
						buf.append("|");
						buf.append(send_id);
						buf.append("|");
						buf.append(id);
						buf.append("|");
						buf.append(ip);
						ServerThread r = all_user.elementAt(i);
						r.sendMessage(buf.toString());
					}
				}
				
				//보내는 파트 미구현
			}
			*/
			/*
			
			// SendFile - 방번호 - 아이디
			public void getSendFile(String str) {
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				int roomNum = Integer.valueOf(t.nextToken());
				String ID = t.nextToken();
				
				ChatRoom chat;
				Vector<User> user = new Vector<User> ();
				for(int i = 0; i < all_chatroom.size(); i++){
					chat = all_chatroom.elementAt(i);
					user = chat.getChatUserList();			//그 방에 있는 모든 사용자들을 불러옴.
					
					if(chat.getChatroomID() == roomNum) {
						
							for(int j = 0; j < user.size(); j++){						//모든 사용자에게 메세지를 보낸다.
								for(int p = 0; p < all_user.size(); p++){
									if(!user.elementAt(j).getID().equals(ID)) {
										if(all_user.elementAt(p).getUser().getID().equals(user.elementAt(j).getID())) {
											ServerThread r = all_user.elementAt(p);
											r.sendMessage(str);
										}
									}
								}
							}
					}
				}
								
			}*/
			public void write(byte[] data, int off, int len) {
				try{
					bout.write(data, off, len);	// data에 i*size부터 rest만큼 쓴다 
					bout.flush();	// data에 저장된 내용을 비우면서 파일에 전송함
				} catch(IOException e) {
					System.out.println(e);
				}
			}
		
			// SendFile - 아이디 - 방번호
			public void getSendFile(String str) {
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				int room = Integer.valueOf(t.nextToken());
				
				Vector<User> newuser = new Vector<User> ();
				for(int i = 0; i < all_chatroom.size(); i++) {
					if(all_chatroom.elementAt(i).getChatroomID() == room) {		//방 번호를 찾으면
						newuser = all_chatroom.elementAt(i).getChatUserList();			//그 방에 있는 모든 사용자들을 불러옴.
						int a = newuser.size();
						for(int j = 0; j < a; j++) {						//모든 사용자에게 메세지를 보낸다.
							if(!(newuser.elementAt(j).getID().equals(id))) {
								for(int p = 0; p < all_user.size(); p++) {
									String b = all_user.elementAt(p).getUser().getID();
									if(all_user.elementAt(p).getUser().getID().equals(newuser.elementAt(j).getID())){
										ServerThread r = all_user.elementAt(p);
										r.sendMessage(str);
									}
								}
							}
						}
					}		
				}
				
				try {
				
				String msg;
				// filename|fileLength 처리
				msg = dis.readUTF();
				System.out.println(msg);
				StringTokenizer st1 = new StringTokenizer(msg, "|" );
				String filename = st1.nextToken();
				int fileLength = Integer.valueOf(st1.nextToken());
				for(int j = 0; j < newuser.size(); j++) {						//모든 사용자에게 메세지를 보낸다.
					if(!(newuser.elementAt(j).getID().equals(id))) {
						for(int p = 0; p < all_user.size(); p++) {
							String b = all_user.elementAt(p).getUser().getID();
							if(all_user.elementAt(p).getUser().getID().equals(newuser.elementAt(j).getID())){
								ServerThread r = all_user.elementAt(p);
								r.sendMessage(filename+ "|" +String.valueOf(fileLength));
							}
						}
					}
				}
				bout.flush();
				
				
				// 파일을 받아 저장할 byte[]
				byte[] data = new byte[fileLength];
				
				int size = 2048;
				int count = fileLength/size;
				int rest = fileLength%size;
				int flag = 1;
				
				if (count == 0) flag = 0;
				
				for (int i=0; i<=count; i++) {
					// 파일 수신이 끝난 경우
					if(i == count && flag == 0) {
						// data를 전송받아서
						bin.read(data, 0, rest);
						for(int j = 0; j < newuser.size(); j++) {						//모든 사용자에게 메세지를 보낸다.
							if(!(newuser.elementAt(j).getID().equals(id))) {
								for(int p = 0; p < all_user.size(); p++) {
									String b = all_user.elementAt(p).getUser().getID();
									if(all_user.elementAt(p).getUser().getID().equals(newuser.elementAt(j).getID())){
										ServerThread r = all_user.elementAt(p);
										r.write(data, 0, rest);
									}
								}
							}
						}
						System.out.println("전송끝");
						break;
					}
					// 파일 수신이 끝난 경우
					else if(i == count) {
						bin.read(data, i*size, rest);
						for(int j = 0; j < newuser.size(); j++) {						//모든 사용자에게 메세지를 보낸다.
							if(!(newuser.elementAt(j).getID().equals(id))) {
								for(int p = 0; p < all_user.size(); p++) {
									String b = all_user.elementAt(p).getUser().getID();
									if(all_user.elementAt(p).getUser().getID().equals(newuser.elementAt(j).getID())){
										ServerThread r = all_user.elementAt(p);
										r.write(data, i*size, rest);
									}
								}
							}
						}
						System.out.println("전송끝");
						break;
					}
					// 파일을 더 수신해야하는 경우
					else {
						bin.read(data, i*size, size);
						for(int j = 0; j < newuser.size(); j++) {						//모든 사용자에게 메세지를 보낸다.
							if(!(newuser.elementAt(j).getID().equals(id))) {
								for(int p = 0; p < all_user.size(); p++) {
									String b = all_user.elementAt(p).getUser().getID();
									if(all_user.elementAt(p).getUser().getID().equals(newuser.elementAt(j).getID())){
										ServerThread r = all_user.elementAt(p);
										r.write(data, i*size, size);
									}
								}
							}
						}
					}
					// 파일 전송 끝 
					//만약 예외상황이 발생했다면 아래의 영역에 정의된 코드를 실행합니다.
				}
				} catch (IOException e) {
						e.printStackTrace();
				}
			}
			
			
			public void getJoinUser(String str){				//채팅방 참여
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				int chatroomID = Integer.valueOf(t.nextToken()).intValue();
				String id = t.nextToken();
				
				User newuser  = new User();
				Vector<User> userList = new Vector<User>();
				ChatRoom newchat = new ChatRoom();
			//	ServerThread r = new ServerThread();
				
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(id)){
						//r = all_user.elementAt(i);
						newuser = actual_user.elementAt(i);
					}
				}
				
				for(int i = 0; i < all_chatroom.size(); i++){
					if(all_chatroom.elementAt(i).getChatroomID() == chatroomID){
						newchat = all_chatroom.elementAt(i);
						userList = newchat.getChatUserList();
					}
				}
				
				//방의 나머지 사용자들에게 새로운 유저의 초대를 알림.
				for(int i = 0; i < userList.size(); i++){
					int a = all_user.size();
					String b = userList.elementAt(i).getID();
					for(int j = 0; j < all_user.size(); j++){
					if(all_user.elementAt(j).getUser().getID().equals(userList.elementAt(i).getID())){
						ServerThread r = all_user.elementAt(j);
						buf.setLength(0);
						buf.append("JoinNewMember");
						buf.append("|");
						buf.append(newchat.getChatroomID());
						buf.append("|");
						buf.append(newuser.getID());
						System.out.println(buf.toString());
						r.sendMessage(buf.toString());
					}
					}
				}
				newchat.addUser(newuser);
				
				//새로 들어오는 유저에게는 makechat을 보내고 사용자들의 목록은 joinNewMember
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(id)){
						ServerThread r2 = all_user.elementAt(i);
						buf.setLength(0);
						buf.append("MakeChat");
						buf.append("|");
						buf.append(newchat.getChatroomID());
						buf.append("|");
						buf.append(newuser.getID());
						buf.append("|");
						buf.append(userList.elementAt(0).getID());
						r2.sendMessage(buf.toString());
						
						int c = userList.size();
						for(int j = 1; j < userList.size(); j++){
							String d = userList.elementAt(j).getID();
							String e = newuser.getID();
							if(!userList.elementAt(j).getID().equals(newuser.getID())){
							buf.setLength(0);
							buf.append("JoinNewMember");
							buf.append("|");
							buf.append(newchat.getChatroomID());
							buf.append("|");
							buf.append(userList.elementAt(j).getID());
							System.out.println(buf.toString());
							r2.sendMessage(buf.toString());
							}
						}
					}
				}
			}
			// 유저 검색 기능
			public void getSearchUser(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				
				String id = t.nextToken();
				String keyword = t.nextToken();
				Vector<String> id_List = new Vector<String>();
				
				int num = 0;
				
				try{
					String query = "select id, password, name from chatuser where id LIKE '%" + keyword + "%'";
					int count = stmt.executeUpdate(query);
					ResultSet rs = stmt.executeQuery("select id, password, name from chatuser where id LIKE '%" + keyword + "%'");
					
					try{
						while(rs.next()){
							String _id = rs.getString("id");
							int _password = rs.getInt("password");
							String _name = rs.getString("name");
							
							System.out.println(_id + "   " + _password + "   " + _name );
							id_List.add(_id);
							num++;
							//보내야 하는데... 애매함.
						}
					} catch(Exception ex){
					}
				} catch(SQLException se){
					System.out.println(se.getMessage());
				}
				
				buf.setLength(0);
				buf.append("SearchUser");
				buf.append("|");
				buf.append(num);
				for(int i = 0; i < id_List.size(); i++){
					buf.append("|");
					buf.append(id_List.elementAt(i));
				}
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(id)){
						ServerThread r = all_user.elementAt(i);
						System.out.println(buf.toString());
						r.sendMessage(buf.toString());
					}
				}
			}
			//친구의 개인정보를 보고 싶을 때
			
			public void getOutChatroom(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();

				String id = t.nextToken();		//나간 클라이언트 아이디
				int chatroom_num = Integer.valueOf(t.nextToken()).intValue();
			//	String id = t.nextToken();		//나간 클라이언트 아이디
				
				Vector<User> temp = new Vector<User>();
				User newuser = new User();
				
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(id)){
						newuser = actual_user.elementAt(i);
					}
				}
				
				for(int i = 0; i < all_chatroom.size(); i++){
					if(all_chatroom.elementAt(i).getChatroomID() == chatroom_num){
						temp = all_chatroom.elementAt(i).getChatUserList();
						all_chatroom.elementAt(i).deleteUser(newuser);			//해당 유저는 채팅방 사용자 목록에서 삭제.
						
						for(int j = 0; j < temp.size(); j++)
						{
							if(!temp.elementAt(j).getID().equals(id))		//나간 사용자는 제외하고 보내야 함.
							{
								for(int k = 0; k < all_user.size(); k++){
									if(all_user.elementAt(k).getUser().getID().equals(temp.elementAt(j).getID())){
										ServerThread r = all_user.elementAt(k);
										buf.setLength(0);
										buf.append("OutChatroom");
										buf.append("|");
										buf.append(chatroom_num);
										buf.append("|");
										buf.append(id);
										r.sendMessage(buf.toString());
									}
								}
							}
						}
					}
				}
			}
			//유저 정보 기능
			public void getUserInfo(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				
				String id = t.nextToken();
				String friend_id = t.nextToken();
				User curuser = new User();
				User frienduser = new User();
				
				//ServerThread r = new ServerThread();
				
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(id)){
						curuser = actual_user.elementAt(i);
					//	r = all_user.elementAt(i);
					}
				}
				
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(friend_id)){
						frienduser = actual_user.elementAt(i);
					}
				}
				
				for(int i = 0; i < all_user.size(); i++){
				buf.setLength(0);
				buf.append("UserInfo");
				buf.append("|");
				buf.append(frienduser.getID());
				buf.append("|");
				buf.append(frienduser.getName());
				System.out.println(buf.toString());
				if(all_user.elementAt(i).getUser().getID().equals(curuser.getID())){
					ServerThread r = all_user.elementAt(i);
					r.sendMessage(buf.toString());
					break;
				}
				//r.sendMessage(buf.toString());
				}
			}
			
			public void getFriendList(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				
				String id = t.nextToken();
				User newuser = new User();
				
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(id)){
						newuser = actual_user.elementAt(i);
						break;
					}
				}
				
				buf.setLength(0);
				buf.append("FriendList");
				buf.append("|");
				buf.append(newuser.getFriendList().size());
				for(int i = 0; i < newuser.getFriendList().size(); i++){
					buf.append("|");
					buf.append(newuser.getFriendList().elementAt(i).getID());
				}
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(id)){
						ServerThread r = all_user.elementAt(i);
						r = all_user.elementAt(i);
						System.out.println(buf.toString());
						r.sendMessage(buf.toString());
					}
				}
			}	
			// CheckAnswer - 방번호 - 내아이디 - 정답
			private void getCheckAnswer(String message) {
				StringTokenizer st = new StringTokenizer(message, "|");
				String ans = st.nextToken();
				int roomNum = Integer.valueOf(st.nextToken());
				String ID = st.nextToken();
				ans = st.nextToken();
				
				ChatRoom chat;
				Vector<User> user;
				
				for(int i = 0; i < all_chatroom.size(); i++){
					chat = all_chatroom.elementAt(i);
					user = chat.getChatUserList();			//그 방에 있는 모든 사용자들을 불러옴.
					
				}
			}

			//퀴즈에 대한 기능

			//클라이언트에게서 받은 메시지를 분석하는 메서드
			//메세지의 종류에 따라 호출되는 메서드가 다름.
			private void inmessage(String str){
				StringTokenizer st = new StringTokenizer(str, "|");
				String protocol = st.nextToken();
				
				System.out.println("Protocol : " + protocol);
				
				if(protocol.equals("Login")){
						getLogIn(str);
				}
				else if(protocol.equals("CheckAnswer")) {
					getCheckAnswer(str);
				}
				else if(protocol.equals("Register")){
						getRegister(str);
				}
				
				else if(protocol.equals("AddFriend")){
						getAddFriend(str);
				}
				
				else if(protocol.equals("DeleteFriend")){
						getDeleteFriend(str);
				}
				
				else if(protocol.equals("MakeChat")){
						getMakeRoom(str);
				}
				
				else if(protocol.equals("SendMessage")){
						getSendMessage(str);
				}
				/*
				else if(protocol.equals("AcceptFile")){
						getAcceptFile(str);
				}
				*/
				else if(protocol.equals("SendFile")){
						getSendFile(str);
				}
				
				else if(protocol.equals("JoinUser")){
						getJoinUser(str);
				}	
				 
				else if(protocol.equals("OutChatroom")){
						getOutChatroom(str);
				}
				
				else if(protocol.equals("SearchUser")){
						getSearchUser(str);
				}
				
				else if(protocol.equals("Paper")) {
					getPaper(str);
				}
				else if(protocol.equals("UserInfo") || protocol.equals("ShowFriendInfo")){
						getUserInfo(str);
				}
				
				else if(protocol.equals("FriendList")){
						getFriendList(str);
				}
			}	

			private void getPaper(String msg) {
				StringTokenizer t = new StringTokenizer(msg, "|");
				String protocol = t.nextToken();
				
				String id = t.nextToken();
				String idTo = t.nextToken();
				String message = t.nextToken();
				
				buf.setLength(0);
				buf.append(protocol);
				buf.append("|");
				buf.append(idTo);
				buf.append("|");
				buf.append(id);
				buf.append("|");
				buf.append(message);
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getUser().getID().equals(id)){
						ServerThread r = all_user.elementAt(i);
						r = all_user.elementAt(i);
						System.out.println(buf.toString());
						r.sendMessage(buf.toString());
					}
				}
			}
			
			private void sendMessage(String msg){
				try{
					dos.writeUTF(msg);
					dos.flush();
					System.out.println("server send완료");
				} catch(IOException e){
					e.printStackTrace();
				}
			}
			
	}
	
	public static void main(String[] args) {
		new Server();
	}

}
