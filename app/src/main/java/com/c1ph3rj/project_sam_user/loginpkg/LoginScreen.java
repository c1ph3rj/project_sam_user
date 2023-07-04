package com.c1ph3rj.project_sam_user.loginpkg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.commonpkg.Alert;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {
    TextInputLayout agentCodeFieldLayout, passwordFieldLayout;
    TextInputEditText agentCodeField, passwordField;
    MaterialButton loginBtn;
    TextView forgetPasswordBtn;
    Alert alert;
    LinearLayout loginLayout;
    FirebaseDatabase firebaseDb;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getWindow().setStatusBarColor(getColor(R.color.color2));
        getWindow().setNavigationBarColor(getColor(R.color.color2));

        init();
    }

    void init() {
        try{
            alert = new Alert(this);
            firebaseDb = FirebaseDatabase.getInstance(getString(R.string.DB_URL));
            usersRef = firebaseDb.getReference("Users");
            loginLayout = findViewById(R.id.loginLayout);
            agentCodeField = findViewById(R.id.agentCodeField);
            agentCodeFieldLayout = findViewById(R.id.agentCodeLayout);
            passwordField = findViewById(R.id.passwordField);
            passwordFieldLayout = findViewById(R.id.agentPasswordLayout);
            loginBtn = findViewById(R.id.loginBtn);
            forgetPasswordBtn = findViewById(R.id.forgetPasswordBtn);

            loginBtn.setOnClickListener(onClickLogIn -> {
                verifyAgentDetails();
            });

            forgetPasswordBtn.setOnClickListener(onClickForgetPassword -> {
                try {
                    alert.showSnackBarAlert("Please contact admin!", loginLayout);
                } catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void verifyAgentDetails() {
        try {
            String agentCode = Objects.requireNonNull(agentCodeField.getText()).toString().trim();
            String agentPassword = Objects.requireNonNull(passwordField.getText()).toString().trim();

            if (agentCode.isEmpty()) {
                agentCodeFieldLayout.setErrorEnabled(true);
                agentCodeFieldLayout.setError("Please enter agent code to continue!");
                return;
            } else {
                agentCodeFieldLayout.setErrorEnabled(false);
            }

            if (agentPassword.isEmpty()) {
                passwordFieldLayout.setError("Please enter agent password to continue!");
                return;
            } else {
                passwordFieldLayout.setErrorEnabled(false);
            }

            if(!agentPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,}$")){
                alert.withTitleAndMessage("Alert!"," Please create a password with a minimum length of 6 characters, including at least one letter (a-z/A-Z), one number (0-9), and one special character: [@ $ ! % * # ? &].")
                        .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                passwordFieldLayout.setError("Please enter a valid password!");
                return;
            }else {
                passwordFieldLayout.setErrorEnabled(false);
            }

            loginWithCredentials(agentCode, agentPassword);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loginWithCredentials(String agentCode, String password) {
        try {
            Query query = usersRef.orderByChild("agentCode").equalTo(agentCode);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean agentFound = false;

                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        System.out.println(agentSnapshot.child("agentName"));
                        String storedPassword = agentSnapshot.child("password").getValue(String.class);

                        if (password.equals(storedPassword)) {
                            String emailId = agentSnapshot.child("emailId").getValue(String.class);
                            System.out.println("Agent email ID: " + emailId);
                            agentFound = true;
                            break;
                        }
                    }

                    if (!agentFound) {
                        System.out.println("Invalid agent code or password.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Error retrieving agent data: " + databaseError.getMessage());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}