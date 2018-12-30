import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class joinUserDisplay extends JFrame {
	private ClientThread thread;
	private String Selected;
	private JList list ;
	private int id;
	
	//���� �������� ����Ʈ ������ �����Ѵ�.
	//��Ʈ�� Ÿ���� ���� ve�� ���� �ش��ϴ� ������ ģ�� ������ ����Ʈȭ �Ѵ�.
	public void setList() {
		Vector<String> ve = thread.getUser().getFriend(); 
		list.setListData(ve);
	}
	
	//������ �κ�����, ���Ϳ� �����ߴ� ������ ģ�� ���� ����Ʈ�� ����Ѵ�.
	public joinUserDisplay(ClientThread th, int ID) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		thread = th;
		id = ID;
		setResizable(false);
		setSize(254,332);
		
		list = new JList();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				Selected = (String) ((JList) e.getSource()).getSelectedValue();
			}
		});
		list.setBounds(12, 10, 221, 237);
		getContentPane().add(list);
		setList();
		
		JButton btn_Invite = new JButton("\uCD08\uB300\uD558\uAE30");
		btn_Invite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestJoinUser(Selected, id);
				dispose();
			}
		});
		btn_Invite.setFont(new Font("����", Font.PLAIN, 13));
		btn_Invite.setBounds(12, 257, 108, 35);
		getContentPane().add(btn_Invite);
		
		JButton btn_Info = new JButton("\uC815\uBCF4\uBCF4\uAE30");
		btn_Info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestUserInfo(Selected);
			}
		});
		btn_Info.setFont(new Font("����", Font.PLAIN, 13));
		btn_Info.setBounds(125, 257, 108, 35);
		getContentPane().add(btn_Info);
		
	}
}
