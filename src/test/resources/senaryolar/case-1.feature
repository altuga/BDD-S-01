Feature: Mevduat Hesaplarına Risk ve Tedbir Blokesi Dağıtımı

  Background:
    Given Müşterinin "AB-1" hesabında 50000 TL bakiye ve 30000 TL mevcut bloke var
    And Müşterinin "AB-2" hesabında 100000 TL bakiye var
    And Müşterinin "AB-3" hesabında 100 TL bakiye var
    And Müşterinin "AB-4" hesabında 0 TL bakiye var

  Scenario: Case 1 - Çoklu hesapta risk ve tedbir blokesi dağıtımı
    Given Müşterinin bankadaki kredi borcu (risk) 40000 TL'dir
    When Müşterinin o günkü toplam varlığının yarısı (%50) oranında tedbir blokesi talep edilirse
    Then Sistem hesaplara aşağıdaki gibi bloke koymalıdır:
      | Hesap | Banka Riski Blokesi | Tedbir Blokesi | Açıklama                          |
      | AB-1  | 20000 TL            | 0 TL           | Risk önceliği burayı doldurur     |
      | AB-2  | 20000 TL            | 30000 TL       | Kalan risk + hesaplanan tedbir    |
      | AB-3  | 0 TL                | 50 TL          | Sadece tedbir (Bakiyenin yarısı)  |
      | AB-4  | 0 TL                | 0 TL           | Bakiye yok                        |

  Scenario: Case 3 - Tek hesapta yüksek bakiye ve risk durumu
    Given Müşterinin tek bir hesabında 750000 TL bakiye var
    And Müşterinin banka riski 15000 TL'dir
    When Hesaba "Yarı Oranında" (375000 TL) bloke konulmak istendiğinde
    Then Konulacak "Banka Tedbir Blokesi" tutarı 15000 TL olmalıdır
    And Konulacak "Normal Tedbir Blokesi" tutarı 360000 TL olmalıdır
    # Toplam bloke: 375.000 (15k Risk + 360k Tedbir)