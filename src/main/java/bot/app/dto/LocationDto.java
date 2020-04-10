package bot.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationDto {
    private static final String LATITUDE_FIELD = "latitude";
    private static final String LONGITUDE_FIELD = "longitude";

    private Long id;

    @JsonProperty("latitude")
    private Float latitude;

    @JsonProperty("longitude")
    private Float longitude;

    public LocationDto(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getLatitude() {
        return this.latitude;
    }

    public Float getLongitude() {
        return this.longitude;
    }

    public String toString() {
        return "Location{latitude=" + this.latitude + ", longitude=" + this.longitude + '}';
    }
}
