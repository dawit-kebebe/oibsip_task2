package com.dawit.oibsip_task2.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dawit.oibsip_task2.R;

public class MyToast {

    public static Toast makeText(Context context, String text, int duration) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.toast, null);
        ((TextView) view.findViewById(R.id.toast_text)).setText(text);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(duration);
        return toast;
    }

}
