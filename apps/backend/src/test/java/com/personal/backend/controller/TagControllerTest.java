package com.personal.backend.controller;

import com.personal.backend.domain.Tag;
import com.personal.backend.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagRepository tagRepository;

    @Test
    @DisplayName("모든 태그 목록 조회 API 성공")
    void getAllTags_Success() throws Exception {
        // given
        Tag tag1 = new Tag("인기상품");
        Tag tag2 = new Tag("세일");
        List<Tag> tagList = List.of(tag1, tag2);

        when(tagRepository.findAll()).thenReturn(tagList);

        // when & then
        mockMvc.perform(get("/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("인기상품"))
                .andExpect(jsonPath("$[1].name").value("세일"));
    }
}
