
package gameAnalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * @author JH
 *
 */
public class LoaderSaver {
	private static final String BASE_URL_CSGO_LOUNGE = "http://csgolounge.com/oldmatch?m=";
	private static final String FILE_NAME = "./CSGOLOmatches.x";
	private static final int SAVING_STEP = 50;

	public static HashMap<Integer, Match> readMatchesFromFile() throws IOException, ClassNotFoundException {
		HashMap<Integer, Match> matches;
		FileInputStream fin = new FileInputStream(FILE_NAME);
		ObjectInputStream oin = new ObjectInputStream(fin);
		matches = (HashMap<Integer, Match>) oin.readObject();
		System.out.println("Loaded match file with size: " + matches.size());
		oin.close();
		return matches;

	}

	public static void updateMatches(final int offset) throws IOException {
		int matchId = offset;

		System.out.println("starting from matchId " + matchId);

		HashMap<Integer, Match> matches;
		try {
			matches = readMatchesFromFile();
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
			System.out.println("Couldn't find file to update: " + FILE_NAME);
			return;
		}

		FileOutputStream fout;
		ObjectOutputStream objout;
		fout = new FileOutputStream(FILE_NAME);
		objout = new ObjectOutputStream(fout);

		int consecutiveErrors = 0;

		while (consecutiveErrors < 20) {
			String url = BASE_URL_CSGO_LOUNGE + Integer.toString(matchId);
			if (matches.containsKey(matchId) && matches.get(matchId).isValid()) {
				consecutiveErrors = 0;
			} else {
				Match match = new Match(url);
				if (match.isFilled()) {
					matches.put(match.getMatchNr(), match);
					consecutiveErrors = 0;
					System.out.println("added " + url);
				} else {
					consecutiveErrors++;
				}

			}
			if (matchId % SAVING_STEP == 0) {
				try {
					objout.close();
					fout.close();
					fout = new FileOutputStream(FILE_NAME);
					objout = new ObjectOutputStream(fout);
					objout.writeObject(matches);
				} catch (IOException e) {
					e.printStackTrace();
				}

				System.out.println("autosave; new HashMap size: " + matches.size());
			}
			matchId++;
		}
		objout.close();
		fout.close();
		fout = new FileOutputStream(FILE_NAME);
		objout = new ObjectOutputStream(fout);
		System.out.println("new HashMap size: " + matches.size() + " writing to: " + FILE_NAME + ".");
		objout.writeObject(matches);
		objout.close();
	}
}
