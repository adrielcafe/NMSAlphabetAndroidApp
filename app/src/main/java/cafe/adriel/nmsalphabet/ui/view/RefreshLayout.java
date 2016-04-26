package cafe.adriel.nmsalphabet.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

public class RefreshLayout extends CircleRefreshLayout {
    private LinearLayoutManager layoutManager;

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLayoutManager(LinearLayoutManager layoutManager){
        this.layoutManager = layoutManager;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(layoutManager.findFirstCompletelyVisibleItemPosition() != 0){
            return false;
        } else {
            return super.onInterceptTouchEvent(event);
        }
    }
}