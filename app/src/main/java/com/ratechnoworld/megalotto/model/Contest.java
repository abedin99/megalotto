package com.ratechnoworld.megalotto.model;

import com.google.gson.annotations.SerializedName;

public class Contest {

    @SerializedName("id")
    private String id;

    @SerializedName("price")
    private String price;

    @SerializedName("no_of_winners")
    private String no_of_winners;

    @SerializedName("no_of_tickets")
    private String no_of_tickets;

    @SerializedName("first_prize")
    private String first_prize;

    @SerializedName("total_prize")
    private String total_prize;

    @SerializedName("no_of_sold")
    private String no_of_sold;

    @SerializedName("rank")
    private String rank;

    @SerializedName("prize")
    private String prize;

    @SerializedName("username")
    private String username;

    @SerializedName("ticket_no")
    private String ticket_no;

    @SerializedName("no_of_bought")
    private String no_of_bought;

    @SerializedName("contest_id")
    private String contest_id;

    @SerializedName("time")
    private String time;

    @SerializedName("date")
    private String date;

    @SerializedName("msg")
    private String msg;

    @SerializedName("success")
    private int success;

    @SerializedName("fees_id")
    private String fees_id;

    @SerializedName("entry_fee")
    private String entry_fee;

    @SerializedName("win_prize")
    private String win_prize;

    @SerializedName("order_id")
    private String order_id;

    @SerializedName("req_amount")
    private String req_amount;

    @SerializedName("remark")
    private String remark;

    @SerializedName("status")
    private String status;


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

    public String getFees_id() {
        return fees_id;
    }

    public void setFees_id(String fees_id) {
        this.fees_id = fees_id;
    }

    public String getEntry_fee() {
        return entry_fee;
    }

    public void setEntry_fee(String entry_fee) {
        this.entry_fee = entry_fee;
    }

    public String getWin_prize() {
        return win_prize;
    }

    public void setWin_prize(String win_prize) {
        this.win_prize = win_prize;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getReq_amount() {
        return req_amount;
    }

    public void setReq_amount(String req_amount) {
        this.req_amount = req_amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContest_id() {
        return contest_id;
    }

    public void setContest_id(String contest_id) {
        this.contest_id = contest_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNo_of_bought() {
        return no_of_bought;
    }

    public void setNo_of_bought(String no_of_bought) {
        this.no_of_bought = no_of_bought;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNo_of_winners() {
        return no_of_winners;
    }

    public void setNo_of_winners(String no_of_winners) {
        this.no_of_winners = no_of_winners;
    }

    public String getNo_of_tickets() {
        return no_of_tickets;
    }

    public void setNo_of_tickets(String no_of_tickets) {
        this.no_of_tickets = no_of_tickets;
    }

    public String getFirst_prize() {
        return first_prize;
    }

    public void setFirst_prize(String first_prize) {
        this.first_prize = first_prize;
    }

    public String getTotal_prize() {
        return total_prize;
    }

    public void setTotal_prize(String total_prize) {
        this.total_prize = total_prize;
    }

    public String getNo_of_sold() {
        return no_of_sold;
    }

    public void setNo_of_sold(String no_of_sold) {
        this.no_of_sold = no_of_sold;
    }
}
