package com.example.yashui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yashui.HomeData;
import com.example.yashui.R;
import com.example.yashui.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Objects;

public class HomeDetails extends Fragment {
    private LinearLayout descriptionLayout;
    private MaterialTextView address,city,state,country,description,bachelors,
            rent,length,width,couple,students,worker,houseType,preferences,
            kitchen,sanitary,electricity,balcony,garden,parking,waterSupply,facilities;
    private View view,view1,view2;
    private final HomeData homeData;
    public HomeDetails(HomeData homeData){
        this.homeData = homeData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_details, container, false);
        initiateView();
        setHasOptionsMenu(true);
        MaterialToolbar registerToolBar = view.findViewById(R.id.home_details_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(registerToolBar);
        Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }
    public void initiateView(){
        address = view.findViewById(R.id.fillAddress);
        country = view.findViewById(R.id.fillCountry);
        state = view.findViewById(R.id.fillState);
        view1 = view.findViewById(R.id.divider2);
        view2 = view.findViewById(R.id.divider4);
        city = view.findViewById(R.id.fillCity);
        rent = view.findViewById(R.id.fillRent);
        length = view.findViewById(R.id.fillLength);
        width = view.findViewById(R.id.fillWidth);
        description = view.findViewById(R.id.fillDescription);
        descriptionLayout = view.findViewById(R.id.descriptionLinear);
        preferences = view.findViewById(R.id.preferences);
        bachelors = view.findViewById(R.id.fillBachelors);
        students = view.findViewById(R.id.fillStudents);
        couple = view.findViewById(R.id.fillCouple);
        worker = view.findViewById(R.id.fillWorker);
        houseType = view.findViewById(R.id.fill_house_type);

        facilities = view.findViewById(R.id.additional_facilities);
        kitchen = view.findViewById(R.id.fill_kitchen);
        balcony = view.findViewById(R.id.fill_balcony);
        waterSupply = view.findViewById(R.id.fill_waterSupply);
        sanitary = view.findViewById(R.id.fill_sanitary);
        garden = view.findViewById(R.id.fill_garden);
        electricity = view.findViewById(R.id.fill_electricity);
        parking = view.findViewById(R.id.fill_parking);
    }
    @Override
    public void onStart() {
        super.onStart();

        if (homeData != null) {
            address.setText(homeData.getHome().getAddress());
            country.setText(homeData.getHome().getCountry());
            state.setText(homeData.getHome().getState());
            city.setText(homeData.getHome().getCity());
            rent.setText(String.valueOf(homeData.getHome().getRent()));
            length.setText(String.valueOf(homeData.getHome().getLength()));
            width.setText(String.valueOf(homeData.getHome().getWidth()));
            houseType.setText(homeData.getHome().getTypeOfHouse());
            if (!homeData.getHome().getDescription().isEmpty()) {
                descriptionLayout.setVisibility(View.VISIBLE);
                description.setText(homeData.getHome().getDescription());
            }
            if (homeData.getHome().isEveryone()
                    || homeData.getHome().isBachelors()
                    || homeData.getHome().isWorker()
                    || homeData.getHome().isStudents()
                    || homeData.getHome().isMarried()) {
                view1.setVisibility(View.VISIBLE);
                preferences.setVisibility(View.VISIBLE);
            }
            if(homeData.getHome().isParking() || homeData.getHome().isElectricity() ||
                    homeData.getHome().isSanitary()|| homeData.getHome().isWaterSupply() ||
                    homeData.getHome().isGarden() || homeData.getHome().isBalcony() ||homeData.getHome().isKitchen()){
                view2.setVisibility(View.VISIBLE);
                facilities.setVisibility(View.VISIBLE);
            }
            if (homeData.getHome().isKitchen())
                    kitchen.setVisibility(View.VISIBLE);
            if (homeData.getHome().isBalcony())
                balcony.setVisibility(View.VISIBLE);
            if (homeData.getHome().isGarden())
                garden.setVisibility(View.VISIBLE);
            if (homeData.getHome().isParking())
                parking.setVisibility(View.VISIBLE);
            if (homeData.getHome().isSanitary())
                sanitary.setVisibility(View.VISIBLE);
            if (homeData.getHome().isWaterSupply())
                waterSupply.setVisibility(View.VISIBLE);
            if (homeData.getHome().isElectricity())
                electricity.setVisibility(View.VISIBLE);

            if (homeData.getHome().isEveryone()) {
                students.setVisibility(View.VISIBLE);
                couple.setVisibility(View.VISIBLE);
                bachelors.setVisibility(View.VISIBLE);
                worker.setVisibility(View.VISIBLE);
            } else {
                if (homeData.getHome().isStudents())
                    students.setVisibility(View.VISIBLE);
                if (homeData.getHome().isMarried())
                    couple.setVisibility(View.VISIBLE);
                if (homeData.getHome().isWorker())
                    worker.setVisibility(View.VISIBLE);
                if (homeData.getHome().isBachelors())
                    bachelors.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            getParentFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}

