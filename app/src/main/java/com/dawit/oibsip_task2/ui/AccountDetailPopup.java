package com.dawit.oibsip_task2.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dawit.oibsip_task2.R;
import com.dawit.oibsip_task2.model.UserData;

public class AccountDetailPopup {

    public static Dialog showAlert(Context context, UserData userData, AccountDetailPopup.AlertListener editBtnListener, AccountDetailPopup.AlertListener logoutListener){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.account_layout, null);

        TextView accountOwner = (TextView) view.findViewById(R.id.account_owner);
        Button editAccountBtn = (Button) view.findViewById(R.id.edit_acc_btn);
        Button logoutBtn = (Button) view.findViewById(R.id.logout_btn);

        logoutBtn.setOnClickListener(logoutListener);
        if (userData != null){
            accountOwner.setText(userData.getUserName() + "\n" + userData.getfName() + " " + userData.getlName());
            editBtnListener.initComponents(userData);
            editAccountBtn.setOnClickListener(editBtnListener);
        }else {
            accountOwner.setText("Unknown user ID");
            accountOwner.setText("Unknown user");
            editAccountBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyToast.makeText(context, "Can't edit unknown user.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        return dialog;
    }

    public static abstract class AlertListener implements View.OnClickListener {

        private UserData userData;

        public UserData getUserData() {
            return userData;
        }

        public void initComponents(UserData userData) {
            this.userData = userData;
        }

    }
}
