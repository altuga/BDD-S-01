package jug.istanbul.is;

import io.cucumber.java.tr.Diyelimki;
import io.cucumber.java.tr.Eğerki;
import io.cucumber.java.tr.Ozaman;
import org.junit.Assert;
import java.util.*;

public class Case1Steps {

    private List<BlokService.Hesap> hesaplar = new ArrayList<>();
    private double riskTutari;
    private Map<String, Map<String, Double>> sonuc;

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
}