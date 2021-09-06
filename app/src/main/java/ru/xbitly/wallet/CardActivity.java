package ru.xbitly.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import ru.xbitly.wallet.Database.Card;
import ru.xbitly.wallet.Database.DatabaseClient;

public class CardActivity extends AppCompatActivity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private EditText cardNameOnCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(params);

        TextView cardName = findViewById(R.id.card_name_on_card);
        cardNameOnCard = findViewById(R.id.card_name);
        ImageButton backBtn = findViewById(R.id.back_btn);
        ImageView barcodeImage = findViewById(R.id.barcode_image);
        TextView barcodeDataText = findViewById(R.id.barcode_data_text);
        TextView deleteBtn = findViewById(R.id.delete_btn);
        RelativeLayout cardBackground = findViewById(R.id.card_background);


        Card card = (Card) getIntent().getSerializableExtra("card");

        cardName.setText(card.getCardName());
        cardNameOnCard.setText(card.getCardName());
        barcodeDataText.setText(card.getBarcodeData());
        cardBackground.setBackgroundColor(Color.parseColor(card.getCardColor()));

        deleteBtn.setOnClickListener(view -> deleteCard(card));
        backBtn.setOnClickListener(view -> {
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            getWindow().setAttributes(params);
            Intent mainIntent = new Intent(CardActivity.this, MainActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.appearance, R.anim.disappearance);
            finish();
        });

        barcodeDataText.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) CardActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Address", card.getBarcodeData());
            clipboard.setPrimaryClip(clip);
            FancyToast.makeText(CardActivity.this,"Copied",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
        });

        cardNameOnCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateCard(card);
            }
        });

        Bitmap barcode_bitmap = null;
        try {
            barcode_bitmap = encodeAsBitmap(card.getBarcodeData(), BarcodeFormat.valueOf(card.getBarcodeFormat()), 960, 360);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        barcodeImage.setImageBitmap(barcode_bitmap);
    }

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height)
            throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) { return "UTF-8"; }
        }
        return null;
    }

    private void updateCard(final Card card) {
        final String cardName = cardNameOnCard.getText().toString();

        if (cardName.isEmpty()){
            FancyToast.makeText(CardActivity.this,"Enter card name",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
            return;
        }

        class UpdateCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                card.setCardName(cardName);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .cardDao()
                        .update(card);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        UpdateCard updateCard = new UpdateCard();
        updateCard.execute();
    }

    private void deleteCard(final Card card) {
        class DeleteCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .cardDao()
                        .delete(card);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                getWindow().setAttributes(params);
                FancyToast.makeText(CardActivity.this, "Deleted", FancyToast.LENGTH_SHORT, FancyToast.ERROR,false).show();
                Intent mainIntent = new Intent(CardActivity.this, MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.appearance, R.anim.disappearance);
                finish();
            }
        }

        DeleteCard deleteCard = new DeleteCard();
        deleteCard.execute();

    }

    @Override
    public void onBackPressed() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(params);
        Intent mainIntent = new Intent(CardActivity.this, MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.appearance, R.anim.disappearance);
        finish();
    }

}