package com.example.nav.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nav.R;
import com.example.nav.databinding.FragmentHomeBinding;
import com.example.nav.first;
import com.example.nav.profile;
import com.example.nav.ui.chat.ChatFragment;


public class HomeFragment extends Fragment {
    
    private FragmentHomeBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.weatherCon;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        // ✅ Initialize goprofile inside onCreateView, not onDestroyView
        binding.imageView7.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), profile.class));
        });





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }
}
