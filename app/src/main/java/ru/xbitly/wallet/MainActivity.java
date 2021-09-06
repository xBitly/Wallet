package ru.xbitly.wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.shashank.sony.fancytoastlib.FancyToast;
import java.util.Calendar;
import java.util.List;

import ru.xbitly.wallet.Database.Card;
import ru.xbitly.wallet.Database.DatabaseClient;
import ru.xbitly.wallet.Recycler.ListAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RelativeLayout noFound;
    View searchLine;
    ImageView searchIcon;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView gmTxt = findViewById(R.id.gnma_txt);
        ImageButton addBtn = findViewById(R.id.add_btn);
        recyclerView = findViewById(R.id.recycler);
        searchLine = findViewById(R.id.search_line);
        EditText searchEdit = findViewById(R.id.search_edittext);
        noFound = findViewById(R.id.no_found_window);
        searchIcon = findViewById(R.id.img1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

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
                ListAdapter adapter = new ListAdapter(cards, noFound, searchLine, getResources(), searchIcon, MainActivity.this);
                adapter.search(search_text);
                recyclerView.setAdapter(adapter);
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
}