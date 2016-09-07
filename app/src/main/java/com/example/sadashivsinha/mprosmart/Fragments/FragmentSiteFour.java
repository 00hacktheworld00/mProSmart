package com.example.sadashivsinha.mprosmart.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.SiteFourAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentSiteFour extends Fragment {

    private List<SiteTwoList> itemList = new ArrayList<>();
    RecyclerView recyclerView;
    SiteFourAdapter mainAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_two, container, false);

        mainAdapter = new SiteFourAdapter(itemList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainAdapter);


        SiteTwoList items = new SiteTwoList(1,"1","Processing","I-001","100", "Office", "0");
        itemList.add(items);

        items = new SiteTwoList(1,"2","Processing","I-002","250", "Office", "1");
        itemList.add(items);

        items = new SiteTwoList(1,"3","Processing","I-003","150", "Office", "2");
        itemList.add(items);

        items = new SiteTwoList(1,"4","Processing","I-004","40", "Office", "3");
        itemList.add(items);

        items = new SiteTwoList(1,"5","Processing","I-005","60", "Office", "4");
        itemList.add(items);

        items = new SiteTwoList(1,"6","Processing","I-006","100", "Office", "5");
        itemList.add(items);

        items = new SiteTwoList(1,"7","Processing","I-007","80", "Office", "6");
        itemList.add(items);


        mainAdapter.notifyDataSetChanged();


        return view;

    }
}
