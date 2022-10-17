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

public class CreateAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    EditText emailE;
    EditText mdp1E;
    EditText mdpE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        mAuth = FirebaseAuth.getInstance();
        emailE = (EditText) findViewById(R.id.email);
        mdp1E = (EditText) findViewById(R.id.mdp1);
        mdpE = (EditText) findViewById(R.id.mdp);
    }

    public void creer (View view){
        String email = emailE.getText().toString();
        String mdp1 = mdp1E.getText().toString();
        String mdp = mdpE.getText().toString();
        if(email.equals("")){
            emailE.setError(getString(R.string.emailIsEmpty));
        }
        if(mdp1.length() <6){
            mdp1E.setError(getString(R.string.passwordNeed6char));
        }
        if(!mdp1.equals(mdp)){
            mdp1E.setError(getString(R.string.passwordsDoenstMatch));
        }
        else{
            signUp(email, mdp);
        }

    }

    public void signUp (String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "CreateUserwithEmail:success");
                            Intent intent = new Intent(CreateAccount.this, ListeOffre.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}