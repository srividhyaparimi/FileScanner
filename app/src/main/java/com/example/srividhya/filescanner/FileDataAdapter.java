package com.example.srividhya.filescanner;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
/**
 * Created by srividhya on 7/24/2017.
 */
public class FileDataAdapter extends ArrayAdapter<DataFileFragment.FileDataItem> {
    private Context c;
    private List<DataFileFragment.FileDataItem> data;
    private int ID;

    public FileDataAdapter(Context context, int resource,List<DataFileFragment.FileDataItem> data) {
        super(context, resource);
        this.c = context;
        this.ID = resource;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list = convertView;
        ViewHolder holder = null;

        if(list == null)
        {
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            list = inflater.inflate(ID, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView)list.findViewById(R.id.filenameview);
            holder.size = (TextView)list.findViewById(R.id.filesizeview);

            list.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)list.getTag();
        }

        DataFileFragment.FileDataItem dataHeld = data.get(position);

        if (dataHeld.name.length() > 15) {
            holder.name.setText(dataHeld.name.substring(0, 15));
        } else {
            holder.name.setText(dataHeld.name);
        }
        holder.size.setText(dataHeld.size);
        return list;
    }

    @Override
    public void clear() {
        super.clear();
        data.clear();
        notifyDataSetChanged();
    }

    private static class ViewHolder{
        TextView name;
        TextView size;
    }

    @Override
    public void add(DataFileFragment.FileDataItem object) {
        super.add(object);
        data.add(object);
        notifyDataSetChanged();
    }


}
