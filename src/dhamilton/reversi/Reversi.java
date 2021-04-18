package dhamilton.reversi;

public class Reversi {
	public static void main(String[] args) {
		try {
			new GameWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Something went wrong, application cannot be started");
			e.printStackTrace();
		}
	}
}
