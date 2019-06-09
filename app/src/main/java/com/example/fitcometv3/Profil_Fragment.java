package com.example.fitcometv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profil_Fragment extends Fragment {
    View myView;
    TextView tvUsername, tvEmail, tvWaga, tvWzrost;
    String username;
    Button btnPopraw;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef, mDetialsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.profil_layout,container,false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDetialsRef = mDatabaseRef.child("Dane");

        tvUsername = myView.findViewById(R.id.textViewUsername_Profile);
        tvEmail = myView.findViewById(R.id.textViewEmail_Profile);
        tvWaga = myView.findViewById(R.id.textViewWaga_Profile);
        tvWzrost = myView.findViewById(R.id.textViewWzrost_Profile);
        btnPopraw = myView.findViewById(R.id.buttonPopraw);

        btnPopraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeDetails.class));
            }
        });

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                username = dataSnapshot.child("Username").getValue().toString();
                tvEmail.setText("E-mail:  \n" + dataSnapshot.child("Email").getValue().toString());

                mDetialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvUsername.setText(username + ",  " + dataSnapshot.child("Wiek").getValue().toString());
                        tvWaga.setText("Waga:  " + dataSnapshot.child("Waga").getValue().toString());
                        tvWzrost.setText("Wzrost:  " + dataSnapshot.child("Wzrost").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return myView;
    }
}