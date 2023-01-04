package com.lavdevapp.MyCalc.presentation;

import android.os.Bundle;
import android.os.Handler;
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
    private View fragmentView;
    private CalcViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_buttons, container, false);
        model = new ViewModelProvider(requireActivity()).get(CalcViewModel.class);
        ArrayList<Button> buttons = fillButtonsArray();
        for(Button element : buttons) {
            element.setOnClickListener(createClickListener());
            if (element.getId() == R.id.removeButton) {
                element.setOnLongClickListener(createLongClickListener());
            }
        }
        return fragmentView;
    }

    private View.OnClickListener createClickListener() {
        return view1 -> {
            int id = view1.getId();
            model.onButtonPressed(id);
        };
    }

    private View.OnLongClickListener createLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!view.isPressed()) return;
                        model.onButtonPressed(view.getId());
                        handler.postDelayed(this, 100);
                    }
                };
                handler.postDelayed(runnable, 300);
                return true;
            }
        };
    }

    private ArrayList<Button> fillButtonsArray() {
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(fragmentView.findViewById(R.id.oneButton));
        buttons.add(fragmentView.findViewById(R.id.twoButton));
        buttons.add(fragmentView.findViewById(R.id.threeButton));
        buttons.add(fragmentView.findViewById(R.id.fourButton));
        buttons.add(fragmentView.findViewById(R.id.fiveButton));
        buttons.add(fragmentView.findViewById(R.id.sixButton));
        buttons.add(fragmentView.findViewById(R.id.sevenButton));
        buttons.add(fragmentView.findViewById(R.id.eightButton));
        buttons.add(fragmentView.findViewById(R.id.nineButton));
        buttons.add(fragmentView.findViewById(R.id.zeroButton));
        buttons.add(fragmentView.findViewById(R.id.plusButton));
        buttons.add(fragmentView.findViewById(R.id.minusButton));
        buttons.add(fragmentView.findViewById(R.id.multiplyButton));
        buttons.add(fragmentView.findViewById(R.id.divideButton));
        buttons.add(fragmentView.findViewById(R.id.equalsButton));
        buttons.add(fragmentView.findViewById(R.id.commaButton));
        buttons.add(fragmentView.findViewById(R.id.leftBraceButton));
        buttons.add(fragmentView.findViewById(R.id.rightBraceButton));
        buttons.add(fragmentView.findViewById(R.id.removeButton));
        return buttons;
    }
}

