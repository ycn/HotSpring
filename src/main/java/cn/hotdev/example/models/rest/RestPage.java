package cn.hotdev.example.models.rest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPage {

    private int currentPage; // index from 0
    private int totalPages;
    private long totalElements;
    private boolean hasNext;

    public RestPage(int currentPage, Page page) {
        this.currentPage = currentPage;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.hasNext = page.hasNext();
    }
}
