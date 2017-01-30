package com.example.falmeida.popularmovies_proj1;

/**
 * Created by falmeida on 30/01/17.
 */

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.falmeida.popularmovies_proj1.data.PagedItemLoader;
import com.example.falmeida.popularmovies_proj1.ui.RecyclerViewVisiblePositionsObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Visible Items Changed Listener
 */
public class RecyclerViewPageLoader<T, VH extends RecyclerView.ViewHolder>
        implements RecyclerViewVisiblePositionsObserver.VisibleItemsChangedListener {
    private final String LOG_TAG = RecyclerViewPageLoader.class.getSimpleName();

    private static final int DEFAULT_MAX_ITEMS = 100;
    private static final int DEFAULT_ITEMS_LOAD_THRESHOLD = DEFAULT_MAX_ITEMS / 3;


    public interface PageLoadListener {
        void onPreviousPageLoadStart();
        void onPreviousPageLoadEnd();
        void onNextPageLoadStart();
        void onNextPageLoadEnd();
    }

    private int maxItems;
    boolean loading = false;
    int pageStart;
    int pageEnd;
    int loadThreshold;

    private List< PageLoadListener > mListeners;
    private List<T> mItems;
    private PagedItemLoader<T> mItemLoader;
    private RecyclerView.Adapter<VH> mRvAdapter;

    public RecyclerViewPageLoader(List<T> items,
                                  PagedItemLoader<T> itemLoader,
                                  RecyclerView.Adapter<VH> rvAdapter ) {
        this.mItems = items;
        this.mItemLoader = itemLoader;
        this.mRvAdapter = rvAdapter;
        this.loadThreshold = DEFAULT_ITEMS_LOAD_THRESHOLD;
        this.maxItems = DEFAULT_MAX_ITEMS;
        this.pageStart = this.pageEnd = itemLoader.firstPage();
        mListeners = new ArrayList<>();
    }

    public RecyclerViewPageLoader(List<T> localDataSet,
                                  PagedItemLoader<T> itemLoader,
                                  RecyclerView.Adapter<VH> rvAdapter,
                                  int loadThreshold ) {
        this.mItems = localDataSet;
        this.mItemLoader = itemLoader;
        this.mRvAdapter = rvAdapter;
        this.loadThreshold = loadThreshold;
        this.maxItems = DEFAULT_MAX_ITEMS;
        this.pageStart = this.pageEnd = itemLoader.firstPage();
        mListeners = new ArrayList<>();
    }

    public RecyclerViewPageLoader(List<T> mItems,
                                  PagedItemLoader<T> itemLoader,
                                  RecyclerView.Adapter<VH> rvAdapter,
                                  int loadThreshold,
                                  int maxItems ) {
        this.mItems = mItems;
        this.mItemLoader = itemLoader;
        this.mRvAdapter = rvAdapter;
        this.loadThreshold = loadThreshold;
        this.maxItems = maxItems;
        this.pageStart = this.pageEnd = itemLoader.firstPage();
        mListeners = new ArrayList<>();
    }

    public RecyclerViewPageLoader(List<T> mItems,
                                  PagedItemLoader<T> itemLoader,
                                  RecyclerView.Adapter<VH> rvAdapter,
                                  int loadThreshold,
                                  int maxItems,
                                  int pageStart) {
        this.mItems = mItems;
        this.mItemLoader = itemLoader;
        this.mRvAdapter = rvAdapter;
        this.loadThreshold = loadThreshold;
        this.maxItems = maxItems;
        this.pageStart = this.pageEnd = pageStart;
        mListeners = new ArrayList<>();
    }

    public void registerPageLoadListener( PageLoadListener listener ) {
        mListeners.add(listener);
    }

    public void unregisterPageLoadListener( PageLoadListener listener ) {
        mListeners.remove(listener);
    }

    public void setItemLoader(PagedItemLoader<T> mItemLoader) {
        this.mItemLoader = mItemLoader;
    }

    public void reset() {
        this.mItems.clear();
        this.pageStart = this.pageEnd = mItemLoader.firstPage();
        mRvAdapter.notifyDataSetChanged();
    }

    private boolean hasPreviousPage() {
        return pageStart > mItemLoader.firstPage();
    }

    private boolean hasNextPage() {
        return pageEnd < mItemLoader.lastPage();
    }

    public boolean isLoading() {
        return loading;
    }

    private void loadPreviousPage() {
        loading = true;
        for ( PageLoadListener listener: mListeners) {
            listener.onPreviousPageLoadStart();
        }
        // Load previous page items
        new AsyncTask<Void,Void,List<T> >() {
            @Override
            protected List<T> doInBackground(Void... voids) {
                try {
                    return mItemLoader.getItems(pageStart - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<T> items) {
                loading = false;

                if (items == null) {
                    Log.d(LOG_TAG, "Could not retrieve items");
                } else {

                    int itemsInserted = 0;
                    for (int i = items.size() - 1; i >= 0; i--) {
                        // Add at the beginning
                        T newItem = items.get(i);
                        if (mItems.contains(i)) {
                            continue;
                        }
                        mItems.add(0, newItem);
                        itemsInserted++;
                    }
                    mRvAdapter.notifyItemRangeInserted(0, itemsInserted);
                    pageStart -= 1;

                    if (mItems.size() >= maxItems) {
                        int previousSize = mItems.size();
                        int nItemsToRemove = mItems.size() - maxItems;
                        Log.d(LOG_TAG, "Remove " + nItemsToRemove + " items from " + String.valueOf(mItems.size() - nItemsToRemove) + " to " + String.valueOf(mItems.size() - 1));
                        for (int idx = 0; idx < nItemsToRemove; idx++) {
                            // Remove from end
                            mItems.remove(mItems.size() - 1);
                        }
                        mRvAdapter.notifyItemRangeRemoved(previousSize - nItemsToRemove, nItemsToRemove);
                        pageEnd -= 1;
                    }
                    Log.d(LOG_TAG, "Total number of items=" + mItems.size() + " Pages(" + pageStart + "," + pageEnd + ")");

                    for (PageLoadListener listener : mListeners) {
                        listener.onPreviousPageLoadEnd();
                    }
                }
            }
        }.execute();
    }

    public void loadNextPage() {
        loading = true;

        // Load next page items
        new AsyncTask<Void,Void,List<T> >() {
            @Override
            protected List<T> doInBackground(Void... voids) {
                try {
                    return mItemLoader.getItems(pageEnd);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<T> newItems) {
                loading = false;

                if ( newItems == null ) {
                    Log.d(LOG_TAG, "Could not retrieve items");
                } else {

                    // Append at the end
                    int previousSize = mItems.size();
                    int itemsInserted = 0;
                    for (T newItem : newItems) {
                        if (mItems.contains(newItem)) {
                            continue;
                        }
                        mItems.add(newItem);
                        itemsInserted++;
                    }
                    mRvAdapter.notifyItemRangeInserted(previousSize, itemsInserted);
                    pageEnd += 1;

                    if (mItems.size() >= maxItems) {
                        int nItemsToRemove = mItems.size() - maxItems;
                        Log.d(LOG_TAG, "Remove " + nItemsToRemove + " items from 0 to " + String.valueOf(nItemsToRemove - 1));
                        for (int idx = 0; idx < nItemsToRemove; idx++) {
                            // Remove from the beginning
                            mItems.remove(0);
                        }
                        // Advance also the page Start
                        mRvAdapter.notifyItemRangeRemoved(0, nItemsToRemove);
                        pageStart += 1;
                    }
                    Log.d(LOG_TAG, "Total number of items=" + mItems.size() + " Pages(" + pageStart + "," + pageEnd + ")");
                }

                for ( PageLoadListener listener: mListeners) {
                    listener.onNextPageLoadEnd();
                }
            }
        }.execute();
    }

    @Override
    public void onVisibleItemsChanged(int firstLayoutVisiblePos, int lastLayoutVisiblePos,
                                      RecyclerViewVisiblePositionsObserver.HorizontalScrollDirection hDir,
                                      RecyclerViewVisiblePositionsObserver.VerticalScrollDirection vDir ) {
        // RecyclerView.ViewHolder vhFirstFisible = rvMovies.findViewHolderForLayoutPosition( firstLayoutVisiblePos );
        // RecyclerView.ViewHolder vhLastVisible = rvMovies.findViewHolderForLayoutPosition( lastLayoutVisiblePos );

        if (loading) {
            Log.d(LOG_TAG, "CURRENTLY LOADING Visible Items Changed " + " Layout(" + firstLayoutVisiblePos + "," + lastLayoutVisiblePos + ") scrolling " + vDir.toString() );
            return;
        }

        // Reached the top
        if ( vDir == RecyclerViewVisiblePositionsObserver.VerticalScrollDirection.UP
                && firstLayoutVisiblePos <= loadThreshold
                && hasPreviousPage() ) {
            Log.d(LOG_TAG, "Visible Items Changed " + " Layout(" + firstLayoutVisiblePos + "," + lastLayoutVisiblePos + ") scrolling " + vDir.toString() );
            Log.d(LOG_TAG, "Loading previous page ");
            // Load items from Previous
            loadPreviousPage();
            return;
        }

        // Reached the bottom
        if ( hasNextPage()
                && ( mItems.size() < maxItems
                || (vDir == RecyclerViewVisiblePositionsObserver.VerticalScrollDirection.DOWN && lastLayoutVisiblePos >= maxItems - loadThreshold))) {
            Log.d(LOG_TAG, "Visible Items Changed " + " Layout(" + firstLayoutVisiblePos + "," + lastLayoutVisiblePos + ") scrolling " + vDir.toString() );
            Log.d(LOG_TAG, "Loading next page ");

            // Load items from next page

            for ( PageLoadListener listener: mListeners) {
                listener.onNextPageLoadStart();
            }

            loadNextPage();
            return;
        }

    }
}