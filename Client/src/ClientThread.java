import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

public class ClientThread extends Thread {
	
	private Socket sock;	// 통신을 위한 소켓
	private DataInputStream din;	// dataInput
	private DataOutputStream dout;	// dataOutput
	private StringBuffer buff;	// 서버로 전송할 메시지
	private Thread thread;		// 쓰레드
	private User curUser;	// 로그인한 사용자
	private User userInfo = new User();	// 사용자 정보보기 할 때 사용함
	private loginDisplay loginD;	// 로그인 display
	private mainDisplay mainD;		// 메인 display 
	private Vector<ChatroomDisplay> chatD = new Vector<ChatroomDisplay> ();	 // 채팅방 display 목록
	private userInfoDisplay infoD;	// 사용자정보 display
	
	//소켓을 얻어온다.
	public Socket getSocket() {
		return sock;
	}
	public ClientThread() {
		try {
			
			// 소켓 통신을 위한 초기화과정
			// 실제 통신을 위해서는 아이피주소가 설정되어있어야 하지만 테스트및 시연 용이므로 127.0.0.1
			// 포트는 임의로 5566으로 설정함
			sock = new Socket("127.0.0.1", 5566);
			din = new DataInputStream(sock.getInputStream());
			dout = new DataOutputStream(sock.getOutputStream());
			
			// 전송할 메시지를 위한 버퍼 설정
			buff = new StringBuffer(4096);
			// 쓰레드 설정
			thread = this;
			
			// 로그인화면 출력
			loginD = new loginDisplay(this);

			//로그인 화면이 보이도록 한다. 
			loginD.setVisible(true);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	// 현재 로그인한 사용자 리턴
	public User getUser() {
		return curUser;
	}
	
	// 사용자정보보기를 위한 사용자정보 리턴
	public User getUserInfo() {
		return userInfo;
	}
	
	// 실제 통신 부분
	// 서버에서 메시지를 받아서 처리함
	@SuppressWarnings("deprecation")
	public void run() {
		try {

			boolean bfile = false;
			String filename = ""; // 파일 
			int fileLength = 0;
			byte[] content = new byte[fileLength];
			
			// 현재 쓰레드를 확인해서 메시지를 받고 실행함
			Thread th = Thread.currentThread(); 
			while(th == thread) {
				
				String recvData = din.readUTF();	// 서버에서 메시지 받아옴
				System.out.println("recv : "+ recvData);	// 전송받은 메시지 확인을 위해서 콘솔 창에 출력함 
				
				// 전송받은 메시지에서 필요한 내용들은 | 으로 구분한다  - StringTokenizer
				// 메시지의 제일 처음은 어떤 용도의 메시지인지를 구분하는 것
				// 그 다음이 그 내용을 저장
				
				if(bfile) {
					System.out.println("파일 전송 : " + recvData);
					
					// 파일이름과 크기를 추출합니다.
					StringTokenizer stk = new StringTokenizer(recvData, "|");
					filename = stk.nextToken();
					fileLength = Integer.valueOf(stk.nextToken());
					
					// data에 파일의 내용을 입력받습니다.
					content = new byte[fileLength];
					try {
						// 파일의 내용을 수신합니다.
						int size = 2048;
						int count = fileLength/size;
						int rest = fileLength%size;
						int flag = 1;
						
						if (count == 0) flag = 0;
						
						BufferedInputStream bin = new BufferedInputStream(din, 2048);
						
						for (int i=0; i<=count; i++) {
							// 파일 수신이 끝난 경우
							if(i == count && flag == 0) {
								bin.read(content, 0, rest);
								break;
							}
							// 파일 수신이 끝난 경우
							else if(i == count) {
								bin.read(content, i*size, rest);
								break;
							}
							// 파일을 더 수신해야하는 경우
							else {
								bin.read(content, i*size, size);
							}
						}
					} catch (IOException e) {}
				/////////////////////////////////////////// 파일 내용 읽어오기 끝
				
					System.out.println("읽어오기끝");
				
				// 디렉토리 파일 객체생성 : '받은 파일' 이라는 디렉토리에 파일을 저장한다
				// 각각 클라이언트의 id를 뒤에 추가하여 어느 유저가 받은 파일인지 확인할 수 있게 했습니다.
				File dir = new File("받은파일_" + curUser.getID() +"\\");
				// 해당 디렉토리가 없는 경우 디렉토리를 생성합니다.
				if (!dir.exists()) {
					dir.mkdir();
				}
				
				// 'dir' 디렉토리에 'filename' 이라는 파일 객체를 생성한다.
				File file = new File(dir, filename);
				if(file.exists()) { // 이미 존재하면 filename에 re_를 추가합니다
					file = new File(dir, "re_" + filename);
				}
				else {	// 파일이 존재하지 않으면 파일을 생성
					if(!file.createNewFile()) {	// 파일 생성 에러
						return ;
					}
				}
				
				// 파일 생성하기위한 FileOutputStream, BufferedOutputStream
				// 버퍼에 쓴 내용을 파일에 쓴다
				FileOutputStream fout = new FileOutputStream(file);
				BufferedOutputStream bout = new BufferedOutputStream(fout, fileLength);
				bout.write(content, 0, fileLength);
				bout.flush();
				bfile = false;
				
				
			}
				
				StringTokenizer st = new StringTokenizer(recvData, "|");
				String info = st.nextToken();	// info = 메시지 용도
				String data = st.nextToken();	// data = 메시지 내용 시작
			
				
				// 유저의 정보를 받아올 때 (유저가 로그인한 후 정보를 받아올 때 + 친구 정보 보기 선택할 때)
				// id - 이름 - 생년월일 - 취미 - 성별
				if(info.equals("UserInfo")) {
					// data = id
					String name = st.nextToken();
					
					// curUser의 ID가 -1이다 == 로그인된 유저가 없다
					// UserInfo가 로그인된 유저 정보를 받아오기 위한 것이다.
					if(curUser.getID() == "-1") {
						curUser.setID(data);
						curUser.setname(name);
						
						/*
						// db연동 안되서 테스트용으로 친구 추가함
						if(curUser.getID().equals("jye110"))
							this.requestAddFriend("eunji13");
						else if(curUser.getID().equals("eunji13"))
							this.requestAddFriend("jye110");
						// db연동 안되서 테스트용으로 친구 추가함
						*/
						
						loginD.dispose();
						
						// 로그인 되었으므로 main창 띄움
						mainD = new mainDisplay(this);
						mainD.setVisible(true);
					}
					else {
						User newUser = new User();
						newUser.setID(data);
						newUser.setname(name);
						
						infoD = new userInfoDisplay(this, newUser);
						infoD.setVisible(true);
					}
				}
				
				else if(info.equals("LoginError")) {
					loginD.setVisible(true);
					JOptionPane.showMessageDialog(loginD, "잘못 입력하셨습니다!");
				}
				
				// 사용자 검색 후 검색된 정보 받아올 때
				// 검색된 사용자의 수 - 검색된 ID - 검색된 ID - .....
				else if(info.equals("SearchUser")) {
					int num = Integer.valueOf(data);
					Vector<String> list = new Vector<String> ();
					for(int i = 0; i<num; i++) {
						String id = st.nextToken();
						list.add(id);
					}
					// mainD의 검색된 사용자 리스트 ui 업데이트
					mainD.setSearchList(list);
				}

				// 로그인 직후에 "친구 목록 - 블락 목록" 을 받아온다
				// 로그인 후 친구 목록을 받아올 때
				// 친구 목록의 수 - 친구 ID - 친구 ID - ....
				else if(info.equals("FriendList")) {
					int num = Integer.valueOf(data);
					for(int i = 0; i<num; i++) {
						String ID = st.nextToken();
						curUser.addFriend(ID);
					}
				}
				// 로그인 후 친구 목록 받고 블락 목록을 받아올 때
				// 블락 목록의 수 - 블락 ID - 블락 ID - ....
				
				
				else if(info.equals("Paper")) {
					String id = st.nextToken();
					String msg = st.nextToken();
					
					ViewPaper vp = new ViewPaper(id, msg);
				}
				
				// 나에게로 채팅방에서 전송된 메시지가 도착했을 때
				// 메시지 전송한 사용자의 ID - 채팅방 번호 - 메시지
				else if(info.equals("SendMessage")) {
					int nChatroom = Integer.valueOf(data);
					String ID = st.nextToken();
					String message = st.nextToken();
					
					Chatroom room = curUser.getChatroom(nChatroom); // 사용자의 채팅방목록에서 id가 일치하는 채팅방을 받아옴
					ChatroomDisplay chat;	// 채팅방을 이미 띄웠던 경험이 있는 것이므로 저장된 채팅방 목록에서 일치하는 것을 찾는다.
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChatroom) {	// 일치하는 방을 찾은경우
							chat = chatD.get(i);
							chat.inMessage(ID, message);	// 메시지 전송되었다는 것 출력
						}
					}
				}
				
				// 나에게로 채팅방을 개설한다는 메시지가 왔을 때
				// 채팅방번호 - 본인아이디 - 상대방아이디
				else if(info.equals("MakeChat")) {
					// 현재 사용자에게 새로운 채팅방 개설하여 참여자 업데이트하여 저장
					Chatroom newChat = new Chatroom();
					newChat.setChatroomID(Integer.valueOf(data));
					String user = st.nextToken();
					newChat.addUser(curUser.getID());
					user = st.nextToken();
					newChat.addUser(user);
					curUser.addChatroom(newChat);
					
					// 채팅방 display 갱신
					ChatroomDisplay chat = new ChatroomDisplay(this, newChat);
					chat.setVisible(true);
					
					chatD.add(chat);	// 채팅방 display에 추가
					mainD.setChatroomList();	// 메인 display에 채팅방 목록 업데이트
				}
				
				//SendFile - 보낸아이디 - 방 번호
				else if(info.equals("SendFile")) {
					bfile = true;
					int nChatroom = Integer.valueOf(st.nextToken());
					
					Chatroom room = curUser.getChatroom(nChatroom); // 사용자의 채팅방목록에서 id가 일치하는 채팅방을 받아옴
					ChatroomDisplay chat;	// 채팅방을 이미 띄웠던 경험이 있는 것이므로 저장된 채팅방 목록에서 일치하는 것을 찾는다.
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChatroom) {	// 일치하는 방을 찾은경우
							chat = chatD.get(i);
							chat.inMessage(data, "SendFile");	// 메시지 전송되었다는 것 출력
						}
					}
				}
				
				// 다른 사용자로부터 파일 전송할거라는 요청이 왔을 때
				// 파일 전송을 수락할지 말지를 결정함
				// 파일을 전송할 ID
			/*	else if(info.equals("SendFile")) {
					// 파일전송을 수락할 것인지 선택하는 창 출력
					int result = JOptionPane.showConfirmDialog(null, data + " 님이 파일을 전송합니다. 수락하시겠습니까?", 
							"파일 전송", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					
					// 확인버튼 == 전송받는다
					if(result==0) {
						requestAcceptFile(data);
						new ReceiveFile();	// 파일 전송받을 준비 시작
					}
					// 취소버튼 == 전송받지 않는다
					else {
					}
				}*/
				
				// 다른 사용자들에게 파일 전송한다는 메시지를 보내고 난 뒤에
				// 다른 사용자들로부터 파일전송을 수락한다는 메시지가 왔을 때
				// ip주소
				/*
				else if(info.equals("AcceptFile")) {
					String addr = data;
					new SendFile(addr);	// 파일 전송 시작
				}*/
				
				
				// 채팅방에 새로운 참여자를 추가한다는 메시지를 받았을 때
				// 채팅방번호 - 새로운참여자 ID
				else if(info.equals("JoinNewMember")) {
					int nChat = Integer.valueOf(data);
					String id = st.nextToken();
					
					// 채팅방 번호로 채팅방을 받아와서 참여자 추가
					Chatroom tmp = curUser.getChatroom(nChat);
					tmp.addUser(id);
					
					mainD.setChatroomList();
					
					// 채팅방 display 목록에서 일치하는 채팅방 받아와서
					// 멤버List 업데이트 하고 창 다시 출력
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChat) {
							chatD.get(i).setMemberList();
							chatD.get(i).setVisible(true);
						}
					}
				}
				else if(info.equals("OutChatroom")) {
					int nChat = Integer.valueOf(data);
					String id = st.nextToken();

					curUser.getChatroom(nChat).deleteUser(id);
					

					mainD.setChatroomList();
					
					for(int i=0; i<chatD.size(); i++) {
						if(chatD.get(i).getChatroomID() == nChat) {
							chatD.get(i).setMemberList();
						}
					}
				}
		
			} // while문 끝
		} catch (IOException e) {
			System.out.println(e);
		} // try - catch 끝
	} // run 함수 끝
	
	// 서버에게로 메시지 전송
	// parameter : 전송할 메시지
	public void sendMessage(String Message) {
		System.out.println(Message);	// 테스트 용으로 전송할 메시지 출력
		try {
			dout.writeUTF(Message);
			dout.flush();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/////////////////////////////////////////////
	//// request~ 는 서버에게로 메시지 전송하는 함수이다 ////
	/////////////////////////////////////////////
	
	/*
	public void requestSendFile(int nChatroom, String ID) {
		sendMessage("SendFile|"+nChatroom+"|"+ID);
	}
	*/
	
	// 채팅방 나간다는 메시지 전송
	// OutChatroom - 나의 아이디 - 채팅방 아이디
	public void requestOutChatroom(int chatroomID) {
		buff.setLength(0);
		buff.append("OutChatroom");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(chatroomID);
		sendMessage(buff.toString());
		
		// 현재 사용자에서 chatroom 지우고
		// 채팅방 display에서 목록 지우기
		curUser.deleteChatroom(chatroomID);
		for(int i=0; i<chatD.size(); i++) {
			if(chatD.get(i).getChatroomID()==chatroomID) {
				chatD.remove(i);
			}
		}
		mainD.setChatroomList();
	}
	
	
	// 사용자 초대한다는 메시지 전송
	// JoinUser - 채팅방 아이디 - 추가하는 사용자 아이디
	public void requestJoinUser(String ID, int chatroomID) {
		buff.setLength(0);
		buff.append("JoinUser");
		buff.append("|");
		buff.append(chatroomID);
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());

		// 현재 사용자에서 채팅방 받아와서 사용자 추가
		// curUser.getChatroom(chatroomID).addUser(ID);
	}
	
	// 로그인한다는 메시지 전송
	// Login - 아이디 - 비밀번호
	public void requestLogin(String ID, String password) {
		curUser = new User();
		buff.setLength(0);
		buff.append("Login");
		buff.append("|");
		buff.append(ID);
		buff.append("|");
		buff.append(password);
		sendMessage(buff.toString());
	}
	
	// 회원가입 메시지 전송
	// Register - 아이디 - 비밀번호 - 이름 - 생일 - 취미 - 성별
	public void requestRegister(String ID, String password, String name) {
		buff.setLength(0);
		buff.append("Register");
		buff.append("|");
		buff.append(ID);
		buff.append("|");
		buff.append(password);
		buff.append("|");
		buff.append(name);
		sendMessage(buff.toString());
	
		// 로그인 창 띄움
		loginD.setVisible(true);
	}
	
	// 파일 전송 요청
	// SendFile - 내아이디 - 채팅방 아이디
	public void requestSendFile(int nChatroom) {
		buff.setLength(0);
		buff.append("SendFile");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(nChatroom);
		sendMessage(buff.toString());
	}
	
	// CheckAnswer - 방번호 - 내아이디 - 정답
	public void requestCheckAnswer(int roomNum, String answer) {
		buff.setLength(0);
		buff.append("CheckAnswer");
		buff.append("|");
		buff.append(roomNum);
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(answer);
		sendMessage(buff.toString());
	}
	
	// 파일 전송 받는다고 메시지 전송
	// AcceptFile - 내아이디 - 전송받을 사용자 ID - 내 아이피주소
	public void requestAcceptFile(String ID) {
		try {
			buff.setLength(0);
			buff.append("AcceptFile");
			buff.append("|");
			buff.append(ID);
			buff.append("|");
			buff.append(curUser.getID());
			buff.append("|");
			
			// 내 아이피 주소를 받아옴
			StringTokenizer addr = new StringTokenizer(InetAddress.getLocalHost().toString(), "/");
			String hostname = "";
			String hostaddr = "";
			hostname = addr.nextToken();
			try {
				hostaddr = addr.nextToken();
			} catch(NoSuchElementException e) {
				hostaddr = hostname;
			}
			buff.append(hostaddr);
			sendMessage(buff.toString());
		} catch (Exception e)
		{ System.out.println(e); }
	}
	
	// 친구 추가한다는 메시지 전송
	// AddFriend - 내아이디 - 친구추가할 아이디
	public void requestAddFriend(String ID) {
		curUser.addFriend(ID);
		buff.setLength(0);
		buff.append("AddFriend");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// 친구 삭제한다는 메시지 전송
	// DeleteFriend - 내아이디 - 친구삭제할 아이디
	public void requestDeleteFriend(String ID) {
		curUser.deleteFriend(ID);
		buff.setLength(0);
		buff.append("DeleteFriend");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// 채팅방 만든다는 메시지 전송
	// MakeChat - 내아이디 - 채팅할 사용자 아이디
	public void requestMakeChatroom(String ID) {
		buff.setLength(0);
		buff.append("MakeChat");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// 채팅방에서 메시지 전송한다는 메시지 전송
	// SendMessage - 내아이디 - 채팅방 아이디 - 전송할 메시지
	public void requestSendMessage(int nRoom, String message) {
		buff.setLength(0);
		buff.append("SendMessage");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(nRoom);
		buff.append("|");
		buff.append(message);
		sendMessage(buff.toString());
	}
	
	// 친구 정보보기
	// ShowFriendInfo - 내아이디 - 정보볼 아이디
	public void requestShowFriendInfo(String ID) {
		buff.setLength(0);
		buff.append("ShowFriendInfo");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	// 사용자 검색하기
	// SearchUser - 내아이디 - 검색할 키워드
	public void requestSearchUser(String keyword) {
		buff.setLength(0);
		buff.append("SearchUser");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(keyword);
		sendMessage(buff.toString());
	}
	
	// 사용자 정보보기
	// GetUserInfo - 내아이디 - 정보볼 아이디
	public void requestUserInfo(String ID) {
		buff.setLength(0);
		buff.append("UserInfo");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		sendMessage(buff.toString());
	}
	
	//쪽지 기능
	public void requestSendPaper(String ID, String message) {
		buff.setLength(0);
		buff.append("Paper");
		buff.append("|");
		buff.append(curUser.getID());
		buff.append("|");
		buff.append(ID);
		buff.append("|");
		buff.append(message);
		sendMessage(buff.toString());
	}
	// 채팅방 display에서 일치하는 것 return
	public ChatroomDisplay getChatroomD(int nChatroom) {
		for(int i =0; i<chatD.size(); i++) {
			if (chatD.get(i).getChatroomID() == nChatroom) {
				return chatD.get(i);
			}
		}
		// 검색된 것이 없는 경우 아이디가 -1인 방 리턴
		Chatroom er = new Chatroom();
		er.setChatroomID(-1);
		ChatroomDisplay error = new ChatroomDisplay(this, er);
		return error;
	}
}
