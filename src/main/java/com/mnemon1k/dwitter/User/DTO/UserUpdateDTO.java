package com.mnemon1k.dwitter.User.DTO;

import com.mnemon1k.dwitter.shared.ProfileImage;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {

    @NotNull
    @Size(min = 4, max = 128, message = "{dwitter.constraints.username.Size.message}")
    private String displayName;

    @ProfileImage
    private String image;
}
