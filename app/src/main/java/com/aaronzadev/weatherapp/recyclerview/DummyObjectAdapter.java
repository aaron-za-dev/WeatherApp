package com.aaronzadev.weatherapp.recyclerview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaronzadev.weatherapp.DetailActivity;
import com.aaronzadev.weatherapp.R;
import com.aaronzadev.weatherapp.pojo.DummyObject;

import java.util.List;

public class DummyObjectAdapter extends RecyclerView.Adapter<DummyObjectAdapter.DummyObjectHolder>  {

    private final List<DummyObject> items;

    public DummyObjectAdapter(List<DummyObject> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DummyObjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new DummyObjectHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DummyObjectHolder dummyObjectHolder, int i) {

        dummyObjectHolder.txtTitle.setText(items.get(i).getTitle());
        //dummyObjectHolder.itemView.setTag(items.get(i));

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size():0;
    }


    class DummyObjectHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle;

        public DummyObjectHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            //txtDescription = itemView.findViewById(R.id.txtDesc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){

                        DummyObject selectedItem = items.get(pos);

                        Intent i = new Intent(v.getContext(), DetailActivity.class);
                        i.putExtra("ItmTitle", selectedItem.getTitle());
                        i.putExtra("ItmDesc", selectedItem.getDescription());

                        v.getContext().startActivity(i);
                    }

                }
            });

        }
    }

}
