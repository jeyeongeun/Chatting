
// mainŬ����
// ClientThread�� �����Ű�� ���Ҹ��� �����Ѵ�.
public class Client {
	public static void main(String[] args) {
		try {
			ClientThread thread = new ClientThread();
			thread.start();
			//thread.run();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
