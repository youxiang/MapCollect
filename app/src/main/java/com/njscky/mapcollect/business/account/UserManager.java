package com.njscky.mapcollect.business.account;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static volatile UserManager instance;

    private SharedPreferences sp;

    private UserManager(Context context) {
        sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
    }

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager(context);
                }
            }
        }
        return instance;
    }

    public String getUserId() {
        return sp.getString("userId", null);
    }

    public String getUserName() {
        return sp.getString("username", null);
    }

    public String getPassword() {
        return sp.getString("password", null);
    }

    public boolean autoLogin() {
        return sp.getBoolean("autologin", false);
    }

    public boolean rememberPassword() {
        return sp.getBoolean("rememberpassword", false);
    }

    public void saveUserId(String userId) {
        sp.edit().putString("userId", userId).apply();
    }

    public void saveUsername(String username) {
        sp.edit().putString("username", username).apply();
    }

    public void savePassword(String password) {
        sp.edit().putString("password", password).apply();
    }

    public void saveAutoLogin(boolean autoLogin) {
        sp.edit().putBoolean("autologin", autoLogin).apply();
    }

    public void saveRememberPassword(boolean rememberPassword) {
        sp.edit().putBoolean("rememberpassword", rememberPassword).apply();
    }

}
