package vn.iotstar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data //toString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("username")
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password can not be blank")
    private String password;

    @JsonProperty("retypePassword")
    private String retypePassword;

    @NotBlank(message = "Email can not be blank")
    private String email;
}

