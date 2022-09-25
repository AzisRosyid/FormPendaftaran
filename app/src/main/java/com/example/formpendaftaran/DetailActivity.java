package com.example.formpendaftaran;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.example.formpendaftaran.databinding.ActivityDetailBinding;
import com.example.formpendaftaran.db.DbHelper;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    DbHelper db = new DbHelper(this);

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
        binding.kelamin.setText(getIntent().getStringExtra("gender"));
        binding.latitude.setText(String.valueOf(getIntent().getDoubleExtra("latitude", 0.0)));
        binding.longitude.setText(String.valueOf(getIntent().getDoubleExtra("longitude", 0.0)));
        File path = new File(getApplication().getFilesDir() + "/foto/" + getIntent().getStringExtra("image"));
        binding.foto.setImageBitmap(BitmapFactory.decodeFile(path.getAbsolutePath()));
        if (!path.exists()) binding.foto.setImageResource(R.drawable.ic_baseline_account_box_24);
    }
    
    private void setupListener() {
        binding.btnUpdate.setOnClickListener(s -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("id", getIntent().getIntExtra("id", 0));
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("address", getIntent().getStringExtra("address"));
            intent.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
            intent.putExtra("gender", getIntent().getStringExtra("gender"));
            intent.putExtra("latitude", getIntent().getDoubleExtra("latitude", 0));
            intent.putExtra("longitude", getIntent().getDoubleExtra("longitude", 0));
            intent.putExtra("image", getIntent().getStringExtra("image"));
            startActivity(intent);
        });
        binding.btnDelete.setOnClickListener(s -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi?")
                    .setMessage("Apakah kamu yakin menghapus data ini?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.delete(getIntent().getIntExtra("id", 0));
                        this.finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {

                    }).show();
        });
    }
}