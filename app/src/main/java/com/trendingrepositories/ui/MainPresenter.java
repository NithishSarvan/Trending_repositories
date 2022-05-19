package com.trendingrepositories.ui;




import com.trendingrepositories.base.BasePresenter;
import com.trendingrepositories.data.ResponseData;
import com.trendingrepositories.network.APIClient;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter<V extends MainIPresenter.MainIView> extends BasePresenter<V> implements MainIPresenter<V> {


    @Override
    public void data() {
        getMvpView().showLoading();
        Observable <List<ResponseData>>modelObservable = APIClient.getAPIClient().all();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                    List<ResponseData> dataList = (List<ResponseData>) trendsResponse;
                    getMvpView().hideLoading();
                    getMvpView().response(dataList);
                }, (Consumer) throwable -> getMvpView().onError((Throwable) throwable));
    }


}
