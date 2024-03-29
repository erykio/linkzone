package io.eryk.linkzone.controller;

import io.eryk.linkzone.exception.AlreadyExistsException;
import io.eryk.linkzone.exception.EmailTakenException;
import io.eryk.linkzone.exception.FileStorageException;
import io.eryk.linkzone.exception.ImageUploadException;
import io.eryk.linkzone.exception.MyFileNotFoundException;
import io.eryk.linkzone.exception.NoPermissionsException;
import io.eryk.linkzone.exception.NotFoundException;
import io.eryk.linkzone.exception.ResourceLockedException;
import io.eryk.linkzone.exception.UserNotFoundException;
import io.eryk.linkzone.exception.UsernameTakenException;
import io.eryk.linkzone.exception.ValidationErrorException;
import io.eryk.linkzone.validation.ErrorResponse;
import io.eryk.linkzone.validation.ValidationErrorBuilder;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ControllerAdvice
public class Advice {

    // these exceptions must be repeated in @ExceptionHandler below because this cannot be passed as its param.
    private static final Class[] exceptionClasses = {
            NotFoundException.class,
            UserNotFoundException.class,
            AlreadyExistsException.class,
            UsernameTakenException.class,
            EmailTakenException.class,
            NoPermissionsException.class,
            ResourceLockedException.class,
            FileUploadException.class,
            ImageUploadException.class
    };

    @ExceptionHandler({
            NotFoundException.class,
            UserNotFoundException.class,
            AlreadyExistsException.class,
            UsernameTakenException.class,
            EmailTakenException.class,
            NoPermissionsException.class,
            ResourceLockedException.class,
            FileStorageException.class,
            MyFileNotFoundException.class,
            FileUploadException.class,
            ImageUploadException.class})
    public ResponseEntity<ErrorResponse> handleRegularExceptions(final Exception e)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return createErrorResponse(e);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final ValidationErrorException e) {
        ErrorResponse errorResponse = ValidationErrorBuilder.fromBindingErrors(e.getErrors());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(final Exception exception)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ErrorResponse errorResponse = new ErrorResponse(exception.getClass().getSimpleName());
        errorResponse.addError(exception.getMessage());
        // to get httpStatus from annotation, we need to know subclass of this exception
        for (Class klass : exceptionClasses) {
            if (klass.isInstance(exception)) {
                HttpStatus httpStatus = getResponseStatusAnnotationValue(klass);
                return new ResponseEntity<>(errorResponse, httpStatus);
            }
        }
        // return generic status code if subclass wasn't found
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private HttpStatus getResponseStatusAnnotationValue(final Class klass)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Annotation annotation = klass.getAnnotation(ResponseStatus.class);
        Class<? extends Annotation> type = annotation.annotationType();
        Method method = type.getMethod("value");
        return (HttpStatus) method.invoke(annotation);
    }
}
