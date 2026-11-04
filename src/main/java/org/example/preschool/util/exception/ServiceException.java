package org.example.preschool.util.exception;

import org.springframework.http.HttpStatus;


public class ServiceException extends RuntimeException {

    protected HttpStatus statusCode = HttpStatus.BAD_REQUEST;

    private int code;

    private Object data;


    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.statusCode = HttpStatus.resolve(code) == null ? HttpStatus.BAD_REQUEST : HttpStatus.resolve(code);
    }

    public ServiceException(String message) {
        super(message);
        this.code = 500;
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
        this.statusCode = HttpStatus.resolve(code) == null ? HttpStatus.BAD_REQUEST : HttpStatus.resolve(code);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
