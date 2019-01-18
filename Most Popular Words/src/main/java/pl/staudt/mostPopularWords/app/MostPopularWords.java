package pl.staudt.mostPopularWords.app;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MostPopularWords {

    public static void main(String[] args) {
        Map<String,Integer> titlesMap = new HashMap<String,Integer>();
        String splittedTitles[] = StringUtils.split(titlesToString(titlesDownload()),", .\n;\":!?-„”–[]'()");

        int temp;
        for (int i = 0; i < splittedTitles.length; i++) {
            if (titlesMap.containsKey(splittedTitles[i])) {
                temp = titlesMap.get(splittedTitles[i]);
                temp++;
                titlesMap.replace(splittedTitles[i], temp);
            } else if (checkExcluded(splittedTitles[i])) {
                titlesMap.put(splittedTitles[i], 1);
            }
        }

        Iterator iterator = titlesMap.entrySet().iterator();
        ArrayList<String> titlesRank = new ArrayList<>();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue().hashCode() < 10) {
                titlesRank.add(" " + entry.getValue() + " " + entry.getKey());
            } else {
                titlesRank.add(entry.getValue() + " " + (String) entry.getKey());
            }
            Collections.sort(titlesRank, Collections.reverseOrder());
        }
        List<String>topTen = titlesRank.subList(0,10);
        rankToFile(topTen);
    }

    static ArrayList titlesDownload() {
        String[] websites = {"http://www.onet.pl", "http://www.gazeta.pl", "http://www.rp.pl", "http://www.interia.pl", "http://www.tvn24.pl"};
        Path path = Paths.get("target/classes/pl/staudt/mostPopularWords/text/popular_words.txt");
        Connection connect;
        ArrayList<String> outList = new ArrayList<>();
        try {
            for (int i = 0; i < websites.length; i++) {
                connect = Jsoup.connect(websites[i]);
                Document document = connect.get();
                Elements links = document.select("span.title");
                for (Element elem : links) {
                    outList.add(elem.text().toLowerCase());
                }
            }
            Files.write(path,outList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outList;
    }

    static String titlesToString(ArrayList arrayList) {
        return arrayList.toString().substring(1,arrayList.toString().length());
    }

    static void rankToFile(List<String> list) {
        Path path = Paths.get("target/classes/pl/staudt/mostPopularWords/text/most_popular_words.txt");
        try {
            Files.write(path, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean checkExcluded(String str) {
        Path path = Paths.get("target/classes/pl/staudt/mostPopularWords/text/wykluczenia.txt");
        String excluded = "";
        try (Scanner sc = new Scanner(path)){
            while (sc.hasNextLine()) {
                if (excluded.equals(str)) {
                    return false;
                } else {
                    excluded = sc.nextLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}