package com.c1ph3rj.project_sam_user.neworderpkg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.c1ph3rj.project_sam_user.R;

public class NewOrderScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_screen);

        init();
    }

    void init() {
        try {

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}