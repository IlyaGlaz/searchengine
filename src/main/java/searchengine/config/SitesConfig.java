package searchengine.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "indexing-settings")
public class SitesConfig {
    private List<Site> sites;

    @Data
    @NoArgsConstructor
    public static class Site {
        private String url;
        private String name;
    }
}
