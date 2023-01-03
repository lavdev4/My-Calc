package com.lavdevapp.MyCalc.presentation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lavdevapp.MyCalc.R;
import com.lavdevapp.MyCalc.models.CalcViewModel;

public class MainActivity extends AppCompatActivity {
    private CalcViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(CalcViewModel.class);
    }

    @Override
    protected void onPause() {
        if (!isChangingConfigurations()) viewModel.saveData();
        super.onPause();
    }
}