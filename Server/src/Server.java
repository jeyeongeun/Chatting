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

//ChatServer : ��Ʈ�� ���� Ŭ����.
//�� Ŭ���� ���� �Լ��� ServerTheadŬ������ ����.
public class Server {
	
	private int port = 5566;	// ��Ʈ 5566���� ����
	private ServerSocket server_socket; // ���� ����
	private Socket socket; // ���� Ŭ���̾�Ʈ ���� accept ������ ��ģ Ŭ���̾�Ʈ�� ���� ����
	
	private Vector<ServerThread> all_user = new Vector<ServerThread>();		//���� �α����� ������ USER��.
	private Vector<ChatRoom> all_chatroom = new Vector<ChatRoom>();		//��� ä�ù�
	private Vector<User> actual_user = new Vector<User>();				//��� ����ڵ�
	
	private Connection conn = null;		// DB����
	private Statement stmt = null;		// DB����
	
	public Server(){
		initiate();			//����� ����� ������ �ҷ����� �Լ�
		startNetwork();		//���� ����
	}
	
	/*
	 * DB ���� ������ (����Ŭ DB�� �������� �ۼ��Ͽ����ϴ�.)
	 * 
	 * CREATE TABLE chatfriend(ID VARCHAR2(15), FRIEND_ID VARCHAR2(15), STATE VARCHAR2(8))
	 * CREATE TABLE "CHATUSER" ("ID" VARCHAR2(15), "PASSWORD" NUMBER(10,0), "NAME" VARCHAR2(15))
	 */
	
