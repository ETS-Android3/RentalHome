package com.example.yashui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        fab = findViewById(R.id.fab);
        new AppBarConfiguration.Builder(R.id.navigation_home, R.id.account).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseHandling handleDatabase = new DatabaseHandling();
        if (handleDatabase.getCurrentUser() == null) {
            Intent loginIntent = new Intent(MainActivity2.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user_data").child(handleDatabase.getPhoneNumber());
            if (reference != null) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user.getEmail().isEmpty() || user.getName().isEmpty())
                                startRegisterActivity();
                        }
                        else
                            startRegisterActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
    private void startRegisterActivity(){
        Intent registerIntent = new Intent(MainActivity2.this, RegisterActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }
    @Override
    public void onResume(){

        super.onResume();
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, AddDataActivity.class);
            startActivity(intent);
        });
    }
    public void onClickBtnSignOut(View view){
        mAuth.signOut();
        Intent intent = new Intent(this , MainActivity2.class);
        startActivity(intent);
        finish();
    }
    public boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork==null){
            Snackbar snackbar = Snackbar.make(fab, "No internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAnchorView(fab)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(Color.DKGRAY)
                    .setActionTextColor(Color.WHITE);
            snackbar.setAction("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            return false;
        }
        return true;
    }
}