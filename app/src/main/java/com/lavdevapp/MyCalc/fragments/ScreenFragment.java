package com.lavdevapp.MyCalc.fragments;

import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
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
        CalcViewModel model = new ViewModelProvider(requireActivity()).get(CalcViewModel.class);
        TextView screen = view.findViewById(R.id.screen);
        ScrollView screenScroll = view.findViewById(R.id.screenScroll);
        model.getScreenText().observe(getViewLifecycleOwner(), text -> {
            screen.setText(text);
            screenScroll.post(() -> screenScroll.smoothScrollTo(0, screen.getBottom()));
        });
        return view;
    }
}