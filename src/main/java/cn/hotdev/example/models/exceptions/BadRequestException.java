package cn.hotdev.example.models.exceptions;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public final class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
