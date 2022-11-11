package searchengine.config.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class JsoupUtil {

    private static final String USER_AGENT = "${jsoup.user-agent}";
    private static final String REFERRER = "${jsoup.referrer}";

    public static Document getDocument(String url) throws InterruptedException, IOException {
        TimeUnit.SECONDS.sleep(1);
        var document = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();
        return document;
    }
}
