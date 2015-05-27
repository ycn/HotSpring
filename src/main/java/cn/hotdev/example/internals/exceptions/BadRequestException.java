package cn.hotdev.example.internals.exceptions;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public final class BadRequestException extends Exception {

    public BadRequestException(String message) {
        super(message);
    }
}
