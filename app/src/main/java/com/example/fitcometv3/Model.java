package com.example.fitcometv3;

public class Model {
    public String url, Nazwa, Opis;

    public Model() {

    }

    public Model(String url, String nazwa, String opis) {
        this.url = url;
        Nazwa = nazwa;
        Opis = opis;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNazwa() {
        return Nazwa;
    }

    public void setNazwa(String nazwa) {
        Nazwa = nazwa;
    }

    public String getOpis() {
        return Opis;
    }

    public void setOpis(String opis) {
        Opis = opis;
    }
}