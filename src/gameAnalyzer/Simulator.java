package gameAnalyzer;

import java.io.IOException;
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
public final class Simulator {
	private static final float START_MONEY = 1;

	public static void varyAndCompareWeights() throws ClassNotFoundException, IOException {

		HashMap<Integer, Match> material;
		material = LoaderSaver.readMatchesFromFile();

		Simulator simulator = new Simulator();

		Date startDate = new Date(114, 2, 0);
		float minSuccess = 200000000;
		float[] worstParam = new float[3];
		float maxSuccess = 0;
		int counter = 0;
		float[] bestParam = new float[3];
		for (float ageWeight = 0; ageWeight < 1; ageWeight += .1) {
			for (float experienceWeight = 0; experienceWeight < 1; experienceWeight += .1) {
				for (float encounterWeight = 0; encounterWeight < 1; encounterWeight += .1) {
					for (float oddDifferenceBuffer = 0; oddDifferenceBuffer < 20; oddDifferenceBuffer += 2) {
						float tempSuccess = simulator.runForNMonths(material, startDate, 1, 4, ageWeight,
								experienceWeight, encounterWeight, oddDifferenceBuffer);
						counter++;
						System.out.println("counter: " + counter + "      fac1: " + ageWeight + "; fac2: "
								+ experienceWeight + " fac3: " + encounterWeight + " fac4: " + oddDifferenceBuffer);
						if (tempSuccess < minSuccess) {
							minSuccess = tempSuccess;
							worstParam = new float[] { ageWeight, experienceWeight, encounterWeight };
							System.out.println("new worst Parameters: ");
							for (float f : worstParam) {
								System.out.println(f);
								System.out.println(" ->result: " + minSuccess);
							}
						}
						if (tempSuccess > maxSuccess) {
							// new best weight parameters!
							maxSuccess = tempSuccess;
							bestParam = new float[] { ageWeight, experienceWeight, encounterWeight };
							for (float f : bestParam) {
								System.out.println(f);
							}
							System.out.println(" ->result: " + maxSuccess);
						}
					}
				}
			}
		}
		System.out.println("worst Parameters: ");
		for (float f : worstParam) {
			System.out.println(f);
		}
		System.out.println(" ->result: " + minSuccess);
		System.out.println("best Parameters: ");
		for (float f : bestParam) {
			System.out.println(f);
		}
		System.out.println(" ->result: " + maxSuccess);
	}

	public static float runForNMonths(Map<Integer, Match> matches, Date startDate, int months, int timespanForCalc,
			float ageWeight, float experienceWeight, float encounterWeight, float oddDifferenceBuffer) {
		float money = START_MONEY;

		// starting Simulator for the month after startDate
		int firstMatchIndex = findMatchByDate(startDate, matches);
		// found first Match Id
		long aMonth = 26000000;
		aMonth *= 100;
		long duration = aMonth * months;
		long timesp = aMonth * timespanForCalc;

		for (int i = firstMatchIndex; matches.containsKey(i)
				&& matches.get(i).getMatchDate().getTime() < startDate.getTime() + duration; i++) {
			if (matches.containsKey(i)) {
				Match tempMatch = matches.get(i);
				int tempOdds = OddCalculator.odds2(tempMatch.getName1(), tempMatch.getName2(),
						new Date(tempMatch.getMatchDate().getTime() - timesp), tempMatch.getMatchDate(), matches,
						ageWeight, experienceWeight, encounterWeight);
				if (tempOdds > tempMatch.getOdds() + oddDifferenceBuffer) {
					// bet on team1
					money = money * 9 / 10 + betOnMatch(matches.get(i), tempMatch.getName1(), money * 1 / 10);
				} else if (tempOdds < tempMatch.getOdds() - oddDifferenceBuffer && tempOdds > 0) {
					// bet on team2
					money = money * 9 / 10 + betOnMatch(matches.get(i), tempMatch.getName2(), money * 1 / 10);
				}
			}
		}

		System.out.println("ending simulator for " + months + " months after " + startDate.toString() + " with " + money
				+ " money after starting with " + START_MONEY + ".");
		return money;
	}

	public static float betOnMatch(Match match, String name, float amount) {
		float result = amount;
		if (!match.isValid()) {

		} else {

			if (match.getWinnerName().equals(name)) {
				if (match.getName2().equals(name)) {
					result += amount * match.getOdds() / (100 - match.getOdds());

				} else {
					result += amount * (100 - match.getOdds()) / match.getOdds();
				}

			} else {

				result = 0;
			}
		}
		return result;
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
		Date lowerDate = matches.get(lowerId).getMatchDate();
		Date higherDate = matches.get(higherId).getMatchDate();
		Date middleDate;
		while (higherId - lowerId > 3) {
			// //System.out.println("**");
			lowerDate = matches.get(lowerId).getMatchDate();
			higherDate = matches.get(higherId).getMatchDate();
			middleId = (lowerId + higherId) / 2;
			while (!matches.keySet().contains(middleId)) {
				middleId--;
				// //System.out.println("*");
			}
			middleDate = matches.get(middleId).getMatchDate();
			if (middleDate.before(date)) {
				lowerId = middleId;
			} else {
				higherId = middleId;
			}
			// //System.out.println("***");
		}
		return lowerId;
	}
}
