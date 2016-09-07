package com.example.sadashivsinha.mprosmart.Fragments;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.SubmittalFragmentAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saDashiv sinha on 23-Mar-16.
 */
public class FragmentSubmittals extends Fragment {

    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubmittalFragmentAdapter fragmentsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_fragment, container, false);

        fragmentsAdapter = new SubmittalFragmentAdapter(momList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fragmentsAdapter);
        prepareItems();

        return view;
    }


    public void prepareItems()
    {
        MomList items = new MomList(0,"S 001", "101", "Dummy" , "21/03/2016", "Xyz");
        momList.add(items);

        items = new MomList(0,"S 002", "102", "Dummy" , "20/03/2016", "Pqr");
        momList.add(items);

        items = new MomList(0,"S 003", "103", "Dummy" , "19/03/2016", "Xyz");
        momList.add(items);

        items = new MomList(0,"S 004", "104", "Dummy" , "15/03/2016", "Abcd");
        momList.add(items);

        fragmentsAdapter.notifyDataSetChanged();
    }
}

