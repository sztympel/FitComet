package com.example.fitcometv3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class Stopwatch_Fragment extends Fragment {
    View myView;

    Button btnStart, btnStop, btnReset;

    private int milisecs = 0;
    private boolean running;
    private boolean wasRunning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_stopwatch,container,false);

        btnStart = myView.findViewById(R.id.start_button);
        btnStop = myView.findViewById(R.id.stop_button);
        btnReset = myView.findViewById(R.id.reset_button);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                milisecs = 0;
            }
        });

        if (savedInstanceState != null) {
            milisecs = savedInstanceState.getInt("milisecs");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTimer();
        return myView;
    }

    private void runTimer() {
        final TextView timeView = (TextView) myView.findViewById(R.id.time_view);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (milisecs % 216000) / 3600;
                int secs = (milisecs % 3600) / 60;
                int milisec = milisecs % 60;
                String time = String.format("%02d:%02d:%02d", minutes, secs, milisec);
                timeView.setText(time);
                if (running) {
                    milisecs++;
                }
                handler.postDelayed(this, 1);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("milisecs", milisecs);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    public void onStop() {
        super.onStop();
        wasRunning = running;
        running = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (wasRunning) {
            running = true;
        }
    }
}