package dasturlash.uz.service;

import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.entity.SectionEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.mapper.SectionMapper;
import dasturlash.uz.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public SectionDTO create(SectionDTO dto) {
        Optional<SectionEntity> optional = sectionRepository.findBySectionKey(dto.getSectionKey());
        if (optional.isPresent()) {
            throw new AppBadException("Section key already exist");
        }
        SectionEntity entity = new SectionEntity();
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setSectionKey(dto.getSectionKey());
        entity.setVisible(Boolean.TRUE);
        entity.setCreatedDate(LocalDateTime.now());
        sectionRepository.save(entity);
        // response
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public SectionDTO update(Integer id, SectionDTO newDto) {// Jahon
        Optional<SectionEntity> optional = sectionRepository.findByIdAndVisibleIsTrue(id);
        if (optional.isEmpty()) {
            throw new AppBadException("Section not found");
        }
        Optional<SectionEntity> keyOptional = sectionRepository.findBySectionKey(newDto.getSectionKey()); // Jahon
        if (keyOptional.isPresent() && !id.equals(keyOptional.get().getId())) {
            throw new AppBadException("Section present");
        }
        // 1-Jahon,2-Iksodiyot,3-Sport
        SectionEntity entity = optional.get();
        entity.setOrderNumber(newDto.getOrderNumber());
        entity.setNameUz(newDto.getNameUz());
        entity.setNameRu(newDto.getNameRu());
        entity.setNameEn(newDto.getNameEn());
        entity.setSectionKey(newDto.getSectionKey());
        sectionRepository.save(entity);

        newDto.setId(entity.getId());
        return newDto;
    }

    public Boolean delete(Integer id) {
        return sectionRepository.updateVisibleById(id) == 1;
    }

    public List<SectionDTO> getAll() {
        Iterable<SectionEntity> iterable = sectionRepository.findAll();
        List<SectionDTO> dtos = new LinkedList<>();
        iterable.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    public List<SectionDTO> getAllByLang(AppLanguageEnum lang) {
        Iterable<SectionMapper> iterable = sectionRepository.getByLang(lang.name());
        List<SectionDTO> dtoList = new LinkedList<>();
        iterable.forEach(mapper -> {
            SectionDTO dto = new SectionDTO();
            dto.setId(mapper.getId());
            dto.setName(mapper.getName());
            dto.setSectionKey(mapper.getSectionKey());
            dtoList.add(dto);
        });
        return dtoList;
    }

    public List<SectionDTO> getSectionListByArticleIdAndLang(String articleId, AppLanguageEnum lang) {
        List<SectionMapper> iterable = sectionRepository.getSectionListByArticleIdAndLang(articleId, lang.name());
        List<SectionDTO> dtoList = new LinkedList<>();
        iterable.forEach(mapper -> {
            SectionDTO dto = new SectionDTO();
            dto.setId(mapper.getId());
            dto.setName(mapper.getName());
            dto.setSectionKey(mapper.getSectionKey());
            dtoList.add(dto);
        });
        return dtoList;
    }

    private SectionDTO toDto(SectionEntity entity) {
        SectionDTO dto = new SectionDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setNameUz(entity.getNameUz());
        dto.setNameRu(entity.getNameRu());
        dto.setNameEn(entity.getNameEn());
        dto.setSectionKey(entity.getSectionKey());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    private SectionDTO toLangResponseDto(SectionEntity entity, AppLanguageEnum lang) {
        SectionDTO dto = new SectionDTO();
        dto.setId(entity.getId());
        dto.setSectionKey(entity.getSectionKey());
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

    public SectionEntity get(Integer id) {
        return sectionRepository.findById(id).orElseThrow(() -> {
            throw new AppBadException("Item not found");
        });
    }
}
