package com.example.yashui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yashui.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

public class ContactUs extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us,container,false);
        setHasOptionsMenu(true);
        MaterialToolbar registerToolBar = view.findViewById(R.id.contact_us);
        ((AppCompatActivity) getActivity()).setSupportActionBar(registerToolBar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            getParentFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
