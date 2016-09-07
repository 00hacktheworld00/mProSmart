package com.example.sadashivsinha.mprosmart.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.MomFragmentsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentMom extends Fragment {

    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MomFragmentsAdapter fragmentsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_fragment, container, false);

        fragmentsAdapter = new MomFragmentsAdapter(momList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fragmentsAdapter);
        prepareItems();

        return view;
    }


    public void prepareItems()
    {
        MomList items = new MomList(0,"001", "101", "Dummy" , "22/03/2016", "Abcd");
        momList.add(items);

        items = new MomList(0,"002", "102", "Dummy" , "15/03/2016", "Pqr");
        momList.add(items);

        items = new MomList(0,"003", "103", "Dummy" , "10/03/2016", "Xyz");
        momList.add(items);

        fragmentsAdapter.notifyDataSetChanged();
    }
}

