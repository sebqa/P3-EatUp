package com.example.sebastian.appdrawer.appdrawer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sebastian.appdrawer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sebastian on 02-11-2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    int position;
    double haverdistanceKM;
    private ArrayList<Item> arrayList = new ArrayList<Item>();

    Context ctx;

    //Constructor for the RecyclerAdapter
    public RecyclerAdapter(ArrayList<Item> arrayList,Context ctx){
        this.arrayList = arrayList;
        this.ctx =ctx;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Specify the layout of the RecyclerView, in this case 'item_layout'.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,ctx,arrayList);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        //Attach the values we retrieve from the Item class to the values.
        Item item = arrayList.get(position);
        Picasso.with(holder.ctx)
                .load(item.getDownloadUrl())
                .resize(90,90)
                .centerCrop()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.progress_animation )
                .into(holder.imageView);
        holder.title.setText(item.getTitle());
        holder.creator.setText(item.getCreator());
        holder.price.setText(item.getPrice());
        holder.amount.setText(""+item.getAmount());
        holder.timeStamp.setText(item.getCurrentTime());
        //Calculate distance between the two points
        haversine(MainActivity.mLatitude,MainActivity.mLongitude,item.getLatitude(),item.getLongitude());
        //Reduce decimals for listview
        int temp = (int)(haverdistanceKM*1000);
        int shortDouble = (temp);
        holder.distance.setText((shortDouble)+" m");


        this.position = position;
    }

    public void haversine(double lat1, double lon1, double lat2, double lon2) {
        double Rad = 6372.8; //Earth's Radius In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        haverdistanceKM = Rad * c;

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;

        TextView title,creator,price,amount,distance,timeStamp;
        private View container;
        ArrayList<Item> items = new ArrayList<Item>();
        Context ctx;

        //Constructor for the view holder.
        public RecyclerViewHolder(View view, Context ctx, ArrayList<Item> items){
            super(view);
            this.items = items;
            this.ctx = ctx;

            //Cast the values to a Text or ImageView in the layout.
            imageView = (ImageView)view.findViewById(R.id.img);
            title = (TextView)view.findViewById(R.id.itemTitle);
            creator = (TextView)view.findViewById(R.id.itemCreator);
            price = (TextView)view.findViewById(R.id.itemPrice);
            amount = (TextView)view.findViewById(R.id.itemAmount);
            distance = (TextView)view.findViewById(R.id.itemDistance);
            timeStamp = (TextView)view.findViewById(R.id.timeStamp);
            container = view.findViewById(R.id.item_container);



            //Set an onClickListener to the entire view.
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            //Find out which item was clicked
            int position = getAdapterPosition();
                Item item = this.items.get(position);
            if(item != null) {
                //Create new intent that gets us to the next activity.
                Intent intent = new Intent(ctx, ItemDetails.class);
                //Parse the information between the activities.
                intent.putExtra("item_key", item.getKey());

                //Start the new activity.
                this.ctx.startActivity(intent);
            }else{
                Toast.makeText(view.getContext(), "Item no longer exists",
                        Toast.LENGTH_SHORT).show();

            }


        }

    }


}
