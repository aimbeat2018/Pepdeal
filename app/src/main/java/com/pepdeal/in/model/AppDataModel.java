package com.pepdeal.in.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppDataModel {
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("aboutUs")
    @Expose
    private String aboutUs;
    @SerializedName("faq")
    @Expose
    private String faq;
    @SerializedName("terms_condition")
    @Expose
    private String termsCondition;
    @SerializedName("contact_us")
    @Expose
    private String contactUs;
    @SerializedName("email_id")
    @Expose
    private String emailId;

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
    }

    public String getTermsCondition() {
        return termsCondition;
    }

    public void setTermsCondition(String termsCondition) {
        this.termsCondition = termsCondition;
    }

    public String getContactUs() {
        return contactUs;
    }

    public void setContactUs(String contactUs) {
        this.contactUs = contactUs;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
