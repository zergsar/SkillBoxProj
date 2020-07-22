package main.response;

import java.io.Serializable;

public class Info implements Serializable {

    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;

    public Info()
    {
        this.title = "DevPub";
        this.subtitle = "Рассказы разработчиков";
        this.phone = "+7 903 666-44-55";
        this.email = "mail@mail.ru";
        this.copyright = "Тимиров Сергей";
        this.copyrightFrom = "2000";

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyrightFrom() {
        return copyrightFrom;
    }

    public void setCopyrightFrom(String copyrightFrom) {
        this.copyrightFrom = copyrightFrom;
    }
}
