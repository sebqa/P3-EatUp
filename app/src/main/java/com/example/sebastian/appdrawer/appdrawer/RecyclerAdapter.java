package com.example.sebastian.appdrawer.appdrawer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.appdrawer.R;

import java.util.ArrayList;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
int position;
    private ArrayList<Item> arrayList = new ArrayList<Item>();

    public RecyclerAdapter(ArrayList<Item> arrayList){
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Item item = arrayList.get(position);
        holder.imageView.setImageResource(item.getPhotoId());
        holder.title.setText(item.getTitle());
        holder.creator.setText(item.getCreator());
        holder.price.setText(item.getPrice());

        this.position = position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title,creator,price;

        public RecyclerViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.img);
            title = (TextView)view.findViewById(R.id.itemTitle);
            creator = (TextView)view.findViewById(R.id.itemCreator);
            price = (TextView)view.findViewById(R.id.itemPrice);

        }
    }
}
