package dasturlash.uz.service;

import dasturlash.uz.dto.profile.*;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRoleEnum;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ProfileDTO create(ProfileDTO profile) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleIsTrue(profile.getUsername());
        if (optional.isPresent()) {
            throw new AppBadException("User exists");
        }

        ProfileEntity entity = new ProfileEntity();
        entity.setName(profile.getName());
        entity.setSurname(profile.getSurname());
        entity.setPassword(bCryptPasswordEncoder.encode(profile.getPassword()));
        entity.setUsername(profile.getUsername());
        entity.setStatus(ProfileStatus.ACTIVE);
        entity.setVisible(Boolean.TRUE);
        profileRepository.save(entity);

        // ROLE FIX
        List<ProfileRoleEnum> roleList = profile.getRoleList();
        if (roleList == null) {
            roleList = List.of(ProfileRoleEnum.ROLE_USER);
        }
        profileRoleService.merge(entity.getId(), roleList);

        return toDTO(entity);
    }



    public ProfileDTO update(Integer id, ProfileUpdateDTO dto) {
        ProfileEntity entity = get(id);
        // check username exists
        Optional<ProfileEntity> usernameOptional = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());
        if (usernameOptional.isPresent() && !usernameOptional.get().getId().equals(id)) {
            throw new AppBadException("Username exists");
        }
        //
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setUsername(dto.getUsername());
        profileRepository.save(entity); // update
        // role_save
        profileRoleService.merge(entity.getId(), dto.getRoleList());
        // result
        ProfileDTO response = toDTO(entity);
        response.setRoleList(dto.getRoleList());
        return response;
    }

    public ProfileDTO getById(Integer id) {
        ProfileEntity profile = get(id);
        ProfileDTO dto = toDTO(profile);
        dto.setRoleList(profileRoleService.getByProfileId(id));
        return dto;
    }

    public ProfileDTO updateDetail(Integer currentProfileId, ProfileUpdateDetailDTO dto) {
        ProfileEntity entity = get(currentProfileId);
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        profileRepository.save(entity); // update
        return toDTO(entity);
    }

    public PageImpl<ProfileDTO> pagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<ProfileEntity> result = profileRepository.findAllWithRoles(pageable);

        List<ProfileDTO> dtoList = new LinkedList<>();
        for (ProfileEntity profile : result.getContent()) {
            dtoList.add(toDTO(profile));
        }
        return new PageImpl<>(dtoList, pageable, result.getTotalElements());
    }

    public Boolean delete(Integer id) {
        ProfileEntity entity = get(id);
        entity.setVisible(true);
        profileRepository.save(entity); // or write a JPA query to update only visible
        return true;
    }

    public Boolean updatePassword(Integer currentProfileId, ProfileUpdatePasswordDTO dto) {
        ProfileEntity profile = get(currentProfileId);
        if (!bCryptPasswordEncoder.matches(dto.getCurrentPassword(), profile.getPassword())) {
            throw new AppBadException("Wrong password");
        }
        profile.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
        profileRepository.save(profile);
        return true;
    }

    public Boolean updatePhoto(Integer currentProfileId, ProfileUpdatePhotoDTO dto) {
        ProfileEntity profile = get(currentProfileId);
        profile.setPhotoId(dto.getPhotoId());
        profileRepository.save(profile);
        return true;
    }

    public ProfileDTO toDTO(ProfileEntity entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setUsername(entity.getUsername());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public ProfileEntity get(Integer id) {
        return profileRepository.findByIdAndVisibleIsTrue(id).orElseThrow(() -> {
            throw new AppBadException("Profile not found");
        });
        /*Optional<ProfileEntity> optional = profileRepository.findByIdAndVisibleIsTrue(id);
        if (optional.isEmpty()) {
            throw new AppBadException("Profile not found");
        }
        return optional.get();*/
    }




}
