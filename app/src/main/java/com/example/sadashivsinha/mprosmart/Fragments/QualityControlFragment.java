package com.example.sadashivsinha.mprosmart.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.Adapters.InspectionAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InspectionList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saDashiv sinha on 07-Mar-16.
 */
public class QualityControlFragment extends Fragment {

    private List<InspectionList> inspectionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InspectionAdapter inspectionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_quality_control, container, false);
        inspectionAdapter = new InspectionAdapter(inspectionList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inspectionAdapter);

        prepareItems();

        return view;

    }
    public void prepareItems()
    {
        InspectionList items = new InspectionList("001", "1","Item desciption will be written here. This is just a sample of desciption.", "kgs");
        inspectionList.add(items);

        items = new InspectionList("002", "2","Item desciption will be written here. This is just a sample of desciption.", "ml");
        inspectionList.add(items);

        items = new InspectionList("003", "3","Item desciption will be written here. This is just a sample of desciption.", "litre");
        inspectionList.add(items);

        items = new InspectionList("004", "4","Item desciption will be written here. This is just a sample of desciption.", "items");
        inspectionList.add(items);

        items = new InspectionList("005", "5","Item desciption will be written here. This is just a sample of desciption.", "cm");
        inspectionList.add(items);

        items = new InspectionList("006", "6","Item desciption will be written here. This is just a sample of desciption.", "kms");
        inspectionList.add(items);


        inspectionAdapter.notifyDataSetChanged();
    }

}
