package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

        private String username;

        private String password;

        private String confirmPassword;

        private String email;

        private String sex;

}
