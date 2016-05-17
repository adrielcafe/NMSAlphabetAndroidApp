package cafe.adriel.nmsalphabet.ui.util;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerFadeTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        if(position <= -1.0F || position >= 1.0F) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(0.0F);
        } else if( position == 0.0F) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(1.0F);
        } else {
            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(1.0F - Math.abs(position));
        }
    }

}