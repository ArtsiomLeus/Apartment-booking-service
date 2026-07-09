import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigInteger;

@Data
public class ApartmentRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    private String description;

    @NotNull(message = "Price is required")
    @IntegerMin(value = '0', message = "Price must greater that 0")
    @IntegerMax(value = "999999", message = "Price is too high")
    private BigInteger pricePerNight;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @Min(value = 1, message = "At least 1 guest allowed")
    @Max(value = 20, message = "Maximum 1 guests allowed")
    private Integer maxGuests;
}





















