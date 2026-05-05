package com.example.realmonini;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, new HomeFragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_journal) {
                selectedFragment = new JournalFragment();
            } else if (itemId == R.id.bottom_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.bottom_chart) {
                selectedFragment = new ChartFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, selectedFragment)
                    .commit();
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}