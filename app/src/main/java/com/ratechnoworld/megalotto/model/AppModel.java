package com.ratechnoworld.megalotto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppModel {

    @SerializedName("result")
    private List<Result> result;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result {

        @SerializedName("msg")
        private String msg;

        @SerializedName("success")
        private int success;


        @SerializedName("country_code")
        private String country_code;

        @SerializedName("currency_sign")
        private String currency_sign;


        @SerializedName("paytm_mer_id")
        private String paytm_mer_id;

        @SerializedName("payu_id")
        private String payu_id;

        @SerializedName("payu_key")
        private String payu_key;


        @SerializedName("maintenance_mode")
        private int maintenance_mode;

        @SerializedName("mop")
        private int mop;

        @SerializedName("wallet_mode")
        private int wallet_mode;


        @SerializedName("min_withdraw")
        private int min_withdraw;

        @SerializedName("max_withdraw")
        private int max_withdraw;

        @SerializedName("min_deposit")
        private int min_deposit;

        @SerializedName("max_deposit")
        private int max_deposit;


        @SerializedName("share_prize")
        private int share_prize;

        @SerializedName("download_prize")
        private int download_prize;

        @SerializedName("bonus_used")
        private int bonus_used;


        @SerializedName("force_update")
        private String force_update;

        @SerializedName("whats_new")
        private String whats_new;

        @SerializedName("update_date")
        private String update_date;

        @SerializedName("latest_version_name")
        private String latest_version_name;

        @SerializedName("latest_version_code")
        private String latest_version_code;

        @SerializedName("update_url")
        private String update_url;


        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getCurrency_sign() {
            return currency_sign;
        }

        public void setCurrency_sign(String currency_sign) {
            this.currency_sign = currency_sign;
        }

        public String getPaytm_mer_id() {
            return paytm_mer_id;
        }

        public void setPaytm_mer_id(String paytm_mer_id) {
            this.paytm_mer_id = paytm_mer_id;
        }

        public String getPayu_id() {
            return payu_id;
        }

        public void setPayu_id(String payu_id) {
            this.payu_id = payu_id;
        }

        public String getPayu_key() {
            return payu_key;
        }

        public void setPayu_key(String payu_key) {
            this.payu_key = payu_key;
        }

        public int getMaintenance_mode() {
            return maintenance_mode;
        }

        public void setMaintenance_mode(int maintenance_mode) {
            this.maintenance_mode = maintenance_mode;
        }

        public int getMop() {
            return mop;
        }

        public void setMop(int mop) {
            this.mop = mop;
        }

        public int getWallet_mode() {
            return wallet_mode;
        }

        public void setWallet_mode(int wallet_mode) {
            this.wallet_mode = wallet_mode;
        }

        public int getMin_withdraw() {
            return min_withdraw;
        }

        public void setMin_withdraw(int min_withdraw) {
            this.min_withdraw = min_withdraw;
        }

        public int getMax_withdraw() {
            return max_withdraw;
        }

        public void setMax_withdraw(int max_withdraw) {
            this.max_withdraw = max_withdraw;
        }

        public int getMin_deposit() {
            return min_deposit;
        }

        public void setMin_deposit(int min_deposit) {
            this.min_deposit = min_deposit;
        }

        public int getMax_deposit() {
            return max_deposit;
        }

        public void setMax_deposit(int max_deposit) {
            this.max_deposit = max_deposit;
        }

        public int getShare_prize() {
            return share_prize;
        }

        public void setShare_prize(int share_prize) {
            this.share_prize = share_prize;
        }

        public int getDownload_prize() {
            return download_prize;
        }

        public void setDownload_prize(int download_prize) {
            this.download_prize = download_prize;
        }

        public int getBonus_used() {
            return bonus_used;
        }

        public void setBonus_used(int bonus_used) {
            this.bonus_used = bonus_used;
        }

        public String getForce_update() {
            return force_update;
        }

        public void setForce_update(String force_update) {
            this.force_update = force_update;
        }

        public String getWhats_new() {
            return whats_new;
        }

        public void setWhats_new(String whats_new) {
            this.whats_new = whats_new;
        }

        public String getUpdate_date() {
            return update_date;
        }

        public void setUpdate_date(String update_date) {
            this.update_date = update_date;
        }

        public String getLatest_version_name() {
            return latest_version_name;
        }

        public void setLatest_version_name(String latest_version_name) {
            this.latest_version_name = latest_version_name;
        }

        public String getLatest_version_code() {
            return latest_version_code;
        }

        public void setLatest_version_code(String latest_version_code) {
            this.latest_version_code = latest_version_code;
        }

        public String getUpdate_url() {
            return update_url;
        }

        public void setUpdate_url(String update_url) {
            this.update_url = update_url;
        }
    }
}
