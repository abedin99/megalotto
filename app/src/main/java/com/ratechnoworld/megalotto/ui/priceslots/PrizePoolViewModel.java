package com.ratechnoworld.megalotto.ui.priceslots;

import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.repository.PriceSoltRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrizePoolViewModel extends ViewModel {

    private MutableLiveData<List<Contest>> arrayInvoiceMutableLiveData;

    void init(String ID) {
        if (arrayInvoiceMutableLiveData != null) {
            return;
        }
        PriceSoltRepository newsRepository = PriceSoltRepository.getInstance();
        arrayInvoiceMutableLiveData = newsRepository.getNews(ID);
    }

    LiveData<List<Contest>> getDomesticList() {
        return arrayInvoiceMutableLiveData;
    }
}
