package com.example.formpendaftaran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
        getSupportActionBar().setTitle("List Data");
        setupListener();

        adapter = new Adapter(this, items);
        binding.listview.setAdapter(adapter);

        binding.listview.setOnItemClickListener((parent, view, position, id) -> {
            Data data = items.get(position);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("id", data.getId());
            intent.putExtra("name", data.getName());
            intent.putExtra("address", data.getAddress());
            intent.putExtra("phoneNumber", data.getPhoneNumber());
            intent.putExtra("gender", data.getGender());
            intent.putExtra("latitude", data.getLocation().getLatitude());
            intent.putExtra("longitude", data.getLocation().getLongitude());
            intent.putExtra("image", data.getImage());
            startActivity(intent);
        });
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
        this.items.addAll(db.getData());
        adapter.notifyDataSetChanged();
    }
}