package com.yeonfish.bigbrother.ui.ringing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yeonfish.bigbrother.databinding.FragmentRingingBinding;
import com.yeonfish.bigbrother.databinding.FragmentUpdateposBinding;

public class RingingFragment extends Fragment {

    private FragmentRingingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RingingViewModel ringingViewModel =
                new ViewModelProvider(this).get(RingingViewModel.class);

        binding = FragmentRingingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textRinging;
        ringingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}