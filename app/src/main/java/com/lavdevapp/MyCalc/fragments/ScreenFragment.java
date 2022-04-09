package com.lavdevapp.MyCalc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lavdevapp.MyCalc.models.CalcViewModel;
import com.lavdevapp.MyCalc.R;

public class ScreenFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen, container, false);
        TextView screen = view.findViewById(R.id.screen);
        CalcViewModel model = new ViewModelProvider(requireActivity()).get(CalcViewModel.class);
        LiveData<String> screenText = model.getScreenText();
        screenText.observe(getViewLifecycleOwner(), s -> screen.setText(screenText.getValue()));
        return view;
    }
}