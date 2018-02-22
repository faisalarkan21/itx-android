package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateNewInventory extends AppCompatActivity {

    @BindView(R.id.et_add_inventory_name)
    EditText mEtInventName;
    @BindView(R.id.et_add_inventory_deskripsi) EditText mEtInventDeskripsi;
    @BindView(R.id.et_add_inventory_space) EditText mEtInventSpace;
    @BindView(R.id.et_add_inventory_stock) EditText mEtInventStock;
    @BindView(R.id.btn_add_new_inventory)
    Button mBtnAddInvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_inventory);

        ButterKnife.bind(this);

        mBtnAddInvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement for adding the invent to the server
            }
        });

    }
}
