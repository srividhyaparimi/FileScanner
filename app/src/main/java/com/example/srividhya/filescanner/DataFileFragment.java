package com.example.srividhya.filescanner;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srividhya on 7/24/2017.
 */


public class DataFileFragment extends Fragment {

    private List<DataFileFragment.FileDataItem> listData;
    private FileDataAdapter adapter;
    private ListView listView;


    public DataFileFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_file_fragment, container, false);

        listData = new ArrayList<>();
        listView = (ListView)v.findViewById(R.id.fileslist);
        adapter = new FileDataAdapter(getContext(),R.layout.file,listData);
        listView.setAdapter(adapter);

        return v;
    }

    public void updateData(Update update){
        adapter.clear();
        int i = 0;
        while(i<update.largeToptenFilenames.length){
            FileDataItem fileDataItem = new FileDataItem();
            fileDataItem.name = update.largeToptenFilenames[i];
            fileDataItem.size = Long.toString(update.largeToptenFilesizes[i]);
            adapter.add(fileDataItem);
            i++;
        }
        adapter.notifyDataSetChanged();
    }

    public static class FileDataItem{
        public String name ;
        public String size ;
    }

}