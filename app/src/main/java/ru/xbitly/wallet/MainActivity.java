package ru.xbitly.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.shashank.sony.fancytoastlib.FancyToast;
import java.util.Calendar;
import java.util.List;

import ru.xbitly.wallet.CardStackView.CardStackAdapter;
import ru.xbitly.wallet.Database.Card;
import ru.xbitly.wallet.Database.DatabaseClient;

public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener{

    RelativeLayout noFound;
    View searchLine;
    ImageView searchIcon;
    TextView ycTxt;
    private long backPressedTime;
    private CardStackView mStackView;
    private CardStackAdapter mTestStackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView gmTxt = findViewById(R.id.gnma_txt);
        ycTxt = findViewById(R.id.yc_txt);
        ImageButton addBtn = findViewById(R.id.add_btn);
        mStackView = findViewById(R.id.recycler);
        searchLine = findViewById(R.id.search_line);
        EditText searchEdit = findViewById(R.id.search_edittext);
        noFound = findViewById(R.id.no_found_window);
        searchIcon = findViewById(R.id.img1);

        mStackView.setItemExpendListener(this);

        //приветсвенное сообщение
        int hours = Calendar.getInstance().getTime().getHours();
        String[] hello = {"Good morning", "Good afternoon", "Good evening", "Good night"};
        if(hours >= 6 && hours < 12){
            gmTxt.setText(hello[0]);
        } else if(hours >= 12 && hours < 18){
            gmTxt.setText(hello[1]);
        } else if(hours >= 18 && hours < 23){
            gmTxt.setText(hello[2]);
        } else {
            gmTxt.setText(hello[3]);
        }

        addBtn.setOnClickListener(view -> {
            Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(addIntent);
            overridePendingTransition(R.anim.appearance, R.anim.disappearance);
            finish();
        });

        loadCards("");

        searchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadCards(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    private void loadCards(String search_text){
        class GetCards extends AsyncTask<Void, Void, List<Card>> {

            @Override
            protected List<Card> doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .cardDao()
                        .getAll();
            }

            @Override
            protected void onPostExecute(List<Card> cards) {
                super.onPostExecute(cards);
                if(cards.size() > 0){
                    ycTxt.setText(R.string.c);
                } else {
                    ycTxt.setText(R.string.y);
                }
                mTestStackAdapter = new CardStackAdapter(cards, noFound, searchLine, getResources(), searchIcon, MainActivity.this);
                mStackView.setAdapter(mTestStackAdapter);
                mTestStackAdapter.updateData(cards);
                mTestStackAdapter.search(search_text);
            }
        }

        GetCards getCards = new GetCards();
        getCards.execute();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            FancyToast.makeText(this, "Click again to exit", FancyToast.LENGTH_SHORT, FancyToast.INFO,false).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onItemExpend(boolean expend) {

    }
}