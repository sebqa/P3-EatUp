package com.example.sebastian.appdrawer.appdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int position;
    public double haverdistanceKM;
    private  static final int AD_VIEW_TYPE = 1;
    private  static final int MENU_ITEM_VIEW_TYPE = 0;
    boolean showedAd;
    ArrayList<Item> arrayList = new ArrayList<Item>();
    ArrayList<Item> arrayList2 = new ArrayList<Item>();

    int posOffset = -1;

    Context ctx;

    //Constructor for the RecyclerAdapter
    public RecyclerAdapter(ArrayList<Item> arrayList,Context ctx){
        this.arrayList = arrayList;
        this.ctx =ctx;
    }


    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GenericViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case AD_VIEW_TYPE:
                View v = inflater.inflate(R.layout.native_express_ad_container,parent,false);
                viewHolder = new AdRecyclerViewHolder(v);
                break;
            case MENU_ITEM_VIEW_TYPE:
                default:
                    View v2 = inflater.inflate(R.layout.item_layout,parent,false);
                    viewHolder = new mRecyclerViewHolder(v2,ctx,arrayList);

        }
        //Specify the layout of the RecyclerView, in this case 'item_layout'.

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Item item;
        switch (holder.getItemViewType()){
            case AD_VIEW_TYPE:
                showedAd = true;
                break;
            case MENU_ITEM_VIEW_TYPE:

                mRecyclerViewHolder mholder = (mRecyclerViewHolder) holder;
                //Attach the values we retrieve from the Item class to the values.
                item = arrayList.get(position);

                if(item != null) {

                    Picasso.with(mholder.ctx)
                            .load(item.getDownloadUrl())
                            .resize(90, 90)
                            .centerCrop()
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.progress_animation)
                            .into(mholder.imageView);

                    mholder.title.setText(item.getTitle());
                    mholder.creator.setText(item.getCreator());
                    if (item.getPrice().equals("10")) {
                        mholder.price.setText("Free");
                    } else {
                        mholder.price.setText(item.getPrice() + " kr");
                    }
                    mholder.amount.setText("" + item.getAmount());
                    mholder.timeStamp.setText(item.getCurrentTime());
                    //Calculate distance between the two points
                    haversine(MainActivity.mLatitude, MainActivity.mLongitude, item.getLatitude(), item.getLongitude());
                    //Reduce decimals for listview
                    int temp = (int) (haverdistanceKM * 1000);
                    int shortDouble = (temp);
                    mholder.distance.setText((shortDouble) + " m");


                    this.position = position;
                }
                break;
        }

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
    public int getItemViewType(int position){
        return (position % 8 == 0) ? AD_VIEW_TYPE:MENU_ITEM_VIEW_TYPE;
    }

    public class mRecyclerViewHolder extends GenericViewHolder{

        ImageView imageView;
        TextView title,creator,price,amount,distance,timeStamp;
        private View container;
        ArrayList<Item> items = new ArrayList<Item>();
        Context ctx;

        //Constructor for the view holder.
        public mRecyclerViewHolder(View view, Context ctx, ArrayList<Item> items){
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
                ((Activity) ctx).overridePendingTransition(R.anim.slidein, 0);

            }else{
                Toast.makeText(view.getContext(), "Item no longer exists",
                        Toast.LENGTH_SHORT).show();

            }


        }

    }
    public static class AdRecyclerViewHolder extends GenericViewHolder{

        ImageView imageView;
        TextView title,creator,price,amount,distance,timeStamp;
        private View container;
        ArrayList<Item> items = new ArrayList<Item>();
        Context ctx;

        //Constructor for the view holder.
        public AdRecyclerViewHolder(View view){
            super(view);
            this.items = items;
            this.ctx = ctx;

            //Cast the values to a Text or ImageView in the layout.
            imageView = (ImageView)view.findViewById(R.id.img);
            creator= (TextView)view.findViewById(R.id.itemTitle);
            title= (TextView)view.findViewById(R.id.itemCreator);
            price = (TextView)view.findViewById(R.id.itemPrice);
            amount = (TextView)view.findViewById(R.id.itemAmount);
            distance = (TextView)view.findViewById(R.id.itemDistance);
            timeStamp = (TextView)view.findViewById(R.id.timeStamp);
            container = view.findViewById(R.id.item_container);



            //Set an onClickListener to the entire view.
            view.setOnClickListener(this);


        }



        public void onClick(View view) {

            Toast.makeText(ctx, "REKLAME", Toast.LENGTH_SHORT).show();


        }

    }

}
