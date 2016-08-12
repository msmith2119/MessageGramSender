package com.msmith.messagegramsender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity {

    public static final String ALIAS="alias";
    public static final String CONTACT="contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydialog);
        Intent intent = getIntent();
        String alias = intent.getStringExtra(ALIAS);
        String contact = intent.getStringExtra(CONTACT);
        EditText aliasText = (EditText)findViewById(R.id.alias);
        EditText contactText = (EditText)findViewById(R.id.contact);
        Log.v("onCreate","alias="+alias);
        Log.v("onCreate","aliastext="+aliasText);
        aliasText.setText(alias);
        contactText.setText(contact);

    }
}
