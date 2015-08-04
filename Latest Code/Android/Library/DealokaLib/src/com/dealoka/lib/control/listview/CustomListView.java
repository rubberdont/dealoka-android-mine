package com.dealoka.lib.control.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CustomListView extends ListView implements InfiniteScrollListPageListener {
	public static final String TAG = CustomListView.class.getSimpleName();
	public static enum LoadingMode {
		SCROLL_TO_TOP,
		SCROLL_TO_BOTTOM
	};
	public static enum StopPosition {
		START_OF_LIST,
		END_OF_LIST,
		REMAIN_UNCHANGED
	};
	private View loading_view;
	private LoadingMode loading_mode = LoadingMode.SCROLL_TO_BOTTOM;
	private StopPosition stop_position = StopPosition.REMAIN_UNCHANGED;
	private boolean loading_view_visible = false;
	public CustomListView(Context context) {
		super(context);
	}
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public void setAdapter(ListAdapter adapter) {
		if(!(adapter instanceof InfiniteScrollListAdapter)) {
			throw new IllegalArgumentException(InfiniteScrollListAdapter.class.getSimpleName() + " expected");
		}
		InfiniteScrollListAdapter infiniteListAdapter = (InfiniteScrollListAdapter) adapter;
		infiniteListAdapter.setLoadingMode(loading_mode);
		infiniteListAdapter.setStopPosition(stop_position);
		infiniteListAdapter.setInfiniteListPageListener(this);
		this.setOnScrollListener(infiniteListAdapter);
		View dummy = new View(getContext());
		addLoadingView(CustomListView.super, dummy);
		super.setAdapter(adapter);
		removeLoadingView(CustomListView.super, dummy);
	}
	@Override
	public void endOfList() {
		removeLoadingView(this, loading_view);
	}
	@Override
	public void hasMore() {
		addLoadingView(CustomListView.this, loading_view);
	}
	public void setLoadingView(View loading_view) {
		this.loading_view = loading_view;
	}
	public void setLoadingMode(LoadingMode loading_mode) {
		this.loading_mode = loading_mode;
	}
	public void setStopPosition(StopPosition stop_position) {
		this.stop_position = stop_position;
	}
	private void addLoadingView(ListView list_view, View loading_view) {
		if(list_view == null || loading_view == null) {
			return;
		}
		if(!loading_view_visible) {
			if(loading_mode == LoadingMode.SCROLL_TO_TOP) {
				list_view.addHeaderView(loading_view);
			}else {
				list_view.addFooterView(loading_view);
			}
			loading_view_visible = true;
		}
	}
	private void removeLoadingView(ListView list_view, View loading_view) {
		if(list_view == null || loading_view == null) {
			return;
		}
		if(loading_view_visible) {
			if(loading_mode == LoadingMode.SCROLL_TO_TOP) {
				list_view.removeHeaderView(loading_view);
			}else {
				list_view.removeFooterView(loading_view);
			}
			loading_view_visible = false;
		}
	}
}
