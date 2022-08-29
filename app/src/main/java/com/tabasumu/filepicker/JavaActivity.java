package com.tabasumu.filepicker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.tabasumu.filepicker.databinding.ActivityJavaBinding;

public class JavaActivity extends AppCompatActivity {

    private ActivityJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // create builder
        FilePicker.Builder builder = new FilePicker.Builder(this)
                .inputType("*/*");

        binding.btnPickSingle.setOnClickListener(listener -> {
            builder.pick(items -> {
                String message = "ITEMS IS EMPTY -> " + (items.isEmpty());
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
                return null;
            });
        });

        binding.btnPickMultiple.setOnClickListener(listener -> {
            builder.pickSingle((uri, file) -> {
                String message = "ITEM IS NULL -> " + (file != null);
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
                return null;
            });

        });

    }
}