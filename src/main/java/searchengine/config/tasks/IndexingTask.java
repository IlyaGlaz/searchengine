package searchengine.config.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import searchengine.config.utils.JsoupUtil;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@Getter
@RequiredArgsConstructor
public class IndexingTask extends RecursiveTask<Boolean> {

    private static final String REF_VALUE = "abs:href";
    private static final String REF_SELECTOR = "a[href]";
    private static final String INNER_SOURCE_DELIMITER = "[.[^#]]+";

    private final String url;
    private final String rootUrl;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;

    @Override
    protected Boolean compute() {
        Optional<Document> document = Optional.empty();
        try {
            document = JsoupUtil.getDocument(url);
        } catch (InterruptedException | IOException e) {
        }
        var site = siteRepository.findByUrl(rootUrl);
        var page = Page.builder()
                .path(url.replace(rootUrl, "/"))
                .site(site)
                .build();
        var result = Boolean.TRUE;
        if (document.isPresent()) {
            page.setCode(document.get().connection().response().statusCode());
            page.setContent(document.get().toString());
            insertBoth(site, page);

            result = document.get().select(REF_SELECTOR).stream()
                    .map(element -> element.attr(REF_VALUE))
                    .filter(url -> url.matches(rootUrl + INNER_SOURCE_DELIMITER))
                    .filter(url ->
                            pageRepository.findBySiteAndPath(url.replace(rootUrl, "/")).isEmpty())
                    .map(url -> new IndexingTask(url, rootUrl, siteRepository, pageRepository).fork())
                    .map(ForkJoinTask::join)
                    .reduce((a, b) -> a && b)
                    .orElse(Boolean.TRUE);
        } else {
            page.setCode(HttpStatus.NOT_FOUND.value());
            page.setContent("");
            insertBoth(site, page);
        }
        return result;
    }

    private void insertBoth(Site site, Page page) {
        pageRepository.save(page);
        site.setStatus_time(LocalDateTime.now());
        siteRepository.save(site);
    }
}
