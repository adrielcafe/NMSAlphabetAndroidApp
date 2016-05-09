package cafe.adriel.nmsalphabet.ui.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SwipeRefreshLayoutToggleScrollListener extends RecyclerView.OnScrollListener {
    private List<RecyclerView.OnScrollListener> mScrollListeners = new ArrayList<>();
    private SwipeRefreshLayout mSwipeLayout;
    private int mExpectedVisiblePosition = 0;

    public SwipeRefreshLayoutToggleScrollListener(SwipeRefreshLayout mSwipeLayout) {
        this.mSwipeLayout = mSwipeLayout;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        notifyScrollStateChanged(recyclerView,newState);
        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisible = llm.findFirstCompletelyVisibleItemPosition();
        if(firstVisible != RecyclerView.NO_POSITION)
            mSwipeLayout.setEnabled(firstVisible == mExpectedVisiblePosition);

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        notifyOnScrolled(recyclerView, dx, dy);
    }

    public void setExpectedFirstVisiblePosition(int position){
        mExpectedVisiblePosition = position;
    }

    public void addScrollListener(RecyclerView.OnScrollListener listener){
        mScrollListeners.add(listener);
    }

    public boolean removeScrollListener(RecyclerView.OnScrollListener listener){
        return mScrollListeners.remove(listener);
    }

    private void notifyOnScrolled(RecyclerView recyclerView, int dx, int dy){
        for(RecyclerView.OnScrollListener listener : mScrollListeners){
            listener.onScrolled(recyclerView, dx, dy);
        }
    }

    private void notifyScrollStateChanged(RecyclerView recyclerView, int newState){
        for(RecyclerView.OnScrollListener listener : mScrollListeners){
            listener.onScrollStateChanged(recyclerView, newState);
        }
    }
}