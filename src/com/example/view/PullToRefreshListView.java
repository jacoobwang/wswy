package  com.example.view;

import java.util.Date;

import com.example.wswy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PullToRefreshListView extends ListView implements OnScrollListener, OnItemClickListener, OnItemLongClickListener{

	private static final String TAG = "listview";
	
	public final  int REFRESH_UP = 0;
	public final  int REFRESH_DOWN = 1;

	private final  int RELEASE_To_REFRESH = 0;
	private final  int PULL_To_REFRESH = 1;
	private final  int REFRESHING = 2;
	private final  int DONE = 3;
	private final  int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final  int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;
	private LinearLayout footView;

	private TextView head_tipsTextview;
	private TextView head_lastUpdatedTextView;
	private ImageView head_arrowImageView;
	private ProgressBar head_progressBar;
	
	private TextView foot_tipsTextview;
	private TextView foot_lastUpdatedTextView;
	private ImageView foot_arrowImageView;
	private ProgressBar foot_progressBar;

	private RotateAnimation headAnimation;
	private RotateAnimation headReverseAnimation;
	private RotateAnimation footAnimation;
	private RotateAnimation footReverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isHeadRecored;
	private boolean isFootRecored;

	private int headContentWidth;
	private int headContentHeight;
	
	private int	footContentWidth;
	private int footContentHeight;

	private int startY;
	private int firstItemIndex;
	private int totalItemCount;
	private int visibleItemCount;
	private int lastItemIndex;

	private int headState;
	private int footState;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;
	
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public PullToRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.pulltorefresh_head, null);

		head_arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		head_arrowImageView.setMinimumWidth(70);
		head_arrowImageView.setMinimumHeight(50);
		head_progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		head_tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		head_lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);

		addHeaderView(headView, null, false);
		
		footView = (LinearLayout) inflater.inflate(R.layout.pulltorefresh_foot, null);

		foot_arrowImageView = (ImageView) footView
				.findViewById(R.id.foot_arrowImageView);
		foot_arrowImageView.setMinimumWidth(70);
		foot_arrowImageView.setMinimumHeight(50);
		foot_progressBar = (ProgressBar) footView
				.findViewById(R.id.foot_progressBar);
		foot_tipsTextview = (TextView) footView.findViewById(R.id.foot_tipsTextView);
		foot_lastUpdatedTextView = (TextView) footView
				.findViewById(R.id.foot_lastUpdatedTextView);

		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footContentWidth = footView.getMeasuredWidth();

		footView.setPadding(0, 0, 0, -1 * footContentHeight);
		footView.invalidate();

		Log.v("size", "width:" + footContentWidth + " height:"
				+ footContentHeight);
		
		addFooterView(footView, null, false);
		
		setOnScrollListener(this);

		headAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		headAnimation.setInterpolator(new LinearInterpolator());
		headAnimation.setDuration(250);
		headAnimation.setFillAfter(true);

		headReverseAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		headReverseAnimation.setInterpolator(new LinearInterpolator());
		headReverseAnimation.setDuration(200);
		headReverseAnimation.setFillAfter(true);
		
		footAnimation = new RotateAnimation(0, 180,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		footAnimation.setInterpolator(new LinearInterpolator());
		footAnimation.setDuration(250);
		footAnimation.setFillAfter(true);

		footReverseAnimation = new RotateAnimation(180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		footReverseAnimation.setInterpolator(new LinearInterpolator());
		footReverseAnimation.setDuration(200);
		footReverseAnimation.setFillAfter(true);
		
		headState = DONE;
		footState = DONE;
		isRefreshable = false;
		
		super.setOnItemClickListener(this);
		super.setOnItemLongClickListener(this);
	}

	@Override
	public void onScroll(AbsListView arg0, int firstVisiableItem, int visibleItemCount,
            int totalItemCount) {
		firstItemIndex = firstVisiableItem;
		this.visibleItemCount = visibleItemCount;
		this.totalItemCount = totalItemCount;
		lastItemIndex = firstItemIndex + this.visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isHeadRecored) 
				{
					isHeadRecored = true;
					startY = (int) event.getY();
//					Log.v(TAG, "在down时候记录当前位置(head)");
				}
				
				if(lastItemIndex == totalItemCount-1 && visibleItemCount < totalItemCount && !isFootRecored)
				{
					isFootRecored = true;
					startY = (int)event.getY();
//					Log.v(TAG, "在down时候记录当前位置(foot)");
				}
				
				break;

			case MotionEvent.ACTION_UP:
				
				if(isHeadRecored)
				{
					if (headState != REFRESHING && headState != LOADING) 
					{
						if (headState == DONE) {
							// 什么都不做
						}
						if (headState == PULL_To_REFRESH) {
							headState = DONE;
							changeHeaderViewByState();

//							Log.v(TAG, "由下拉刷新状态，到done状态");
						}
						if (headState == RELEASE_To_REFRESH) {
							headState = REFRESHING;
							changeHeaderViewByState();
							onRefresh(REFRESH_UP);

//							Log.v(TAG, "由松开刷新状态，到done状态");
						}
					}

					isHeadRecored = false;
					isBack = false;
				}
				
				if(isFootRecored)
				{
					if (footState != REFRESHING && footState != LOADING) 
					{
						if (footState == DONE) {
							// 什么都不做
						}
						if (footState == PULL_To_REFRESH) {
							footState = DONE;
							changeFooterViewByState();

//							Log.v(TAG, "由上拉刷新状态，到done状态");
						}
						if (footState == RELEASE_To_REFRESH) {
							footState = REFRESHING;
							changeFooterViewByState();
							onRefresh(REFRESH_DOWN);

//							Log.v(TAG, "由松开刷新状态，到done状态");
						}
					}

					isFootRecored = false;
					isBack = false;
				}

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

//				if (!isHeadRecored && firstItemIndex == 0) 
//				{
////					Log.v(TAG, "在move时候记录下位置(head)");
//					isHeadRecored = true;
//					startY = tempY;
//				}
				if(!isFootRecored && lastItemIndex == totalItemCount-1 && visibleItemCount < totalItemCount)
				{
//					Log.v(TAG, "在move时候记录下位置(foot)");
					isFootRecored = true;
					startY = tempY;
				}

				if(isHeadRecored)
				{
					if (headState != REFRESHING && headState != LOADING) 
					{

						// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

						// 可以松手去刷新了
						if (headState == RELEASE_To_REFRESH) {

							setSelection(0);

							// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
							if (((tempY - startY) / RATIO < headContentHeight)
									&& (tempY - startY) > 0) {
								headState = PULL_To_REFRESH;
								changeHeaderViewByState();

//								Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
							}
							// 一下子推到顶了
							else if (tempY - startY <= 0) {
								headState = DONE;
								changeHeaderViewByState();

//								Log.v(TAG, "由松开刷新状态转变到done状态");
							}
							// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (headState == PULL_To_REFRESH) {

							setSelection(0);

							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((tempY - startY) / RATIO >= headContentHeight) {
								headState = RELEASE_To_REFRESH;
								isBack = true;
								changeHeaderViewByState();

//								Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
							}
							// 上推到顶了
							else if (tempY - startY <= 0) {
								headState = DONE;
								changeHeaderViewByState();

//								Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
							}
						}

						// done状态下
						if (headState == DONE) {
							if (tempY - startY > 0) {
								headState = PULL_To_REFRESH;
								changeHeaderViewByState();
							}
						}

						// 更新headView的size
						if (headState == PULL_To_REFRESH) {
							headView.setPadding(0, -1 * headContentHeight
									+ (tempY - startY) / RATIO, 0, 0);

						}

						// 更新headView的paddingTop
						if (headState == RELEASE_To_REFRESH) {
							headView.setPadding(0, (tempY - startY) / RATIO
									- headContentHeight, 0, 0);
						}

					}
				}
				
				if(isFootRecored)
				{
					if (footState != REFRESHING && footState != LOADING) 
					{

						// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

						// 可以松手去刷新了
						if (footState == RELEASE_To_REFRESH) {

							setSelection(totalItemCount-1);

							// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
							if (((startY - tempY) / RATIO < footContentHeight)
									&& (startY - tempY) > 0) {
								footState = PULL_To_REFRESH;
								changeFooterViewByState();

//								Log.v(TAG, "由松开刷新状态转变到上拉刷新状态");
							}
							// 一下子推到顶了
							else if (startY - tempY <= 0) {
								footState = DONE;
								changeFooterViewByState();

//								Log.v(TAG, "由松开刷新状态转变到done状态");
							}
							// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (footState == PULL_To_REFRESH) {

							setSelection(totalItemCount-1);

							// 上拉到可以进入RELEASE_TO_REFRESH的状态
							if ((startY - tempY) / RATIO >= footContentHeight) {
								footState = RELEASE_To_REFRESH;
								isBack = true;
								changeFooterViewByState();

//								Log.v(TAG, "由done或者上拉刷新状态转变到松开刷新");
							}
							// 下推到顶了
							else if (startY - tempY <= 0) {
								footState = DONE;
								changeFooterViewByState();

//								Log.v(TAG, "由DOne或者上拉刷新状态转变到done状态");
							}
						}

						// done状态下
						if (footState == DONE) {
							if (startY - tempY > 0) {
								footState = PULL_To_REFRESH;
								changeFooterViewByState();
							}
						}

						// 更新footView的size
						if (footState == PULL_To_REFRESH) {
							footView.setPadding(0, 0, 0, -1 * footContentHeight + (startY - tempY) / RATIO);
						}

						// 更新headView的paddingTop
						if (footState == RELEASE_To_REFRESH) {
							footView.setPadding(0, 0, 0, (startY - tempY) / RATIO - footContentHeight);
						}

					}
				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (headState) {
		case RELEASE_To_REFRESH:
			head_arrowImageView.setVisibility(View.VISIBLE);
			head_progressBar.setVisibility(View.GONE);

			head_arrowImageView.clearAnimation();
			head_arrowImageView.startAnimation(headAnimation);

			head_tipsTextview.setText("松开刷新");

//			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			head_progressBar.setVisibility(View.GONE);
			head_arrowImageView.clearAnimation();
			head_arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				head_arrowImageView.clearAnimation();
				head_arrowImageView.startAnimation(headReverseAnimation);

				head_tipsTextview.setText("下拉刷新");
			} else {
				head_tipsTextview.setText("下拉刷新");
			}
//			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			head_progressBar.setVisibility(View.VISIBLE);
			head_arrowImageView.clearAnimation();
			head_arrowImageView.setVisibility(View.GONE);
			head_tipsTextview.setText("正在刷新...");

//			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			head_progressBar.setVisibility(View.GONE);
			head_arrowImageView.clearAnimation();
			head_tipsTextview.setText("下拉刷新");

//			Log.v(TAG, "当前状态，done");
			break;
		}
	}
	
	private void changeFooterViewByState() {
		switch (footState) {
		case RELEASE_To_REFRESH:
			foot_arrowImageView.setVisibility(View.VISIBLE);
			foot_progressBar.setVisibility(View.GONE);

			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.startAnimation(footAnimation);

			foot_tipsTextview.setText("松开刷新");

//			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			foot_progressBar.setVisibility(View.GONE);
			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				foot_arrowImageView.clearAnimation();
				foot_arrowImageView.startAnimation(footReverseAnimation);

				foot_tipsTextview.setText("上拉刷新");
			} else {
				foot_tipsTextview.setText("上拉刷新");
			}
//			Log.v(TAG, "当前状态，上拉刷新");
			break;

		case REFRESHING:

			footView.setPadding(0, 0, 0, 0);

			foot_progressBar.setVisibility(View.VISIBLE);
			foot_arrowImageView.clearAnimation();
			foot_arrowImageView.setVisibility(View.GONE);
			foot_tipsTextview.setText("正在刷新...");

//			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			footView.setPadding(0, -1 * footContentHeight, 0, 0);

			foot_progressBar.setVisibility(View.GONE);
			foot_arrowImageView.clearAnimation();
			foot_tipsTextview.setText("上拉刷新");

//			Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh(int refreshType);
	}

	public void onRefreshComplete(OnRefreshListener refreshListener) {
		headState = DONE;
		head_lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
		footState = DONE;
		foot_lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		changeFooterViewByState();
	}

	private void onRefresh(int refreshType) {
		if (refreshListener != null) {
			refreshListener.onRefresh(refreshType);
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		head_lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		foot_lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}
	
	@Override
	public ListAdapter getAdapter()
	{
		HeaderViewListAdapter adapter = (HeaderViewListAdapter)super.getAdapter();
		if(adapter != null)
		{
			return adapter.getWrappedAdapter();
		}
		return null;
	}
	
	@Override
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mOnItemClickListener = listener;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if(mOnItemClickListener != null)
		{
			mOnItemClickListener.onItemClick(parent, view, position-getHeaderViewsCount(), id);
		}
	}
	
	@Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) 
    {
		mOnItemLongClickListener = listener;
    }
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		if(mOnItemLongClickListener != null)
		{
			return mOnItemLongClickListener.onItemLongClick(parent, view, position-getHeaderViewsCount(), id);
		}
		return false;
	}
}
