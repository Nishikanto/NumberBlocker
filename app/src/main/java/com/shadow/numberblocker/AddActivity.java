package com.shadow.numberblocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AddActivity extends Activity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_add);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        try {
            et = ((EditText) findViewById(R.id.textValue));
            et.setText(getIntent().getExtras().getString("value"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Button) findViewById(R.id.btnDone)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                executeDone();
            }
        });

        //for keyboard done button
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch(result) {
                    case EditorInfo.IME_ACTION_DONE:
                        executeDone();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        executeDone();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        executeDone();
        super.onBackPressed();
    }

    private void executeDone() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("value", AddActivity.this.et.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}