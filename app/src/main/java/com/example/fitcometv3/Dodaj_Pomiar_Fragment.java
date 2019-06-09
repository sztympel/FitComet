package com.example.fitcometv3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Dodaj_Pomiar_Fragment extends Fragment{
    View myView;
    EditText talia1, biceps1, chest1, biodro1, udo1, lydka1;
    Button btnDodaj, btnAnuluj;

    String talia1S, biceps1S, chest1S, biodro1S, udo1S, lydka1S;

    FirebaseAuth mAuth;
    DatabaseReference mPomiaryRef;

    //
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Zapis.editDodaj = true;
        myView = inflater.inflate(R.layout.dodaj_pomiar_layout, container, false);
        talia1 = myView.findViewById(R.id.taliET);
        biceps1 = myView.findViewById(R.id.bicepsET);
        chest1 = myView.findViewById(R.id.chestET);
        biodro1 = myView.findViewById(R.id.biodraET);
        udo1 = myView.findViewById(R.id.udaET);
        lydka1 = myView.findViewById(R.id.lydkET);

        btnDodaj = myView.findViewById(R.id.ButtonDodaj_Pomiar);
        btnAnuluj = myView.findViewById(R.id.ButtonAnulujPomiar);

        mAuth = FirebaseAuth.getInstance();
        mPomiaryRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Pomiary");

        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talia1S = talia1.getText().toString();
                biceps1S = biceps1.getText().toString();
                chest1S = chest1.getText().toString();
                biodro1S = biodro1.getText().toString();
                udo1S = udo1.getText().toString();
                lydka1S = lydka1.getText().toString();

                if(!talia1S.isEmpty() && !biceps1S.isEmpty() && !chest1S.isEmpty() &&
                        !biodro1S.isEmpty() && !udo1S.isEmpty() && !lydka1S.isEmpty())
                {
                    if(     Integer.valueOf(chest1S)>50 && Integer.valueOf(chest1S)<=200 &&
                            Integer.valueOf(biceps1S)>10 && Integer.valueOf(biceps1S)<=60 &&
                            Integer.valueOf(talia1S)>30 && Integer.valueOf(talia1S)<=150 &&
                            Integer.valueOf(biodro1S)>50 && Integer.valueOf(biodro1S)<=200 &&
                            Integer.valueOf(udo1S)>30 && Integer.valueOf(udo1S)<=120 &&
                            Integer.valueOf(lydka1S)>20 && Integer.valueOf(lydka1S)<=70)
                    {

                        mPomiaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mPomiaryRef.child("P_KlatkaPiersiowa").setValue(dataSnapshot.child("N_KlatkaPiersiowa").getValue().toString().trim());
                                mPomiaryRef.child("P_Biceps").setValue(dataSnapshot.child("N_Biceps").getValue().toString().trim());
                                mPomiaryRef.child("P_Talia").setValue(dataSnapshot.child("N_Talia").getValue().toString().trim());
                                mPomiaryRef.child("P_Pas").setValue(dataSnapshot.child("N_Pas").getValue().toString().trim());
                                mPomiaryRef.child("P_Udo").setValue(dataSnapshot.child("N_Udo").getValue().toString().trim());
                                mPomiaryRef.child("P_Lydka").setValue(dataSnapshot.child("N_Lydka").getValue().toString().trim());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mPomiaryRef.child("N_KlatkaPiersiowa").setValue(String.valueOf(chest1S));
                                mPomiaryRef.child("N_Biceps").setValue(String.valueOf(biceps1S));
                                mPomiaryRef.child("N_Talia").setValue(String.valueOf(talia1S));
                                mPomiaryRef.child("N_Pas").setValue(String.valueOf(biodro1S));
                                mPomiaryRef.child("N_Udo").setValue(String.valueOf(udo1S));
                                mPomiaryRef.child("N_Lydka").setValue(String.valueOf(lydka1S));
                            }
                        }, 1000);


                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Popraw swoje dane !", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Uzupełnij brakujące pola !", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnAnuluj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Pomiar_Fragment()).commit();
            }
        });



        return myView;
    }
}