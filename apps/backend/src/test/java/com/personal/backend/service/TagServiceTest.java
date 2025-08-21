package com.personal.backend.service;

import com.personal.backend.domain.Tag;
import com.personal.backend.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Test
    @DisplayName("태그 찾기 또는 생성 - 기존 태그 반환")
    void findOrCreate_FindsExistingTag() {
        // given
        String tagName = "테스트태그";
        Tag existingTag = new Tag(tagName);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(existingTag));

        // when
        Tag result = tagService.findOrCreate(tagName);

        // then
        assertThat(result).isEqualTo(existingTag);
        verify(tagRepository, times(1)).findByName(tagName);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 찾기 또는 생성 - 새 태그 생성")
    void findOrCreate_CreatesNewTag() {
        // given
        String tagName = "새로운태그";
        Tag newTag = new Tag(tagName);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

        // when
        Tag result = tagService.findOrCreate(tagName);

        // then
        assertThat(result).isEqualTo(newTag);
        verify(tagRepository, times(1)).findByName(tagName);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }
}
