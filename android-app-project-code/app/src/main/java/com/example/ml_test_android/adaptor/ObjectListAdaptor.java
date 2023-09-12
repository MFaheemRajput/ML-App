package com.example.ml_test_android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ml_test_android.R;
import com.example.ml_test_android.models.ObjectModel;

import java.util.ArrayList;
public class ObjectListAdaptor extends RecyclerView.Adapter<ObjectListAdaptor.ObjectsViewHolder> {

    private ArrayList<ObjectModel> localDataSet;

    public ObjectListAdaptor(ArrayList<ObjectModel> dataSet){
        setLocalDataSet(dataSet);
    }

    public void setLocalDataSet(ArrayList<ObjectModel> dataSet) {
        localDataSet = dataSet;
    }

    public static class ObjectsViewHolder extends RecyclerView.ViewHolder {
        private final TextView objectName;
        private final TextView percentAge;

        public ObjectsViewHolder(View view) {
            super(view);
            objectName = view.findViewById(R.id.objectName);
            percentAge = view.findViewById(R.id.percentAge);
        }

        public void bind(ObjectModel objectModel) {
            objectName.setText(objectModel.getName());
            percentAge.setText(objectModel.getProb().toString()+"%");
        }
    }

    @Override
    public ObjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View photoView = inflater.inflate(R.layout.object_cell, parent, false);

        ObjectsViewHolder viewHolder = new ObjectsViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ObjectsViewHolder holder, int position) {
        ObjectModel objectModel = localDataSet.get(position);
        holder.bind(objectModel);
    }
    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
