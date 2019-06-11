package com.example.fitcometv3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class Pomiar_Fragment extends Fragment {
    View myView;
    Button btnDodajPomiar;
    TextView tvPomiarTalia1, tvPomiarBiceps1, tvPomiarChest1,
             tvPomiarBiodra1, tvPomiarUdo1, tvPomiarLydka1,
             tvPomiarTalia2, tvPomiarBiceps2, tvPomiarChest2,
             tvPomiarBiodra2, tvPomiarUdo2, tvPomiarLydka2,
             tvTkankaTluszczowa1, tvTkankaTluszczowa2;

    String waga, talia1, talia2, FinalWynik;
    double a,b,c,d,e,WynikProcent;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef, mPomiaryRef;

    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.pomiar_layout, container, false);

        btnDodajPomiar = myView.findViewById(R.id.ButtonDodaj_Pomiar);

        //ostatnio
        tvPomiarTalia1 = myView.findViewById(R.id.talia1TV);
        tvPomiarBiceps1 = myView.findViewById(R.id.biceps1TV);
        tvPomiarChest1 = myView.findViewById(R.id.chest1TV);
        tvPomiarBiodra1 = myView.findViewById(R.id.biodraTV);
        tvPomiarUdo1 = myView.findViewById(R.id.udo1TV);
        tvPomiarLydka1 = myView.findViewById(R.id.lydka1TV);
        tvTkankaTluszczowa1 = myView.findViewById(R.id.tkanka1TV);

        //aktualnie
        tvPomiarTalia2 = myView.findViewById(R.id.talia2TV);
        tvPomiarBiceps2 = myView.findViewById(R.id.biceps2TV);
        tvPomiarChest2 = myView.findViewById(R.id.chest2TV);
        tvPomiarBiodra2 = myView.findViewById(R.id.biodra2TV);
        tvPomiarUdo2 = myView.findViewById(R.id.udo2TV);
        tvPomiarLydka2 = myView.findViewById(R.id.lydka2TV);
        tvTkankaTluszczowa2 = myView.findViewById(R.id.tkanka2TV);

        mAuth = FirebaseAuth.getInstance();
        mPomiaryRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Pomiary");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Dane");

        btnDodajPomiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Dodaj_Pomiar_Fragment()).commit();
            }
        });

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                waga = dataSnapshot.child("Waga").getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mPomiaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("P_Biceps").exists() && dataSnapshot.child("N_Biceps").exists())
                {

                    tvPomiarBiceps1.setText(dataSnapshot.child("P_Biceps").getValue().toString().trim());
                    tvPomiarBiodra1.setText(dataSnapshot.child("P_Pas").getValue().toString().trim());
                    tvPomiarChest1.setText(dataSnapshot.child("P_KlatkaPiersiowa").getValue().toString().trim());
                    tvPomiarLydka1.setText(dataSnapshot.child("P_Lydka").getValue().toString().trim());
                    talia1 = dataSnapshot.child("P_Talia").getValue().toString().trim();
                    tvPomiarTalia1.setText(talia1);
                    tvPomiarUdo1.setText(dataSnapshot.child("P_Udo").getValue().toString().trim());

                    if(Integer.valueOf(talia1)==0)
                    {
                        tvTkankaTluszczowa1.setText("");
                    }
                    else
                    {
                        a = (4.15 * (Integer.valueOf(talia1)));
                        b = a / 2.54;
                        c = 0.082 * (Integer.valueOf(waga)) * 2.2;
                        d = b - c - 98.42;
                        e = (Integer.valueOf(waga)) * 2.2;
                        WynikProcent = d / e * 100;
                        FinalWynik = decimalFormat.format(WynikProcent);

                        tvTkankaTluszczowa1.setText(FinalWynik);
                    }

                    tvPomiarBiceps2.setText(dataSnapshot.child("N_Biceps").getValue().toString().trim());
                    tvPomiarBiodra2.setText(dataSnapshot.child("N_Pas").getValue().toString().trim());
                    tvPomiarChest2.setText(dataSnapshot.child("N_KlatkaPiersiowa").getValue().toString().trim());
                    tvPomiarLydka2.setText(dataSnapshot.child("N_Lydka").getValue().toString().trim());
                    talia2 = dataSnapshot.child("N_Talia").getValue().toString().trim();
                    tvPomiarTalia2.setText(talia2);
                    tvPomiarUdo2.setText(dataSnapshot.child("N_Udo").getValue().toString().trim());

                    a = (4.15 * (Integer.valueOf(talia2)));
                    b = a / 2.54;
                    c = 0.082 * (Integer.valueOf(waga)) * 2.2;
                    d = b - c - 98.42;
                    e = (Integer.valueOf(waga)) * 2.2;
                    WynikProcent = d / e * 100;
                    FinalWynik = decimalFormat.format(WynikProcent);

                    tvTkankaTluszczowa2.setText(FinalWynik);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return myView;
    }


}