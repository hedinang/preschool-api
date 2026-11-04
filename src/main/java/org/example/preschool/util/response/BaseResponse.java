package org.example.preschool.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    public int status;
    public String message;
    public T data;

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
