package cn.hotdev.example.models.rest;


import lombok.Getter;

public enum RestStatus {

    OK(0, "ok"),
    NOT_MODIFIED(304, "not modified"),
    BAD_REQUEST(400, "bad request"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not found"),
    TOO_MANY_REQUEST(429, "too many request"),
    INTERNAL_ERROR(500, "internal error");


    @Getter
    private int value;
    @Getter
    private String defaultMessage;

    private RestStatus(int value, String defaultMessage) {
        this.value = value;
        this.defaultMessage = defaultMessage;
    }
}
