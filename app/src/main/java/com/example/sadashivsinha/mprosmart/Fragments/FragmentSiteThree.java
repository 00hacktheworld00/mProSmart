package com.example.sadashivsinha.mprosmart.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.SiteThreeAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class FragmentSiteThree extends Fragment {

    private List<SiteTwoList> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SiteThreeAdapter mainAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_two, container, false);

        mainAdapter = new SiteThreeAdapter(itemList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainAdapter);

        SiteTwoList items = new SiteTwoList("1","001","41454.","V-001","I-101", "Processing");
        itemList.add(items);

        items = new SiteTwoList("2","002","41774.","V-002","I-102", "Processing");
        itemList.add(items);

        items = new SiteTwoList("3","003","41575.","V-003","I-103", "Delivered");
        itemList.add(items);

        items = new SiteTwoList("4","004","25874.","V-004","I-104", "Processing");
        itemList.add(items);

        items = new SiteTwoList("5","005","11424.","V-005","I-105", "Processing");
        itemList.add(items);

        mainAdapter.notifyDataSetChanged();


        return view;

    }
}
