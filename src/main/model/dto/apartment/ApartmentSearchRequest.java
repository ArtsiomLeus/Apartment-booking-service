import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
public class ApartmentSearchRequest {
    private String city;
    private Integer minGuests;
    private BigInteger minPrice;
    private BigInteger maxPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}









