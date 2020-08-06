package com.thomas.firebasechatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import Base.BaseActivity;

import com.thomas.firebasechatapp.Database.MyDataBase;
import com.thomas.firebasechatapp.Models.User;
import com.thomas.firebasechatapp.R;

public class RegisterActivity extends BaseActivity {

    MaterialEditText username, email, pass;
    Button registerBtn;

    FirebaseAuth auth;

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        container = findViewById(R.id.container);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);

        registerBtn = findViewById(R.id.login_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = username.getText().toString();
                String txtEmail = email.getText().toString();
                String txtPassword = pass.getText().toString();

                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "password must 6 or more", Toast.LENGTH_SHORT).show();
                } else {
                    register(txtUsername, txtEmail, txtPassword);
                }
            }
        });

    }

    public void register(final String username, String email, String password) {
        showProgressBar(R.string.loading);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    assert firebaseUser != null;
                    User user = new User();
                    user.setUsername(username);
                    user.setImageUrl("default");
                    user.setStatus("offline");

                    String userId = firebaseUser.getUid();

                    MyDataBase.getInstance().createNewUser(user, userId, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                hideProgressDialog();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                hideProgressDialog();
                                Toast.makeText(activity, "Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Log.e("DATABASEFIRESTORE", e.getLocalizedMessage());
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Log.e("FIRENASEAUTH", e.getLocalizedMessage());
            }
        });
    }
}
