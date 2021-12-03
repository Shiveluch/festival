package com.shiveluch.festival;




import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PoligonsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Poligons> objects;

    protected ListView mListView;
    public PoligonsAdapter(Context context, ArrayList<Poligons> stalkers, Activity activity) {
        super();
        ctx = context;
        objects = stalkers;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView = activity.findViewById(R.id.location);
    }

    // ???-?? ?????????
    @Override
    public int getCount() {
        return objects.size();
    }

    // ??????? ?? ???????
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.locationadapter, parent, false);
        }

        Poligons p = getPoligons(position);
        ((TextView) view.findViewById(R.id.locationname)).setText(p.name);
        ((TextView) view.findViewById(R.id.locationid)).setText(p.id);


        return view;
    }

    Poligons getPoligons(int position) {
        return ((Poligons) getItem(position));
    }

}