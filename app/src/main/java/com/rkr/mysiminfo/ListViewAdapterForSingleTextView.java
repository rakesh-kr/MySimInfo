package com.rkr.mysiminfo;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapterForSingleTextView extends BaseAdapter {
    Activity context;
    String items[];

    int layout_id;

    public ListViewAdapterForSingleTextView(Activity context, String[] items,int id) {
        super();
        this.context = context;
        this.items = items;
        this.layout_id=id;
    }

    public int getCount() {
        return items.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtViewTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(layout_id, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(items[position]);

        return convertView;
    }

}