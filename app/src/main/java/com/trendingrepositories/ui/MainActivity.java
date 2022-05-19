package com.trendingrepositories.ui;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import com.trendingrepositories.R;
import com.trendingrepositories.adapter.Adapter;
import com.trendingrepositories.base.BaseActivity;
import com.trendingrepositories.data.OfflineData;
import com.trendingrepositories.data.ResponseData;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MainIPresenter.MainIView, SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout linearLayout;
    LinearLayout rvLl;
    Button button;
    private SearchView searchView;
    Adapter adapter;
    OfflineData data;
    String filename;
    int page = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;


    List<ResponseData> dataList;
    private final MainPresenter<MainActivity> presenter = new MainPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;

    }

    @Override
    public void initView() {
        presenter.attachView(this);

        recyclerView = findViewById(R.id.category_rv_r);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        linearLayout = findViewById(R.id.no_nw_ll);
        button = findViewById(R.id.try_again);
        rvLl = findViewById(R.id.rv_ll);
        Toolbar toolbar = findViewById(R.id.toolbar);
        filename = "json_" + "trend";
        swipeRefresh.setOnRefreshListener(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);

    }

    public void afterPermission() {
        if (CreateDirectory()) {
            if (isNetworkConnection()) {

                swipeRefresh.setRefreshing(false);
                linearLayout.setVisibility(View.GONE);
                rvLl.setVisibility(View.VISIBLE);
                presenter.data();

            } else {
                swipeRefresh.setRefreshing(false);
                linearLayout.setVisibility(View.GONE);
                rvLl.setVisibility(View.VISIBLE);
                List<ResponseData> messages = new ArrayList<>();
                String jsonstr = data.ReadJsondata(filename);
                ResponseData responseData = new Gson().fromJson(jsonstr, ResponseData.class);
                messages.add(responseData);
                updateUi(messages);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.data();
                    linearLayout.setVisibility(View.GONE);
                    rvLl.setVisibility(View.VISIBLE);

                }
            });
        }

    }



    @Override
    public void response(List<ResponseData> list) {
        responsedata1(list);
        this.dataList = list;
        updateUi(list);

    }
    public void responsedata1(List<ResponseData> list) {
        try {

            dataList.clear();
            dataList.addAll(list);
            updateUi(list);
            Gson gson = new Gson();
            String jsonstr = gson.toJson(list);
            data.StoreJsondata(jsonstr, filename);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateUi(List<ResponseData> list) {
        swipeRefresh.setRefreshing(false);
        adapter = new Adapter(activity(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void initApi(Integer page) {
        if (page == 1) {
            swipeRefresh.setRefreshing(true);
            presenter.data();
        }

    }

    @Override
    public void onRefresh() {
        page = 1;
        initApi(page);

    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        initApi(page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    afterPermission();
                } else {
                    OpenSettings();
                }
                return;
            }

        }

    }

    public void OpenSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public static boolean CreateDirectory() {
        boolean fileflag = false;

        File extStore = Environment.getExternalStorageDirectory();
        String pathdir1 = extStore.getAbsolutePath() + "/TrendingRepositoriesOffline/";

        try {


            File dir1 = new File(pathdir1);
            if (!dir1.exists()) {
                dir1.mkdir();
            }


            fileflag = true;
        } catch (Exception e) {
            fileflag = false;
            e.printStackTrace();
        }
        return fileflag;
    }


}