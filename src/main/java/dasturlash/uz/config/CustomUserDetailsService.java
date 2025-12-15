package dasturlash.uz.config;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRoleEnum;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> optional = profileRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        ProfileEntity profileEntity = optional.get();

        List<ProfileRoleEnum> roleList = profileRoleRepository.getRoleListByProfileId(profileEntity.getId());

        return new CustomUserDetails(profileEntity.getId(),
                profileEntity.getUsername(),
                profileEntity.getPassword(),
                profileEntity.getStatus(),
                roleList
           );
    }
}
