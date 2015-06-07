package cn.hotdev.example.constants;


import cn.hotdev.example.models.enums.StatusEnum;

public enum DefaultRestStatus implements StatusEnum {

    OK(0, "ok"),
    NOT_MODIFIED(304, "not modified"),
    BAD_REQUEST(400, "bad request"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not found"),
    TOO_MANY_REQUEST(429, "too many request"),
    INTERNAL_ERROR(500, "internal error");


    private int status;
    private String defaultMessage;

    private DefaultRestStatus(int value, String defaultMessage) {
        this.status = value;
        this.defaultMessage = defaultMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
