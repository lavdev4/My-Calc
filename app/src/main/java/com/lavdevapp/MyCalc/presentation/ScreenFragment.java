package com.lavdevapp.MyCalc.presentation;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lavdevapp.MyCalc.models.CalcViewModel;
import com.lavdevapp.MyCalc.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenFragment extends Fragment {
    private static final String ANSWER_REGEX = "= -?[0-9]+\\.?([0-9]+)?";
    private static final String SIGNS_REGEX = String.format("[+\\-%s%s]", (char) 215, (char) 247);
//    private final String BRACES_REGEX = "[()]";

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
        SpannableString spannableString = new SpannableString(text);
        Pattern answerLinePattern = Pattern.compile(ANSWER_REGEX);
        Matcher answerLineMatcher = answerLinePattern.matcher(text);
        while (answerLineMatcher.find()) {
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.answerLineColor)), answerLineMatcher.start(), answerLineMatcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        Pattern allSingsPattern = Pattern.compile(SIGNS_REGEX);
        Matcher allSignsMatcher = allSingsPattern.matcher(text);
        while (allSignsMatcher.find()) {
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.signsColor)), allSignsMatcher.start(), allSignsMatcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
//        Pattern bracesPattern = Pattern.compile(bracesRegex);
//        Matcher bracesMatcher = bracesPattern.matcher(text);
//        while (bracesMatcher.find()) {
//            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bracesColor)), bracesMatcher.start(), bracesMatcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        }
        return spannableString;
    }
}