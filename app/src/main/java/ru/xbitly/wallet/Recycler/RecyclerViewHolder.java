package ru.xbitly.wallet.Recycler;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.xbitly.wallet.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private final TextView cardName;
    private final RelativeLayout cardBackground;
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        cardName = itemView.findViewById(R.id.card_name);
        cardBackground = itemView.findViewById(R.id.card_background);
    }

    public TextView getCardName(){
        return cardName;
    }

    public RelativeLayout getCardBackground(){
        return cardBackground;
    }
}