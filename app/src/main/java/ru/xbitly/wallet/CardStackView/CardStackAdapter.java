package ru.xbitly.wallet.CardStackView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.List;

import ru.xbitly.wallet.CardActivity;
import ru.xbitly.wallet.Database.Card;
import ru.xbitly.wallet.R;

public class CardStackAdapter extends StackAdapter<Card> {

    private final List<Card> cardList;
    private final RelativeLayout noFound;
    private final View searchLine;
    private final Resources resources;
    private final ImageView searchIcon;

    public CardStackAdapter(List<Card> cardList, RelativeLayout noFound, View searchLine, Resources resources, ImageView searchIcon, Context context) {
        super(context);
        this.cardList = cardList;
        this.noFound = noFound;
        this.searchLine = searchLine;
        this.resources = resources;
        this.searchIcon = searchIcon;
    }

    @Override
    public void bindView(Card data, int position, CardStackView.ViewHolder holder) {
        ColorItemViewHolder h = (ColorItemViewHolder) holder;
        h.onBind(cardList, position);
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        view = getLayoutInflater().inflate(R.layout.item_card, parent, false);
        return new ColorItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_card;
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {

        TextView cardName;
        RelativeLayout cardBackground;
        List<Card> cardListH;
        int position;

        public ColorItemViewHolder(View view) {
            super(view);
            cardName = itemView.findViewById(R.id.card_name);
            cardBackground = itemView.findViewById(R.id.card_background);
        }

        @Override
        public void onItemExpand(boolean b) {
            if (b) {
                Intent cardIntent = new Intent(getContext(), CardActivity.class);
                cardIntent.putExtra("card", cardListH.get(position));
                getContext().startActivity(cardIntent);
                ((Activity) getContext()).overridePendingTransition(R.anim.appearance, R.anim.disappearance);
                ((Activity) getContext()).finish();
            }
        }

        public void onBind(List<Card> cardList, int position) {
            int count = cardList.size() - position - 1;
            this.cardListH = cardList;
            this.position = count;
            cardName.setText(cardList.get(count).getCardName());
            cardBackground.setBackgroundColor(Color.parseColor(cardList.get(count).getCardColor()));
        }
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
            updateData(cardList);
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