	//�̸� ����� DB���� �ҷ��´�.
	public void initiate(){
		
		try{
			// JDBC �������� ����Ŭ DB ��� ����
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("����̹� �ε�����");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE","hr","hr"); // (����Ŭ DB�� ������ ��ο�, ��Ʈ��ȣ, �����ͺ��̽� �׸��� ���̵�� �н�����)
			System.out.println("DB���� ����");
			
			stmt = conn.createStatement(); //���ǹ� ����
			ResultSet rs = stmt.executeQuery("select id, password, name from chatuser"); // ���� ������ �����ϴ� ���ǹ��� �����Ͽ�, ����� ��ȯ
			System.out.println("<<�����DB>>");
			try{
				//���ǹ��� ���� ������ ������
				while(rs.next()){
					//������ ���� ������ ��� �߰��Ѵ�.
					String id = rs.getString("id"); 
					String password = rs.getString("password");
					String name = rs.getString("name");
					
					User newuser = new User();
					
					//�ҷ��� ����ڵ��� all_user�� �ϳ��ϳ� �����Ѵ�.
					newuser.setUserInfo(id, password, name);
					actual_user.add(newuser);
					
					System.out.println(id + "   " + password + "   " + name);
					
				}
			} catch(Exception ex){}			//��������� �����DB �ҷ�����
			
			ResultSet rs2 = stmt.executeQuery("select id, friend_id, state from chatfriend"); //���� ģ���� ���� ������ �����ϴ� ���ǹ��� �����Ͽ�, ����� ��ȯ.
			System.out.println("<<ģ��DB>>");
			try{
				//�� ������ ����
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
							}		//ģ�� ���� ������ ã�� ����.
							
							if(state.equals("ģ��")){
								actual_user.elementAt(i).addFriend(newuser2);
							}
						}
					}
					
				}
			} catch(Exception ex){}		//������� ģ�� DB �ҷ�����.


		} catch(ClassNotFoundException cnfe){
			System.out.println("�ش� Ŭ������ ã�� �� �����ϴ�." + cnfe.getMessage());
		} catch(SQLException se){
			System.out.println(se.getMessage());
		}
	}
	
	// ���� open
	private void startNetwork(){
		try{
			server_socket = new ServerSocket(port); //������ ��Ʈ��ȣ�� ������ �����Ѵ�.
			System.out.println("������ �����մϴ�...");
			connect();
		} catch(IOException e){
			System.out.println("�̹� ��� ���� ��Ʈ�Դϴ�.");
		} catch(Exception e) {
			System.out.println("�߸� �Է��Ͽ����ϴ�.");
		}
	}
	
	// ��� ����
	private void connect(){
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try{
						//Ŭ���̾�Ʈ �ʿ��� ������ �Ǿ�� ����� �۵�.
						System.out.println("������� ������ ��ٸ��ϴ�..\n");
						socket = server_socket.accept(); 
						System.out.println("success");
						ServerThread servThread = new ServerThread(socket);
						all_user.add(servThread);
						servThread.start();
					}catch(IOException e){
						System.out.println("���� ����!! �ٽ� �õ��ϼ���.");
					}
				}
			}
		});
		th.start();	
	}
	
	//test������ ��� user, ģ�� ����Ʈ Ȯ��
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
	
	//������ �Լ��̴�. �� user���� �����尡 ���鼭 �޼����� ���޹޾� ����� �����Ѵ�.
	class ServerThread extends Thread{

			// �����͸� �ְ� �ޱ� ���� ���
			private InputStream is;
			private OutputStream os;
			private DataInputStream dis;
			private DataOutputStream dos;
			
			private BufferedInputStream bin;
			private BufferedOutputStream bout ;
			///////////////////////////////////
			private Socket thread_socket;
			Thread thread;
			
			private int all_room_number = 0;		//���� ��
			User myUser = new User();				//���� �� �������� user.
			
			private StringBuffer buf = new StringBuffer(4096);			//�޼����� ���� �� ���

			// ������ ����
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

			// ���� ���� ����
			public void setUser(User _user){
				myUser = _user;
			}
			
			public User getUser(){
				return myUser;
			}
			
			//�α����� ������ �����ϴ� �Լ� - Ŭ���̾�Ʈ �ʿ��� ���̵�� �н����� ���� �޾��� ���.
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
							System.out.println("��й�ȣ�� �ٸ��ϴ�. �ٽ� �õ��ϼ���.\n");
							return null;
						}
					}
				}
				
				System.out.println("���� �����Դϴ�.\n");
				return null;
			}
			
			// Ŭ���̾�Ʈ �� ȸ�� ���� ������ ��Ŀ� ������ ä���־��� ��,
			public void registerUser(String id, String password, String name){
				User newuser = new User();
				newuser.setUserInfo(id, password, name);
				actual_user.add(newuser);
				
				System.out.println("ȸ������ �Ϸ�");
				
				
				try{
					String query = "insert into chatuser values('" + id + "', " + password + ", '" + name + "')"; // ���ǹ��� ���� chatuser ���̺� ����
	
					//���� ���� �����ϴ� �κ�
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
			
			//�ʿ��� STREAM�� ����.
			private void setStream(){
				try{
					is = thread_socket.getInputStream();
					dis = new DataInputStream(is);
					os = thread_socket.getOutputStream();
					dos = new DataOutputStream(os);
					bout =  new BufferedOutputStream(dos, 2048);
					bin = new BufferedInputStream(dis, 2048);
				} catch(IOException e){
					System.out.println("Stream ���� ����!\n");
				}
				
			}
			
			
			public void run(){
				try{
					Thread th2 = Thread.currentThread();
					
					while(th2 == thread){
						String msg = dis.readUTF(); // Ŭ���̾�Ʈ ���� sendMessage �޼��带 ���� �޼����� �޾ƿͼ� �а� �̸� inmessage���� ó��
						System.out.println("received message : " + msg);
						
						inmessage(msg);
					}
				} catch(IOException e){
					System.out.println("Fail");
				}
			}
			
			//ID�� �н����带 �����Ͽ�, ������ �˻���.		
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
			
			//��Ͻ� �Է¹��� ���� ����
			public void getRegister(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String password = t.nextToken();
				String name = t.nextToken();
				
				registerUser(id, password, name);	
			}
			
			//ģ�� �߰� �� ���
			public void getAddFriend(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String add_id = t.nextToken();
				
				User newuser = new User();
				//ģ�� ������ ���� ã��
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(add_id)){
						newuser = actual_user.elementAt(i);
					}
				}
				
				if(newuser.getID() == ""){
					//�߰��� ������ ã�� �� �����ϴ�.
				}
				else{
					for(int i = 0; i < actual_user.size(); i++){
						if(actual_user.elementAt(i).getID().equals(id)){
							actual_user.elementAt(i).addFriend(newuser);
						}
					}
				}
				
				try{
					String query = "insert into chatfriend values('" + id + "', '" + add_id + "', 'ģ��')";

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
			
			//ģ�� ���� �� ���
			public void getDeleteFriend(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				String delete_id = t.nextToken();
				
				User newuser = new User();
				//ģ�� ������ ���� ã��
				for(int i = 0; i < actual_user.size(); i++){
					if(actual_user.elementAt(i).getID().equals(delete_id)){
						newuser = actual_user.elementAt(i);
					}
				}
				
				if(newuser.getID() == ""){
					//�߰��� ������ ã�� �� �����ϴ�.
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
			
			
			//�޼����� ������ ���
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
				//�ش� ����ڰ� ������ �ִ� �� ��ȣ�� ã�� �� �濡 �ִ� ��� ������� �� �޼����� ����.
				for(int i = 0; i < all_chatroom.size(); i++)
				{
					if(all_chatroom.elementAt(i).getChatroomID() == roomNumber){		//�� ��ȣ�� ã����
						newuser = all_chatroom.elementAt(i).getChatUserList();			//�� �濡 �ִ� ��� ����ڵ��� �ҷ���.
						int a = newuser.size();
						for(int j = 0; j < newuser.size(); j++){						//��� ����ڿ��� �޼����� ������.
					
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
			 //���� ����� ���
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
					System.out.println("���� ���� �� �����ϴ�.");
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
				
				//������ ��Ʈ �̱���
			}
			*/
			/*
			
			// SendFile - ���ȣ - ���̵�
			public void getSendFile(String str) {
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				int roomNum = Integer.valueOf(t.nextToken());
				String ID = t.nextToken();
				
				ChatRoom chat;
				Vector<User> user = new Vector<User> ();
				for(int i = 0; i < all_chatroom.size(); i++){
					chat = all_chatroom.elementAt(i);
					user = chat.getChatUserList();			//�� �濡 �ִ� ��� ����ڵ��� �ҷ���.
					
					if(chat.getChatroomID() == roomNum) {
						
							for(int j = 0; j < user.size(); j++){						//��� ����ڿ��� �޼����� ������.
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
					bout.write(data, off, len);	// data�� i*size���� rest��ŭ ���� 
					bout.flush();	// data�� ����� ������ ���鼭 ���Ͽ� ������
				} catch(IOException e) {
					System.out.println(e);
				}
			}
		
			// SendFile - ���̵� - ���ȣ
			public void getSendFile(String str) {
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();
				System.out.println(protocol);
				
				String id = t.nextToken();
				int room = Integer.valueOf(t.nextToken());
				
				Vector<User> newuser = new Vector<User> ();
				for(int i = 0; i < all_chatroom.size(); i++) {
					if(all_chatroom.elementAt(i).getChatroomID() == room) {		//�� ��ȣ�� ã����
						newuser = all_chatroom.elementAt(i).getChatUserList();			//�� �濡 �ִ� ��� ����ڵ��� �ҷ���.
						int a = newuser.size();
						for(int j = 0; j < a; j++) {						//��� ����ڿ��� �޼����� ������.
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
				// filename|fileLength ó��
				msg = dis.readUTF();
				System.out.println(msg);
				StringTokenizer st1 = new StringTokenizer(msg, "|" );
				String filename = st1.nextToken();
				int fileLength = Integer.valueOf(st1.nextToken());
				for(int j = 0; j < newuser.size(); j++) {						//��� ����ڿ��� �޼����� ������.
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
				
				
				// ������ �޾� ������ byte[]
				byte[] data = new byte[fileLength];
				
				int size = 2048;
				int count = fileLength/size;
				int rest = fileLength%size;
				int flag = 1;
				
				if (count == 0) flag = 0;
				
				for (int i=0; i<=count; i++) {
					// ���� ������ ���� ���
					if(i == count && flag == 0) {
						// data�� ���۹޾Ƽ�
						bin.read(data, 0, rest);
						for(int j = 0; j < newuser.size(); j++) {						//��� ����ڿ��� �޼����� ������.
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
						System.out.println("���۳�");
						break;
					}
					// ���� ������ ���� ���
					else if(i == count) {
						bin.read(data, i*size, rest);
						for(int j = 0; j < newuser.size(); j++) {						//��� ����ڿ��� �޼����� ������.
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
						System.out.println("���۳�");
						break;
					}
					// ������ �� �����ؾ��ϴ� ���
					else {
						bin.read(data, i*size, size);
						for(int j = 0; j < newuser.size(); j++) {						//��� ����ڿ��� �޼����� ������.
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
					// ���� ���� �� 
					//���� ���ܻ�Ȳ�� �߻��ߴٸ� �Ʒ��� ������ ���ǵ� �ڵ带 �����մϴ�.
				}
				} catch (IOException e) {
						e.printStackTrace();
				}
			}
			
			
			public void getJoinUser(String str){				//ä�ù� ����
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
				
				//���� ������ ����ڵ鿡�� ���ο� ������ �ʴ븦 �˸�.
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
				
				//���� ������ �������Դ� makechat�� ������ ����ڵ��� ����� joinNewMember
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
			// ���� �˻� ���
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
							//������ �ϴµ�... �ָ���.
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
			//ģ���� ���������� ���� ���� ��
			
			public void getOutChatroom(String str){
				StringTokenizer t = new StringTokenizer(str, "|");
				String protocol = t.nextToken();

				String id = t.nextToken();		//���� Ŭ���̾�Ʈ ���̵�
				int chatroom_num = Integer.valueOf(t.nextToken()).intValue();
			//	String id = t.nextToken();		//���� Ŭ���̾�Ʈ ���̵�
				
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
						all_chatroom.elementAt(i).deleteUser(newuser);			//�ش� ������ ä�ù� ����� ��Ͽ��� ����.
						
						for(int j = 0; j < temp.size(); j++)
						{
							if(!temp.elementAt(j).getID().equals(id))		//���� ����ڴ� �����ϰ� ������ ��.
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
			//���� ���� ���
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
			// CheckAnswer - ���ȣ - �����̵� - ����
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
					user = chat.getChatUserList();			//�� �濡 �ִ� ��� ����ڵ��� �ҷ���.
					
				}
			}

			//��� ���� ���

			//Ŭ���̾�Ʈ���Լ� ���� �޽����� �м��ϴ� �޼���
			//�޼����� ������ ���� ȣ��Ǵ� �޼��尡 �ٸ�.
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
					System.out.println("server send�Ϸ�");
				} catch(IOException e){
					e.printStackTrace();
				}
			}
			
	}
	
	public static void main(String[] args) {
		new Server();
	}

}
