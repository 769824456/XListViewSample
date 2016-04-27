package win.yulongsun.xlistviewsample;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements XListView.OnRefreshListener {

    private XListView         xlv;
    private ArrayList<String> list;
    private MyAdapter         adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xlv = (XListView) findViewById(R.id.xlv);
        list = new ArrayList<String>();
        initData();
    }


    private void initData() {
        for (int i = 0; i < 15; i++) {
            list.add("listview原来的数据 - " + i);
        }


//		final View headerView = View.inflate(this, R.layout.layout_header, null);
        //第一种方法
//		headerView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//				headerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//				int headerViewHeight = headerView.getHeight();
//
//
//				Log.e("MainActivity", "headerViewHeight: "+headerViewHeight);
//				headerView.setPadding(0, -headerViewHeight, 0, 0);
//				refreshListView.addHeaderView(headerView);//
//			}
//		});
        //第二种方法
//		headerView.measure(0, 0);//主动通知系统去测量
//		int headerViewHeight = headerView.getMeasuredHeight();
//		Log.e("MainActivity", "headerViewHeight: "+headerViewHeight);
//		headerView.setPadding(0, -headerViewHeight, 0, 0);
//		refreshListView.addHeaderView(headerView);//


        adapter = new MyAdapter();
        xlv.setAdapter(adapter);

        xlv.setOnRefreshListener(this);

    }

    @Override public void onPullRefresh() {

    }

    @Override public void onLoadMore() {

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(MainActivity.this);
            textView.setPadding(20, 20, 20, 20);
            textView.setTextSize(18);

            textView.setText(list.get(position));

            return textView;
        }

    }

}
