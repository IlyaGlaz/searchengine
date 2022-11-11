package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import searchengine.model.Page;

import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Integer> {

    @Query("select p from Page p " +
            "join fetch p.site s " +
            "where p.path = :path")
    Optional<Page> findBySiteAndPath(String path);
}
