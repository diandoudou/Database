import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the path of database file：");
		String dbPath = scanner.nextLine();
		DbUser myDbUser = null;
		try {
			myDbUser = new DbUser(dbPath);
			DbUser.retrieveMeta();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		} finally {
			if (myDbUser != null) {
				myDbUser.close();
			}
		}
		scanner.close();
	}
}
