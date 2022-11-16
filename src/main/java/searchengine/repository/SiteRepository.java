package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import searchengine.model.Site;
import searchengine.model.Status;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Integer> {

    Site findByUrl(String url);

    @Query("select s from Site s " +
            "where s.status = :status")
    List<Site> findAllWithStatus(Status status);
}
