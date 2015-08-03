package com.dealoka.lib.control.listview;

import com.dealoka.lib.control.listview.CustomListView.LoadingMode;
import com.dealoka.lib.control.listview.CustomListView.StopPosition;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public abstract class InfiniteScrollListAdapter extends BaseAdapter implements OnScrollListener {
	public static final String TAG = InfiniteScrollListAdapter.class.getSimpleName();
	protected boolean canScroll = false;
	protected boolean rowEnabled = true;
	protected LoadingMode loadingMode;
	protected StopPosition stopPosition;
	protected InfiniteScrollListPageListener infiniteListPageListener;
	protected abstract void onScrollNext();
	public abstract View getInfiniteScrollListView(int position, View convertView, ViewGroup parent);
	public void setInfiniteListPageListener(InfiniteScrollListPageListener infiniteListPageListener) {
		this.infiniteListPageListener = infiniteListPageListener;
	}
	public void lock() {
		canScroll = false;
	}
	public void unlock() {
		canScroll = true;
	}
	@Override
	public boolean isEnabled(int position) {
		return rowEnabled;
	}
	public void setRowEnabled(boolean rowEabled) {
		this.rowEnabled = rowEabled;
	}
	public void setLoadingMode(LoadingMode loadingMode) {
		this.loadingMode = loadingMode;
	}
	public StopPosition getStopPosition() {
		return stopPosition;
	}
	public void setStopPosition(StopPosition stopPosition) {
		this.stopPosition = stopPosition;
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(view instanceof CustomListView) {
			if(loadingMode == LoadingMode.SCROLL_TO_TOP && firstVisibleItem == 0 && canScroll) {
				onScrollNext();
			}
			if(loadingMode == LoadingMode.SCROLL_TO_BOTTOM && firstVisibleItem + visibleItemCount - 1 == getCount() && canScroll) {
				onScrollNext();
			}
		}
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		return getInfiniteScrollListView(position, convertView, parent);
	}
	public void notifyEndOfList() {
		lock();
		if(infiniteListPageListener != null) {
			infiniteListPageListener.endOfList();
		}
	}
	public void notifyHasMore() {
		unlock();
		if(infiniteListPageListener != null) {
			infiniteListPageListener.hasMore();
		}
	}
}