import lombok.Builder;
import lombok.Data;
import java.math.BigInteger;

@Date
@Builder
public class ApartmentResponse {
    private Long id;
    private String title;
    private String description;
    private BigInteger pricePerNight;
    private String address;
    private String city;
    private Integer maxGuests;
    private Boolean isActive;
    private String ownerFullName;
}