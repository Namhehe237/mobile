package com.map.nguyennhatminh.demo2.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.map.nguyennhatminh.demo2.R;

public class IconAdapter extends BaseAdapter {
    private Context context;
    private int[] iconIds = {
            R.drawable.book,
            R.drawable.home,
            R.drawable.school,
            R.drawable.cart,
            R.drawable.gift,
            R.drawable.hang_out,
            R.drawable.salary
    };

    public IconAdapter(Context context, int[] iconIds) {
        this.context = context;
        this.iconIds = iconIds;
    }

    @Override
    public int getCount() {
        return iconIds.length;
    }

    @Override
    public Object getItem(int position) {
        return iconIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.icon_spinner_item, parent, false);
        }

        ImageView imgIcon = convertView.findViewById(R.id.imgIcon);

        imgIcon.setImageResource(iconIds[position]);

        return convertView;
    }
}
