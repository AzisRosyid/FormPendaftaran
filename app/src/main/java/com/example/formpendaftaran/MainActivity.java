package com.example.formpendaftaran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.formpendaftaran.adapter.Adapter;
import com.example.formpendaftaran.databinding.ActivityMainBinding;
import com.example.formpendaftaran.databinding.ActivityRegisterBinding;
import com.example.formpendaftaran.db.DbHelper;
import com.example.formpendaftaran.model.Data;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Adapter adapter;
    private List<Data> items = new ArrayList<>();
    DbHelper db = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupListener();

        adapter = new Adapter(this, new ArrayList<>());
        binding.listview.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void setupListener() {
        binding.btnAdd.setOnClickListener(s -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void getData() {
        items.clear();
        this.items = db.getData();
        adapter.notifyDataSetChanged();
    }
}