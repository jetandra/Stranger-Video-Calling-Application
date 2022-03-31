package com.example.strangers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.strangers.databinding.ActivityConnectingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class connectingActivity extends AppCompatActivity {

    ActivityConnectingBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    boolean isOky = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        String profile = getIntent().getStringExtra("profile");
        Glide.with(this)
                .load(profile)
                .into(binding.profile);

        String username = auth.getUid();
        database.getReference().child("users")
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0){
                            isOky = true;
                            //ROOM AVAILABLE
                            for (DataSnapshot childSnap : snapshot.getChildren()){
                                database.getReference()
                                        .child("users")
                                        .child(childSnap.getKey())
                                        .child("incoming")
                                        .setValue(username);
                                database.getReference()
                                        .child("users")
                                        .child(childSnap.getKey())
                                        .child("status")
                                        .setValue(1);
                                Intent intent = new Intent(connectingActivity.this,CallActivity.class);
                                String incoming = childSnap.child("incoming").getValue(String.class);
                                String createdBy = childSnap.child("createdBy").getValue(String.class);
                                boolean isAvailable = childSnap.child("isAvailable").getValue(Boolean.class);
                                intent.putExtra("username",username);
                                intent.putExtra("incoming",incoming);
                                intent.putExtra("createdBy",createdBy);
                                intent.putExtra("isAvailable",isAvailable);
                                startActivity(intent);
                                finish();

                            }
                        }else {
                            //ROOM NOR AVAILABLE

                            HashMap<String,Object> room = new HashMap<>();
                            room.put("incoming",username);
                            room.put("createdBy",username);
                            room.put("isAvailable",true);
                            room.put("status",0);

                            database.getReference()
                                    .child("user")
                                    .child(username)
                                    .setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference()
                                            .child("user")
                                            .child(username).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child("status").exists()){
                                                if (snapshot.child("status").getValue(Integer.class)==1){
                                                    if (isOky)
                                                        return;

                                                    isOky = true;

                                                    Intent intent = new Intent(connectingActivity.this,CallActivity.class);
                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                    boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);
                                                    intent.putExtra("username",username);
                                                    intent.putExtra("incoming",incoming);
                                                    intent.putExtra("createdBy",createdBy);
                                                    intent.putExtra("isAvailable",isAvailable);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
