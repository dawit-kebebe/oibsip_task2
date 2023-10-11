package com.dawit.oibsip_task2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dawit.oibsip_task2.db.DatabaseHelper;
import com.dawit.oibsip_task2.model.UserData;
import com.dawit.oibsip_task2.ui.MyToast;

import java.util.Date;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView warningTextView;
    private EditText userIdEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView createAccTextView;

    private String userId;
    private String password;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        if (
                sharedPreferences.contains("uid") &&
                sharedPreferences.contains("logged_in_at") &&
                sharedPreferences.contains("expires_at")
        ) {
            Map<String, ?> session = sharedPreferences.getAll();
            String expiresAt = (String) session.get("expires_at");
            if (Long.parseLong(expiresAt) >= (new Date().getTime())) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                MyToast.makeText(LoginActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        setContentView(R.layout.login);

        databaseHelper = new DatabaseHelper(this);

        warningTextView = (TextView) findViewById(R.id.warning_txt);

        userIdEditText = (EditText) findViewById(R.id.user_id);
        passwordEditText = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login_btn);
        createAccTextView = (TextView) findViewById(R.id.signup_btn);

        loginButton.setOnClickListener(new LoginListener());
        createAccTextView.setOnClickListener(new CreateAccountListener());
    }

    private class LoginListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            userId = userIdEditText.getText().toString();
            password = passwordEditText.getText().toString();

            UserData userData = new UserData(null, null, userId, password);

            if (userId.length() <= 0){
                warningTextView.setText("Username is required,");
                return;
            }

            if(password.length() <= 0){
                warningTextView.setText("Password is required.");
                return;
            }

            if(databaseHelper.loginUser(userData)){
                SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("uid", userId);
                editor.putString("logged_in_at", String.valueOf(new Date().getTime()));
                editor.putString("expires_at", String.valueOf(new Date().getTime() + (3 * 60 * 60 * 1000)));
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
//                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                warningTextView.setText("Username or Password not found.");
                MyToast.makeText(LoginActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateAccountListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
