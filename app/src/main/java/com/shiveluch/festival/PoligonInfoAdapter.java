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

public class PoligonInfoAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<PoligonInfo> objects;

    protected ListView mListView;
    public PoligonInfoAdapter(Context context, ArrayList<PoligonInfo> stalkers, Activity activity) {
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
            view = lInflater.inflate(R.layout.locationinfoadapter, parent, false);
        }

        PoligonInfo p = getPoligonInfo(position);
        ((TextView) view.findViewById(R.id.name)).setText(p.name);
        ((TextView) view.findViewById(R.id.info)).setText(p.info);
        ((TextView) view.findViewById(R.id.field)).setText(p.field);



        return view;
    }

    PoligonInfo getPoligonInfo(int position) {
        return ((PoligonInfo) getItem(position));
    }

}