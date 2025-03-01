package com.example.greenconnect.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.greenconnect.R;
import com.example.greenconnect.databinding.FragmentHomeBinding;
import com.example.greenconnect.edit_profile;
import com.example.greenconnect.first;
import com.example.greenconnect.profile;
import com.example.greenconnect.ui.news.NewsFragment;
import com.example.greenconnect.ui.news.NewsViewModel;


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
        binding.rectangle6.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.popBackStack();  // Clears previous fragments
            navController.navigate(R.id.navigation_chat);
        });
        binding.rectangle14.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.popBackStack();  // Clears previous fragments
            navController.navigate(R.id.navigation_market);
        });
        binding.rectangle13.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.popBackStack();  // Clears previous fragments
            navController.navigate(R.id.navigation_news);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }
}
