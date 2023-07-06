package com.c1ph3rj.project_sam_user.loginpkg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.commonpkg.AgentDetailModel;
import com.c1ph3rj.project_sam_user.commonpkg.Alert;
import com.c1ph3rj.project_sam_user.commonpkg.DatabaseHelper;
import com.c1ph3rj.project_sam_user.commonpkg.LoadingDialog;
import com.c1ph3rj.project_sam_user.dashboardpkg.DashboardScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    FirebaseAuth firebaseAuth;
    LoadingDialog loadingDialog;
    String emailId;
    FirebaseFirestore fireStoreDb;
    CollectionReference userDetailsRef;

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
            firebaseAuth = FirebaseAuth.getInstance();
            usersRef = firebaseDb.getReference("Users");
            fireStoreDb = FirebaseFirestore.getInstance();
            userDetailsRef = fireStoreDb.collection("Users");
            loadingDialog = new LoadingDialog(this);
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
                passwordFieldLayout.setError("Please enter a valid password!");
                return;
            }else {
                passwordFieldLayout.setErrorEnabled(false);
            }

            loadingDialog.show();
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
                        String storedPassword = agentSnapshot.child("password").getValue(String.class);
                        if (password.equals(storedPassword)) {
                            emailId = agentSnapshot.child("emailId").getValue(String.class);
                            agentFound = true;
                        }else{
                            passwordFieldLayout.setError("Incorrect password!");
                            loadingDialog.dismiss();
                            return;
                        }
                        break;
                    }

                    if (!agentFound && emailId == null) {
                        loadingDialog.dismiss();
                        alert.withTitleAndMessage("Alert","Agent Not Found, Please contact Admin.")
                                .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }else{
                        assert emailId != null;
                        firebaseAuth.signInWithEmailAndPassword(emailId, password)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        try {
                                            AuthResult result = task.getResult();
                                            userDetailsRef.document(Objects.requireNonNull(result.getUser()).getUid())
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if(task1.isSuccessful()){
                                                            DocumentSnapshot document = task1.getResult();
                                                            if (document != null && document.exists()) {
                                                                String agentCode1 = document.getString("agentCode");
                                                                String agentName = document.getString("agentName");
                                                                String agentType = document.getString("agentType");
                                                                String emailId = document.getString("emailId");
                                                                String mobileNumber = document.getString("mobileNumber");
                                                                String password1 = document.getString("password");
                                                                String address = document.getString("address");

                                                                // Create an AgentDetailModel object with the retrieved details
                                                                AgentDetailModel agent = new AgentDetailModel();
                                                                agent.agentCode = agentCode1;
                                                                agent.agentName = agentName;
                                                                agent.agentType = agentType;
                                                                agent.emailId = emailId;
                                                                agent.mobileNumber = mobileNumber;
                                                                agent.address = address;

                                                                DatabaseHelper localDbHelper = new DatabaseHelper(LoginScreen.this);
                                                                localDbHelper.addAgentDetail(agent);
                                                                localDbHelper.close();
                                                            }
                                                            startActivity(new Intent(LoginScreen.this, DashboardScreen.class));
                                                            loadingDialog.dismiss();
                                                        }else {
                                                            loadingDialog.dismiss();
                                                            alert.somethingWentWrong();
                                                        }
                                                    }).addOnFailureListener(e -> {
                                                        e.printStackTrace();
                                                        loadingDialog.dismiss();
                                                        alert.somethingWentWrong();
                                                    });
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            loadingDialog.dismiss();
                                            alert.somethingWentWrong();
                                        }
                                    }else{
                                        loadingDialog.dismiss();
                                        alert.somethingWentWrong();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    loadingDialog.dismiss();
                                    alert.somethingWentWrong();
                                });
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