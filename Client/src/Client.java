
// main클래스
// ClientThread를 실행시키는 역할만을 수행한다.
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
