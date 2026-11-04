package org.example.preschool.util.response;


public class Response {
    public static <T> BaseResponse<T>toData(T data, String message) {
        return new BaseResponse<T>(200, message, data);
    }

    public static <T> BaseResponse<T>  toData(T data) {
        return  new BaseResponse<T>(200, "Successfully", data);
    }

    public static BaseResponse<Object> toError(int status, String message) {
        return new BaseResponse<>(status, message);
    }
}
