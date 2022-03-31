package com.example.strangers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.strangers.R;
import com.example.strangers.databinding.ActivityMainBinding;
import com.example.strangers.models.User;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    long coins = 0;
    String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private int requestCode = 1;
    User user;
    KProgressHUD progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        progress = KProgressHUD.create(this);
        progress.setDimAmount(0.4f);
        progress.show();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        database.getReference().child("profiles")
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progress.dismiss();
                        user = snapshot.getValue(User.class);
                        coins = user.getCoins();

                        binding.coins.setText("You have: " + coins);

                        Glide.with(MainActivity.this)
                                .load(user.getProfile())
                                .into(binding.profile);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted()) {
                    if (coins > 5) {
                        coins = coins-5;
                        database.getReference().child("profiles")
                                .child(currentUser.getUid())
                                .child("coins")
                                .setValue(coins);

                        Intent intent = new Intent(MainActivity.this,connectingActivity.class);
                        intent.putExtra("profile",user.getProfile());
                        startActivity(intent);
                         // startActivity(new Intent(MainActivity.this, connectingActivity.class));
                        //  Toast.makeText(MainActivity.this, "Call Finding", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient Coins", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    askPermission();
                }
            }
        });

        binding.rewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RewardActivity.class));
            }
        });

    }

    void askPermission(){
        ActivityCompat.requestPermissions(this,permissions,requestCode);
    }

     private boolean isPermissionGranted(){
        for (String permission : permissions){
            if (ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
     }


     }