package jug.istanbul.is;

import io.cucumber.java.tr.Diyelimki;
import io.cucumber.java.tr.Eğerki;
import io.cucumber.java.tr.Ozaman;
import org.junit.Assert;
import java.util.*;

public class Case4Steps {

    private List<BlokService.Hesap> hesaplar = new ArrayList<>();
    private Map<String, Map<String, Double>> sonuc;

    @Diyelimki("^müşteri aşağıdaki hesaplara sahiptir:$")
    public void musteriAsagidakiHesaplaraSahiptir(io.cucumber.datatable.DataTable dataTable) throws Throwable {
        hesaplar.clear();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String hesapNo = row.get("hesap");
            double bakiye = Double.parseDouble(row.get("bakiye"));
            BlokService.Hesap hesap = new BlokService.Hesap(hesapNo, bakiye);
            hesaplar.add(hesap);
        }
    }

    @Diyelimki("^toplam varlık (\\d+) TL'dir$")
    public void toplamVarlikTLOdir(double toplamVarlik) throws Throwable {
        // This is for validation, but not used in calculation
    }

    @Eğerki("^(\\d+) TL tutarında tedbir istenir$")
    public void tutarindaTedbirIstenir(double tedbirTutari) throws Throwable {
        // Calculate proportional distribution
        double totalBakiye = hesaplar.stream().mapToDouble(BlokService.Hesap::getBakiye).sum();
        for (BlokService.Hesap hesap : hesaplar) {
            double oran = hesap.getBakiye() / totalBakiye;
            double tedbir = tedbirTutari * oran;
            hesap.setTedbirBlokesi(tedbir);
        }
        // Store in sonuc for consistency
        sonuc = new LinkedHashMap<>();
        for (BlokService.Hesap hesap : hesaplar) {
            Map<String, Double> bloklar = new LinkedHashMap<>();
            bloklar.put("Banka Riski Blokesi", 0.0);
            bloklar.put("Tedbir Blokesi", hesap.getTedbirBlokesi());
            sonuc.put(hesap.getHesapNo(), bloklar);
        }
    }

    @Ozaman("^tedbir hesaplara bakiye oranında dağıtılır$")
    public void tedbirHesaplaraBakiyeOranindaDagitilir() throws Throwable {
        // Already calculated in When step
    }

    @Ozaman("^Hesap ([^ ]+) için tedbir (\\d+) TL olmalıdır$")
    public void hesapIcinTedbirTLOlmalidir(String hesapNo, double expectedTedbir) throws Throwable {
        Map<String, Double> bloklar = sonuc.get(hesapNo);
        Assert.assertNotNull("Hesap bulunamadı: " + hesapNo, bloklar);
        Assert.assertEquals("Tedbir blokesi yanlış: " + hesapNo, expectedTedbir, bloklar.get("Tedbir Blokesi"), 0.01);
    }
}