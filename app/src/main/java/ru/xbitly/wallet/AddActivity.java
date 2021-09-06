package ru.xbitly.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shashank.sony.fancytoastlib.FancyToast;

import ru.xbitly.wallet.Database.Card;
import ru.xbitly.wallet.Database.DatabaseClient;

public class AddActivity extends AppCompatActivity {

    private int barcode_type_id = 1;
    private String card_color = "#DEFFDB";
    private boolean edit_card_ok = false;
    private boolean edit_barcode_ok = false;
    private TextView qrBtn;
    private TextView eanBtn;
    private TextView codeBtn;
    private TextView otherBtn;
    private View greenColorBtn;
    private View blueColorBtn;
    private View redColorBtn;
    private View pinkColorBtn;
    private View yellowColorBtn;
    private View turquoiseColorBtn;
    private View orangeColorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ImageButton backBtn = findViewById(R.id.back_btn);
        TextView saveBtn = findViewById(R.id.save_btn);
        EditText cardNameEdit = findViewById(R.id.card_name_edittext);
        View cardNameLine = findViewById(R.id.card_name_line);
        TextView cardNameCharLimit = findViewById(R.id.card_name_text);
        EditText barcodeDataEdit = findViewById(R.id.barcode_data_edittext);
        View barcodeDataLine = findViewById(R.id.barcode_data_line);
        TextView barcodeDataCharLimit = findViewById(R.id.barcode_data_text);
        qrBtn = findViewById(R.id.qr_btn);
        eanBtn = findViewById(R.id.ean13_btn);
        codeBtn = findViewById(R.id.code128_btn);
        otherBtn = findViewById(R.id.other_btn);
        greenColorBtn = findViewById(R.id.round_select_green);
        blueColorBtn = findViewById(R.id.round_select_blue);
        redColorBtn = findViewById(R.id.round_select_red);
        pinkColorBtn = findViewById(R.id.round_select_pink);
        yellowColorBtn = findViewById(R.id.round_select_yellow);
        turquoiseColorBtn = findViewById(R.id.round_select_torquoise);
        orangeColorBtn = findViewById(R.id.round_select_orange);

