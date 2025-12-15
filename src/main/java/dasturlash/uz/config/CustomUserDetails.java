package dasturlash.uz.config;

import dasturlash.uz.entity.ProfileRoleEntity;
import dasturlash.uz.enums.ProfileRoleEnum;
import dasturlash.uz.enums.ProfileStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private Integer id;
    private String username;
    private String password;
    private List<SimpleGrantedAuthority> rolelist;
    private ProfileStatus status;

    public CustomUserDetails(Integer id, String username, String password, ProfileStatus status,
                             List<ProfileRoleEnum> roleList) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;

        List<SimpleGrantedAuthority> roles = new LinkedList<>();
        roleList.forEach(role -> {
            roles.add(new SimpleGrantedAuthority(role.name()));
        });
        this.rolelist = roles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolelist;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals("ACTIVE");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
