package jug.istanbul.is;

import java.util.*;

public class BlokService {

    public Map<String, Map<String, Double>> hesapBlokesiDagit(List<Hesap> hesaplar, double riskTutari) {
        // Risk blokesi dağıtımı
        double kalanRisk = riskTutari;
        for (Hesap hesap : hesaplar) {
            if (kalanRisk > 0) {
                double blok = Math.min(kalanRisk, hesap.getBakiye());
                hesap.setRiskBlokesi(blok);
                kalanRisk -= blok;
            }
        }

        // Tedbir blokesi hesaplama: her hesap için %50 hedef, risk sonrası kalan
        for (Hesap hesap : hesaplar) {
            double hedefTedbir = hesap.getBakiye() * 0.5;
            double kalanHedef = Math.max(0, hedefTedbir - hesap.getRiskBlokesi());
            hesap.setTedbirBlokesi(kalanHedef);
        }

        // Tedbir blokesi dağıtımı: kalan bakiye kadar
        for (Hesap hesap : hesaplar) {
            double mevcutBakiye = hesap.getBakiye() - hesap.getRiskBlokesi();
            if (mevcutBakiye > 0) {
                double blok = Math.min(hesap.getTedbirBlokesi(), mevcutBakiye);
                hesap.setTedbirBlokesi(blok);
            } else {
                hesap.setTedbirBlokesi(0);
            }
        }

        // Sonuçları map olarak döndür
        Map<String, Map<String, Double>> sonuc = new LinkedHashMap<>();
        for (Hesap hesap : hesaplar) {
            Map<String, Double> bloklar = new LinkedHashMap<>();
            bloklar.put("Banka Riski Blokesi", hesap.getRiskBlokesi());
            bloklar.put("Tedbir Blokesi", hesap.getTedbirBlokesi());
            sonuc.put(hesap.getHesapNo(), bloklar);
        }
        return sonuc;
    }

    public static class Hesap {
        private String hesapNo;
        private double bakiye;
        private double riskBlokesi;
        private double tedbirBlokesi;

        public Hesap(String hesapNo, double bakiye) {
            this.hesapNo = hesapNo;
            this.bakiye = bakiye;
        }

        public String getHesapNo() { return hesapNo; }
        public double getBakiye() { return bakiye; }
        public double getRiskBlokesi() { return riskBlokesi; }
        public void setRiskBlokesi(double riskBlokesi) { this.riskBlokesi = riskBlokesi; }
        public double getTedbirBlokesi() { return tedbirBlokesi; }
        public void setTedbirBlokesi(double tedbirBlokesi) { this.tedbirBlokesi = tedbirBlokesi; }
    }
}