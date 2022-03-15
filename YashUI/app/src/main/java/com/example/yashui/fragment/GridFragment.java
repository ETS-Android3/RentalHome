package com.example.yashui.fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yashui.DatabaseHandling;
import com.example.yashui.Home;
import com.example.yashui.HomeData;
import com.example.yashui.MainActivity2;
import com.example.yashui.R;
import com.example.yashui.User;
import com.example.yashui.adapter.GridAdapter;
import com.example.yashui.ui.home.HomeFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class GridFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<HomeData> list;
    private DatabaseReference ref;
    private CircularProgressIndicator circularProgressIndicator;
    private GridAdapter adapter;
    private SearchView searchView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_grid,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        circularProgressIndicator = view.findViewById(R.id.circular_progressbar);
        searchView = view.findViewById(R.id.searchView);
        int resId = getResources().getIdentifier("android:id/search_src_text",null,null);
        EditText searchEditText = (EditText)searchView.findViewById(resId);
        searchEditText.setTextColor(Color.WHITE);
        ref = FirebaseDatabase.getInstance().getReference().child("home_data");
        adapter = new GridAdapter(this,circularProgressIndicator);
        prepareTransitions();
        postponeEnterTransition();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if(ref!=null) {
            if (((MainActivity2) requireActivity()).checkInternet()) {
                circularProgressIndicator.setVisibility(View.VISIBLE);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list = new ArrayList<>();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                HomeData homeData = ds.getValue(HomeData.class);
                                Home home = ds.child("home").getValue(Home.class);
                                if (home!=null)
                                    homeData.setHome(home);
                                list.add(homeData);
                            }
                            adapter.setList(list);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }
    private  void scrollToPosition(){
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(HomeFragment.currentPosition);
                if(viewAtPosition == null||layoutManager.isViewPartiallyVisible(viewAtPosition,false,true)){
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(HomeFragment.currentPosition);
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(list!=null)
                        search(newText);
                    return true;
                }
            });
        }
    }
    private void search(String str){
        ArrayList<HomeData> myList = new ArrayList<>();
        for (HomeData user:list){
            if (user.getHome().getCity().toLowerCase().contains(str.toLowerCase())){
                myList.add(user);
            }
        }
        GridAdapter recyclerAdapter = new GridAdapter(this,circularProgressIndicator);
        recyclerAdapter.setList(myList);
        recyclerView.setAdapter(recyclerAdapter);
    }
    private void prepareTransitions(){
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.grid_exit_transition));
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                RecyclerView.ViewHolder selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(HomeFragment.currentPosition);
                if(selectedViewHolder == null){
                    return;
                }
                Log.d("DO",HomeFragment.currentPosition +"...."+names.get(0));
                sharedElements.put(names.get(0),selectedViewHolder.itemView.findViewById(R.id.card_image));
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getActivity().startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + new DatabaseHandling().getPhoneNumber())));
            }
        }
    }
}