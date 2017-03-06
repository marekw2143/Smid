package com.smid.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.smid.app.smid.R;

public class ActionPopup extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        Bundle b = i.getExtras();

        String text = null;

        if(b!= null) {
            Object text_obj = b.get(GD_CONSTS.ErrorMessage);

            if(text_obj != null) {
                text = (String) text_obj;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.Log("ActionPopup", "Calling finish");
                        ActionPopup.this.finish();
                    }
                })
                .create().show();
    }
}
