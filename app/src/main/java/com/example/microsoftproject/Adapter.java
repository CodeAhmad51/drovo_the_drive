package com.example.microsoftproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ProgramingViewHolder> {

    private ItemDetail[] data;

    @NonNull
    @Override
    public ProgramingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view,parent,false);

        return new ProgramingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramingViewHolder holder, int position) {
        ItemDetail itemDetail = data[position];
        holder.item_name.setText(itemDetail.getItem_name());
        holder.item_image.setImageResource(itemDetail.getImgId());
        holder.size.setText(itemDetail.getSize());

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ProgramingViewHolder extends RecyclerView.ViewHolder{

        ImageView item_image;
        TextView item_name , size;
        public ProgramingViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name.findViewById(R.id.item_name);
            item_image.findViewById(R.id.item_image);
        }
    }


}
