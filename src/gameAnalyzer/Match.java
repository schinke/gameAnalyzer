package gameAnalyzer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/**
 * @author JH
 *
 */
/**
 * @author JH
 *
 */
public class Match implements Serializable {

	private static final long serialVersionUID = 388945530535504723L;

	private static boolean containsMatchfromDocument(Document doc) {

		String[] titleWords = doc.title().split("\\s");
		if (titleWords[titleWords.length - 2].equals("Match")) {
			return true;
		} else {
			System.out.println("Error, 2nd last title word was: " + titleWords[titleWords.length - 2] + " in title "
					+ doc.title());
			return false;
		}

	}
	boolean filled;
	private boolean hasWinner;
	private Date matchDate;

	private int matchNr;

	private String name1;
	private String name2;
	// the odds for team1
	private int odds;
	private String url;

	private String winnerName;

	public Match(String url) {
		try {
			Document doc = Jsoup.connect(url)
					.userAgent(
							"gameAnalyzer")
					.get();
			hasWinner = false;
			if (containsMatchfromDocument(doc)) {
				setUrl(url);
				setName1fromDocument(doc);
				setName2fromDocument(doc);
				setMatchDateFromDocument(doc);
				setMatchNrFromDocument(doc);
				setOddsFromDocument(doc);
				filled = true;
			} else {
				filled = false;
			}

		} catch (IOException e) {
			filled = false;
			e.printStackTrace();
		}

	}

	public Date getMatchDate() {
		if (matchDate != null) {
			return matchDate;
		} else {
			return new Date(0);
		}
	}

	public int getMatchNr() {
		return this.matchNr;
	}

	public String getName1() {
		return name1;
	}

	public String getName2() {
		return name2;
	}

	public int getOdds() {
		return odds;
	}

	public String getUrl() {
		return url;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public boolean isFilled() {
		return filled;
	}

	public boolean isValid() {
		return (matchDate != null && hasWinner && !url.isEmpty() && isFilled());
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public void setMatchDateFromDocument(Document doc) {
		Date result = new Date();

		String rawDate;
		ArrayList<Element> el = doc.getElementsByClass("half").select("div[title]");
		if (el.size() == 1) {
			rawDate = el.get(0).attr("title");
			String[] rawDateElements = rawDate.split("\\s");
			int day = Integer.parseInt(rawDateElements[1].substring(0, rawDateElements[1].length() - 2));

			int month = 0;
			if (rawDateElements[2].startsWith("Jan")) {
				month = 0;
			} else if (rawDateElements[2].startsWith("Feb")) {
				month = 1;
			} else if (rawDateElements[2].startsWith("Mar")) {
				month = 2;
			} else if (rawDateElements[2].startsWith("Apr")) {
				month = 3;
			} else if (rawDateElements[2].startsWith("May")) {
				month = 4;
			} else if (rawDateElements[2].startsWith("Jun")) {
				month = 5;
			} else if (rawDateElements[2].startsWith("Jul")) {
				month = 6;
			} else if (rawDateElements[2].startsWith("Aug")) {
				month = 7;
			} else if (rawDateElements[2].startsWith("Sep")) {
				month = 8;
			} else if (rawDateElements[2].startsWith("Oct")) {
				month = 9;
			} else if (rawDateElements[2].startsWith("Nov")) {
				month = 10;
			} else if (rawDateElements[2].startsWith("Dec")) {
				month = 11;
			}

			int year = Integer.parseInt(rawDateElements[3]) - 1900;
			result = new Date(year, month, day);
			setMatchDate(result);
		}

	}

	public void setMatchNr(int matchNr) {
		this.matchNr = matchNr;
	}

	public void setMatchNrFromDocument(Document doc) {
		String[] docTitle = doc.title().split("\\s");
		this.setMatchNr(Integer.parseInt(docTitle[docTitle.length - 1]));
	}

	protected void setName1(String name1) {
		this.name1 = name1;
	}

	protected void setName1fromDocument(Document doc) {

		String tag1 = doc.select("a[onclick=selectTeam($(this), 'a')]").text().toString();
		if (tag1.contains("(win)")) {
			hasWinner = true;
			String[] result = tag1.split("\\s");
			this.name1 = "";
			for (int x = 0; x < result.length && !result[x].endsWith("(win)"); x++) {
				this.name1 = this.name1.concat(result[x]);
			}
			setWinnerName(this.name1);

		} else {
			String[] result = tag1.split("\\s");
			this.name1 = "";
			for (int x = 0; x < result.length && !result[x].endsWith("%"); x++) {
				this.name1 = this.name1.concat(result[x]);
			}
		}

	}

	protected void setName2(String name2) {
		this.name2 = name2;
	}

	protected void setName2fromDocument(Document doc) {

		String tag1 = doc.select("a[onclick=selectTeam($(this), 'b')]").text().toString();
		if (tag1.contains("(win)")) {
			hasWinner = true;
			String[] result = tag1.split("\\s");
			this.name2 = "";
			for (int x = 0; x < result.length && !result[x].endsWith("(win)"); x++) {
				this.name2 = this.name2.concat(result[x]);
			}
			setWinnerName(this.name2);

		} else {
			String[] result = tag1.split("\\s");
			this.name2 = "";
			for (int x = 0; x < result.length && !result[x].endsWith("%"); x++) {
				this.name2 = this.name2.concat(result[x]);
			}
		}

	}

	public void setOdds(int odds) {
		this.odds = odds;
	}

	public void setOddsFromDocument(Document doc) {

		String tag1 = doc.select("a[onclick=selectTeam($(this), 'a')]").select("i").text();
		int result;
		if (tag1 != null && tag1.length() > 0) {
			result = Integer.parseInt(tag1.substring(0, tag1.length() - 1));
		} else {
			result = 50;
		}

		setOdds(result);

	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}

	@Override
	public String toString() {
		String result = name1 + " vs " + name2 + "; MatchNr: " + Integer.toString(matchNr) + "; Winner: "
				+ this.getWinnerName() + "; Date:" + getMatchDate().toString();
		return result;
	}
}
