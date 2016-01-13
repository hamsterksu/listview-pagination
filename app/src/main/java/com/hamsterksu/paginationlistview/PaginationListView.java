package com.hamsterksu.paginationlistview;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by hamsterksu on 13.01.16.
 */
public class PaginationListView extends ListView{

    private static final int INVALID_ID = -1;

    private long loadingId = 0;

    private IPaginationListener paginationListener;

    public PaginationListView(Context context) {
        this(context, null);
    }

    public PaginationListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaginationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollListener(scrollListener);
    }

    public long getLoadingId() {
        return loadingId;
    }

    /**
     * call when new data loaded
     */
    public void pageLoaded(){
        loadingId = INVALID_ID;//will be reset to 0;
        ((BaseAdapter)getAdapter()).notifyDataSetChanged();
    }

    private void startLoadingNewPage(long itemId) {
        ((BaseAdapter)getAdapter()).notifyDataSetChanged();
        if(paginationListener != null){
            paginationListener.onLoadNextPage(itemId);
        }
    }

    public void setPaginationListener(IPaginationListener paginationListener) {
        this.paginationListener = paginationListener;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if(!(adapter instanceof IPaginationAdapter)){
            throw new IllegalArgumentException("should implement IPaginationAdapter");
        }
        super.setAdapter(adapter);
    }

    public interface IPaginationListener {
        void onLoadNextPage(long itemId);
    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastPosition = getLastVisiblePosition();
            IPaginationAdapter adapter = (IPaginationAdapter)getAdapter();
            if(loadingId != 0  || visibleItemCount == 0 || adapter == null || adapter.getCount() == 0 || lastPosition < 1 || adapter.isItemIsNull(lastPosition)){
                if(loadingId == INVALID_ID) {
                    loadingId = 0;
                }
                return;
            }

            if ((lastPosition == adapter.getCount() - 1 && getChildAt(getChildCount() - 1).getBottom() <= getHeight())) {
                loadingId = adapter.getItemId(lastPosition);
                startLoadingNewPage(loadingId);
            }
        }
    };

    public interface IPaginationAdapter{
        int getCount();
        boolean isItemIsNull(int position);
        long getItemId(int lastPosition);
    }
}
