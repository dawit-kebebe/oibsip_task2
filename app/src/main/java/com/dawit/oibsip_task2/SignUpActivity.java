package com.dawit.oibsip_task2;

import android.content.Intent;
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

public class SignUpActivity extends AppCompatActivity {

    private TextView warningTextView;

    private EditText fNameEditText;
    private EditText lNameEditText;
    private EditText uNameEditText;
    private EditText passEditText;

    private TextView loginTextView;

    private Button signupBtn;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        databaseHelper = new DatabaseHelper(this);

        warningTextView = (TextView) findViewById(R.id.warning_txt);

        fNameEditText = (EditText) findViewById(R.id.f_name);
        lNameEditText = (EditText) findViewById(R.id.l_name);
        uNameEditText = (EditText) findViewById(R.id.user_id);
        passEditText = (EditText) findViewById(R.id.password);

        loginTextView = (TextView) findViewById(R.id.already_have_acc);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signupBtn = (Button) findViewById(R.id.signup_btn);
        Intent thisIntent = getIntent();
        Bundle bundled = thisIntent.getExtras();

        if(
                bundled != null &&
                bundled.containsKey("uid") &&
                bundled.containsKey("password") &&
                bundled.containsKey("f_name") &&
                bundled.containsKey("l_name")
        ){
            loginTextView.setVisibility(View.INVISIBLE);
            signupBtn.setText("Submit Edit");
            fNameEditText.setText(bundled.getString("f_name"));
            lNameEditText.setText(bundled.getString("l_name"));
            uNameEditText.setText(bundled.getString("uid"));
            passEditText.setText(bundled.getString("password"));
            signupBtn.setOnClickListener(new EditAccountListener());
        }else {
            signupBtn.setOnClickListener(new SignupBtnListener());
        }

    }

    private class SignupBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String fName = fNameEditText.getText().toString();
            String lName = lNameEditText.getText().toString();
            String userId = uNameEditText.getText().toString();
            String password = passEditText.getText().toString();

            UserData userData = new UserData(fName, lName, userId, password);

            if (fName.length() <= 2){
                warningTextView.setText(R.string.fname_invalid);
                return;
            }

            if(lName.length() <= 2){
                warningTextView.setText(R.string.lname_invalid);
                return;
            }

            if (userId.length() <= 3){
                warningTextView.setText(R.string.uid_invalid);
                return;
            }

            if(password.length() <= 5){
                warningTextView.setText(R.string.pass_invalid);
                return;
            }

            if(databaseHelper.signupUser(userData)) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                MyToast.makeText(SignUpActivity.this, "Signed up successfully !", Toast.LENGTH_SHORT).show();
            }else {
                warningTextView.setText("Some sort of error happened while signing you up.");
            }
        }
    }

    private class EditAccountListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String fName = fNameEditText.getText().toString();
            String lName = lNameEditText.getText().toString();
            String userId = uNameEditText.getText().toString();
            String password = passEditText.getText().toString();

            UserData userData = new UserData(fName, lName, userId, password);

            if (fName.length() <= 2){
                warningTextView.setText(R.string.fname_invalid);
                return;
            }

            if(lName.length() <= 2){
                warningTextView.setText(R.string.lname_invalid);
                return;
            }

            if (userId.length() <= 3){
                warningTextView.setText(R.string.uid_invalid);
                return;
            }

            if(password.length() <= 5){
                warningTextView.setText(R.string.pass_invalid);
                return;
            }

            if(databaseHelper.editUser(userData)) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                MyToast.makeText(SignUpActivity.this, "Signed up successfully !", Toast.LENGTH_SHORT).show();
            }else {
                warningTextView.setText("Some sort of error happened while signing you up.");
            }
        }
    }
}
