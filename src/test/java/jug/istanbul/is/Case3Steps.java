package jug.istanbul.is;

import io.cucumber.java.tr.Diyelimki;
import io.cucumber.java.tr.Eğerki;
import io.cucumber.java.tr.Ozaman;
import org.junit.Assert;
import java.util.*;

public class Case3Steps {

    private List<BlokService.Hesap> hesaplar = new ArrayList<>();
    private double riskTutari;
    private double bankaTedbirBlokesi;
    private double normalTedbirBlokesi;

    @Diyelimki("^Müşterinin tek bir hesabında (\\d+) TL bakiye var$")
    public void musterininTekBirHesabindaTLBakiyeVar(double bakiye) throws Throwable {
        hesaplar.clear();
        BlokService.Hesap hesap = new BlokService.Hesap("TekHesap", bakiye);
        hesaplar.add(hesap);
    }

    @Diyelimki("^Müşterinin banka riski (\\d+) TL'dir$")
    public void musterininBankaRiskiTLDir(double risk) throws Throwable {
        this.riskTutari = risk;
    }

    @Eğerki("^Hesaba \"([^\"]*)\" \\((\\d+) TL\\) bloke konulmak istendiğinde$")
    public void hesabaTLBlokeKonulmakIstendiğinde(String oran, double tedbirTutari) throws Throwable {
        // For single account, calculate blocks
        BlokService.Hesap hesap = hesaplar.get(0);
        double bakiye = hesap.getBakiye();
        // Risk block
        bankaTedbirBlokesi = Math.min(riskTutari, bakiye);
        // Normal precaution block: total precaution - risk
        normalTedbirBlokesi = tedbirTutari - bankaTedbirBlokesi;
        // Ensure it doesn't exceed available
        double available = bakiye - bankaTedbirBlokesi;
        normalTedbirBlokesi = Math.min(normalTedbirBlokesi, available);
    }

    @Ozaman("^Konulacak \"([^\"]*)\" tutarı (\\d+) TL olmalıdır$")
    public void konulacakTutarıTLOlmalıdır(String blokTipi, double expected) throws Throwable {
        if ("Banka Tedbir Blokesi".equals(blokTipi)) {
            Assert.assertEquals(expected, bankaTedbirBlokesi, 0.01);
        } else if ("Normal Tedbir Blokesi".equals(blokTipi)) {
            Assert.assertEquals(expected, normalTedbirBlokesi, 0.01);
        }
    }
}