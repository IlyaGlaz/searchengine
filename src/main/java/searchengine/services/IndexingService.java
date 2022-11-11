package searchengine.services;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import searchengine.config.tasks.IndexingTask;
import searchengine.config.SitesConfig;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

@Service
@AllArgsConstructor
public class IndexingService {

    private static final String DELETE_SITE_ROWS = "DELETE FROM site";
    private static final String DELETE_PAGE_ROWS = "DELETE FROM page";

    private final SitesConfig sitesConfig;
    private final JdbcTemplate jdbcTemplate;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;

    public boolean start() {
        jdbcTemplate.execute(DELETE_SITE_ROWS);
        jdbcTemplate.execute(DELETE_PAGE_ROWS);

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        sitesConfig.getSites().stream()
                .forEach(site -> {
                    Site siteEntity = Site.builder()
                            .status(Status.INDEXING)
                            .status_time(LocalDateTime.now())
                            .url(site.getUrl())
                            .name(site.getName())
                            .build();
                    siteRepository.saveAndFlush(siteEntity);

                    forkJoinPool.execute(new IndexingTask(site.getUrl(), site.getUrl(),
                            siteRepository, pageRepository));
                });

        return true;
    }
}
