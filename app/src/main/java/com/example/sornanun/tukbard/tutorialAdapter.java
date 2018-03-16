package com.example.sornanun.tukbard;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by sornanun on 19/2/2559.
 */
public class tutorialAdapter extends PagerAdapter {

    private int[] imageResource = {R.drawable.step1, R.drawable.step2, R.drawable.step3, R.drawable.step4, R.drawable.step5};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public tutorialAdapter(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return imageResource.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.single_page_tutorial_layout,container,false);
        ImageView imageView = (ImageView) item_view.findViewById(R.id.img);
        imageView.setImageResource(imageResource[position]);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
