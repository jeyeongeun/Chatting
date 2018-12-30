import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

// 파일 전송 클래스
public class SendFile extends JFrame {
	
	private JTextField filename;	// 파일 이름을 출력할 textField
	private JButton btn_search;		// 파일 검색
	private JButton btn_send;	// 파일 전송
	private JButton btn_close;	// 닫기
	private JLabel lbl_status;	// 상태 (파일 전송 대기, 파일 전송 중 등)


	private Socket socket;
	private ClientThread thread;
	
	public SendFile(ClientThread th) {
		// 기본 설정
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		thread = th;
		socket = th.getSocket();
		getContentPane().setLayout(null);
		
		JLabel lbl = new JLabel("파일이름");
		lbl.setFont(new Font("돋움", Font.PLAIN, 13));
		lbl.setBounds(10,10,60,20);
		getContentPane().add(lbl);
		
		// 선택된 파일 이름
		filename = new JTextField();
		filename.setEditable(false);
		filename.setFont(new Font("돋움", Font.PLAIN, 13));
		filename.setBounds(80, 10, 169, 20);
		getContentPane().add(filename);

		SendFile sd = this;

		// 찾기 버튼
		btn_search = new JButton("찾기");
		// 찾기 버튼이 눌려지면
		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 파일 다이얼로그 생성한다 (SendFile UI가 parent가 되고 fileLoad가 목적임)
				FileDialog fd = new FileDialog(sd, "", FileDialog.LOAD);
				fd.setVisible(true);
				// filename TextField에 선택된 파일의 정보 출력
				filename.setText(fd.getDirectory() + fd.getFile());
				if(filename.getText().startsWith("null"))
					filename.setText("");
			}
		});
		btn_search.setFont(new Font("돋움", Font.PLAIN, 13));
		btn_search.setBounds(10,40,70,30);
		getContentPane().add(btn_search);
		
		// 전송하기 버튼
		btn_send = new JButton("전송");
		// 전송하기 버튼이 눌려지면
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 파일 이름을 받아와서
				String fname = filename.getText();
				if(fname.equals("")) {
					lbl_status.setText("파일이름을 입력하세요.");
					return ;
				}
				lbl_status.setText("파일검색 중");
				
				// 파일 이름에 일치하는 파일을 찾는다
				File file = new File(fname);
				if(!file.exists()) {
					lbl_status.setText("해당 파일을 찾을 수 없습니다.");
					return ;
				}
				StringBuffer buff = new StringBuffer();
				int fileLength = (int) file.length();
				
				// 파일을 전송받는 쪽에 파일 이름과 파일의 길이를 전송할 준비를 한다.
				buff.append(file.getName());
				buff.append("|");
				buff.append(fileLength);
				
				lbl_status.setText("연결설정 중");
				
				// 파일을 전송받을 쪽과 통신 연결하고 전송한다
				// 파일 전송을 위한 연결을 하고 파일 전송을 준비한다
				try {
					//Socket sock = new Socket("127.0.0.1", 6666);
					
					// 파일을 전송하기 위해 'data'에 읽어온다
					// 이를 위해 FileInputStream에 파일을 읽어온 후에 Buffer에 읽어온다.
					FileInputStream fin = new FileInputStream(file);
					BufferedInputStream bin = new BufferedInputStream(fin, fileLength);
					byte data[] = new byte[fileLength];
					try {
						lbl_status.setText("전송할 파일 로드 중");
						bin.read(data, 0, fileLength);
						bin.close();
					} catch(IOException ee) {
						lbl_status.setText("파일읽기 오류");
						return ;
					}
					
					// 전송받는 쪽에 파일 이름과 파일의 길이를 전송한다.
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(buff.toString());
					
					filename.setText("");
					lbl_status.setText("파일전송 중 (0 Byte)");
					BufferedOutputStream bout = new BufferedOutputStream(out, 2048);
					DataInputStream din = new DataInputStream(socket.getInputStream());


					///////////////////////////////// 파일 전송 시작
					int size = 2048;	// 한번에 쓸 크기
					int count = fileLength/size;	// 파일에 써야하는 횟수
					int rest = fileLength % size;	// 파일에 쓰고 나머지의 경우
					int flag = 1;	// 
					
					if(count == 0) flag = 0; //  count가 0이면 flag 0 (마지막 전송)
					
					for(int i = 0; i<=count; i++) {
						// 마지막 전송
						if(i == count && flag == 0) {
							bout.write(data, 0, rest);	// data에 0부터 rest만큼 쓴다
							bout.flush();	// data에 저장된 내용을 비우면서 파일에 전송함
							break;
						}
						// 마지막 전송
						else if(i == count) {
							bout.write(data, i*size, rest);	// data에 i*size부터 rest만큼 쓴다 
							bout.flush();	// data에 저장된 내용을 비우면서 파일에 전송함
							break;
						}
						// 전송할 내용이 남았음
						else {
							bout.write(data, i*size, size);	// data에 i*size부터 size만큼 쓴다
							bout.flush();	// data에 저장된 내용을 비우면서 파일에 전소한다.
							lbl_status.setText("파일전송 중 (" + ((i+1)*size) + "/" + fileLength + "Byte)");
						}
					}
					///////////////////////////////// 파일전송 끝
					
					// 파일 전송 끝나고 통신완료
			//		bout.close();
			//		din.close();
					
					lbl_status.setText(file.getName() + " 파일전송이 완료되었습니다.");
				} catch(IOException e1) {
					System.out.println(e1);
					lbl_status.setText("연결에 실패했습니다.");
				}
			}
		});
		
		// 파일 전송 버튼
		btn_send.setFont(new Font("돋움", Font.PLAIN, 13));
		btn_send.setBounds(92,40,70,30);
		getContentPane().add(btn_send);
		
		// 닫기 버튼
		btn_close = new JButton("종료");
		// 닫기 버튼이 눌려지면 창 종료
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btn_close.setFont(new Font("돋움", Font.PLAIN, 13));
		btn_close.setBounds(179,40,70,30);
		getContentPane().add(btn_close);
		
		lbl_status = new JLabel("파일전송 대기");
		lbl_status.setFont(new Font("돋움", Font.PLAIN, 13));
		lbl_status.setBounds(10,80,230,20);
		lbl_status.setBackground(new Color(248, 248, 255));
		lbl_status.setForeground(new Color(128, 0, 0));
		getContentPane().add(lbl_status);
		
		setSize(277,148);
		setVisible(true);
		setResizable(false);
	}
}
