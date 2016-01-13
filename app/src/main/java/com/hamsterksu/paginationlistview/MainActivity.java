package com.hamsterksu.paginationlistview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    MyAdapter adapter;
    PaginationListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (PaginationListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter = new MyAdapter(this));
        listView.setPaginationListener(new PaginationListView.IPaginationListener() {
            @Override
            public void onLoadNextPage(long itemId) {
                getNextItems(itemId);
            }
        });
        loadItemsFrom(-1);
    }

    private Handler handler = new Handler();

    private void getNextItems(final long itemId) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                loadItemsFrom(itemId);
            }
        }, 3 * 1000L);
    }

    private void loadItemsFrom(long itemId) {
        long itemIdInc = itemId;
        ArrayList<Item> result = new ArrayList<>(20);
        for(int i = 0; i < 20; i++){
            itemIdInc++;
            result.add(new Item("Item " + itemIdInc, itemIdInc));
        }
        onPageLoaded(result);
    }

    private void onPageLoaded(ArrayList<Item> result) {
        adapter.addAll(result);
        listView.pageLoaded();
    }

    public class MyAdapter extends ArrayAdapter<Item> implements PaginationListView.IPaginationAdapter {

        public MyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.rss_item_loading, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.text1)).setText(getItem(position).text);

            long id = getItemId(position);
            convertView.findViewById(R.id.loading_container).setVisibility(id == listView.getLoadingId() ? View.VISIBLE : View.GONE);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public boolean isItemIsNull(int position) {
            return getItem(position) == null;
        }

    }

    public static class Item {

        final long id;

        final String text;

        public Item(String text, long id) {
            this.text = text;
            this.id = id;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
