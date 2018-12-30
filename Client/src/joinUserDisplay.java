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
	
	//참가 유저들의 리스트 정보를 설정한다.
	//스트링 타입의 벡터 ve를 통해 해당하는 유저의 친구 정보를 리스트화 한다.
	public void setList() {
		Vector<String> ve = thread.getUser().getFriend(); 
		list.setListData(ve);
	}
	
	//생성자 부분으로, 벡터에 저장했던 유저의 친구 정보 리스트를 출력한다.
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
		btn_Invite.setFont(new Font("돋움", Font.PLAIN, 13));
		btn_Invite.setBounds(12, 257, 108, 35);
		getContentPane().add(btn_Invite);
		
		JButton btn_Info = new JButton("\uC815\uBCF4\uBCF4\uAE30");
		btn_Info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.requestUserInfo(Selected);
			}
		});
		btn_Info.setFont(new Font("돋움", Font.PLAIN, 13));
		btn_Info.setBounds(125, 257, 108, 35);
		getContentPane().add(btn_Info);
		
	}
}
