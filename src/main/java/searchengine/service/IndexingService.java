package searchengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import searchengine.config.SitesConfig;
import searchengine.config.tasks.IndexingTask;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private static final String DELETE_SITE_ROWS = "DELETE FROM site";

    private final SitesConfig sitesConfig;
    private final JdbcTemplate jdbcTemplate;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

    public Boolean start() {
        jdbcTemplate.execute(DELETE_SITE_ROWS);

        return sitesConfig.getSites().stream()
                .map(site -> {
                    Site siteEntity = Site.builder()
                            .status(Status.INDEXING)
                            .status_time(LocalDateTime.now())
                            .url(site.getUrl())
                            .name(site.getName())
                            .build();
                    siteRepository.saveAndFlush(siteEntity);

                    return new IndexingTask(site.getUrl(), site.getUrl(),
                            siteRepository, pageRepository);
                })
                .peek(forkJoinPool::execute)
                .filter(ForkJoinTask::isDone)
                .peek(task -> siteRepository.findByUrl(task.getRootUrl()).setStatus(Status.INDEXED))
                .map(ForkJoinTask::join)
                .reduce((a, b) -> a && b)
                .orElse(Boolean.TRUE);
    }

    public void stop() {
        forkJoinPool.shutdownNow();
        List<Site> sites = siteRepository.findAllWithStatus(Status.INDEXING);
        for (Site site : sites) {
            site.setStatus(Status.FAILED);
            site.setStatus_time(LocalDateTime.now());
            site.setLastError("Индексация остановлена пользователем");
        }
    }
}
