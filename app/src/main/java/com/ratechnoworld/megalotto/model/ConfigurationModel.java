package com.ratechnoworld.megalotto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigurationModel {
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

        @SerializedName("about")
        private String about;

        @SerializedName("contact")
        private String contact;

        @SerializedName("faq")
        private String faq;

        @SerializedName("privacy")
        private String privacy;

        @SerializedName("terms")
        private String terms;

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

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getFaq() {
            return faq;
        }

        public void setFaq(String faq) {
            this.faq = faq;
        }

        public String getPrivacy() {
            return privacy;
        }

        public void setPrivacy(String privacy) {
            this.privacy = privacy;
        }

        public String getTerms() {
            return terms;
        }

        public void setTerms(String terms) {
            this.terms = terms;
        }
    }

}
