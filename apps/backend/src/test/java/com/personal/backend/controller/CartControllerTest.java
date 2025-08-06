package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.CartDto;
import com.personal.backend.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
// Apply this to all tests in the class to simulate a user logged in with the specified email.
@WithMockUser(username = "test@user.com")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("장바구니 조회 API 성공")
    void getCart_Success() throws Exception {
        // given
        String userEmail = "test@user.com"; // Must match the username in @WithMockUser
        CartDto.CartItemResponse itemResponse = new CartDto.CartItemResponse(1L, 100L, "상품1", 10000, "img.jpg", 1);
        CartDto.CartResponse cartResponse = new CartDto.CartResponse(List.of(itemResponse), 10000);

        // Mock a call to the service with the expected user email
        when(cartService.getCartForUser(userEmail)).thenReturn(cartResponse);

        // when & then
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(10000))
                .andExpect(jsonPath("$.items.size()").value(1));
    }

    @Test
    @DisplayName("장바구니에 상품 추가 API 성공")
    void addItemToCart_Success() throws Exception {
        // given
        String userEmail = "test@user.com";
        CartDto.AddItemRequest request = new CartDto.AddItemRequest(100L, 2);
        CartDto.CartResponse cartResponse = new CartDto.CartResponse(List.of(), 20000); // Dummy response

        // Mock the service call, ensuring the userEmail and request object are handled
        when(cartService.addItem(eq(userEmail), any(CartDto.AddItemRequest.class))).thenReturn(cartResponse);

        // when & then
        mockMvc.perform(post("/cart/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20000));
    }

    @Test
    @DisplayName("장바구니 아이템 삭제 API 성공")
    void removeItemFromCart_Success() throws Exception {
        // given
        String userEmail = "test@user.com";
        Long itemId = 1L;
        CartDto.CartResponse cartResponse = new CartDto.CartResponse(List.of(), 0); // Dummy response

        // Mock the service call
        when(cartService.removeItemFromCart(userEmail, itemId)).thenReturn(cartResponse);

        // when & then
        mockMvc.perform(delete("/cart/items/{itemId}", itemId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(0));
    }
}