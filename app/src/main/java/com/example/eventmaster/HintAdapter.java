package com.example.eventmaster;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HintAdapter extends ArrayAdapter<String> {
    public HintAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        if (position == 0) {
            ((TextView) v).setTextColor(Color.GRAY); // Set hint color if needed
        }
        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0; // Allow selection for positions other than 0
    }
}