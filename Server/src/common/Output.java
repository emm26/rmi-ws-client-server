
package common;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Class used to format output messages.
 */

public class Output {

	private static final String RED = "\u001B[31m";
	private static final String GREEN = "\u001B[32m";
	private static final String YELLOW = "\u001B[33m";
	private static final String BLUE = "\u001B[34m";

	public static void printError(String toPrint){
		System.err.println(RED + getCurrentTime() + " - ERROR: " + toPrint + RED);
	}

	public static void printWarning(String toPrint){
		System.out.println(YELLOW + getCurrentTime() + " - WARNING: " + toPrint + YELLOW);
	}

	public static void printInfo(String toPrint){
		System.out.println(BLUE + getCurrentTime() + " - INFO: " + toPrint + BLUE);
	}

	public static void printSuccess(String toPrint){
		System.out.println(GREEN + getCurrentTime() + " - SUCCESS: " + toPrint + GREEN);
	}

	public static void print(String toPrint){
		System.out.println(getCurrentTime() + ": " + toPrint);
	}

	private static String getCurrentTime(){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
