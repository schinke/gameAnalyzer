package gameAnalyzer;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JH
 *
 */
/**
 * @author JH
 *
 */
public final class OddCalculator {
	public static void main(String[] args) {
		HashMap<Integer, Match> matches;
		try {
			matches = LoaderSaver.readMatchesFromFile();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Couldn't load matches from file.");
			return;
		}
		//this specifies the match under test.
		Match subjectMatch = new Match("https://csgolounge.com/oldmatch?m=6999");

//		a month has about 2600000000 milliseconds
		long aMonth = 26000000;
		aMonth *= 100;

		long timesp = aMonth * 4;
		System.out.println(
				odds2(subjectMatch.getName1(), subjectMatch.getName2(), new Date(subjectMatch.getMatchDate().getTime() - timesp),
						new Date(subjectMatch.getMatchDate().getTime() - 114265000), matches, 0.9f, 0.0f, 2f));
	}

	/**
	 * @param name1:            name of team on left side of future match
	 * @param name2:            name of team on right side of future match
	 * @param earliestDate:     earliest date matches taken into account for
	 *                          evaluation
	 * @param latestDate:       latest date matches taken into account for
	 *                          evaluation
	 * @param matches:          the input of evaluated matches
	 * @param ageingWeight:     how much the meaning of a match at the beginning of
	 *                          the evaluated period is
	 * @param experienceWeight: value of a game no matter if won or lost
	 * @param encounterWeight:  how much additional meaning direct encounters have
	 * @return the calculated probability of team 1 to win (in percent)
	 */
	public static int odds2(String name1, String name2, Date earliestDate, Date latestDate, Map<Integer, Match> matches,
			float ageingWeight, float experienceWeight, float encounterWeight) {
		
		float team1Value = 0;
		float team2Value = 0;
		float team1Games = 0;
		float team2Games = 0;
		int directEncounters = 0;
		for (int i = findMatchByDate(earliestDate, matches); i < findMatchByDate(latestDate, matches); i++) {
			if (matches.containsKey(i)) {
				Match tempMatch = matches.get(i);
				
				float matchValue = 1
						- ageingWeight * (float) (latestDate.getTime() - tempMatch.getMatchDate().getTime())
								/ (latestDate.getTime() - earliestDate.getTime());
				if (directEncounter(tempMatch, name1, name2)) {
					matchValue += encounterWeight;
					directEncounters++;
				}
				if (tempMatch.getName1().contains(name1) || tempMatch.getName2().contains(name1)) {
					// team1 played
					team1Games += matchValue;

					if (tempMatch.isValid()) {
						// team1 won
						if (tempMatch.getWinnerName().contains(name1)) {
							team1Value += matchValue;
						}
					}
				}
				if (tempMatch.getName1().contains(name2) || tempMatch.getName2().contains(name2)) {
					// team2 played
					team2Games += matchValue;

					if (tempMatch.isValid()) {
						if (tempMatch.getWinnerName().contains(name2)) {
							// team2 won
							team2Value += matchValue;
						}

					}
				}

			}

		}
		if (team1Games > 0 && team2Games > 0 && directEncounters > 0) {
			// winQuote + gamesPlayed
			System.out.println(
					team1Value + " " + team1Games + " " + team2Value + " " + team2Games + " " + directEncounters);
			int val1 = (int) (team1Value * 100 / team1Games + team1Games * (experienceWeight));
			int val2 = (int) (team2Value * 100 / team2Games + team2Games * (experienceWeight));
			return (int) (val1 * 100 / Math.max(val2 + val1, 1));
		} else {
			System.out.println(
					team1Value + " " + team1Games + " " + team2Value + " " + team2Games + " " + directEncounters);
			return -1;
		}

	}

	private static boolean directEncounter(Match match, String name1, String name2) {
		return ((match.getName1().contains(name1) && match.getName2().contains(name2))
				|| (match.getName1().contains(name2) && match.getName2().contains(name1)));
	}

	private static int findMatchByDate(Date date, Map<Integer, Match> matches) {

		int lastId = 0;
		int firstId = matches.size();

		// find highest and lowest matchIds
		for (Integer id : matches.keySet()) {
			if (id > lastId) {
				lastId = id;
			}
			if (id < firstId) {
				firstId = id;
			}

		}
		int lowerId = firstId;
		int higherId = lastId;
		int middleId;
		Date middleDate;
		while (higherId - lowerId > 3) {
			middleId = (lowerId + higherId) / 2;
			while (!matches.keySet().contains(middleId)) {
				middleId--;
			}
			middleDate = matches.get(middleId).getMatchDate();
			if (middleDate.before(date)) {
				lowerId = middleId;
			} else {
				higherId = middleId;
			}
		}
		return lowerId;
	}
}
