package com.lavdevapp.MyCalc.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen, container, false);
        CalcViewModel model = new ViewModelProvider(requireActivity()).get(CalcViewModel.class);
        TextView screen = view.findViewById(R.id.screen);
        ScrollView screenScroll = view.findViewById(R.id.screenScroll);
        model.getScreenText().observe(getViewLifecycleOwner(), text -> {
            SpannableString result = setColorSpans(text);
            screen.setText(result);
            screenScroll.post(() -> screenScroll.smoothScrollTo(0, screen.getBottom()));
        });
        return view;
    }

    private SpannableString setColorSpans(String text) {
        Pattern answerLinePattern = Pattern.compile("= -?[0-9]+\\.?([0-9]+)?");
        Matcher answerLineMatcher = answerLinePattern.matcher(text);
        Pattern allSingsPattern = Pattern.compile(String.format("[+\\-%s%s]", (char) 215, (char) 247));
        Matcher allSignsMatcher = allSingsPattern.matcher(text);
        SpannableString spannableString = new SpannableString(text);
        while (allSignsMatcher.find()) {
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.allSignsColor)), allSignsMatcher.start(), allSignsMatcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        while (answerLineMatcher.find()) {
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.answerLineColor)), answerLineMatcher.start(), answerLineMatcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannableString;
    }
}