package com.example.formpendaftaran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.example.formpendaftaran.databinding.ActivityDetailBinding;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Detail Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupActivity();
        setupListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setupActivity() {
        binding.nama.setText(getIntent().getStringExtra("name"));
        binding.alamat.setText(getIntent().getStringExtra("address"));
        binding.noHp.setText(getIntent().getStringExtra("phoneNumber"));
        binding.gender.setText(getIntent().getStringExtra("gender"));
        binding.latitude.setText(String.valueOf(getIntent().getDoubleExtra("latitude", 0.0)));
        binding.longitude.setText(String.valueOf(getIntent().getDoubleExtra("longitude", 0.0)));
        File path = new File(getApplication().getFilesDir() + "/foto/" + getIntent().getStringExtra("image"));
        binding.foto.setImageBitmap(BitmapFactory.decodeFile(path.getAbsolutePath()));
        if (!path.exists()) binding.foto.setImageResource(R.drawable.ic_baseline_account_box_24);
    }
    
    private void setupListener() {
        binding.btnUpdate.setOnClickListener(s -> {
            Toast.makeText(this, "get", Toast.LENGTH_SHORT).show();
        });
    }
}