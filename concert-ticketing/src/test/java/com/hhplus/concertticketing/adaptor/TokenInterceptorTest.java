package com.hhplus.concertticketing.adaptor;

import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.business.model.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class TokenInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenUseCase tokenUseCase;

    @InjectMocks
    private TokenInterceptor tokenInterceptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new Object())
                .addInterceptors(tokenInterceptor)
                .build();
    }

    @Test
    void preHandle_ShouldReturn200_WhenTokenIsValid() throws Exception {
        when(tokenUseCase.getTokenStatus(anyString())).thenReturn(new TokenStatusResponse(TokenStatus.ACTIVE, 1L));

        mockMvc.perform(get("/api/some-endpoint")
                        .header("Authorization", "validToken"))
                .andExpect(status().isOk());
    }

    @Test
    void preHandle_ShouldReturn401_WhenTokenIsInvalid() throws Exception {
        when(tokenUseCase.getTokenStatus(anyString())).thenReturn(new TokenStatusResponse(TokenStatus.EXPIRED, 1L));

        mockMvc.perform(get("/api/some-endpoint")
                        .header("Authorization", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void preHandle_ShouldReturn401_WhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/some-endpoint"))
                .andExpect(status().isUnauthorized());
    }
}