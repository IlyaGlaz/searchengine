package searchengine.http.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import searchengine.repository.SiteRepository;

@RestControllerAdvice(basePackages = "searchengine.http.controller")
@RequiredArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private final SiteRepository siteRepository;

    @ExceptionHandler(Exception.class)
    private void handleException(Exception e) {
//        Optional<Site> maybeSite = siteRepository.findById(1);
//        maybeSite.ifPresent(site -> {
//            site.setLastError(e.getMessage());
//            siteRepository.save(site);
//        });
        System.out.println(e.getMessage());
    }
}
