package org.example.identityservice.configuration;


import org.example.identityservice.service.jwt.AuthEntryPointJwt;
import org.example.identityservice.service.jwt.AuthTokenFilter;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.user.UserDetailsServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@ExtendWith(MockitoExtension.class)
class FilterTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthEntryPointJwt unauthorizedHandler;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationConfiguration authConfiguration;

    @InjectMocks
    private Filter filterConfig;

    @Test
    void authenticationJwtTokenFilter_ShouldReturnConfiguredFilter() {
        AuthTokenFilter filter = filterConfig.authenticationJwtTokenFilter();
        assertNotNull(filter);
    }

    @Test
    void authenticationProvider_ShouldReturnDaoProvider() {
        DaoAuthenticationProvider provider = filterConfig.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void authenticationManager_ShouldDelegateToConfiguration() throws Exception {
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager result = filterConfig.authenticationManager(authConfiguration);

        assertNotNull(result);
        assertEquals(mockAuthManager, result);
        verify(authConfiguration).getAuthenticationManager();
    }

    @Test
    void passwordEncoder_ShouldReturnBCrypt() {
        PasswordEncoder encoder = filterConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void filterChain_ShouldConfigureHttpAndBuildChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, Answers.RETURNS_SELF);
        DefaultSecurityFilterChain mockChain = mock(DefaultSecurityFilterChain.class);

        when(http.build()).thenReturn(mockChain);

        SecurityFilterChain result = filterConfig.filterChain(http);

        assertNotNull(result);
        assertEquals(mockChain, result);

        verify(http).csrf(any());
        verify(http).exceptionHandling(any());
        verify(http).sessionManagement(any());
        verify(http).authorizeHttpRequests(any());
        verify(http).authenticationProvider(any());
        verify(http).addFilterBefore(any(AuthTokenFilter.class), any());
        verify(http).build();
    }
}
