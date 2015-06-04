package cn.hotdev.example.models.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPagingResponse<T> extends RestResponse<T> {

    protected RestPage page;

    public RestPagingResponse<T> page(RestPage page) {
        this.page = page;
        return this;
    }

    @Override
    public RestPagingResponse<T> data(T value) {
        return (RestPagingResponse<T>) super.data(value);
    }
}
