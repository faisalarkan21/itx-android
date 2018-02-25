package com.itx.android.android_itx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.ListInventoryService;
import com.itx.android.android_itx.Service.ListUsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewInventory extends AppCompatActivity {


    private List<String> mListFacilitiesChecked = new ArrayList<>();
    @BindView(R.id.et_add_inventory_name)
    EditText mEtInventName;
    @BindView(R.id.et_add_inventory_deskripsi)
    EditText mEtInventDeskripsi;
    @BindView(R.id.et_add_inventory_space)
    EditText mEtInventSpace;
    @BindView(R.id.et_add_inventory_stock)
    EditText mEtInventStock;
    @BindView(R.id.btn_add_new_inventory)
    Button mBtnAddInvent;

    @BindView(R.id.layoutFacilities)
    LinearLayout layoutFacilities;

    CheckBox checkFacilities;

    int totalFacilities;


    SessionManager session;
    ListInventoryService mInventoryAPIService;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_inventory);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        session = new SessionManager(this);
        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());
        ButterKnife.bind(this);

        prepareFacilitiesData();

        progressDialog = new ProgressDialog(CreateNewInventory.this);
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.show();

    }

    public void prepareFacilitiesData() {



        Call<JsonObject> response = mInventoryAPIService.getAllFacilities();

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        final JsonArray jsonArray = rawResponse.body().get("data").getAsJsonArray();

                        for (int i = 0; i < jsonArray.size(); i++) {

                            final JsonObject Data = jsonArray.get(i).getAsJsonObject();
                            Inventory invent = new Inventory();





                            checkFacilities = new CheckBox(CreateNewInventory.this);
                            checkFacilities.setText(Data.get("name").getAsString());
                            checkFacilities.setTextSize(12);
                            checkFacilities.setId(i);
                            checkFacilities.setTextColor(Color.BLACK);
                            layoutFacilities.addView(checkFacilities);

                            new CountDownTimer(1000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    // You don't need anything here
                                }

                                public void onFinish() {

                                    progressDialog.dismiss();
                                }
                            }.start();

                            checkFacilities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked == true){
                                        int getChecked = buttonView.getId();

                                        final JsonObject DataChecked = jsonArray.get(getChecked).getAsJsonObject();

                                        Log.i("checkbox", DataChecked.get("_id").getAsString());
                                        mListFacilitiesChecked.add( DataChecked.get("_id").getAsString());
                                    }else{
                                        int getChecked = buttonView.getId();

                                        final JsonObject DataChecked = jsonArray.get(getChecked).getAsJsonObject();

                                        Log.i("checkbox", DataChecked.get("_id").getAsString());
                                        mListFacilitiesChecked.remove(DataChecked.get("_id").getAsString());
                                    }
                                }
                            });

                            mBtnAddInvent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String inventoryName = mEtInventName.getText().toString().trim();
                                    final String inventoryDescription = mEtInventDeskripsi.getText().toString().trim();
                                    final String inventoryStock = mEtInventStock.getText().toString().trim();
                                    final String inventorySpace = mEtInventSpace.getText().toString().trim();
                                    Log.d("getAllChecked", mListFacilitiesChecked.toString());


                                    try {

                                        String idAsset = getIntent().getStringExtra("idAsset");
                                        JSONObject object0 = new JSONObject();
                                        object0.put("asset", idAsset);
                                        object0.put("name", inventoryName);
                                        object0.put("description", inventoryDescription);
                                        object0.put("space", inventorySpace);
                                        object0.put("price", "6000");
                                        object0.put("stock", inventoryStock);
                                        Gson gson = new Gson();



                                        JsonElement element = gson.toJsonTree(mListFacilitiesChecked, new TypeToken<List>() {}.getType());
                                        JsonArray jsonArray = element.getAsJsonArray();


                                        JSONArray jsonArrayChecked = new JSONArray(mListFacilitiesChecked);




                                        object0.put("facilities",jsonArrayChecked);

                                        JSONObject object = new JSONObject();
                                        object.put("data", object0 );

                                        saveInventoryToServer(object);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
                        }
                        Toast.makeText(CreateNewInventory.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CreateNewInventory.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(CreateNewInventory.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    public void saveInventoryToServer(JSONObject jsonParams){


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (jsonParams).toString());

        Log.d("testJson", body.toString());

        Call<ResponseBody> addInventoryRequest = mInventoryAPIService.createInventoryCategory(body);

        addInventoryRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Post", response.body().toString());
                    //success then send back the user to the list user and destroy this activity
                    startActivity(new Intent(CreateNewInventory.this, ListUsers.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}
