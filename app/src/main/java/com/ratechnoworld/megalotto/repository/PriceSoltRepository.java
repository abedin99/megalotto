package com.ratechnoworld.megalotto.repository;

import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.model.Contest;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceSoltRepository {

    private static PriceSoltRepository invoiceRepository;
    private final ApiCalling apiCalling;
    private final List<Contest> responseList = new ArrayList<>();

    public static PriceSoltRepository getInstance() {
        if (invoiceRepository == null) {
            invoiceRepository = new PriceSoltRepository();
        }
        return invoiceRepository;
    }

    private PriceSoltRepository() {
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    public MutableLiveData<List<Contest>> getNews(String ID) {
        MutableLiveData<List<Contest>> newsData = new MutableLiveData<>();
        Call<List<Contest>> call = apiCalling.getPriceSlot(AppConstant.PURCHASE_KEY,ID);
        call.enqueue(new Callback<List<Contest>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                responseList.clear();
                if (response.isSuccessful()) {
                    List<Contest> invoiceModels = response.body();
                    newsData.setValue(invoiceModels);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contest>> call, @NonNull Throwable t) {
                responseList.clear();
                newsData.setValue(responseList);
            }
        });

        return newsData;
    }
}
