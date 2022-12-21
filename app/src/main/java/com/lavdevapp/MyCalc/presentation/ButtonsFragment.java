package com.lavdevapp.MyCalc.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.lavdevapp.MyCalc.models.CalcViewModel;
import com.lavdevapp.MyCalc.R;
import java.util.ArrayList;

public class ButtonsFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buttons, container, false);
        CalcViewModel model = new ViewModelProvider(requireActivity()).get(CalcViewModel.class);
        View.OnClickListener clickListener = view -> {
            int id = view.getId();
            model.onButtonPressed(id);
        };
        ArrayList<Button> buttons = fillButtonsArray();
        for(Button element : buttons) {
            element.setOnClickListener(clickListener);
        }
        return view;
    }

    private ArrayList<Button> fillButtonsArray() {
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(view.findViewById(R.id.oneButton));
        buttons.add(view.findViewById(R.id.twoButton));
        buttons.add(view.findViewById(R.id.threeButton));
        buttons.add(view.findViewById(R.id.fourButton));
        buttons.add(view.findViewById(R.id.fiveButton));
        buttons.add(view.findViewById(R.id.sixButton));
        buttons.add(view.findViewById(R.id.sevenButton));
        buttons.add(view.findViewById(R.id.eightButton));
        buttons.add(view.findViewById(R.id.nineButton));
        buttons.add(view.findViewById(R.id.zeroButton));
        buttons.add(view.findViewById(R.id.plusButton));
        buttons.add(view.findViewById(R.id.minusButton));
        buttons.add(view.findViewById(R.id.multiplyButton));
        buttons.add(view.findViewById(R.id.divideButton));
        buttons.add(view.findViewById(R.id.equalsButton));
        buttons.add(view.findViewById(R.id.commaButton));
        buttons.add(view.findViewById(R.id.leftBraceButton));
        buttons.add(view.findViewById(R.id.rightBraceButton));
        buttons.add(view.findViewById(R.id.deleteButton));
        return buttons;
    }
}

