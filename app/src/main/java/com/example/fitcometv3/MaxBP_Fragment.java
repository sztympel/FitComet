package com.example.fitcometv3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MaxBP_Fragment extends Fragment {
    View myView;
    EditText etIle;
    Button btnPush;
    ListView listBP;
    String ile;
    int j=0;

    FirebaseAuth mAuth;
    DatabaseReference mMaxRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.maxbp_layout,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        etIle = myView.findViewById(R.id.editTextWycisniete);
        btnPush = myView.findViewById(R.id.buttonPush);
        listBP = myView.findViewById(R.id.listBP);

        mAuth = FirebaseAuth.getInstance();
        mMaxRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Max").child("MaxBP");

        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ile = etIle.getText().toString().trim();
                mMaxRef.child(String.valueOf(j)).child(ile);
                if(!ile.equals("") || !ile.equals("0"))
                {
                    mMaxRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            j = (int)dataSnapshot.getChildrenCount();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mMaxRef.child(String.valueOf(j)).child(ile);
                }
                else
                {
                    Toast.makeText(getActivity(), "Wprowadź wartość !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return myView;
    }
}
