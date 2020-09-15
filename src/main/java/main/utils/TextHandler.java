package main.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TextHandler {

  public static String getTextWithoutHtml(String text) {
    Document doc = Jsoup.parseBodyFragment(text);
    return doc.text();
  }

}
