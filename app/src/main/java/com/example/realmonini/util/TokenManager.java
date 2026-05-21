package com.example.realmonini.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "monini_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NICKNAME = "nickname";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLoginInfo(long userId, String email, String nickname,
                              String accessToken, String refreshToken) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_NICKNAME, nickname)
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public String getAccessToken() { return prefs.getString(KEY_ACCESS_TOKEN, null); }
    public String getRefreshToken() { return prefs.getString(KEY_REFRESH_TOKEN, null); }
    public long getUserId() { return prefs.getLong(KEY_USER_ID, -1); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getNickname() { return prefs.getString(KEY_NICKNAME, null); }

    public boolean isLoggedIn() { return getAccessToken() != null; }

    public void clear() { prefs.edit().clear().apply(); }
}
