package com.smid.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.smid.app.smid.R;

/**
 * Created by marek on 13.07.16.
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_view);

        Intent i = getIntent();
        if(i.getExtras() == null) {
            i.putExtra(GD_CONSTS.WhereUserIsText, getString(R.string.About_TooltipText));
        } else {
            Bundle b = i.getExtras();

            if(b.get(GD_CONSTS.WhereUserIsText) == null){
                i.putExtra(GD_CONSTS.WhereUserIsText, getString(R.string.About_TooltipText));
            }
        }

        Helper.setTopText(this);
    }
}
