package cn.hotdev.example.models.rest;

import cn.hotdev.example.utils.StringTool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponse<T> {

    protected int status;
    protected String message;
    protected T data;
    protected long createdTime = System.currentTimeMillis();
    protected long updatedTime = System.currentTimeMillis();

    public RestResponse() {
        this(RestStatus.OK);
    }

    public RestResponse(RestStatus status) {
        this(status, status.getDefaultMessage());
    }

    public RestResponse(RestStatus status, String message) {
        this.status = status.getValue();
        this.message = (message == null) ? status.getDefaultMessage() : message;
    }

    public RestResponse(RestStatus status, String format, Object... arguments) {
        this(status, StringTool.formatString(format, arguments));
    }

    public RestResponse<T> data(T value) {
        data = value;
        return this;
    }

}
