package com.example.sebastian.appdrawer.appdrawer;

import android.content.Context;
import android.content.Intent;
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

    Context ctx;
    public RecyclerAdapter(ArrayList<Item> arrayList,Context ctx){
        this.arrayList = arrayList;
        this.ctx =ctx;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,ctx,arrayList);

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

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView title,creator,price;
        private View container;
        ArrayList<Item> items = new ArrayList<Item>();
        Context ctx;

        public RecyclerViewHolder(View view, Context ctx, ArrayList<Item> items){
            super(view);
            this.items = items;
            this.ctx = ctx;
            imageView = (ImageView)view.findViewById(R.id.img);
            title = (TextView)view.findViewById(R.id.itemTitle);
            creator = (TextView)view.findViewById(R.id.itemCreator);
            price = (TextView)view.findViewById(R.id.itemPrice);
            container = view.findViewById(R.id.item_container);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
                Item item = this.items.get(position);
                Intent intent = new Intent(ctx,ItemDetails.class);
            intent.putExtra("item_img",item.getPhotoId());
            intent.putExtra("item_creator",item.getCreator());
            intent.putExtra("item_price",item.getPrice());
            intent.putExtra("item_title",item.getTitle());
            this.ctx.startActivity(intent);


        }

    }

}
