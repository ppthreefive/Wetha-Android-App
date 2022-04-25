package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.Models.*;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ForecastActivityAdapter extends RecyclerView.Adapter<ForecastActivityAdapter.ForecastViewHolder> implements View.OnClickListener {

    private List<FullDayCard> mFullDays;
    View.OnClickListener onClickListener;

    public ForecastActivityAdapter(List<FullDayCard> fullDays) {
        this.mFullDays = fullDays;
    }

    public boolean onClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return true;
    }

    @Override
    public void onClick(View v) {
        onClickListener.onClick(v);
    }

    @NonNull
    @Override
    public ForecastActivityAdapter.ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(view);
        view.setOnClickListener(this);
        return forecastViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastActivityAdapter.ForecastViewHolder holder, int position) {
        FullDayCard currentFullDay = mFullDays.get(position);

        Picasso.get().load(currentFullDay.getDayImageLocation()).into(holder.mDayImage);
        Picasso.get().load(currentFullDay.getNightImageLocation()).into(holder.mNightImage);
        holder.mDayTempText.setText(holder.mDayTempText.getText() + " " + Integer.toString(currentFullDay.getDayTemp()) + currentFullDay.getTempUnit());
        holder.mNightTempText.setText(holder.mNightTempText.getText() + " " + Integer.toString(currentFullDay.getNightTemp()) + currentFullDay.getTempUnit());
        holder.mDayForecastText.setText(currentFullDay.getDayShortForecast());
        holder.mNightForecastText.setText(currentFullDay.getNightShortForecast());
        holder.mName.setText(currentFullDay.getName());
    }

    @Override
    public int getItemCount() {
        return mFullDays.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        public ImageView mDayImage;
        public ImageView mNightImage;
        public TextView mDayTempText;
        public TextView mNightTempText;
        public TextView mDayForecastText;
        public TextView mNightForecastText;
        public CardView mCard;
        public TextView mName;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.forecastCard);
            mDayImage = itemView.findViewById(R.id.todayIcon);
            mNightImage = itemView.findViewById(R.id.tonightIcon);
            mDayTempText = itemView.findViewById(R.id.dayTemp);
            mNightTempText = itemView.findViewById(R.id.nightTemp);
            mDayForecastText = itemView.findViewById(R.id.dayForecast);
            mNightForecastText = itemView.findViewById(R.id.nightForecast);
            mName = itemView.findViewById(R.id.dayName);
        }
    }
}
