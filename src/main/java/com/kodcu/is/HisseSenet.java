package com.kodcu.is;


public class HisseSenet {

    private double fiyat;
    private double esikDeger;
    private String durum;
    private String isim;

    public HisseSenet(String isim, double threshold) {
        this.isim = isim;
        this.esikDeger = threshold;

    }

    public void islem(double fiyat) {
        this.fiyat = fiyat;
        if(fiyat < esikDeger) {
            this.durum = "OFF";
        } else {
            this.durum = "ON";
        }
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getDurum() {
        return durum;
    }
}
