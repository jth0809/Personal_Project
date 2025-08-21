package com.personal.backend.service;

import com.personal.backend.domain.Tag;
import com.personal.backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreate(String name) {
        Optional<Tag> tag = tagRepository.findByName(name);
                return tag.orElseGet(() -> tagRepository.save(new Tag(name)));
    }

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

}
