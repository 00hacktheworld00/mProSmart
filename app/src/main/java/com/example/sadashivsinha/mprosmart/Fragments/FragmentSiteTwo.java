package com.example.sadashivsinha.mprosmart.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.SiteTwoAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class FragmentSiteTwo extends Fragment {

    private List<SiteTwoList> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SiteTwoAdapter mainAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_two, container, false);

        mainAdapter = new SiteTwoAdapter(itemList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainAdapter);

        SiteTwoList items = new SiteTwoList("1","Abcdef.","Abcdef.","Abcdef.","10");
        itemList.add(items);

        items = new SiteTwoList("2","Ghijkl.","Ghijkl.","Ghijkl.","40");
        itemList.add(items);

        items = new SiteTwoList("3","Mnopqr.","Mnopqr.","Mnopqr.","8");
        itemList.add(items);

        items = new SiteTwoList("4","Stuvw.","Stuvw.","Stuvw.","7");
        itemList.add(items);

        items = new SiteTwoList("5","Xyz.","Xyz.","Xyz.","5");
        itemList.add(items);

        mainAdapter.notifyDataSetChanged();


        return view;

    }
}
