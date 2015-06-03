package cn.hotdev.example.constants;


import lombok.Getter;

public enum ErrorStatus {

    TEST(1000, "test error status"),
    USER_NOT_FOUND(2404, "user not found"),
    BAD_REQUEST(1400, "bad request"),
    INTERNAL_ERROR(1500, "internal error"),
    PAGE_NOT_FOUND(1404, "page not found");


    @Getter
    private int value;
    @Getter
    private String message;

    private ErrorStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }
}
