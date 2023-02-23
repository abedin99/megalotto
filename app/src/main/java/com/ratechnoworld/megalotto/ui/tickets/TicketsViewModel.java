package com.ratechnoworld.megalotto.ui.tickets;

import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.repository.BronzeRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketsViewModel extends ViewModel {

    private MutableLiveData<List<Contest>> arrayInvoiceMutableLiveData;

    void init(String contestId, String pkgId) {
        if (arrayInvoiceMutableLiveData != null) {
            return;
        }
        BronzeRepository newsRepository = BronzeRepository.getInstance();
        arrayInvoiceMutableLiveData = newsRepository.getNews(contestId, pkgId);
    }

    LiveData<List<Contest>> getDomesticList() {
        return arrayInvoiceMutableLiveData;
    }
}
