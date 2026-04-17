package dasturlash.uz.service;

import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.entity.RegionEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.mapper.RegionMapper;
import dasturlash.uz.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public RegionDTO create(RegionDTO dto) {
        Optional<RegionEntity> optional = regionRepository.findByRegionKey(dto.getRegionKey());
        if (optional.isPresent()) {
            throw new AppBadException("Region key already exist");
        }
        RegionEntity entity = new RegionEntity();
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setRegionKey(dto.getRegionKey());
        entity.setVisible(Boolean.TRUE);
        entity.setCreatedDate(LocalDateTime.now());
        regionRepository.save(entity);
        // response
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public RegionDTO update(Integer id, RegionDTO newDto) {// Jahon
        Optional<RegionEntity> optional = regionRepository.findByIdAndVisibleIsTrue(id);
        if (optional.isEmpty()) {
            throw new AppBadException("Region not found");
        }
        Optional<RegionEntity> keyOptional = regionRepository.findByRegionKey(newDto.getRegionKey()); // Jahon
        if (keyOptional.isPresent() && !id.equals(keyOptional.get().getId())) {
            throw new AppBadException("Region present");
        }
        // 1-Jahon,2-Iksodiyot,3-Sport
        RegionEntity entity = optional.get();
        entity.setOrderNumber(newDto.getOrderNumber());
        entity.setNameUz(newDto.getNameUz());
        entity.setNameRu(newDto.getNameRu());
        entity.setNameEn(newDto.getNameEn());
        entity.setRegionKey(newDto.getRegionKey());
        regionRepository.save(entity);

        newDto.setId(entity.getId());
        return newDto;
    }

    public Boolean delete(Integer id) {
        return regionRepository.updateVisibleById(id) == 1;
    }

    public List<RegionDTO> getAll() {
        Iterable<RegionEntity> iterable = regionRepository.findAll();
        List<RegionDTO> dtos = new LinkedList<>();
        iterable.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    public List<RegionDTO> getAllByLang(AppLanguageEnum lang) {
        Iterable<RegionMapper> iterable = regionRepository.getByLang(lang.name());
        List<RegionDTO> dtoList = new LinkedList<>();
        iterable.forEach(mapper -> {
            RegionDTO dto = new RegionDTO();
            dto.setId(mapper.getId());
            dto.setName(mapper.getName());
            dto.setRegionKey(mapper.getRegionKey());
            dtoList.add(dto);
        });
        return dtoList;
    }

    public RegionDTO getByIdAndLang(Integer id, AppLanguageEnum lang) {
        Optional<RegionMapper> regionOp = regionRepository.getByIdAndLang(id, lang.name());
        if (regionOp.isEmpty()) {
            return null;
        }
        return regionOp.map(mapper -> {
            RegionDTO dto = new RegionDTO();
            dto.setId(mapper.getId());
            dto.setName(mapper.getName());
            dto.setRegionKey(mapper.getRegionKey());
            return dto;
        }).get();
    }


    private RegionDTO toDto(RegionEntity entity) {
        RegionDTO dto = new RegionDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setNameUz(entity.getNameUz());
        dto.setNameRu(entity.getNameRu());
        dto.setNameEn(entity.getNameEn());
        dto.setRegionKey(entity.getRegionKey());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    private RegionDTO toLangResponseDto(RegionEntity entity, AppLanguageEnum lang) {
        RegionDTO dto = new RegionDTO();
        dto.setId(entity.getId());
        dto.setRegionKey(entity.getRegionKey());
        switch (lang) {
            case UZ:
                dto.setName(entity.getNameUz());
                break;
            case RU:
                dto.setName(entity.getNameRu());
                break;
            case EN:
                dto.setName(entity.getNameEn());
                break;
        }
        return dto;
    }

    public RegionEntity get(Integer id) {
        return regionRepository.findById(id).orElseThrow(() -> {
            throw new AppBadException("Item not found");
        });
    }
}