        //тип баркода
        qrBtn.setOnClickListener(view -> {
            barcode_type_id = 1;
            selectBarcodeType(qrBtn);
            barcodeDataCharLimit.setText("0/32");
            barcodeDataEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });
            barcodeDataEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        });

        eanBtn.setOnClickListener(view -> {
            barcode_type_id = 2;
            selectBarcodeType(eanBtn);
            barcodeDataCharLimit.setText("0/12");
            barcodeDataEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
            barcodeDataEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        codeBtn.setOnClickListener(view -> {
            barcode_type_id = 3;
            selectBarcodeType(codeBtn);
            barcodeDataCharLimit.setText("0/32");
            barcodeDataEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });
            barcodeDataEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        otherBtn.setOnClickListener(view -> {
            barcode_type_id = 4;
            selectBarcodeType(otherBtn);
            barcodeDataCharLimit.setText("0/32");
            barcodeDataEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });
            barcodeDataEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        });

        //цвет карты
        greenColorBtn.setOnClickListener(view -> {
            card_color = "#DEFFDB";
            selectCardColor(greenColorBtn);
        });
        blueColorBtn.setOnClickListener(view -> {
            card_color = "#DBE5FF";
            selectCardColor(blueColorBtn);
        });
        redColorBtn.setOnClickListener(view -> {
            card_color = "#FFDBDB";
            selectCardColor(redColorBtn);
        });
        pinkColorBtn.setOnClickListener(view -> {
            card_color = "#FEDBFF";
            selectCardColor(pinkColorBtn);
        });
        yellowColorBtn.setOnClickListener(view -> {
            card_color = "#FCFFDB";
            selectCardColor(yellowColorBtn);
        });
        turquoiseColorBtn.setOnClickListener(view -> {
            card_color = "#DBFFFB";
            selectCardColor(turquoiseColorBtn);
        });
        orangeColorBtn.setOnClickListener(view -> {
            card_color = "#FFECDB";
            selectCardColor(orangeColorBtn);
        });

        cardNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cardNameCharLimit.setText(s.toString().length() + "/20");
                if (s.toString().length() < 1) {
                    cardNameCharLimit.setTextColor( getResources().getColor(R.color.red));
                    cardNameLine.setBackground( getResources().getDrawable(R.drawable.back_line_error));
                    edit_card_ok = false;
                } else {
                    cardNameCharLimit.setTextColor( getResources().getColor(R.color.black));
                    cardNameLine.setBackground( getResources().getDrawable(R.drawable.back_line_basic));
                    edit_card_ok = true;
                }
            }
        });

        barcodeDataEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (barcode_type_id == 2) {
                    barcodeDataCharLimit.setText(s.toString().length() + "/12");
                } else {
                    barcodeDataCharLimit.setText(s.toString().length() + "/32");
                }
                if (barcode_type_id == 2 && s.toString().length() < 12) {
                    barcodeDataCharLimit.setTextColor( getResources().getColor(R.color.red));
                    barcodeDataLine.setBackground( getResources().getDrawable(R.drawable.back_line_error));
                    edit_barcode_ok = false;

                } else if (s.toString().length() < 1){
                    barcodeDataCharLimit.setTextColor( getResources().getColor(R.color.red));
                    barcodeDataLine.setBackground( getResources().getDrawable(R.drawable.back_line_error));
                    edit_barcode_ok = false;
                } else {
                    barcodeDataCharLimit.setTextColor( getResources().getColor(R.color.black));
                    barcodeDataLine.setBackground( getResources().getDrawable(R.drawable.back_line_basic));
                    edit_barcode_ok = true;
                }
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent mainIntent = new Intent(AddActivity.this, MainActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.appearance, R.anim.disappearance);
            finish();
        });

        saveBtn.setOnClickListener(view -> {
            if (edit_card_ok && edit_barcode_ok) {
                class SaveCard extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {

                        Card card = new Card();
                        card.setCardName(cardNameEdit.getText().toString());
                        card.setCardColor(card_color);
                        card.setBarcodeData(barcodeDataEdit.getText().toString());
                        if (barcode_type_id == 1) {
                            card.setBarcodeFormat("QR_CODE");
                        }
                        if (barcode_type_id == 2) {
                            card.setBarcodeFormat("EAN_13");
                        }
                        if (barcode_type_id == 3) {
                            card.setBarcodeFormat("CODE_128");
                        }
                        if (barcode_type_id == 4) {
                            card.setBarcodeFormat("PDF_417");
                        }

                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .cardDao()
                                .insert(card);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        FancyToast.makeText(AddActivity.this, "Saved", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();
                        Intent mainIntent = new Intent(AddActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.appearance, R.anim.disappearance);
                        finish();
                    }
                }

                SaveCard saveCard = new SaveCard();
                saveCard.execute();
            } else {
                FancyToast.makeText(this, "Check the entered data", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
            }
        });
    }

    private void selectBarcodeType(TextView selectBtn){
        qrBtn.setTextColor( getResources().getColor(R.color.black30));
        eanBtn.setTextColor( getResources().getColor(R.color.black30));
        codeBtn.setTextColor( getResources().getColor(R.color.black30));
        otherBtn.setTextColor( getResources().getColor(R.color.black30));
        selectBtn.setTextColor( getResources().getColor(R.color.black));
    }

    private void selectCardColor(View selectBtn){
        greenColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        blueColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        redColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        pinkColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        yellowColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        turquoiseColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        orangeColorBtn.setBackgroundColor(getResources().getColor(R.color.black30));
        selectBtn.setBackgroundColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(AddActivity.this, MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.appearance, R.anim.disappearance);
        finish();
    }
}