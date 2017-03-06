package com.smid.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.smid.app.smid.R;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek on 24.06.16.
 */
public class BaseActivity extends AppCompatActivity implements OnMenuItemClickListener {

    protected boolean menuActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createMenu();
    }

    public void onShowMenuClick(View v) {
        contextMenuDialogFragment.show(fragmentManager, "ContextMenuDialogFragment");
    }

    protected ContextMenuDialogFragment contextMenuDialogFragment;
    protected FragmentManager fragmentManager;
    protected void createMenu() {
        fragmentManager = getSupportFragmentManager();

        MenuParams menuParams = new MenuParams();

        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.toolbar_height));
        menuParams.setMenuObjects(createMenuObjects());
        menuParams.setClosableOutside(true);

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        contextMenuDialogFragment.setItemClickListener(this);
    }

    List<MenuObject> createMenuObjects() {
        List<MenuObject> ret= new ArrayList<>();

        ret.add(createItem(getString(R.string.Menu_RegisterGesture)));
        ret.add(createItem(getString(R.string.Menu_GestureList)));
        ret.add(createItem(getString(R.string.Menu_ActionList)));
        ret.add(createItem(getString(R.string.Menu_AddNewAction)));
        ret.add(createItem(getString(R.string.Menu_Settings)));
        ret.add(createItem(getString(R.string.Menu_About)));

        return ret;
    }

    MenuObject createItem(String text) {
        MenuObject menuObject = new MenuObject();

        menuObject.setResource(R.drawable.arrowru);
        menuObject.setTitle(text);

        return menuObject;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if(!menuActive) {
            return;
        }
        switch(position) {
            case 0:
                Helper.startGestureRegisterDetect(this);
                break;

            case 1:
                Helper.goToGestureList(this, getString(R.string.TooltipText_Flow_MainMenu_GestureList));
                break;
            case 2:
                Helper.startActionListToEdit(this);
                break;
            case 3:
                Helper.startSelectActionIntent(this);
                break;
            case 4:
                Helper.startSettings(this);
                break;
            case 5:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
