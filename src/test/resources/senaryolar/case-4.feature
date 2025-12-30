Feature: Tedbir blokesi hesaplara oransal dağıtılır

  Scenario: Tedbir, hesap bakiyelerine oranla dağıtılır
    Given müşteri aşağıdaki hesaplara sahiptir:
      | hesap | bakiye |
      | A     | 60000  |
      | B     | 40000  |
    And toplam varlık 100000 TL'dir
    When 50000 TL tutarında tedbir istenir
    Then tedbir hesaplara bakiye oranında dağıtılır
    And Hesap A için tedbir 30000 TL olmalıdır
    And Hesap B için tedbir 20000 TL olmalıdır
