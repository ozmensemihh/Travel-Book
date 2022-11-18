package com.example.travelfragment.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelfragment.databinding.RowListBinding;
import com.example.travelfragment.model.Travel;
import com.example.travelfragment.view.ListFragmentDirections;

import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelHolder> {

    private List<Travel> list;

    public TravelAdapter(List<Travel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TravelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowListBinding binding = RowListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new TravelHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelHolder holder, int position) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(list.get(position).image,0,list.get(position).image.length);
        holder.binding.imgResim.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListFragmentDirections.ActionListFragmentToDetailsFragment directions = ListFragmentDirections.actionListFragmentToDetailsFragment("old",list.get(position).getId());

                Navigation.findNavController(view).navigate(directions);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class TravelHolder extends RecyclerView.ViewHolder{

        private RowListBinding binding;

        public TravelHolder(RowListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
