package gameAnalyzer;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author JH
 *
 */
public class Main {
	/**
	 * 
	 * @param args: No arg -> run the simulation. If the first arg is a number,
	 *              download more matches starting with that match number. If there
	 *              is another arg first, find the best parameters.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			Map<Integer, Match> material = new HashMap<>();
			try {
				material = LoaderSaver.readMatchesFromFile();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Could not read matches to feed to simulator.");
				e.printStackTrace();
			}
			Simulator.runForNMonths(material, new Date(115, 6, 0), 1, 4, 0.0f, 0.65f, 0.8f, 0.1f);
		} else {
			try {
				LoaderSaver.updateMatches(Integer.valueOf(args[0]));
				// If the first argument was an integer, we're done.
				return;
			} catch (NumberFormatException | IOException e) {
				System.out.println("Could not update matches.");
				e.printStackTrace();
			}
			// If this is reached, the first argument wasn't an integer. So this is what
			// should be done.
			try {
				Simulator.varyAndCompareWeights();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Could not search best parameter set.");
				e.printStackTrace();
			}
		}
	}

}
