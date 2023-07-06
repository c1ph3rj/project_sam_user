package com.c1ph3rj.project_sam_user.dashboardpkg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.TextView;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.commonpkg.AgentDetailModel;
import com.c1ph3rj.project_sam_user.commonpkg.DatabaseHelper;

import java.util.Locale;

public class DashboardScreen extends AppCompatActivity {
    TextView userNameView;
    CardView settingsBtn;
    DatabaseHelper localDbHelper;
    AgentDetailModel currentAgentDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);
        getWindow().setStatusBarColor(getColor(R.color.color2));

        init();
    }

    void init(){
        try {
            localDbHelper = new DatabaseHelper(this);
            userNameView = findViewById(R.id.userNameView);
            settingsBtn = findViewById(R.id.settingsBtn);

            currentAgentDetails = localDbHelper.getAgentDetail();
            localDbHelper.close();

            String greetings = "HELLO,\n" + currentAgentDetails.agentName.toUpperCase(Locale.ROOT) + "!";
            userNameView.setText(greetings);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}