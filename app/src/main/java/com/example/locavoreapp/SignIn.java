package com.example.locavoreapp;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    EditText emailE;
    EditText passwordE;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(SignIn.this, ListeOffre.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        emailE = (EditText)findViewById(R.id.email);
        emailE.setText("");
        passwordE = (EditText)findViewById(R.id.password);
        passwordE.setText("");
    }

    public void onStart(){
        super.onStart();
        mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void signIn(View view){
        String email = emailE.getText().toString();
        String password = passwordE.getText().toString();
        if(!(email.matches("")) && !(password.matches(""))){
            signInConnexion (email, password);
        }
        if(email.matches("") ){
            emailE.setError(getString(R.string.emailIsEmpty));
        }
        if(password.matches("") ){
            passwordE.setError(getString(R.string.passwordIsEmpty));
        }
    }


    public void signInConnexion (String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(SignIn.this, ListeOffre.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp (View view){
        Intent intent = new Intent(SignIn.this, CreateAccount.class);
        startActivity(intent);
    }

}