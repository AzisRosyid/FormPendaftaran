package com.example.formpendaftaran;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.formpendaftaran.databinding.ActivityRegisterBinding;
import com.example.formpendaftaran.db.DbHelper;
import com.example.formpendaftaran.model.Location;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private Uri selectedImage;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String[]> rPermission;
    private List<String> permissionRequest = new ArrayList<>();
    private Location location;
    private String gender;
    private LocationRequest locationRequest;
    DbHelper db = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupListener();
        requestPermission();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setupListener() {
        binding.btnFoto.setOnClickListener(s -> openImageLauncher.launch("image/*"));
        binding.btnSubmit.setOnClickListener(s -> {
            boolean valid = true;
            if (binding.nama.getText().toString().equals("")) {
                binding.tlName.setHelperText("Required*");
                valid = false;
            } else binding.tlName.setHelperText("");
            if (binding.alamat.getText().toString().equals("")) {
                binding.tlAlamat.setHelperText("Required*");
                valid = false;
            } else binding.tlAlamat.setHelperText("");
            if (binding.noHp.getText().toString().equals("")) {
                binding.tlNoHp.setHelperText("Required*");
                valid = false;
            } else binding.tlNoHp.setHelperText("");
            if (gender == null) {
                binding.tvKelamin.setText("Jenis Kelamin*");
                binding.tvKelamin.setTextColor(Color.parseColor("#E61E1E"));
                valid = false;
            } else {
                binding.tvKelamin.setText("Jenis Kelamin");
                binding.tvKelamin.setTextColor(getResources().getColor(R.color.black));
            }
            if (location == null) {
                binding.tvLokasi.setText("Lokasi Pendaftaran*");
                binding.tvLokasi.setTextColor(Color.parseColor("#E61E1E"));
                valid = false;
            } else {
                binding.tvLokasi.setText("Lokasi Pendaftaran");
                binding.tvLokasi.setTextColor(getResources().getColor(R.color.black));
            }
            if (selectedImage == null) {
                binding.tvFoto.setText("Upload Foto*");
                binding.tvFoto.setTextColor(Color.parseColor("#E61E1E"));
                valid = false;
            } else {
                binding.tvFoto.setText("Upload Foto");
                binding.tvFoto.setTextColor(getResources().getColor(R.color.black));
            }
            if (!valid) return;
            String fileName = System.currentTimeMillis() + "_" + getFileName(selectedImage);
            db.insert(binding.nama.getText().toString(), binding.alamat.getText().toString(), binding.noHp.getText().toString(), gender, location, fileName);
            savefile(selectedImage, fileName);
            this.finish();
        });
        binding.jenisKelamin.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.pria:
                    gender = binding.pria.getText().toString();
                    break;
                case R.id.wanita:
                    gender = binding.wanita.getText().toString();
                    break;
                default:
                    gender = null;
                    break;
            }
            binding.tvKelamin.setText("Jenis Kelamin");
            binding.tvKelamin.setTextColor(getResources().getColor(R.color.black));
        });
        binding.btnLokasi.setOnClickListener(s -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 100);
                binding.statusLokasi.setText("Get Location Failure!");
                binding.statusLokasi.setTextColor(Color.parseColor("#E61E1E"));
                return;
            }

            binding.statusLokasi.setText("Get Location Progress...");
            binding.statusLokasi.setTextColor(getResources().getColor(R.color.black));

            turnOnGPS();
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(RegisterActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int index = locationResult.getLocations().size() - 1;
                        RegisterActivity.this.location = new Location(locationResult.getLocations().get(index).getLatitude(), locationResult.getLocations().get(index).getLongitude());
                        binding.statusLokasi.setText("Get Location Success!");
                        binding.statusLokasi.setTextColor(Color.parseColor("#43B500"));
                        binding.tvLokasi.setText("Lokasi Pendaftaran");
                        binding.tvLokasi.setTextColor(getResources().getColor(R.color.black));
                    }
                }
            }, Looper.getMainLooper());

//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
//                if (location != null) {
//                    this.location = new Location(location.getLatitude(), location.getLongitude());
//                    binding.statusLokasi.setText("Get Location Success!");
//                    binding.statusLokasi.setTextColor(Color.parseColor("#43B500"));
//                } else {
//                    binding.statusLokasi.setText("Get Location Failure!");
//                    binding.statusLokasi.setTextColor(Color.parseColor("#E61E1E"));
//                }
//            });
        });
        binding.nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tlName.setHelperText("");
            }
        });
        binding.alamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tlAlamat.setHelperText("");
            }
        });
        binding.noHp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tlNoHp.setHelperText("");
            }
        });
    }

    ActivityResultLauncher<String> openImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        selectedImage = result;
        binding.foto.setImageURI(result);
        binding.tvFoto.setText("Upload Foto");
        binding.tvFoto.setTextColor(getResources().getColor(R.color.black));
    });

    private void savefile(Uri sourceuri, String fileName) {
        String sourceFilename= sourceuri.getPath();
        String destinationFilename = getApplication().getFilesDir()+"/foto/"+ fileName;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileName(Uri uri){
        String path = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor.moveToFirst()) path = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        return path;
    }

    private void turnOnGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                            resolvableApiException.startResolutionForResult(this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void requestPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocationGranted = false;
            boolean coarseLocationGranted = false;
            boolean backgroundLocationGranted = false;
            if(result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) result.get(Manifest.permission.ACCESS_FINE_LOCATION);
            if(result.get(Manifest.permission.ACCESS_COARSE_LOCATION) != null) result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
            if(result.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != null) result.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (!fineLocationGranted) {
                permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!coarseLocationGranted) {
                permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (!backgroundLocationGranted) {
                permissionRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        });

        if(!permissionRequest.isEmpty()) locationPermissionRequest.launch(permissionRequest.toArray(new String[0]));
    }
}