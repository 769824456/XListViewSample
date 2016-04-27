package win.yulongsun.xlistviewsample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Project XListViewSample
 * @Packate win.yulongsun.xlistviewsample
 * @Author yulongsun
 * @Email yulongsun@gmail.com
 * @Date 2016/4/27
 * @Version 1.0.0
 * @Description
 */
public class XListView extends ListView {
    private static final String TAG = XListView.class.getSimpleName();
    private LayoutInflater  mLayoutInflater;
    private View            footerView;
    private View            headerView;
    private ImageView       iv_arrow;
    private ProgressBar     pb_rotate;
    private TextView        tv_state;
    private TextView        tv_time;
    private int             footerViewMeasuredHeight;
    private int             headerViewMeasuredHeight;
    private RotateAnimation upAnimation;
    private RotateAnimation downAnimation;

    ////////////////////////////////////////////////
    private final int     PULL_REFRESH    = 0;//下拉刷新
    private final int     RELEASE_REFRESH = 1;//松开刷新
    private final int     REFRESHING      = 2;//正在刷新
    private       int     downY           = 0;
    private       int     CURRENT_STATE   = PULL_REFRESH;
    private       boolean isLoadingMore   = false;//当前是否正在处于加载更多
    ////////////////////////////////////////////////


    public XListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithContext(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public XListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWithContext(context);
    }

    /*初始化上下文*/
    private void initWithContext(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        initHeaderView();
        initFooterView();
        initRotateAnimation();

    }

    private void initHeaderView() {
        headerView = mLayoutInflater.inflate(R.layout.layout_header, null);
        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        pb_rotate = (ProgressBar) headerView.findViewById(R.id.pb_rotate);
        tv_state = (TextView) headerView.findViewById(R.id.tv_state);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);

        //主动通知系统是测量View
        headerView.measure(0, 0);
        headerViewMeasuredHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewMeasuredHeight, 0, 0);

        addHeaderView(headerView);
    }

    private void initFooterView() {
        footerView = mLayoutInflater.inflate(R.layout.layout_footer, null);

        //主动通知系统去测量View
        footerView.measure(0, 0);
        footerViewMeasuredHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewMeasuredHeight, 0, 0);

        addFooterView(footerView);
    }


    /*初始化旋转动画*/
    private void initRotateAnimation() {
        upAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);
        downAnimation = new RotateAnimation(-180, -360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
    }


    @Override public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

    }

    /*设置下拉刷新*/
    public void setPullRefreshEnable(boolean enable) {

    }

    /*设置上拉加载更多*/
    public void setPullLoadEnable(boolean enable) {

    }


    /*关闭上拉加载更多*/
    public void stopLoadMore() {

    }

    /*关闭下拉刷新*/
    public void stopRefresh() {

    }

    @Override public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                downY = (int) ev.getY();
//                Log.e(TAG, "onTouchEvent:downY " + downY);
                break;
            case MotionEvent.ACTION_UP://抬起
                if (CURRENT_STATE == PULL_REFRESH) {
                    headerView.setPadding(0, -headerViewMeasuredHeight, 0, 0);
                } else if (CURRENT_STATE == RELEASE_REFRESH) {
                    headerView.setPadding(0, 0, 0, 0);
                    CURRENT_STATE = REFRESHING;
                    updateHeaderView();
                    if (mListener != null) {
                        mListener.onPullRefresh();
                    }
                }


                break;
            case MotionEvent.ACTION_MOVE://移动

                if (CURRENT_STATE == REFRESHING) {
                    break;
                }

                int deltaY = (int) (ev.getY() - downY);
                Log.e(TAG, "onTouchEvent: deltaY" + deltaY);
                int paddingTop = deltaY - headerViewMeasuredHeight;
                if (paddingTop > -headerViewMeasuredHeight && getFirstVisiblePosition() == 0) {
                    headerView.setPadding(0, paddingTop, 0, 0);

                    if (paddingTop > 0 && CURRENT_STATE == PULL_REFRESH) {
                        CURRENT_STATE = RELEASE_REFRESH;
                        //从下拉刷新进入松开刷新
                        updateHeaderView();
                    } else if (paddingTop < 0) {
                        //进入下拉刷新状态
                        CURRENT_STATE = PULL_REFRESH;
                        updateHeaderView();
                    }


                    return true;//拦截事件，不让listview消费触摸事件
                }

                break;
        }


        return super.onTouchEvent(ev);
    }

    private void updateHeaderView() {
        switch (CURRENT_STATE) {
            case PULL_REFRESH:
                tv_state.setText("下拉刷新");
                iv_arrow.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                tv_state.setText("松开刷新");
                iv_arrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(View.INVISIBLE);
                pb_rotate.setVisibility(View.VISIBLE);
                tv_state.setText("正在刷新...");
                break;
        }
    }

    /**
     * 完成刷新操作，重置状态,在你获取完数据并更新完adater之后，去在UI线程中调用该方法
     */
    public void completeRefresh() {
        if (isLoadingMore) {
            //重置footerView状态
            footerView.setPadding(0, -footerViewMeasuredHeight, 0, 0);
            isLoadingMore = false;
        } else {
            //重置headerView状态
            headerView.setPadding(0, -footerViewMeasuredHeight, 0, 0);
            CURRENT_STATE = PULL_REFRESH;
            pb_rotate.setVisibility(View.INVISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            tv_state.setText("下拉刷新");
            tv_time.setText("最后刷新：" + getCurrentTime());
        }
    }

    /**
     * 获取当前系统时间，并格式化
     * @return
     */
    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    @Override public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        if (CURRENT_STATE == OnScrollListener.SCROLL_STATE_IDLE
                && getLastVisiblePosition() == (getCount() - 1) && !isLoadingMore) {
            isLoadingMore = true;

            footerView.setPadding(0, 0, 0, 0);//显示出footerView
            setSelection(getCount());//让listview最后一条显示出来

            if (mListener != null) {
                mListener.onLoadMore();
            }
        }

    }


    ///////////////////////////////////////////////////////////////////////////
    //接口
    ///////////////////////////////////////////////////////////////////////////


    interface OnRefreshListener {
        void onPullRefresh();

        void onLoadMore();
    }

    private OnRefreshListener mListener = null;


    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }


}
