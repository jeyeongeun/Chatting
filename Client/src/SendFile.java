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

// ���� ���� Ŭ����
public class SendFile extends JFrame {
	
	private JTextField filename;	// ���� �̸��� ����� textField
	private JButton btn_search;		// ���� �˻�
	private JButton btn_send;	// ���� ����
	private JButton btn_close;	// �ݱ�
	private JLabel lbl_status;	// ���� (���� ���� ���, ���� ���� �� ��)


	private Socket socket;
	private ClientThread thread;
	
	public SendFile(ClientThread th) {
		// �⺻ ����
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		thread = th;
		socket = th.getSocket();
		getContentPane().setLayout(null);
		
		JLabel lbl = new JLabel("�����̸�");
		lbl.setFont(new Font("����", Font.PLAIN, 13));
		lbl.setBounds(10,10,60,20);
		getContentPane().add(lbl);
		
		// ���õ� ���� �̸�
		filename = new JTextField();
		filename.setEditable(false);
		filename.setFont(new Font("����", Font.PLAIN, 13));
		filename.setBounds(80, 10, 169, 20);
		getContentPane().add(filename);

		SendFile sd = this;

		// ã�� ��ư
		btn_search = new JButton("ã��");
		// ã�� ��ư�� ��������
		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ���� ���̾�α� �����Ѵ� (SendFile UI�� parent�� �ǰ� fileLoad�� ������)
				FileDialog fd = new FileDialog(sd, "", FileDialog.LOAD);
				fd.setVisible(true);
				// filename TextField�� ���õ� ������ ���� ���
				filename.setText(fd.getDirectory() + fd.getFile());
				if(filename.getText().startsWith("null"))
					filename.setText("");
			}
		});
		btn_search.setFont(new Font("����", Font.PLAIN, 13));
		btn_search.setBounds(10,40,70,30);
		getContentPane().add(btn_search);
		
		// �����ϱ� ��ư
		btn_send = new JButton("����");
		// �����ϱ� ��ư�� ��������
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ���� �̸��� �޾ƿͼ�
				String fname = filename.getText();
				if(fname.equals("")) {
					lbl_status.setText("�����̸��� �Է��ϼ���.");
					return ;
				}
				lbl_status.setText("���ϰ˻� ��");
				
				// ���� �̸��� ��ġ�ϴ� ������ ã�´�
				File file = new File(fname);
				if(!file.exists()) {
					lbl_status.setText("�ش� ������ ã�� �� �����ϴ�.");
					return ;
				}
				StringBuffer buff = new StringBuffer();
				int fileLength = (int) file.length();
				
				// ������ ���۹޴� �ʿ� ���� �̸��� ������ ���̸� ������ �غ� �Ѵ�.
				buff.append(file.getName());
				buff.append("|");
				buff.append(fileLength);
				
				lbl_status.setText("���ἳ�� ��");
				
				// ������ ���۹��� �ʰ� ��� �����ϰ� �����Ѵ�
				// ���� ������ ���� ������ �ϰ� ���� ������ �غ��Ѵ�
				try {
					//Socket sock = new Socket("127.0.0.1", 6666);
					
					// ������ �����ϱ� ���� 'data'�� �о�´�
					// �̸� ���� FileInputStream�� ������ �о�� �Ŀ� Buffer�� �о�´�.
					FileInputStream fin = new FileInputStream(file);
					BufferedInputStream bin = new BufferedInputStream(fin, fileLength);
					byte data[] = new byte[fileLength];
					try {
						lbl_status.setText("������ ���� �ε� ��");
						bin.read(data, 0, fileLength);
						bin.close();
					} catch(IOException ee) {
						lbl_status.setText("�����б� ����");
						return ;
					}
					
					// ���۹޴� �ʿ� ���� �̸��� ������ ���̸� �����Ѵ�.
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(buff.toString());
					
					filename.setText("");
					lbl_status.setText("�������� �� (0 Byte)");
					BufferedOutputStream bout = new BufferedOutputStream(out, 2048);
					DataInputStream din = new DataInputStream(socket.getInputStream());


					///////////////////////////////// ���� ���� ����
					int size = 2048;	// �ѹ��� �� ũ��
					int count = fileLength/size;	// ���Ͽ� ����ϴ� Ƚ��
					int rest = fileLength % size;	// ���Ͽ� ���� �������� ���
					int flag = 1;	// 
					
					if(count == 0) flag = 0; //  count�� 0�̸� flag 0 (������ ����)
					
					for(int i = 0; i<=count; i++) {
						// ������ ����
						if(i == count && flag == 0) {
							bout.write(data, 0, rest);	// data�� 0���� rest��ŭ ����
							bout.flush();	// data�� ����� ������ ���鼭 ���Ͽ� ������
							break;
						}
						// ������ ����
						else if(i == count) {
							bout.write(data, i*size, rest);	// data�� i*size���� rest��ŭ ���� 
							bout.flush();	// data�� ����� ������ ���鼭 ���Ͽ� ������
							break;
						}
						// ������ ������ ������
						else {
							bout.write(data, i*size, size);	// data�� i*size���� size��ŭ ����
							bout.flush();	// data�� ����� ������ ���鼭 ���Ͽ� �����Ѵ�.
							lbl_status.setText("�������� �� (" + ((i+1)*size) + "/" + fileLength + "Byte)");
						}
					}
					///////////////////////////////// �������� ��
					
					// ���� ���� ������ ��ſϷ�
			//		bout.close();
			//		din.close();
					
					lbl_status.setText(file.getName() + " ���������� �Ϸ�Ǿ����ϴ�.");
				} catch(IOException e1) {
					System.out.println(e1);
					lbl_status.setText("���ῡ �����߽��ϴ�.");
				}
			}
		});
		
		// ���� ���� ��ư
		btn_send.setFont(new Font("����", Font.PLAIN, 13));
		btn_send.setBounds(92,40,70,30);
		getContentPane().add(btn_send);
		
		// �ݱ� ��ư
		btn_close = new JButton("����");
		// �ݱ� ��ư�� �������� â ����
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btn_close.setFont(new Font("����", Font.PLAIN, 13));
		btn_close.setBounds(179,40,70,30);
		getContentPane().add(btn_close);
		
		lbl_status = new JLabel("�������� ���");
		lbl_status.setFont(new Font("����", Font.PLAIN, 13));
		lbl_status.setBounds(10,80,230,20);
		lbl_status.setBackground(new Color(248, 248, 255));
		lbl_status.setForeground(new Color(128, 0, 0));
		getContentPane().add(lbl_status);
		
		setSize(277,148);
		setVisible(true);
		setResizable(false);
	}
}
