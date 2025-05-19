package vn.iotstar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //toString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @JsonProperty("username")
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password can not be blank")
    private String password;
}
