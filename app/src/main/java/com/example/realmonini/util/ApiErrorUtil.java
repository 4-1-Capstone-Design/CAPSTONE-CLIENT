package com.example.realmonini.util;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit2.Response;

public class ApiErrorUtil {

    private static class ErrorBody {
        @SerializedName("msg")
        String msg;
    }

    public static String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                ErrorBody error = new Gson().fromJson(
                        response.errorBody().charStream(), ErrorBody.class);
                if (error != null && error.msg != null && !error.msg.isEmpty()) {
                    return error.msg;
                }
            }
        } catch (Exception ignored) {}
        return "오류가 발생했습니다. (" + response.code() + ")";
    }
}
