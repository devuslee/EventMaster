package com.example.eventmanager;

import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends ArrayAdapter<Event> {
    Context context;

    DBHelper DB;
    List<Event> events;
    int resource;


    public EventsAdapter(@NonNull Context context, int resource, List<Event> events, DBHelper dbHelper) {
        super(context, resource, events);
        this.context = context;
        this.events = events;
        this.resource = resource;
        this.DB = dbHelper; //
    }

    static class ViewHolder {
        TextView eventname;
        TextView type;
        TextView time;
        TextView interested;
        TextView status;
        ImageView eventimage;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        DB = new DBHelper(context);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.eventname = convertView.findViewById(R.id.eventname);
            holder.type = convertView.findViewById(R.id.type);
            holder.time = convertView.findViewById(R.id.time);
            holder.eventimage = convertView.findViewById(R.id.eventimage);
            holder.interested = convertView.findViewById(R.id.interested);
            holder.status = convertView.findViewById(R.id.status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setBackgroundResource(R.drawable.list_item_border);

        Event event = events.get(position);
        int interestedcount = DB.countFavourite(event.getEventID());
        int statusvalue = event.getStatus();

        if (statusvalue == 0) {
            holder.status.setText("Status: Pending");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.Yellow)); // Set to the color resource for yellow
            holder.status.setVisibility(View.VISIBLE);
        } else if (statusvalue == 1) {
            holder.status.setText("Status: Approved");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.Green)); // Set to the color resource for green
            holder.status.setVisibility(View.VISIBLE);
        } else if (statusvalue == 2) {
            holder.status.setText("Status: Rejected");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.Red)); // Set to the color resource for red
            holder.status.setVisibility(View.VISIBLE);
        } else {
            holder.status.setVisibility(View.GONE); // or set any default behavior if statusvalue is not 0, 1, or 2
        }

        long startDatetime = event.getStartdatetime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(startDatetime));

        holder.eventname.setText(event.getName());
        holder.type.setText(event.getType());
        holder.time.setText(formattedDate);
        holder.interested.setText("Participants: " + String.valueOf(interestedcount));

        if (event.getImage() != null && event.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(event.getImage(), 0, event.getImage().length);
            holder.eventimage.setImageBitmap(bitmap);
        } else {
            holder.eventimage.setImageResource(R.drawable.noimage);
        }

        return convertView;
    }


}