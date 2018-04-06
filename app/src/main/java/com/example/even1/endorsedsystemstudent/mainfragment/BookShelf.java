package com.example.even1.endorsedsystemstudent.mainfragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.even1.endorsedsystemstudent.Adapter.GridViewAdapter;
import com.example.even1.endorsedsystemstudent.Adapter.ImageListAdapter;
import com.example.even1.endorsedsystemstudent.R;
import com.example.even1.endorsedsystemstudent.StackFragment.Book_Detail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class BookShelf extends Fragment implements AdapterView.OnItemLongClickListener ,AdapterView.OnItemClickListener{

    private Toolbar toolbar;
    private GridView gridview;

    private boolean isShowDelete = false;

    private ImageListAdapter adapter;
    private ArrayList<HashMap<String, Object>> myList = new ArrayList<>();
    private ArrayList<Integer>mbookid = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> mybooklist = new ArrayList<>();
    private int userid;
    private int bookid;
    private String img,bookname,writer,brief,introduce;

    public int IS_FINISH;
    private ImageView keepread;
    private Bitmap bitmap;

    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == IS_FINISH) {
                adapter = new ImageListAdapter(getContext(),mybooklist);
                gridview.setAdapter(adapter);
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book_shelf,container,false);

        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        gridview = (GridView)view.findViewById(R.id.gridview);
        init();

        keepread = (ImageView)view.findViewById(R.id.keepread);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.bookshelfmenu,menu);
    }

    private void init() {
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");

        SharedPreferences sp = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userid = sp.getInt("id",0);
        getbookid(userid);



        gridview.setOnItemLongClickListener(this);
        gridview.setOnItemClickListener(this);
    }



    private void getbookid(int uid) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://118.25.100.167/android/bookcase.action?uid="+uid;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String resultDate = new String(bytes,"utf-8");
                    JSONArray array = new JSONArray(resultDate);
                    for(int j=0;j<array.length();j++){
                        JSONObject js = array.getJSONObject(j);
                        bookid = js.getInt("bookid");
                        mbookid.add(bookid);
                        System.out.println("getbookid---------------------------------------");
                        System.out.println("bookid-------------"+bookid);
                    }
                    System.out.println("bookidsize-------------"+mbookid.size());

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getbookcase();
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getbookcase(){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://118.25.100.167/android/book.action";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String resultDate = new String(bytes,"utf-8");
                    JSONArray array = new JSONArray(resultDate);
                    for(int j=0;j<array.length();j++){
                        JSONObject js = array.getJSONObject(j);
                        int id = js.getInt("id");
                        img = js.getString("img");
                        String imgurl = "http://118.25.100.167"+img;
                        bookname = js.getString("bookname");
                        writer = js.getString("writer");
                        brief = js.getString("brief");
                        introduce = js.getString("introduce");
                        System.out.println("getbookcase---------------------------------------");
                        System.out.println("bookname-------------"+bookname);
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("image",imgurl);
                        map.put("name",bookname);
                        map.put("id",id);
                        myList.add(map);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getbook();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getbook() {
        for(int position=0;position<mbookid.size();position++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("image",myList.get(mbookid.get(position)-1).get("image"));
            map.put("name",myList.get(mbookid.get(position)-1).get("name"));
            System.out.println(position);
            System.out.println("bookname-------------"+position+":"+myList.get(mbookid.get(position)).get("name"));
            mybooklist.add(map);
        }
        Message msg = Message.obtain();
        msg.what = IS_FINISH;
        handler.sendMessage(msg);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (isShowDelete) {
            isShowDelete = false;

        } else {
            isShowDelete = true;
            adapter.setIsShowDelete(isShowDelete);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    delete(position);//删除选中项
                    adapter = new ImageListAdapter(getActivity(), myList);//重新绑定一次adapter
                    gridview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();//刷新gridview

                }

            });
        }

        adapter.setIsShowDelete(isShowDelete);//setIsShowDelete()方法用于传递isShowDelete值

        return true;
    }

    private void delete(int position) {//删除选中项方法
        ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
        if (isShowDelete) {
            myList.remove(position);
            isShowDelete = false;
        }
        newList.addAll(myList);
        myList.clear();
        myList.addAll(newList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent  = new Intent(getActivity(), Book_Detail.class);
        Bundle bundle = new Bundle();
        bundle.putInt("bookid",mbookid.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.manage:
                gridview.setOnItemLongClickListener(this);
        }
        return false;
    }

}
