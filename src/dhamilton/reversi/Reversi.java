package dhamilton.reversi;
import java.io.IOException;

public class Reversi {
	public static void main(String[] args) {
		try {
			new GameWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong, application cannot be started");
			e.printStackTrace();
		}
	}
}
