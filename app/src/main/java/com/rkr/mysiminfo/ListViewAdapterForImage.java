package com.rkr.mysiminfo;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ListViewAdapterForImage extends BaseAdapter {
    Activity context;
    String title[];
    Integer myImgId[];

    public ListViewAdapterForImage(Activity context, String[] title, Integer[] imgId) {
        super();
        this.context = context;
        this.title = title;
        this.myImgId = imgId;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return title.length;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        ImageView myImageView;
        TextView txtViewTitle;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.display_image_text_list_item, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.list_textView);
            holder.myImageView = (ImageView) convertView.findViewById(R.id.list_image_id);

            convertView.setTag(holder);
            holder.myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(title[position]);
        holder.myImageView.setImageResource(myImgId[position]);

        return convertView;
    }


}