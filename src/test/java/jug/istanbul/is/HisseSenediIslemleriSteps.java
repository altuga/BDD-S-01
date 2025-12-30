package jug.istanbul.is;

import io.cucumber.java.tr.Diyelimki;
import io.cucumber.java.tr.Eğerki;
import io.cucumber.java.tr.Ozaman;
import org.junit.Assert;


public class HisseSenediIslemleriSteps {

    private HisseService hisseServisi;
    private HisseSenet hisse;


    @Diyelimki("^hisse senedinin birim eşik satış fiyatı \"(.+)\"$")
    public void hisseSenedininBirimEşikSatışFiyatı(double esikDeger) throws Throwable {
        hisseServisi = new HisseService();
        hisse = hisseServisi.yeniHisseEkle("STK", esikDeger);
    }

    @Eğerki("^hisse senedi \"(.+)\" üzerinden işlem görüyorsa$")
    public void hisseSenediÜzerindenIşlemGörüyorsa(double fiyat) throws Throwable {
        hisse.islem(fiyat);
    }

    @Ozaman("^sistem  uyarısı  şöyle olmalıdır \"(.*?)\" \\.$")
    public void sistemUyarısıŞöyleOlmalıdır(String arg1) throws Throwable {
        Assert.assertEquals(hisse.getDurum(), "OFF");
    }


}