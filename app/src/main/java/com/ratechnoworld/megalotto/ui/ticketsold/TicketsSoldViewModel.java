package com.ratechnoworld.megalotto.ui.ticketsold;

import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.repository.TicketSoldRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketsSoldViewModel extends ViewModel {

    private MutableLiveData<List<Contest>> arrayInvoiceMutableLiveData;

    void init(String contestId, String FeesId) {
        if (arrayInvoiceMutableLiveData != null) {
            return;
        }
        TicketSoldRepository newsRepository = TicketSoldRepository.getInstance();
        arrayInvoiceMutableLiveData = newsRepository.getNews(contestId, FeesId);
    }

    LiveData<List<Contest>> getDomesticList() {
        return arrayInvoiceMutableLiveData;
    }
}
