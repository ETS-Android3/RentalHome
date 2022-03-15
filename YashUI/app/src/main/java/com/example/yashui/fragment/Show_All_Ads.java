package com.example.yashui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yashui.HomeData;
import com.example.yashui.R;
import com.example.yashui.adapter.AdsAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
public class Show_All_Ads extends Fragment {
    private final HashMap<String,String> homeIds;
    private ArrayList<HomeData> homeData;
    private DatabaseReference ref;
    private RecyclerView recyclerView;
    private AdsAdapter adapter;
    public Show_All_Ads(HashMap<String ,String> homeIds){
        this.homeIds = homeIds;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(homeIds!=null) {
            view = inflater.inflate(R.layout.user_ads, container, false);
            homeData = new ArrayList<>();
            ArrayList<String> homeIdsList = new ArrayList<>(homeIds.values());
            ref = FirebaseDatabase.getInstance().getReference().child("home_data");
            for (int i = 0; i < homeIds.size(); i++) {
                loadData(homeIdsList.get(i));
            }
            recyclerView = view.findViewById(R.id.adsRecycler);
            adapter = new AdsAdapter(this);
        }
        else
            view = inflater.inflate(R.layout.show_no_home,container,false);
        setHasOptionsMenu(true);
        MaterialToolbar registerToolBar = view.findViewById(R.id.ads_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(registerToolBar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }
    private  void loadData(String id) {
        Query newRef = ref.child(id);
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HomeData homeData1 = snapshot.getValue(HomeData.class);
                    if (homeData1!=null){
                        homeData.add(homeData1);
                        adapter.setList(homeData);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            getParentFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
