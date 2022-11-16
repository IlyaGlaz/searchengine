package searchengine.dto.common;

import lombok.Value;

@Value
public class IndexingResponseErrorDto {
    String result;
    String error;
}
