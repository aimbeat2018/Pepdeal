package com.pepdeal.in.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pepdeal.in.R;
import com.pepdeal.in.model.location.Prediction;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutoCompleteAdapter
        extends RecyclerView.Adapter<PlaceAutoCompleteAdapter.PlacesViewHolder> {

    private Context mContext;
    private CharacterStyle styleBold;
    private CharacterStyle styleNormal;
    private List<Prediction> predictions;

    public PlaceAutoCompleteAdapter(Context context) {
        mContext = context;
        styleBold = new StyleSpan(Typeface.BOLD);
        styleNormal = new StyleSpan(Typeface.NORMAL);
        predictions = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_location, viewGroup, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder placesViewHolder, int i) {
        placesViewHolder.area.setText(predictions.get(i).getStructuredFormatting().getMainText());
        placesViewHolder.address.setText(predictions.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        private TextView area;
        private TextView address;

        PlacesViewHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
        }
    }
}
