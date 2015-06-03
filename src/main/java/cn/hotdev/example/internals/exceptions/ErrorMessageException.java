package cn.hotdev.example.internals.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
public final class ErrorMessageException extends RuntimeException {
    private cn.hotdev.example.models.error.Error error;
}
