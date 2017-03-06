package com.smid.app;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smid.app.smid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek on 24.06.16.
 */
public class MotionImagePresenter {
    public List<ImageView> transformMoveToImages(String moveDescription, Context context) {
        String splitter = GD_CONSTS.HORIZONTAL_SIMPLE + "\\" +  GD_CONSTS.VERTICAL_SIMPLE;

        String rr = moveDescription.replaceAll(splitter, "");

        String []parts = new String[rr.length() / 2];

        for(int i=0;i<rr.length() / 2;i++) {
            parts[i] = rr.substring(i*2, i*2 + 2);
        }

        List<Integer> drawables = new ArrayList<>(parts.length);

        for(int i=0; i<parts.length; i++) {
            drawables.add(getImageForDirection(parts[i]));
        }

        List<ImageView> imageViews = new ArrayList<>(parts.length);

        for(int i=0;i<drawables.size();i++) {
            ImageView imageView = new ImageView(context);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            imageView.setLayoutParams(lp);

            imageView.setImageResource(drawables.get(i));

            imageViews.add(imageView);
        }

        return imageViews;
    }

    public int getImageForDirection(String direction){
        switch(direction){
            case(GD_CONSTS.HORIZONTAL_LEFT + GD_CONSTS.VERTICAL_SIMPLE):
                return R.drawable.arrowl_;

            case(GD_CONSTS.HORIZONTAL_LEFT + GD_CONSTS.VERTICAL_DOWN):
                return R.drawable.arrowld;

            case(GD_CONSTS.HORIZONTAL_SIMPLE + GD_CONSTS.VERTICAL_DOWN):
                return R.drawable.arrow_d;

            case(GD_CONSTS.HORIZONTAL_RIGHT + GD_CONSTS.VERTICAL_DOWN):
                return R.drawable.arrowrd;

            case(GD_CONSTS.HORIZONTAL_RIGHT + GD_CONSTS.VERTICAL_SIMPLE):
                return R.drawable.arrowr_;// ru, u, lu
            case(GD_CONSTS.HORIZONTAL_RIGHT + GD_CONSTS.VERTICAL_UP):
                return R.drawable.arrowru;

            case(GD_CONSTS.HORIZONTAL_SIMPLE + GD_CONSTS.VERTICAL_UP):
                return R.drawable.arrow_u;

            case(GD_CONSTS.HORIZONTAL_LEFT + GD_CONSTS.VERTICAL_UP):
                return R.drawable.arrowlu;

            default:
                return R.drawable.arrowl_;
        }
    }
}
