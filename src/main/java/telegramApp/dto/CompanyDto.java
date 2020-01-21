package telegramApp.dto;

public class CompanyDto {

    private Long id;
    private String name;
    private String startTime;
    private String closeTime;
    private Long orgType;
    private Long userId;

    public CompanyDto(Long id, String name, String startTime, String closeTime, Long orgType, Long userId) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.closeTime = closeTime;
        this.orgType = orgType;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public Long getOrgType() {
        return orgType;
    }

    public void setOrgType(Long orgType) {
        this.orgType = orgType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
