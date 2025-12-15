package dasturlash.uz.service;


import dasturlash.uz.dto.auth.AuthorizationDTO;
import dasturlash.uz.dto.auth.RegistrationDTO;
import dasturlash.uz.dto.auth.VerificationDTO;
import dasturlash.uz.dto.profile.ProfileDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRoleEnum;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    SmsSenderService smsSenderService;
    @Autowired
    private EmailHistoryService emailHistoryService;


    public String registration(RegistrationDTO dto) {
        // check
        Optional<ProfileEntity> existOptional = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());
        if (existOptional.isPresent()) {
            ProfileEntity existsProfile = existOptional.get();
            if (existsProfile.getStatus() == ProfileStatus.NOT_ACTIVE) {
                profileRoleService.deleteRolesByProfileId(existsProfile.getId());
                profileRepository.deleteById(existsProfile.getId()); // delete
            } else {
                throw new AppBadException("Username already exists");
            }
        }
        // create profile
        ProfileEntity profile = new ProfileEntity();
        profile.setName(dto.getName());
        profile.setSurname(dto.getSurname());
        profile.setUsername(dto.getUsername());
        profile.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        profile.setStatus(ProfileStatus.NOT_ACTIVE);
        profileRepository.save(profile);
        // create profile roles
        profileRoleService.create(profile.getId(), ProfileRoleEnum.ROLE_USER);
        // send sms to amail or phone

        if (isEmail(dto.getUsername())) {
//            emailSendingService.sendSimpleMessage(dto.getUsername(), "Complete Registration", "Sms code to confirm: 12345");
            emailSendingService.sendRegistrationStyledEmail(dto.getUsername());
        }
        if(isPhoneNumber(dto.getUsername())){
            smsSenderService.sendRegistrationSMS(dto.getUsername());
        }

        // response
        return "Verification code sent to your email";
    }

    public ProfileDTO login(AuthorizationDTO dto) {
        Optional<ProfileEntity> profileOptional = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());
        if (profileOptional.isEmpty()) {
            throw new AppBadException("Username or password wrong");
        }
        ProfileEntity entity = profileOptional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), entity.getPassword())) {
            throw new AppBadException("Username or password wrong");
        }
//        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
//            throw new AppBadException("User in wrong status");
//        }
        // status
        ProfileDTO response = new ProfileDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSurname(entity.getSurname());
        response.setUsername(entity.getUsername());
        response.setRoleList(profileRoleService.getByProfileId(entity.getId()));
        response.setJwt(JwtUtil.encode(entity.getUsername(),response.getRoleList()));
        return response;
    }

    public String regEmailVerification(VerificationDTO dto) {

        Optional<ProfileEntity> existOptional = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());
        if (existOptional.isEmpty()) {
            throw new AppBadException("Username not found");
        }
        ProfileEntity profile = existOptional.get();
        if (!profile.getStatus().equals(ProfileStatus.NOT_ACTIVE)) {
            throw new AppBadException("Username in wrong status");
        }
        // check fo sms code and time
        if (emailHistoryService.isSmsSendToAccount(dto.getUsername(), dto.getCode())) {
            profile.setStatus(ProfileStatus.ACTIVE);
            profileRepository.save(profile);
            return "Verification successfully completed";
        }
        throw new AppBadException("Not completed");
    }


    public boolean isEmail(String username) {
        return username.contains("@");
    }

    public boolean isPhoneNumber(String username) {
        return username.matches("\\+?\\d{10,15}");
    }

}
