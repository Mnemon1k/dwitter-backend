package com.mnemon1k.dwitter.User;

import com.mnemon1k.dwitter.Record.Record;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @Size(min = 4, max = 32, message = "{dwitter.constraints.username.Size.message}")
    @UniqueUsername
    private String username;

    @NotNull
    @Size(min = 4, max = 128, message = "{dwitter.constraints.username.Size.message}")
    private String displayName;

    @NotNull
    @Size(min = 6, max = 64, message = "{dwitter.constraints.username.Size.message}")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}", message = "{dwitter.constraints.username.NotMatchPattern.message}")
    private String password;

    private String image;

    @OneToMany(mappedBy = "user")
    private List<Record> records;

    @Override
    @java.beans.Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("Role_USER");
    }

    @Override
    @java.beans.Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @java.beans.Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @java.beans.Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @java.beans.Transient
    public boolean isEnabled() {
        return true;
    }
}
