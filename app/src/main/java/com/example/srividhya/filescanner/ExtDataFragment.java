package com.example.srividhya.filescanner;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by srividhya on 7/24/2017.
 */

public class ExtDataFragment extends Fragment {
    private ArrayAdapter<String> extAdapter;
    private ListView listView;
    public ExtDataFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.externaldata_fragment, container, false);
        listView = (ListView)v.findViewById(R.id.extDataList);
        extAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);
        listView.setAdapter(extAdapter);
        return v;
    }

    public void updateData(Update update){
        extAdapter.clear();
        int i = 0;
        while(i < 5 ){
            extAdapter.add(update.mostRecentFiveExtensions[i]);
            i++;
        }
        extAdapter.notifyDataSetChanged();
    }


}