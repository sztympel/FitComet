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

    int userWiek, userWaga, userWzrost;
    double userKalorie, userPoziomAktywnosci;
    String wylosowano;

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
                if(dataSnapshot.child("Plec").getValue().toString().equals("mezczyzna"))
                {
                    userWaga = dataSnapshot.child("Waga").getValue(Integer.class);
                    userWzrost = dataSnapshot.child("Wzrost").getValue(Integer.class);
                    userWiek = dataSnapshot.child("Wiek").getValue(Integer.class);
                    userPoziomAktywnosci = dataSnapshot.child("PoziomAktywnosci").getValue(Double.class);

                    userKalorie = (66.5 + (13.7 * userWaga) + (5 * userWzrost) - (6.8 * userWiek)) * userPoziomAktywnosci;
                    tvKalorie.setText(String.format("%.0f", userKalorie));
                    mDatabaseRef.child("TargetKalorie").setValue(userKalorie);
                }
                else
                {
                    userWaga = dataSnapshot.child("Waga").getValue(Integer.class);
                    userWzrost = dataSnapshot.child("Wzrost").getValue(Integer.class);
                    userWiek = dataSnapshot.child("Wiek").getValue(Integer.class);
                    userPoziomAktywnosci = dataSnapshot.child("PoziomAktywnosci").getValue(Double.class);

                    userKalorie = (655 + (9.6 * userWaga) + (1.85 * userWzrost) - (4.7 * userWiek)) * userPoziomAktywnosci;
                    tvKalorie.setText(String.format("%.0f", userKalorie));
                    mDatabaseRef.child("TargetKalorie").setValue(userKalorie);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChecksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wylosowano = dataSnapshot.child("wylosowano").getValue().toString().trim();
                tvSniadanie.setText(wylosowano);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mPosilkiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //userKalorie jest widoczne, dieta jako zestawy co 100 kalorii, <2000, 3000>, łapiące przedział 50
                //ewentualnie zamiana z posilkiem z innego zestawu, ale tego samego poziomu kalorii
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
