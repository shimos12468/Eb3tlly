package com.armjld.eb3tly;



public class SOMEUSERDATAPROVIDER {

    String mail ,password,phone ,user;

    public SOMEUSERDATAPROVIDER(String mail, String password, String phone, String user) {
        this.mail = mail;
        this.password = password;
        this.phone = phone;
        this.user = user;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public SOMEUSERDATAPROVIDER() {
    }
}
