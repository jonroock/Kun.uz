package dasturlash.uz.util;

import dasturlash.uz.config.CustomUserDetails;
import dasturlash.uz.enums.ProfileRoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class SpringSecurityUtil {

    public static Integer currentProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.getId();
    }

    public static Boolean hasAnyRoles(ProfileRoleEnum... roles) {
        List<String> roleList = Arrays.stream(roles).map(Enum::name).toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        for (GrantedAuthority authority : user.getAuthorities()) {
            if (roleList.contains(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
