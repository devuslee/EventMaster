package com.example.eventmaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmaster.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends ArrayAdapter<NotificationClass> {
    Context context;
    List<NotificationClass> notificationclasss;
    int resource;

    public NotificationAdapter(@NonNull Context context, int resource, List<NotificationClass> notificationclasss) {
        super(context, resource, notificationclasss);
        this.context = context;
        this.notificationclasss = notificationclasss;
        this.resource = resource;
    }

    static class ViewHolder {
        ImageView symbol;
        TextView notificationmessage;
        TextView time;
        TextView eventname;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            holder = new NotificationAdapter.ViewHolder();
            holder.symbol = convertView.findViewById(R.id.symbol);
            holder.notificationmessage = convertView.findViewById(R.id.notificationmessage);
            holder.time = convertView.findViewById(R.id.time);
            holder.eventname = convertView.findViewById(R.id.eventname);

            convertView.setTag(holder);
        } else {
            holder = (NotificationAdapter.ViewHolder) convertView.getTag();
        }

        NotificationClass notificationClass = notificationclasss.get(position);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(notificationClass.getTime());

            if (date != null) {
                // Extract components using Calendar
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                // Extract individual components
                int year = calendar.get(Calendar.YEAR);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                String monthString = monthFormat.format(date);


                holder.time.setText(dayOfMonth + " " + monthString + " " + hour + ":" + minute);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the case where the timestamp cannot be parsed
        }

        holder.notificationmessage.setText(notificationClass.getNotificationMessage());
        holder.eventname.setText(notificationClass.getEventname());

        if ("Update".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.update);
        } else if ("Cancel".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.cancel);
        } else if ("Reminder".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.reminder);
        } else if ("Add".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.add);
        } else if ("Removed".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.delete);
        } else if ("Approved".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.approved);
        } else if ("Rejected".equals(notificationClass.getType())) {
            holder.symbol.setImageResource(R.drawable.reject);
        }else {
            holder.symbol.setImageResource(R.drawable.noimage);
        }



        return convertView;
    }

}