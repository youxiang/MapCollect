package com.njscky.mapcollect.business.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.njscky.mapcollect.MainActivity;
import com.njscky.mapcollect.R;
import com.njscky.mapcollect.util.WebServiceUtils;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    View loadingProgressBar;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    CheckBox rememberPasswordCheckBox;
    CheckBox autologinCheckBox;
    private UserManager userManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        rememberPasswordCheckBox = findViewById(R.id.remember_password);
        autologinCheckBox = findViewById(R.id.auto_login);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        userManager = UserManager.getInstance(this);
        boolean autoLogin = userManager.autoLogin();
        String userId = userManager.getUserId();
        String username = userManager.getUserName();
        String password = userManager.getPassword();
        boolean rememberPassword = userManager.rememberPassword();
        if (rememberPassword) {
            usernameEditText.setText(username);
            passwordEditText.setText(password);
        }
        rememberPasswordCheckBox.setChecked(rememberPassword);
        autologinCheckBox.setChecked(autoLogin);
        if (autoLogin) {
            login();
        }
    }

    private void login() {
        String strUserName = usernameEditText.getText().toString().trim();
        String strPsw = passwordEditText.getText().toString().trim();

        if (strUserName.equals("")) {
            usernameEditText.setError("请输入账号");
            return;
        }
        if (strPsw.equals("")) {
            passwordEditText.setError("请输入密码");
            return;
        }
        loadingProgressBar.setVisibility(View.VISIBLE);
        try {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("UserName", strUserName);
            properties.put("UserPSW", strPsw);
            WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "UserLogin", properties, result -> {
                loadingProgressBar.setVisibility(View.GONE);
                if (result.toString().contains("登录失败") || result.toString().contains("账号密码不正确")) {
                    Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                    enterMainActivity();
                } else {
                    String userId = result.toString().substring(result.toString().indexOf("=") + 1, result.toString().indexOf(";"));
                    userManager.saveUserId(userId);
                    userManager.saveUsername(usernameEditText.getText().toString());
                    userManager.savePassword(passwordEditText.getText().toString());
                    userManager.saveAutoLogin(autologinCheckBox.isChecked());
                    userManager.saveRememberPassword(rememberPasswordCheckBox.isChecked());
                    enterMainActivity();
                }
            });
        } catch (Exception ex) {
            Log.i(TAG, "login: " + ex);
        }
    }

    private void enterMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
