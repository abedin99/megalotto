package com.ratechnoworld.megalotto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerModel {

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

        @SerializedName("id")
        private String id;

        @SerializedName("user_profile")
        private String user_profile;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("name")
        private String name;

        @SerializedName("gender")
        private String gender;

        @SerializedName("dob")
        private String dob;

        @SerializedName("content")
        private String content;

        @SerializedName("ticket_no")
        private String ticket_no;

        @SerializedName("cur_balance")
        private String cur_balance;

        @SerializedName("won_balance")
        private String won_balance;

        @SerializedName("bonus_balance")
        private String bonus_balance;

        @SerializedName("entry_fee")
        private String entry_fee;

        @SerializedName("current_time")
        private String current_time;

        @SerializedName("end_time")
        private String end_time;

        @SerializedName("start_time")
        private String start_time;

        @SerializedName("upcoming_contest")
        private int upcoming_contest;

        @SerializedName("live_contest")
        private int live_contest;

        @SerializedName("status")
        private int status;

        @SerializedName("is_block")
        private int is_block;

        public int getUpcoming_contest() {
            return upcoming_contest;
        }

        public void setUpcoming_contest(int upcoming_contest) {
            this.upcoming_contest = upcoming_contest;
        }

        public int getLive_contest() {
            return live_contest;
        }

        public void setLive_contest(int live_contest) {
            this.live_contest = live_contest;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getCur_balance() {
            return cur_balance;
        }

        public void setCur_balance(String cur_balance) {
            this.cur_balance = cur_balance;
        }

        public String getWon_balance() {
            return won_balance;
        }

        public void setWon_balance(String won_balance) {
            this.won_balance = won_balance;
        }

        public String getBonus_balance() {
            return bonus_balance;
        }

        public void setBonus_balance(String bonus_balance) {
            this.bonus_balance = bonus_balance;
        }

        public String getTicket_no() {
            return ticket_no;
        }

        public void setTicket_no(String ticket_no) {
            this.ticket_no = ticket_no;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getCurrent_time() {
            return current_time;
        }

        public void setCurrent_time(String current_time) {
            this.end_time = current_time;
        }

        public String getEntry_fee() {
            return entry_fee;
        }

        public void setEntry_fee(String entry_fee) {
            this.entry_fee = entry_fee;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_profile() {
            return user_profile;
        }

        public void setUser_profile(String user_profile) {
            this.user_profile = user_profile;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIs_block() {
            return is_block;
        }

        public void setIs_block(int is_block) {
            this.is_block = is_block;
        }
    }

}
