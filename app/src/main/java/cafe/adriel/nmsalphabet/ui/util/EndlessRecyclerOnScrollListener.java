package cafe.adriel.nmsalphabet.ui.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager layoutManager;
    private boolean loading = true;
    private int currentPage = 0;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public void reset() {
        loading = true;
        currentPage = 0;
        previousTotal = 0;
    }

    public abstract void onLoadMore(int currentPage);
}