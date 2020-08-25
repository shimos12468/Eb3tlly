package com.armjld.eb3tly.Passaword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.armjld.eb3tly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Password extends AppCompatActivity {

    private Button sendEmail;
    private FirebaseAuth firebaseAuth;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);

        sendEmail = findViewById(R.id.button2);
        email = findViewById(R.id.editTextTextEmailAddress2);
        firebaseAuth = FirebaseAuth.getInstance();
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.toString().isEmpty()){
                    Toast.makeText(Forget_Password.this, "empty", Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.sendPasswordResetEmail("shimos12468@gmail.com").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Forget_Password.this, "sent rest password email to ur email", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Forget_Password.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}