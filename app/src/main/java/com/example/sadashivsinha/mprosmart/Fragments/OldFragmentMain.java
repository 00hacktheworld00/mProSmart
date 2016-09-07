package com.example.sadashivsinha.mprosmart.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.MainAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.ItemList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saDashiv sinha on 01-Mar-16.
 */
public class OldFragmentMain extends Fragment {

    private List<ItemList> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        mainAdapter = new MainAdapter(itemList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainAdapter);

        ItemList items = new ItemList("101", "80","100", "20", "10/02/2016");
        itemList.add(items);


        items = new ItemList("102", "10","20", "10", "1/01/2016");
        itemList.add(items);



        items = new ItemList("103", "25","50", "25", "30/12/2015");
        itemList.add(items);



        items = new ItemList("104", "5","100", "95", "5/11/2015");
        itemList.add(items);



        items = new ItemList("105", "5","7", "2", "10/08/2015");
        itemList.add(items);



        items = new ItemList("106", "10","50", "40", "01/01/2015");
        itemList.add(items);


        mainAdapter.notifyDataSetChanged();


        return view;

    }
}
