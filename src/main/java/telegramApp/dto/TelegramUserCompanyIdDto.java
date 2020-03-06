package telegramApp.dto;

/**
 * Класс для передачи TelegramUser и номера заведения (company), в
 * котором он хочет заказать песню.
 */

public class TelegramUserCompanyIdDto {

    private TelegramUser telegramUser;

    private Long companyId;

    public TelegramUserCompanyIdDto() {
    }

    public TelegramUserCompanyIdDto(TelegramUser telegramUser, Long companyId) {
        this.telegramUser = telegramUser;
        this.companyId = companyId;
    }

    public TelegramUser getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
