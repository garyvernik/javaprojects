package paypaltest;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * IO Utils class
 */
public final class IOUtils {
	
	private static Console console = System.console();

	
	private IOUtils() {
	}
	
	public static void printf(String formatString, Object... args) {
		
		try {
			if (console != null) {
				console.printf(formatString, args);
			} else {
				System.out.print(String.format(formatString, args));
			}
		} catch (IllegalFormatException e) {
			System.err.print("Error printing...\n");
		}
	}
	
	public static void print(String s) {
		printf("%s", s);
	}
	
	public static String readLine(final String userPrompt) throws IOException {
		String line = "";
		
		if (console != null) {
			line = console.readLine(userPrompt);
		} else {
			// print("console is null\n");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

			print(userPrompt);
			line = bufferedReader.readLine();
		}
		// GV: this will not trim the returned result
		//line.trim();
		return line == null ? "" : line.trim();
	}

	public static void printSchedule(List<AmortizationScheduleItem> items) {
		String formatString = "%1$-20s%2$-20s%3$-20s%4$-20s%5$-30s%6$-30s\n";
		printf(formatString,
			"PaymentNumber", "PaymentAmount", "PaymentInterest",
			"CurrentBalance", "TotalPayments", "TotalInterestPaid");
		
		// output is in dollars
		formatString = "%1$-20d%2$-20.2f%3$-20.2f%4$-20.2f%5$-30.2f%6$-30.2f\n";
		for (AmortizationScheduleItem item : items) {
			printf(formatString, item.getPaymentNumber(), 
					item.getPaymentAmount(), 
					item.getPaymentInterest(),
					item.getCurrentBalance(),
					item.getTotalPayments(),
					item.getTotalInterestPaid());
		}
	}
}
