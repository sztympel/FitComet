package com.example.fitcometv3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public boolean kgTrue = true;
    int kalorieSniadanie, kalorieObiad, kalorieKolacja;
    String wylosowano;

    int userWiek, userWaga, userWzrost;
    double userKalorie, userPoziomAktywnosci;

    FirebaseAuth mAuth;

    DatabaseReference mDatabaseRef, mPosilkiRef, mChecksRef;

    TextView tvZapotrzebowanie;

    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Zapis.editDodaj = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        tvZapotrzebowanie = findViewById(R.id.zapotrzebowanietxt);

        circularProgressBar = findViewById(R.id.circularProgressBar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                    tvZapotrzebowanie.setText(String.format("%.0f", userKalorie));
                    mDatabaseRef.child("TargetKalorie").setValue(userKalorie);
                    circularProgressBar.setProgressMax((float)userKalorie);
                }
                else
                {
                    userWaga = dataSnapshot.child("Waga").getValue(Integer.class);
                    userWzrost = dataSnapshot.child("Wzrost").getValue(Integer.class);
                    userWiek = dataSnapshot.child("Wiek").getValue(Integer.class);
                    userPoziomAktywnosci = dataSnapshot.child("PoziomAktywnosci").getValue(Double.class);

                    userKalorie = (655 + (9.6 * userWaga) + (1.85 * userWzrost) - (4.7 * userWiek)) * userPoziomAktywnosci;
                    tvZapotrzebowanie.setText(String.format("%.0f", userKalorie));
                    mDatabaseRef.child("TargetKalorie").setValue(userKalorie);
                    circularProgressBar.setProgressMax((float)userKalorie);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChecksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("wylosowano").exists()) {
                    wylosowano = dataSnapshot.child("wylosowano").getValue().toString().trim();
                }
                else
                {
                    wylosowano = "0";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mPosilkiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!wylosowano.equals("0"))
                {
                    kalorieSniadanie = dataSnapshot.child(String.valueOf(wylosowano)).child("Śniadanie").getValue(Integer.class);
                    kalorieObiad = dataSnapshot.child(String.valueOf(wylosowano)).child("Obiad").getValue(Integer.class);
                    kalorieKolacja = dataSnapshot.child(String.valueOf(wylosowano)).child("Kolacja").getValue(Integer.class);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mChecksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child("ZSniadanie").getValue().toString().equals("true"))
                                    {
                                        circularProgressBar.setProgress(circularProgressBar.getProgress() + kalorieSniadanie);
                                    }
                                    if(dataSnapshot.child("ZObiad").getValue().toString().equals("true"))
                                    {
                                        circularProgressBar.setProgress(circularProgressBar.getProgress() + kalorieObiad);
                                    }
                                    if(dataSnapshot.child("ZKolacja").getValue().toString().equals("true"))
                                    {
                                        circularProgressBar.setProgress(circularProgressBar.getProgress() + kalorieKolacja);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishActivity(0);
        }
    }

    public interface OnBackPressedListner{
        boolean onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_profil) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Profil_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_pomiar) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Pomiar_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_message) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Message_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_setting) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Settings_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_stopwatch) {
            Intent intent = new Intent(this, StopwatchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gymplan) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Gymplan_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_maxweight) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Gymmax_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_diet) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Diet_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_dietpor) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.DietPor_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_dietbook) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Dietbook_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_test) {

        } else if (id == R.id.nav_calculator) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new com.example.fitcometv3.Calculator_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_glowna) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void WagaButtonLicz(View v) {
        switch (v.getId()) {
            case R.id.lbs:
                kgTrue = false;
                break;
            case R.id.kg:
                kgTrue = true;
                break;

        }
    }

    public void onClickPrzelicz(View view) {
        final TextView tv1 = findViewById(R.id.textViewWyswietlone);
        final EditText eText = findViewById(R.id.editTextWpisane);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (eText.length() == 0 || eText.equals("") || eText == null) {
            tv1.setText("Podaj liczbę !");
        } else {
            int wagaprzelicz = Integer.parseInt(eText.getText().toString());

            if (kgTrue == true) {
                double wynik = wagaprzelicz * 0.45359237;
                String FinalWynik = decimalFormat.format(wynik);
                tv1.setText(FinalWynik);
            } else {
                double wynik = wagaprzelicz / 0.45359237;
                String FinalWynik = decimalFormat.format(wynik);
                tv1.setText(FinalWynik);
            }
        }
    }
}
