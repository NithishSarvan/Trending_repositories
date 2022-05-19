package com.trendingrepositories.ui;


import com.trendingrepositories.base.MvpPresenter;
import com.trendingrepositories.base.MvpView;
import com.trendingrepositories.data.ResponseData;

import java.util.List;


public interface MainIPresenter<V extends MainIPresenter.MainIView & MvpView> extends MvpPresenter<V> {


    void data();

    public interface MainIView extends MvpView {

        void response(List<ResponseData> list);
    }

}
