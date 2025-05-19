package vn.iotstar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data //toString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtpDTO {
    @JsonProperty("OTP")
    @NotBlank(message = "OTP is required")
    private String OTP;
}
