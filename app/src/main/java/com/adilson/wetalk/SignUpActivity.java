package com.adilson.wetalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    private EditText passTxt, confirmPassTxt, emailTxt, nameTxt;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;
    private EditText phoneNumberTxt;
    private RadioButton maleBol, femaleBol;
    private RadioGroup genderBol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailTxt = findViewById(R.id.input_email_sign_up);
        passTxt = findViewById(R.id.input_password_sign_up);
        phoneNumberTxt = findViewById(R.id.input_phone_number_sign_up);
        confirmPassTxt = findViewById(R.id.input_verify_password);
        nameTxt = findViewById(R.id.input_full_name_sign_up);

        maleBol = findViewById(R.id.sign_up_gender_male);
        femaleBol = findViewById(R.id.sign_up_gender_female);
        genderBol = findViewById(R.id.sign_up_gender);


        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseHelper.USERS);

        mAuth = FirebaseAuth.getInstance();
    }

    public void createNewAccount(View view) {
        String name = nameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String pass = passTxt.getText().toString();
        String confirmPass = confirmPassTxt.getText().toString();

        Log.d(TAG, "createNewAccount male: " + maleBol.isChecked() + "female: " + femaleBol.isChecked());

        if (name.length() < 5) {
            nameTxt.setError("Nome invalido");
            return;
        }

        if (email.length() < 5 || !email.contains("@") || !email.contains(".")) {
            emailTxt.setError("Email invalido");
            return;
        }

        if (pass.length() > 5 && confirmPass.length() > 5) {
            if (pass.equals(confirmPass)) {
                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPhoneNumber(phoneNumberTxt.getText().toString());
                user.setMale(getGender());


                createAccount(user, pass);
                return;
            }
        }

        Toast.makeText(this, "Palavra passe invalida", Toast.LENGTH_SHORT).show();


    }

    private boolean getGender() {
        return maleBol.isChecked();
    }

    private void createAccount(final User user, String pass) {
        dialog = new ProgressDialog(this);
        dialog.show();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            databaseReference.child(currentUser.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.w(TAG, "createUserWithEmail:failure", databaseError.toException());
                                        Toast.makeText(SignUpActivity.this, "Houve um erro", Toast.LENGTH_SHORT).show();

                                    } else {
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    }

                                    dialog.dismiss();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Houve um erro", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }

                        // ...
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dialog != null)
            dialog.dismiss();
    }
}
