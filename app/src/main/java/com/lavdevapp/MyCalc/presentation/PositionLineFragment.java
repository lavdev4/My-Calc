package com.lavdevapp.MyCalc.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.lavdevapp.MyCalc.models.CalcViewModel;
import com.lavdevapp.MyCalc.R;

public class PositionLineFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer_line, container, false);
        TextView screen = view.findViewById(R.id.positionScreen);
        CalcViewModel model = new ViewModelProvider(requireActivity()).get(CalcViewModel.class);
        LiveData<String> screenText = model.getPositionScreenText();
        screenText.observe(getViewLifecycleOwner(), s -> screen.setText(screenText.getValue()));
        View.OnClickListener clickListener = view1 -> {
            int id = view1.getId();
            model.onButtonPressed(id);
        };
        view.findViewById(R.id.clearButton).setOnClickListener(clickListener);
        return view;
    }
}