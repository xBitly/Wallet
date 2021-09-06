package ru.xbitly.wallet.Recycler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ru.xbitly.wallet.CardActivity;
import ru.xbitly.wallet.Database.Card;
import ru.xbitly.wallet.R;


public class ListAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final List<Card> cardList;
    private RelativeLayout noFound;
    private View searchLine;
    private Resources resources;
    private ImageView searchIcon;
    private Context context;

    public ListAdapter(List<Card> cardList, RelativeLayout noFound, View searchLine, Resources resources, ImageView searchIcon, Context context) {
        this.cardList = cardList;
        this.noFound = noFound;
        this.searchLine = searchLine;
        this.resources = resources;
        this.searchIcon = searchIcon;
        this.context = context;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_card;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        int count = cardList.size() - position - 1;
        holder.getCardName().setText(cardList.get(count).getCardName());
        holder.getCardBackground().setBackgroundColor(Color.parseColor(cardList.get(count).getCardColor()));
        holder.getCardBackground().setOnClickListener(view -> {
            Intent cardIntent = new Intent(context, CardActivity.class);
            cardIntent.putExtra("card", cardList.get(count));
            context.startActivity(cardIntent);
            ((Activity) context).overridePendingTransition(R.anim.appearance, R.anim.disappearance);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void search(String search_text){
        searchLine.setBackground( resources.getDrawable(R.drawable.back_line_basic));
        searchIcon.setImageDrawable( resources.getDrawable(R.drawable.ic_search));
        if (!search_text.equals("")){
            int i = 0;
            while (i < cardList.size()){
                if(!cardList.get(i).getCardName().toLowerCase().contains(search_text.toLowerCase())){
                    cardList.remove(cardList.get(i));
                    i = 0;
                } else {
                    i++;
                }
            }
            notifyDataSetChanged();
            if (cardList.isEmpty()) {
                searchLine.setBackground( resources.getDrawable(R.drawable.back_line_error));
                searchIcon.setImageDrawable( resources.getDrawable(R.drawable.ic_search_error));
            }
        }
        if (cardList.isEmpty()){
            noFound.setVisibility(RelativeLayout.VISIBLE);
        } else {
            noFound.setVisibility(RelativeLayout.GONE);
        }
    }
}