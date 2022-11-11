package searchengine.config.tasks;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import searchengine.config.utils.JsoupUtil;
import searchengine.model.Page;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.concurrent.RecursiveAction;

@RequiredArgsConstructor
public class IndexingTask extends RecursiveAction {

    private static final String REF_VALUE = "abs:href";
    private static final String REF_SELECTOR = "a[href]";
    private static final String INNER_SOURCE_DELIMITER = "[.[^#]]+";

    private final String url;
    private final String rootUrl;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;

    @Override
    @SneakyThrows
    protected void compute() {
        var document = JsoupUtil.getDocument(url);
        var page = Page.builder()
                .path(url.replace(rootUrl, "/"))
                .content(document.toString())
                .site(siteRepository.findByUrl(rootUrl))
                .code(document.connection().response().statusCode())
                .build();
        pageRepository.saveAndFlush(page);

        var elements = document.select(REF_SELECTOR);
        elements.stream()
                .map(element -> element.attr(REF_VALUE))
                .filter(url -> url.matches(rootUrl + INNER_SOURCE_DELIMITER))
                .filter(url ->
                        !pageRepository.findBySiteAndPath(url.replace(rootUrl, "/")).isPresent())
                .forEach(url -> new IndexingTask(url, rootUrl, siteRepository, pageRepository).invoke());
    }
}
