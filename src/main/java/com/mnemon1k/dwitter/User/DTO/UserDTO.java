package com.mnemon1k.dwitter.User.DTO;

import com.mnemon1k.dwitter.User.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String username;
    private String displayName;
    private String image;

    public UserDTO(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setImage(user.getImage());
        this.setDisplayName(user.getDisplayName());
    }
}
