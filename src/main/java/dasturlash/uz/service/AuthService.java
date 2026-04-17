package dasturlash.uz.service;

import dasturlash.uz.dto.JwtDTO;
import dasturlash.uz.dto.auth.AuthorizationDTO;
import dasturlash.uz.dto.auth.RegistrationDTO;
import dasturlash.uz.dto.auth.VerificationBySmsDTO;
import dasturlash.uz.dto.profile.ProfileDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.sms.SmsHistoryEntity;
import dasturlash.uz.enums.ProfileRoleEnum;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.service.email.EmailHistoryService;
import dasturlash.uz.service.email.EmailSenderService;
import dasturlash.uz.service.sms.SmsHistoryService;
import dasturlash.uz.service.sms.SmsSenderService;
import dasturlash.uz.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private EmailSenderService emailSenderService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private SmsSenderService smsSenderService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ProfileService profileService;

    public String registration(RegistrationDTO dto) {
        // check
        Optional<ProfileEntity> existOptional = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());
        if (existOptional.isPresent()) {
            ProfileEntity existsProfile = existOptional.get();
            if (existsProfile.getStatus().equals(ProfileStatus.NOT_ACTIVE)) {
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
        profile.setVisible(true);
        profile.setStatus(ProfileStatus.NOT_ACTIVE);
        profileRepository.save(profile);
        // create profile roles
        profileRoleService.create(profile.getId(), ProfileRoleEnum.ROLE_USER);
        // send verification code
        // send()
        if (profile.getUsername().contains("@")) { // email va phone ga tekshirishni o'zgartirsa bo'ladi.
            // Email Send
            emailSenderService.sendRegistrationStyledEmail(profile.getUsername());
        } else {
            // SMS Send
            smsSenderService.sendRegistrationSMS(profile.getUsername());
        }
        // response
        return "Tastiqlash kodi ketdi mazgi qara.";
    }

    public String regEmailVerification(String token) {
        JwtDTO jwtDTO = null;
        try {
            jwtDTO = JwtUtil.decodeRegistrationToken(token);
        } catch (ExpiredJwtException e) {
            throw new AppBadException("JWT Expired");
        } catch (JwtException e) {
            throw new AppBadException("JWT Not Valid");
        }
        String username = jwtDTO.getUsername();

        Optional<ProfileEntity> existOptional = profileRepository.findByUsernameAndVisibleIsTrue(username);
        if (existOptional.isEmpty()) {
            throw new AppBadException("Username not found");
        }
        ProfileEntity profile = existOptional.get();
        if (!profile.getStatus().equals(ProfileStatus.NOT_ACTIVE)) {
            throw new AppBadException("Username int wrong status");
        }
        // check fo sms code and time
        if (emailHistoryService.isSmsSendToAccount(username, jwtDTO.getCode())) {
            profile.setStatus(ProfileStatus.ACTIVE);
            profileRepository.save(profile);
            return "Verification successfully completed";
        }
        throw new AppBadException("Not completed");
    }

    public String verificationBySms(VerificationBySmsDTO dto) {
        if (smsHistoryService.isSmsSendToPhone(dto.getPhoneNumber(), dto.getCode())) {
            profileService.setStatusByUsername(ProfileStatus.ACTIVE, dto.getPhoneNumber());
            return "Verification Success!";
        }
        throw new AppBadException("Wrong sms code");
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
        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppBadException("User in wrong status");
        }
        ProfileDTO response = new ProfileDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSurname(entity.getSurname());
        response.setUsername(entity.getUsername());
        response.setRoleList(profileRoleService.getByProfileId(entity.getId()));
        response.setJwt(JwtUtil.encode(entity.getUsername(), response.getRoleList()));
        return response;
    }
}
