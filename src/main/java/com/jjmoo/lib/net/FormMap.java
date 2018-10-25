package com.jjmoo.lib.net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zohn on 2016/12/1.
 *
 */
public class FormMap {
    public static Map<String, String> parse(String html) {
        Map<String, String> formData = new HashMap<String, String>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("form input[name],textarea[name],select[name]");
        for (Element element : elements) {
            String nodeName = element.nodeName();
            if (nodeName.contains("input")) {
                String type = element.attr("type");
                if (type.contains("image")) {
                    formData.put(element.attr("name") + ".x", "0");
                    formData.put(element.attr("name") + ".y", "0");
                } else if (!type.contains("checkbox") && !type.contains("submit")) {
                    formData.put(element.attr("name"), element.attr("value"));
                }
            } else if (nodeName.contains("select")) {
                formData.put(element.attr("name"), element.getElementsByTag("option").first().attr("value"));
            } else {
                throw new IllegalStateException("Cannot support analyse textarea.");
            }
        }
        return formData;
    }
}
