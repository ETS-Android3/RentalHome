package com.example.yashui.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.yashui.R;
import com.example.yashui.fragment.GridFragment;
public class HomeFragment extends Fragment {
    public static  int currentPosition;
    private  static final String KEY_CURRENT_POSITION = "com.example.hii.currentPosition";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        if(savedInstanceState != null){
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION,0);
        }
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container,new GridFragment(),GridFragment.class.getSimpleName())
                .commit();
        return root;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }
}
