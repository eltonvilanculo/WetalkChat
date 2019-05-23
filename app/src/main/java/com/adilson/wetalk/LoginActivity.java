package com.adilson.wetalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    private EditText emailTxt, passwordTxt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTxt = findViewById(R.id.input_email_login);
        passwordTxt = findViewById(R.id.input_password_login);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    public void createAccount(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void login(View view) {

        String email = emailTxt.getText().toString();
        String pass = passwordTxt.getText().toString();


        if (email.length() < 5 || !email.contains("@") || !email.contains(".")) {
            emailTxt.setError("Email invalido");
            return;
        }

        if (pass.length() > 5) {


            signIn(email, pass);
            return;
        }

        passwordTxt.setError("Palavra passe invalida");
    }

    private void signIn(String email, String password) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            dialog.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Autenticao falhou.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }

                        // ...
                    }
                });


    }


}
