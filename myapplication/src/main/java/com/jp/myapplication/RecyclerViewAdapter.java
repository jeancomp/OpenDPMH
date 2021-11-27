package com.jp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<ActiveDataProcessor> activeDataProcessorList = new ArrayList();

    public RecyclerViewAdapter(Context context, List<ActiveDataProcessor> activeDataProcessors){
        this.context = context;
        this.activeDataProcessorList = activeDataProcessors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_main_list_item, viewGroup, false);
        ViewHolder adpvh = new ViewHolder(v);
        return adpvh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(activeDataProcessorList == null){
            viewHolder.dataProcessorName.setText("No data processors enabled!");
        }
        else {
            viewHolder.dataProcessorName.setText(activeDataProcessorList.get(position).getDataProcessorName());
        }
    }

    @Override
    public int getItemCount() {
        return activeDataProcessorList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView dataProcessorName;

        public ViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_list_item);
            dataProcessorName = (TextView) itemView.findViewById(R.id.item_name);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), StreamActivity.class);
                    i.putExtra("dataprocessorname", dataProcessorName.getText());
                    Log.i("RecyclerViewAdapter","#### DataProcessorName selected:" + dataProcessorName.getText());
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}
