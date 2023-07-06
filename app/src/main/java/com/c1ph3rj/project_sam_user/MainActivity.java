package com.c1ph3rj.project_sam_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.c1ph3rj.project_sam_user.commonpkg.Alert;
import com.c1ph3rj.project_sam_user.commonpkg.LoadingDialog;
import com.c1ph3rj.project_sam_user.dashboardpkg.DashboardScreen;
import com.c1ph3rj.project_sam_user.loginpkg.LoginScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    LoadingDialog loadingDialog;
    public static String DB_URL;
    Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getColor(R.color.white));
        DB_URL = getString(R.string.DB_URL);

        init();
    }

    void init() {
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            loadingDialog = new LoadingDialog(this);
            alert = new Alert(this);

            if (firebaseAuth.getCurrentUser() != null && (Objects.equals(firebaseAuth.getCurrentUser().getEmail(), getString(R.string.admin)))) {
                firebaseAuth.signOut();
            }
            if (firebaseAuth.getCurrentUser() != null && !Objects.equals(firebaseAuth.getCurrentUser().getEmail(), getString(R.string.admin))) {
                startActivity(new Intent(this, DashboardScreen.class));
            } else {
                firebaseAuth.signOut();
                loadingDialog.show();
                loginWithCustomCredential(getString(R.string.admin), getString(R.string.admin_pswd));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginWithCustomCredential(String userName, String password) {
        firebaseAuth.signInWithEmailAndPassword(userName, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, LoginScreen.class));
                        loadingDialog.dismiss();
                    } else {
                        loadingDialog.dismiss();
                        alert.withTitleAndMessage("Alert!", "Something went wrong please try again Later!")
                                .setPositiveButton("Ok", (dialogInterface, i) -> finishAffinity());
                    }
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                    alert.withTitleAndMessage("Alert!", "Something went wrong please try again Later!")
                            .setPositiveButton("Ok", (dialogInterface, i) -> finishAffinity());
                });
    }
}