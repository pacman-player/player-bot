package telegramApp.dto;

/**
 * Класс для передачи TelegramUser и номера заведения (company), в
 * котором он хочет заказать песню.
 */

public class VisitDto {

    private TelegramUserDto telegramUserDto;

    private Long companyId;

    public VisitDto() {
    }

    public VisitDto(TelegramUserDto telegramUserDto, Long companyId) {
        this.telegramUserDto = telegramUserDto;
        this.companyId = companyId;
    }

    public TelegramUserDto getTelegramUserDto() {
        return telegramUserDto;
    }

    public void setTelegramUserDto(TelegramUserDto telegramUserDto) {
        this.telegramUserDto = telegramUserDto;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
