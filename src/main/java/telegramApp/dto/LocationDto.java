package telegramApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationDto {
    private static final String LATITUDE_FIELD = "latitude";
    private static final String LONGITUDE_FIELD = "longitude";

    @JsonProperty("latitude")
    private Float latitude;

    @JsonProperty("longitude")
    private Float longitude;


    public LocationDto() {
    }

    public LocationDto(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
