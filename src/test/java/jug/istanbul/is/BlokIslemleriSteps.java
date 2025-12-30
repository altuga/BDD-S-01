package jug.istanbul.is;

import io.cucumber.java.tr.Diyelimki;
import io.cucumber.java.tr.Eğerki;
import io.cucumber.java.tr.Ozaman;
import org.junit.Assert;
import java.util.*;

public class BlokIslemleriSteps {

    private List<BlokService.Hesap> hesaplar = new ArrayList<>();
    private double riskTutari;
    private Map<String, Map<String, Double>> sonuc;
    private double bankaTedbirBlokesi;
    private double normalTedbirBlokesi;

    @Diyelimki("^Müşterinin \"([^\"]*)\" hesabında (\\d+) TL bakiye ve (\\d+) TL mevcut bloke var$")
    public void musterininHesabindaBakiyeVeBlokeVar(String hesapNo, double bakiye, double mevcutBloke) throws Throwable {
        BlokService.Hesap hesap = new BlokService.Hesap(hesapNo, bakiye - mevcutBloke); // Mevcut bloke çıkarılarak kullanılabilir bakiye
        hesaplar.add(hesap);
    }

    @Diyelimki("^Müşterinin \"([^\"]*)\" hesabında (\\d+) TL bakiye var$")
    public void musterininHesabindaBakiyeVar(String hesapNo, double bakiye) throws Throwable {
        BlokService.Hesap hesap = new BlokService.Hesap(hesapNo, bakiye);
        hesaplar.add(hesap);
    }

    @Diyelimki("^Müşterinin bankadaki kredi borcu \\(risk\\) (\\d+) TL'dir$")
    public void musterininBankadakiKrediBorcuRisk(double risk) throws Throwable {
        this.riskTutari = risk;
    }

    @Eğerki("^Müşterinin o günkü toplam varlığının yarısı \\(%\\d+\\) oranında tedbir blokesi talep edilirse$")
    public void musterininToplamVarligininYarisiOranindaTedbirBlokesiTalepEdilirse() throws Throwable {
        BlokService service = new BlokService();
        this.sonuc = service.hesapBlokesiDagit(hesaplar, riskTutari);
    }

    @Ozaman("^Sistem hesaplara aşağıdaki gibi bloke koymalıdır:$")
    public void sistemHesaplaraAsagidakiGibiBlokeKoymali(io.cucumber.datatable.DataTable dataTable) throws Throwable {
        List<Map<String, String>> expected = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : expected) {
            String hesapNo = row.get("Hesap");
            double expectedRisk = parseAmount(row.get("Banka Riski Blokesi"));
            double expectedTedbir = parseAmount(row.get("Tedbir Blokesi"));

            Map<String, Double> actual = sonuc.get(hesapNo);
            Assert.assertNotNull("Hesap bulunamadı: " + hesapNo, actual);
            Assert.assertEquals("Risk blokesi yanlış: " + hesapNo, expectedRisk, actual.get("Banka Riski Blokesi"), 0.01);
            Assert.assertEquals("Tedbir blokesi yanlış: " + hesapNo, expectedTedbir, actual.get("Tedbir Blokesi"), 0.01);
        }
    }

    private double parseAmount(String amount) {
        return Double.parseDouble(amount.replace(" TL", "").replace(",", "."));
    }

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