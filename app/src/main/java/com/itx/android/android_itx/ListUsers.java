package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Utils.RecyclerTouchListener;
import com.itx.android.android_itx.Adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListUsers extends AppCompatActivity {

//    private ListView userListView ;
//    private ArrayAdapter<String> listUserAdapter ;
//    private ArrayList <Integer> listAssetAdapter;

    private List<Users> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersAdapter uAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);




        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        uAdapter = new UsersAdapter(userList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep user_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep user_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(uAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Users user = userList.get(position);
                Toast.makeText(getApplicationContext(), user.getFirstName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareUserData();

    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareUserData() {
        Users user = new Users("Faisal", "Arkan", "23");
        userList.add(user);

        user = new Users("Lea", "Sativa", "29");
        userList.add(user);

        user = new Users("Zia", "Orang", "12");
        userList.add(user);

        user = new Users("Orange", "Zizag", "90");
        userList.add(user);

        user = new Users("Tifa", "Ruki", "12");
        userList.add(user);
        user = new Users("Tifa", "Ruki", "12");
        userList.add(user);
        user = new Users("Tifa", "Ruki", "12");
        userList.add(user);
        user = new Users("Tifa", "Ruki", "12");
        userList.add(user);

        // notify adapter about data set changes
        // so that it will render the list with new data
        uAdapter.notifyDataSetChanged();
    }


}
