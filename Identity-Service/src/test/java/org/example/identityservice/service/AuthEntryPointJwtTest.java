package org.example.identityservice.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.example.identityservice.service.jwt.AuthEntryPointJwt;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    void commence_ShouldSetUnauthorizedResponseAndWriteJson() throws IOException, Exception {
        // Arrange
        String requestPath = "/api/test-path";
        String errorMessage = "Full authentication is required to access this resource";

        when(request.getServletPath()).thenReturn(requestPath);
        when(authException.getMessage()).thenReturn(errorMessage);

        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        
        ServletOutputStream mockOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                capturedOutput.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };

        when(response.getOutputStream()).thenReturn(mockOutputStream);

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseBody = capturedOutput.toString();

        assertTrue(responseBody.contains("\"status\":401"));
        assertTrue(responseBody.contains("\"error\":\"Unauthorized\""));
        assertTrue(responseBody.contains("\"message\":\"" + errorMessage + "\""));
        assertTrue(responseBody.contains("\"path\":\"" + requestPath + "\""));
    }
}