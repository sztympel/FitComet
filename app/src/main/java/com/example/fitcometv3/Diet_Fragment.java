package com.example.fitcometv3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Diet_Fragment extends Fragment {
    View myView;

    CheckBox cbSniadanie, cbObiad, cbKolacja;
    TextView tvKalorie, tvSniadanie, tvObiad, tvKolacja;

    double userKalorie;
    String wylosowano="0";

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef, mPosilkiRef, mChecksRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.diet_layout,container,false);

        cbSniadanie = myView.findViewById(R.id.checkBoxZjedzone_Sniadanie);
        cbObiad = myView.findViewById(R.id.checkBoxZjedzone_Obiad);
        cbKolacja = myView.findViewById(R.id.checkBoxZjedzone_Kolacja);
        tvSniadanie = myView.findViewById(R.id.textViewSniadanie_Posilek);
        tvObiad = myView.findViewById(R.id.textViewObiad_Posilek);
        tvKolacja = myView.findViewById(R.id.textViewKolacja_Posilek);
        tvKalorie = myView.findViewById(R.id.textViewTarget);

        mAuth = FirebaseAuth.getInstance();

        mPosilkiRef = FirebaseDatabase.getInstance().getReference().child("Posilki");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Dane");
        mChecksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Checks");

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userKalorie = dataSnapshot.child("TargetKalorie").getValue(Double.class);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChecksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("wylosowano").exists()){
                    wylosowano = dataSnapshot.child("wylosowano").getValue().toString().trim();
                    tvSniadanie.setText(wylosowano);
                    if (dataSnapshot.child("ZSniadanie").getValue().toString().trim().equals("true")) {
                        cbSniadanie.setChecked(true);
                        cbSniadanie.setEnabled(false);
                    }
                    if (dataSnapshot.child("ZObiad").getValue().toString().trim().equals("true")){
                        cbObiad.setChecked(true);
                        cbObiad.setEnabled(false);
                    }
                    if(dataSnapshot.child("ZKolacja").getValue().toString().trim().equals("true")) {
                        cbKolacja.setChecked(true);
                        cbKolacja.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mPosilkiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(wylosowano.equals("0"))
                {
                    if (userKalorie < 1950)
                    {
                        tvSniadanie.setText(dataSnapshot.child("1").child("Śniadanie").getKey().trim() + " nr.1");
                        tvObiad.setText(dataSnapshot.child("1").child("Obiad").getKey().trim() + " nr.1");
                        tvKolacja.setText(dataSnapshot.child("1").child("Kolacja").getKey().trim() + " nr.1");

                        mChecksRef.child("wylosowano").setValue("1");
                    }
                    else if(userKalorie > 3050)
                    {
                        tvSniadanie.setText(dataSnapshot.child(String.valueOf(dataSnapshot.getChildrenCount()-1)).child("Śniadanie").getKey().trim() + " nr." + String.valueOf(dataSnapshot.getChildrenCount()-1));
                        tvObiad.setText(dataSnapshot.child(String.valueOf(dataSnapshot.getChildrenCount()-1)).child("Obiad").getKey().trim() + " nr." + String.valueOf(dataSnapshot.getChildrenCount()-1));
                        tvKolacja.setText(dataSnapshot.child(String.valueOf(dataSnapshot.getChildrenCount()-1)).child("Kolacja").getKey().trim() + " nr." + String.valueOf(dataSnapshot.getChildrenCount()-1));

                        mChecksRef.child("wylosowano").setValue(String.valueOf(dataSnapshot.getChildrenCount()-1));
                    }
                    else
                    {
                        Random r = new Random();
                        while (true) {
                            int k = r.nextInt((int) dataSnapshot.getChildrenCount() - 1 + 1) + 1;

                            int kalorie = dataSnapshot.child(String.valueOf(k)).child("Suma").getValue(Integer.class);

                            if (userKalorie - kalorie <= 50 && userKalorie - kalorie >= -50) {
                                tvSniadanie.setText(dataSnapshot.child(String.valueOf(k)).child("Śniadanie").getKey().trim() + " nr." + String.valueOf(k));
                                tvObiad.setText(dataSnapshot.child(String.valueOf(k)).child("Obiad").getKey().trim() + " nr." + String.valueOf(k));
                                tvKolacja.setText(dataSnapshot.child(String.valueOf(k)).child("Kolacja").getKey().trim() + " nr." + String.valueOf(k));

                                mChecksRef.child("wylosowano").setValue(String.valueOf(k));
                                break;
                            }
                        }
                    }
                }
                else
                {
                    tvSniadanie.setText(dataSnapshot.child(wylosowano).child("Śniadanie").getKey().trim() + " nr." + wylosowano);
                    tvObiad.setText(dataSnapshot.child(wylosowano).child("Obiad").getKey().trim() + " nr." + wylosowano);
                    tvKolacja.setText(dataSnapshot.child(wylosowano).child("Kolacja").getKey().trim() + " nr." + wylosowano);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cbSniadanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbSniadanie.setEnabled(false);
                mChecksRef.child("ZSniadanie").setValue("true");
            }
        });

        cbObiad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbObiad.setEnabled(false);
                mChecksRef.child("ZObiad").setValue("true");
            }
        });

        cbKolacja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbKolacja.setEnabled(false);
                mChecksRef.child("ZKolacja").setValue("true");
            }
        });

        return myView;
    }
}
