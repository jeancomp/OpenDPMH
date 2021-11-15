package com.jp.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;

public class ListDataProcessorAdapter extends RecyclerView.Adapter<ListDataProcessorAdapter.ViewHolder> {
    private static final String TAG = ListDataProcessorAdapter.class.getName();
    private Context context;
    private List<ListDataProcessor> listDataProcessors = new ArrayList();
    public List<String> dataProcessorBackup = new ArrayList();
    List<String> listProcessors = new ArrayList();

    public ListDataProcessorAdapter(Context context, List<ListDataProcessor> listDataProcessors, List<String> dataProcessorBackup){
        this.context = context;
        this.dataProcessorBackup = dataProcessorBackup;
        this.listDataProcessors = listDataProcessors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_add_activedataprocessor, viewGroup, false);
        ViewHolder adpvh = new ViewHolder(v);
        return adpvh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if(viewHolder == null){
            viewHolder.txtName_dataprocessor.setText("No data processors!");
        }
        else {
            viewHolder.txtName_dataprocessor.setText(listDataProcessors.get(position).getDataProcessorName());

            viewHolder.chbSelect.setText(viewHolder.txtName_dataprocessor.getText());

            viewHolder.chbSelect.setOnCheckedChangeListener(null);

            viewHolder.chbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    viewHolder.chbSelect.setChecked(isChecked);
                    Log.i(TAG,"#### Position: " + viewHolder.getAdapterPosition() + ", " + viewHolder.chbSelect.getText());
                    listProcessors.add(viewHolder.chbSelect.getText().toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listDataProcessors.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView txtName_dataprocessor;
        TextView txtDescription_dataprocessor;
        CheckBox chbSelect;

        public ViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtName_dataprocessor = (TextView) itemView.findViewById(R.id.txtName_dataprocessor);
            txtDescription_dataprocessor = (TextView)itemView.findViewById(R.id.txtDescription_dataprocessor);
            chbSelect = (CheckBox) itemView.findViewById(R.id.chbSelect);

        }
    }
}
