package com.trendingrepositories.network;


import com.trendingrepositories.data.ResponseData;

import java.util.List;

import io.reactivex.Observable;

import retrofit2.http.GET;



public interface ApiInterface {

    @GET("repositories")
    Observable<List<ResponseData>> all();

//    @GET("repositories")
//    Observable<String> all1();

}
