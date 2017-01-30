package com.example.falmeida.popularmovies_proj1.ui;

/**
 * Created by falmeida on 20/01/17.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.example.falmeida.popularmovies_proj1.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Track changes in visible items in a Recycler View
 */
public class RecyclerViewVisiblePositionsObserver
        extends  RecyclerView.OnScrollListener {
    private static final String LOG_TAG = RecyclerViewVisiblePositionsObserver.class.getSimpleName();

    public enum HorizontalScrollDirection { NONE, LEFT, RIGHT }
    public enum VerticalScrollDirection { NONE, UP, DOWN }

    public interface VisibleItemsChangedListener {
        /**
         *
         * @param firstVisiblePos The position of the new first visible item
         * @param lastVisiblePos The position of the new last visible item
         */
        void onVisibleItemsChanged( int firstVisiblePos, int lastVisiblePos, HorizontalScrollDirection hDir, VerticalScrollDirection vDir );
    }

    /**
     * Keep track of the first visible position
     */
    Integer firstVisiblePos;
    Integer lastVisiblePos;
    List< VisibleItemsChangedListener > listeners;
    int scrollState;

    public RecyclerViewVisiblePositionsObserver( ) {
        firstVisiblePos = RecyclerView.NO_POSITION;
        lastVisiblePos = RecyclerView.NO_POSITION;
        listeners = new ArrayList<>();
        scrollState = RecyclerView.SCROLL_STATE_IDLE;
    }

    public RecyclerViewVisiblePositionsObserver addOnVisibleItemsChangeListener( VisibleItemsChangedListener listener ) {
        if ( BuildConfig.DEBUG ) {
            assert (!listeners.contains(listener));
        }

        listeners.add( listener );
        return this;
    }

    public RecyclerViewVisiblePositionsObserver removeOnVisibleItemsChangeListener( VisibleItemsChangedListener listener ) {
        if ( BuildConfig.DEBUG ) {
            assert (!listeners.contains(listener));
        }

        listeners.remove( listener );
        return this;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int newFirstVisiblePos = RecyclerView.NO_POSITION;
        int newLastVisiblePos = RecyclerView.NO_POSITION;

        HorizontalScrollDirection hDir = dx == 0
                                        ? HorizontalScrollDirection.NONE
                                        : dx > 0
                                            ? HorizontalScrollDirection.RIGHT
                                            : HorizontalScrollDirection.LEFT;


        VerticalScrollDirection vDir = dy == 0
                ? VerticalScrollDirection.NONE
                : dy > 0
                ? VerticalScrollDirection.DOWN
                : VerticalScrollDirection.UP;


        RecyclerView.LayoutManager layoutMgr = recyclerView.getLayoutManager();

        if (layoutMgr instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutMgr = (LinearLayoutManager) layoutMgr;
            newFirstVisiblePos = linearLayoutMgr.findFirstVisibleItemPosition();
            newLastVisiblePos = linearLayoutMgr.findLastVisibleItemPosition();
        }
        else if (layoutMgr instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutMgr = (StaggeredGridLayoutManager) layoutMgr;
            int[] newFirstVisiblePositions = staggeredGridLayoutMgr.findFirstVisibleItemPositions(null);
            int[] newLastVisiblePositions = staggeredGridLayoutMgr.findLastVisibleItemPositions(null);

            if ( newFirstVisiblePositions != null && newFirstVisiblePositions.length > 0 ) {
                newFirstVisiblePos = newFirstVisiblePositions[0];
                if ( newFirstVisiblePositions.length > 1 ) {
                    for (int i = 1; i < newFirstVisiblePositions.length; i++ ){
                        if ( newFirstVisiblePos < newFirstVisiblePositions[i]) {
                            newFirstVisiblePos = newFirstVisiblePositions[i];
                        }
                    }
                }
            }

            if ( newLastVisiblePositions != null && newLastVisiblePositions.length > 0 ) {
                lastVisiblePos = newLastVisiblePositions[0];
                if ( newLastVisiblePositions.length > 1 ) {
                    for (int i = 1; i < newLastVisiblePositions.length; i++ ){
                        if ( newLastVisiblePos > newLastVisiblePositions[i]) {
                            newLastVisiblePos = newLastVisiblePositions[i];
                        }
                    }
                }
            }
        }
        else {
            Log.e(LOG_TAG, "Unhandled Layout Manager instance" + layoutMgr.getClass().getSimpleName());
            throw new AssertionError("Unhandle Layout Manager");
        }


        // Check if there were changes on the visible positions
        // if ( newFirstVisiblePos != firstVisiblePos || newLastVisiblePos != lastVisiblePos ) {
            // One of the visible positions was updated
            firstVisiblePos = newFirstVisiblePos;
            lastVisiblePos = newLastVisiblePos;

            // Notify observers
            for ( VisibleItemsChangedListener listener: listeners ) {
                listener.onVisibleItemsChanged( firstVisiblePos, lastVisiblePos, hDir, vDir );
            }
        // }


    }
}
