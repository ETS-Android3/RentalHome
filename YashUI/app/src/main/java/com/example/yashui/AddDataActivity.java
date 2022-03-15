package com.example.yashui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.vikktorn.picker.City;
import com.vikktorn.picker.CityPicker;
import com.vikktorn.picker.Country;
import com.vikktorn.picker.CountryPicker;
import com.vikktorn.picker.OnCityPickerListener;
import com.vikktorn.picker.OnCountryPickerListener;
import com.vikktorn.picker.OnStatePickerListener;
import com.vikktorn.picker.State;
import com.vikktorn.picker.StatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDataActivity extends AppCompatActivity implements OnStatePickerListener, TaskCompleted, OnCountryPickerListener, OnCityPickerListener , View.OnClickListener {
    private TextView mediaSize;
    private View view;
    private UploadDataIntoDatabase uploadDataIntoDatabase;
    private ArrayList<Uri> imageUri;
    private LinearLayout state_linear_layout, city_linear_layout;
    private ProgressDialog progressDialog;
    private ArrayList<String> downloadImageUri;
    private final int PICK_IMAGE_MULTIPLE = 1;
    private int count = 0;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Chip kitcheN, electricitY, watersupplY, sanitarY, balconY, parkinG, gardeN;
    private NestedScrollView scrollView;
    private CheckBox everyone, married_couple, students, bachelors, worker;
    private TextInputLayout addressLayout;
    private TextInputLayout countryLayout;
    private TextInputLayout stateLayout;
    private TextInputLayout cityLayout;
    private TextInputLayout rentLayout;
    private TextInputLayout lengthLayout;
    private TextInputLayout widthLayout;
    private TextInputLayout type_of_house;
    private ExtendedFloatingActionButton pickStateButton, pickCountry, pickCity, submitButton, uploadImageButton;
    private TextInputEditText stateName, countryName, countryCode, countryPhoneCode, countryCurrency, cityName, addressText, rentText, lengthText, widthText,
             descriptionText;
    private MaterialAutoCompleteTextView houseType;
    // Pickers
    private CountryPicker countryPicker;
    private StatePicker statePicker;
    private CityPicker cityPicker;
    // arrays of state object
    private static List<State> stateObject;
    // arrays of city object
    private static List<City> cityObject;
    private DatabaseHandling handling;
    private HomeData homeData;
    private LinearLayout mediaLinear;

    public AddDataActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        // initialize view
        initView();
        homeData = new HomeData();
        imageUri = new ArrayList<>();

        MaterialToolbar registerToolBar = findViewById(R.id.detail_ToolBar);
        setSupportActionBar(registerToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fill HomeDetails");
        ArrayList<String> houseTypeList = getHouseTypeList();
        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddDataActivity.this, android.R.layout.simple_spinner_item, houseTypeList);
        houseType.setAdapter(adapter);

        // get state from assets JSON
        try {
            getStateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // get City from assets JSON
        try {
            getCityJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // initialize country picker
        countryPicker = new CountryPicker.Builder().with(this).listener(this).build();

        // initialize listeners
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        super.onResume();
        initializeRecyclerView();
        setStateListener();
        setCountryListener();
        setCityListener();
        submissionButtonClicked();

    }
    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.home_image_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(imageUri, this);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(AddDataActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayout);
        recyclerView.setAdapter(adapter);
        uploadImageButton.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap;
            if (view.getId() == R.id.upload_button) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    for (int i = 0; i < count; i++) {
                        imageUri.add(data.getClipData().getItemAt(i).getUri());
                    }
                } else {
                    imageUri.add(data.getData());
                }
                setMediaSize(imageUri.size());
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "You haven't picked image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void setMediaSize(int size){
        if (size> 0) {
            recyclerView.setVisibility(View.VISIBLE);
            mediaLinear.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.GONE);
            mediaLinear.setVisibility(View.GONE);
        }
        mediaSize.setText(String.valueOf(imageUri.size()));
    }
    // INIT VIEWS

    public void initView() {
        scrollView = findViewById(R.id.nested_scrollView);
        state_linear_layout = findViewById(R.id.state_linear_layout);
        city_linear_layout = findViewById(R.id.city_linear_layout);
        //ExtendedFloatingButtons
        pickStateButton = findViewById(R.id.state_floating_button);
        pickCountry = findViewById(R.id.country_floating_button);
        pickCity = findViewById(R.id.city_floating_button);

        // TextInputEditText
//        countryCode         =       findViewById(R.id.countryCodeTextView);
//        countryPhoneCode    =       findViewById(R.id.countryDialCodeTextView);
//        countryCurrency     =       findViewById(R.id.countryCurrencyTextView);

        // ImageView
//        flagImage = findViewById(R.id.flag_image);
        mediaSize = findViewById(R.id.mediaSize);
        mediaLinear = findViewById(R.id.MediaLinear);
        uploadImageButton = findViewById(R.id.upload_button);
// chipGroup id's
        kitcheN = findViewById(R.id.one);
        electricitY = findViewById(R.id.two);
        watersupplY = findViewById(R.id.three);
        sanitarY = findViewById(R.id.four);
        balconY = findViewById(R.id.five);
        parkinG = findViewById(R.id.six);
        gardeN = findViewById(R.id.seven);

        addressLayout = findViewById(R.id.address_input_layout);
        addressText = findViewById(R.id.address_edit);

        countryLayout = findViewById(R.id.country_input_layout);
        countryName = findViewById(R.id.country_edittext);

        stateLayout = findViewById(R.id.state_input_layout);
        stateName = findViewById(R.id.state_edittext);

        cityLayout = findViewById(R.id.city_input_layout);
        cityName = findViewById(R.id.city_edittext);

        rentLayout = findViewById(R.id.textInputLayout5);
        rentText = findViewById(R.id.rent);

        lengthLayout = findViewById(R.id.length_input_layout);
        lengthText = findViewById(R.id.length);

        widthLayout = findViewById(R.id.textInputLayout2);
        widthText = findViewById(R.id.width);

        type_of_house = findViewById(R.id.type_of_house);
        houseType = findViewById(R.id.houseType);
        everyone = findViewById(R.id.everyone);

        married_couple = findViewById(R.id.married_couple);

        bachelors = findViewById(R.id.bachelors);

        students = findViewById(R.id.students);

        worker = findViewById(R.id.employee_worker);
        descriptionText = findViewById(R.id.description_text);

        submitButton = findViewById(R.id.submit_button);
        // initiate state object, parser, and arrays
        stateObject = new ArrayList<>();
        cityObject = new ArrayList<>();
    }
    private ArrayList<String> getHouseTypeList()
    {
        ArrayList<String> houseList = new ArrayList<>();
        houseList.add("Apartment");houseList.add("Log Cabin");
        houseList.add("Bungalow");houseList.add("Manor");
        houseList.add("Caravan");houseList.add("Mansion");
        houseList.add("Castle");houseList.add("Motel");
        houseList.add("Condominium/Condo");houseList.add("Palace");
        houseList.add("Cottage");houseList.add("Semi-Detached House");
        houseList.add("Dormitory/dorm");houseList.add("Shack");
        houseList.add("Duplex");houseList.add("Single Family Home");
        houseList.add("Farmhouse");houseList.add("Skyscraper");
        houseList.add("Flat");houseList.add("TeePee");
        houseList.add("Hotel");houseList.add("Tent");
        houseList.add("Houseboat");houseList.add("Terraced House");
        houseList.add("Hut");houseList.add("Townhouse");
        houseList.add("Igloo");houseList.add("Trailer");
        houseList.add("LightHouse");houseList.add("Tree House");
        houseList.add("Lodge");
        return houseList;
    }
    // SET STATE LISTENER
    private void setStateListener() {
        pickStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateName.setEnabled(true);
                statePicker.showDialog(getSupportFragmentManager());
            }
        });
    }

    //SET COUNTRY LISTENER
    private void setCountryListener(){
        pickCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryName.setEnabled(true);
                countryPicker.showDialog(getSupportFragmentManager());
            }
        });
    }
    //SET CITY LISTENER
    private void setCityListener(){
        pickCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName.setEnabled(true);
                cityPicker.showDialog(getSupportFragmentManager());
            }
        });
    }

    // ON SELECTED COUNTRY ADD STATES TO PICKER
    @SuppressLint("SetTextI18n")
    @Override
    public void onSelectCountry(Country country) {
        // get country name and country ID
        countryName.setText(country.getName());
        int countryID = country.getCountryId();
        StatePicker.equalStateObject.clear();
        CityPicker.equalCityObject.clear();
        state_linear_layout.setVisibility(View.VISIBLE);

        // GET STATES OF SELECTED COUNTRY
        for (int i = 0; i < stateObject.size(); i++) {
            // init state picker
            statePicker = new StatePicker.Builder().with(this).listener(this).build();
            State stateData = new State();
            if (stateObject.get(i).getCountryId() == countryID) {

                stateData.setStateId(stateObject.get(i).getStateId());
                stateData.setStateName(stateObject.get(i).getStateName());
                stateData.setCountryId(stateObject.get(i).getCountryId());
                stateData.setFlag(country.getFlag());
                StatePicker.equalStateObject.add(stateData);
            }
        }
    }

    // ON SELECTED STATE ADD CITY TO PICKER
    @Override
    public void onSelectState(State state) {
        CityPicker.equalCityObject.clear();
        stateName.setText(state.getStateName());
        int stateID = state.getStateId();

        for (int i = 0; i < cityObject.size(); i++) {
            cityPicker = new CityPicker.Builder().with(this).listener(this).build();
            City cityData = new City();
            if (cityObject.get(i).getStateId() == stateID) {
                ++count;
                cityData.setCityId(cityObject.get(i).getCityId());
                cityData.setCityName(cityObject.get(i).getCityName());
                cityData.setStateId(cityObject.get(i).getStateId());
                CityPicker.equalCityObject.add(cityData);
            }
        }

        if(count!=0)
            city_linear_layout.setVisibility(View.VISIBLE);
    }

    // ON SELECTED CITY
    @Override
    public void onSelectCity(City city) {
        cityName.setText(city.getCityName());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    // GET STATE FROM ASSETS JSON
    public void getStateJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("states.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("states");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            State stateData = new State();

            stateData.setStateId(Integer.parseInt(cit.getString("id")));
            stateData.setStateName(cit.getString("name"));
            stateData.setCountryId(Integer.parseInt(cit.getString("country_id")));
            stateObject.add(stateData);
        }
    }

    // GET CITY FROM ASSETS JSON
    public void getCityJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("cities");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            City cityData = new City();

            cityData.setCityId(Integer.parseInt(cit.getString("id")));
            cityData.setCityName(cit.getString("name"));
            cityData.setStateId(Integer.parseInt(cit.getString("state_id")));
            cityObject.add(cityData);
        }
    }

    private void submissionButtonClicked() {
        addressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 5) {
                    addressLayout.setErrorEnabled(false);
                    addressLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 5) {
                    addressLayout.setErrorEnabled(false);
                    addressLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }
        });
        stateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    stateLayout.setErrorEnabled(false);
                    stateLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        countryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String state = null;
                if (s.toString().length() >= 1) {
                    countryLayout.setErrorEnabled(false);
                    countryLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        lengthText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    lengthLayout.setErrorEnabled(false);
                    lengthLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        widthText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    widthLayout.setErrorEnabled(false);
                    widthLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        houseType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    type_of_house.setErrorEnabled(false);
                    type_of_house.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    rentLayout.setErrorEnabled(false);
                    rentLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        cityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    cityLayout.setErrorEnabled(false);
                    cityLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        everyone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                students.setChecked(true);
                married_couple.setChecked(true);
                bachelors.setChecked(true);
                worker.setChecked(true);
            } else {
                students.setChecked(false);
                married_couple.setChecked(false);
                bachelors.setChecked(false);
                worker.setChecked(false);
            }

        });
        married_couple.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                if(everyone.isChecked()){
                    everyone.setChecked(false);
                }
            }
            if(isChecked){
                if (bachelors.isChecked()&&students.isChecked()&&worker.isChecked())
                    everyone.setChecked(true);
            }
        });
        bachelors.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                if(everyone.isChecked()){
                    everyone.setChecked(false);
                }
            }
            if(isChecked){
                if (married_couple.isChecked()&&students.isChecked()&&worker.isChecked())
                    everyone.setChecked(true);
            }
        });
        worker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                if(everyone.isChecked()){
                    everyone.setChecked(false);
                }
            }
            if(isChecked){
                if (bachelors.isChecked()&&students.isChecked()&&married_couple.isChecked())
                    everyone.setChecked(true);
            }
        });
        students.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                if(everyone.isChecked()){
                    everyone.setChecked(false);
                }
                if(isChecked){
                    if (bachelors.isChecked()&&married_couple.isChecked()&&worker.isChecked())
                        everyone.setChecked(true);
                }
            }
        });

        submitButton.setOnClickListener(this);
    }

    private boolean checkAllFields() {
        String ADDRESS_STRING = addressText.getText().toString();
        String COUNTRY_STRING = countryName.getText().toString();
        String STATE_STRING = stateName.getText().toString();
        String CITY_STRING = cityName.getText().toString();
        String RENT_STRING = rentText.getText().toString();
        String LENGTH_STRING = lengthText.getText().toString();
        String WIDTH_STRING = widthText.getText().toString();
        String TYPE_OF_HOUSE = houseType.getText().toString();
        boolean ADDRESS_BOOLEAN = false;
        boolean COUNTRY_BOOLEAN = false;
        boolean STATE_BOOLEAN = false;
        boolean CITY_BOOLEAN = false;
        boolean RENT_BOOLEAN = false;
        boolean LENGTH_BOOLEAN = false;
        boolean WIDTH_BOOLEAN = false;
        boolean HOUSE_TYPE_BOOLEAN = false;

        if (ADDRESS_STRING.isEmpty() || ADDRESS_STRING.length() < 5) {
            addressLayout.setError("Address minimum length should be 5.");
            addressLayout.setBoxStrokeColor(Color.RED);
        } else {
            ADDRESS_BOOLEAN = true;
        }
        if (COUNTRY_STRING.isEmpty()) {
            countryLayout.setError("Country name is mandatory.");
            countryLayout.setBoxStrokeColor(Color.RED);
        } else {
            COUNTRY_BOOLEAN = true;
        }
        if (STATE_STRING.isEmpty()) {
            stateLayout.setError("State name is mandatory.");
            stateLayout.setBoxStrokeColor(Color.RED);
        } else {
            STATE_BOOLEAN = true;
        }
        if (count!=0) {
            if (CITY_STRING.isEmpty()) {
                cityLayout.setError("City name is mandatory.");
                cityLayout.setBoxStrokeColor(Color.RED);
            }
            else{
                CITY_BOOLEAN = true;
            }
        }else {
            CITY_BOOLEAN = true;
        }
        if (RENT_STRING.isEmpty()) {
            rentLayout.setError("Rent is mandatory.");
            rentLayout.setBoxStrokeColor(Color.RED);
        } else {
            RENT_BOOLEAN = true;
        }
        if (LENGTH_STRING.isEmpty()) {
            lengthLayout.setError("*Required");
            lengthLayout.setBoxStrokeColor(Color.RED);
        } else {
            LENGTH_BOOLEAN = true;
        }
        if (WIDTH_STRING.isEmpty()) {
            widthLayout.setError("*Required");
            widthLayout.setBoxStrokeColor(Color.RED);
        } else {
            WIDTH_BOOLEAN = true;
        }
        if (TYPE_OF_HOUSE.isEmpty()) {
            type_of_house.setError("*Required");
            type_of_house.setBoxStrokeColor(Color.RED);
        } else {
            HOUSE_TYPE_BOOLEAN = true;
        }
        if (TextUtils.isEmpty(ADDRESS_STRING)||ADDRESS_STRING.length() < 5)
            scrollView.smoothScrollTo(0, 0);
        else {
            if (TextUtils.isEmpty(COUNTRY_STRING))
                scrollView.smoothScrollTo(0, countryLayout.getTop());
            else {
                if (TextUtils.isEmpty(STATE_STRING))
                    scrollView.smoothScrollTo(0, stateLayout.getTop());
                else {
                    if (TextUtils.isEmpty(CITY_STRING))
                        scrollView.smoothScrollTo(0, cityLayout.getTop());
                    else {
                        if (TextUtils.isEmpty(LENGTH_STRING))
                            scrollView.smoothScrollTo(0, lengthLayout.getTop());
                        else {
                            if (TextUtils.isEmpty(WIDTH_STRING))
                                scrollView.smoothScrollTo(0, widthLayout.getTop());
                            else {
                                if (TextUtils.isEmpty(RENT_STRING))
                                    scrollView.smoothScrollTo(0, rentLayout.getTop());
                                else {
                                    if (TextUtils.isEmpty(TYPE_OF_HOUSE))
                                        scrollView.smoothScrollTo(0, type_of_house.getTop());
                                }
                            }
                        }
                    }
                }
            }
        }

        return (ADDRESS_BOOLEAN && COUNTRY_BOOLEAN && STATE_BOOLEAN && CITY_BOOLEAN && LENGTH_BOOLEAN && WIDTH_BOOLEAN && RENT_BOOLEAN && HOUSE_TYPE_BOOLEAN);
    }

    private void uploadHomeData() {

        String address, country, state, city, description,typeOfHouse;
        float rent, length, width;
        boolean everyone1, married, student, workers, bachelor, kitchen, electricity, waterSupply, sanitary, parking, garden, balcony;

        description = descriptionText.getText().toString();
        address = addressText.getText().toString();
        country = countryName.getText().toString();
        state = stateName.getText().toString();
        if (cityName.getText().toString().isEmpty())
            city = "No City";
        else
            city = cityName.getText().toString();
        length = Float.parseFloat(lengthText.getText().toString());
        width = Float.parseFloat(widthText.getText().toString());
        rent = Float.parseFloat(rentText.getText().toString());
        typeOfHouse = houseType.getText().toString();
        if(everyone.isChecked()||married_couple.isChecked()||students.isChecked()||worker.isChecked()||bachelors.isChecked()) {
            everyone1 = everyone.isChecked();
            married = married_couple.isChecked();
            workers = worker.isChecked();
            student = students.isChecked();
            bachelor = bachelors.isChecked();
        }
        else{
            everyone1 = true;
            married = true;
            workers = true;
            bachelor = true;
            student = true;
        }
        kitchen = kitcheN.isChecked();
        electricity = electricitY.isChecked();
        waterSupply = watersupplY.isChecked();
        sanitary = sanitarY.isChecked();
        balcony = balconY.isChecked();
        garden = gardeN.isChecked();
        parking = parkinG.isChecked();
        Home rentalHome = new Home(address, country, state, city, rent, length, width, typeOfHouse,everyone1, married, student,
                workers, bachelor, kitchen, electricity, waterSupply, sanitary, parking, garden, balcony, description);
        handling = new DatabaseHandling();
        homeData.setHome(rentalHome);
        uploadImageToFirebase();
    }
    private void uploadImageToFirebase() {
        downloadImageUri = new ArrayList<>();
        uploadDataIntoDatabase = new UploadDataIntoDatabase(this);

        if (imageUri != null) {
            Log.d("SUBMIT", "Inside IF statement");
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("Please don't press back button...");
            progressDialog.show();
            int i = 0;
            Log.d("Check", String.valueOf(imageUri.size()));
            for (Uri filePath : imageUri) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSS").format(Calendar.getInstance().getTime());
                Log.d("SUBMIT", "Inside for loop " + imageUri.size());
                StorageReference storageReference = new DatabaseStorageReference().getStorageReference();
                StorageReference ref = storageReference.child("images/" + timeStamp + System.currentTimeMillis() + System.nanoTime() + i);

                uploadDataIntoDatabase.setImageUri(filePath);
                uploadDataIntoDatabase.uploadTask(ref);
                ++i;
            }
        }
    }

    @Override
    public void onClick(View v) {
        view = v;
        if (v.getId() == R.id.upload_button) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        }
        if (v.getId() == R.id.submit_button) {
            Log.d("SUBMIT", "Submit button clicked");
            if (checkAllFields()) {
                if (imageUri.size() != 0) {
                    uploadHomeData();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Alert !");
                    alertDialog.setMessage("Please select atLeast one home image...");
                    alertDialog.show();
                }
            }
        }
    }

    @Override
    public void onTaskCompleted() {
        if(view.getId()==R.id.submit_button) {
//            Log.d("SUBMIT", "Inside onTaskCompleted of TestAddDataActivity" + uploadDataIntoDatabase.isUploaded());
//            Log.d("SUBMIT", "Inside onTaskCompleted of TestAddDataActivity isUploaded" + !uploadDataIntoDatabase.isUploaded());
            if (!uploadDataIntoDatabase.isUploaded()) {
//                Log.d("SUBMIT", "Inside IFIF statement");
            }
            downloadImageUri.add(uploadDataIntoDatabase.getImageDownloadUrl());
//            Log.d("SUBMIT", "Inside onTaskCompleted of TestAddDataActivity getImageDownloadUrl" + uploadDataIntoDatabase.getImageDownloadUrl());
//            Log.d("SUBMIT", "Inside onTaskCompleted of TestAddDataActivity downloadImageUri ArrayList" + downloadImageUri.get(0));
            if (downloadImageUri.size() == 1) {
                homeData.setImageUrl(uploadDataIntoDatabase.getImageDownloadUrl());
            }
//            Log.d("SUBMIT", "Inside onTaskCompleted of TestAddDataActivity downloadImageUriSize" + downloadImageUri.size());
//            Log.d("SUBMIT", "Inside onTaskCompleted of TestAddDataActivity imageUriSize" + imageUri.size());
            if (downloadImageUri.size() == imageUri.size()) {
                homeData.setImagesClass(downloadImageUri);
                homeData.setPhone(new DatabaseHandling().getPhoneNumber());
                handling.get_home_data_child(handling.generate_home_data_UUID()).setValue(homeData);
                FirebaseDatabase.getInstance().getReference().child("user_data").child(new DatabaseHandling().getPhoneNumber()).child("HomeIds").push().setValue(handling.get_home_UUID());
                progressDialog.setTitle("Successfully Uploaded");
                progressDialog.setMessage("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                      Log.d("SUBMIT", "Inside runnable method");
                        progressDialog.dismiss();
                        finish();
                    }
                }, 1500);
            }
        }
    }
}