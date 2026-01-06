package org.example.identityservice.service;


import org.example.identityservice.model.entity.Users;
import org.example.identityservice.model.enums.Role;
import org.example.identityservice.service.jwt.JwtUtils;
import org.example.identityservice.service.user.UserDetailsImpl;
import org.example.identityservice.service.user.UserDetailsServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String SECRET = "dGhpcy1pcy1hLXZlcnktc3Ryb25nLXNlY3JldC1rZXktZm9yLWp3dC10ZXN0aW5n"; 
    private final long EXPIRATION = 60000; 
    private final String COOKIE_NAME = "jwt";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION);
        ReflectionTestUtils.setField(jwtUtils, "jwtCookie", COOKIE_NAME);
    }

    @Test
    void getJwtFromCookies_returnsValue_whenCookieExists() {
        Cookie cookie = new Cookie(COOKIE_NAME, "token_value");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = jwtUtils.getJwtFromCookies(request);

        assertEquals("token_value", result);
    }

    @Test
    void getJwtFromCookies_returnsNull_whenCookieMissing() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        String result = jwtUtils.getJwtFromCookies(request);

        assertNull(result);
    }

    @Test
    void getJwtFromCookies_returnsNull_whenCookiesNull() {
        when(request.getCookies()).thenReturn(null);

        String result = jwtUtils.getJwtFromCookies(request);

        assertNull(result);
    }

    @Test
    void generateJwtCookie_returnsCookie() {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn("user1");
        
        Users user = new Users();
        user.setRole(Role.USER);
        when(userDetailsService.getUserDetails("user1")).thenReturn(user);

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);

        assertNotNull(cookie);
        assertEquals(COOKIE_NAME, cookie.getName());
        assertFalse(cookie.getValue().isEmpty());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(86400, cookie.getMaxAge().getSeconds());
    }


    @Test
    void validateJwtToken_returnsTrue_whenValid() {
        Users user = new Users();
        user.setRole(Role.USER);
        when(userDetailsService.getUserDetails("user1")).thenReturn(user);

        String token = jwtUtils.generateTokenFromUsername("user1");

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_returnsFalse_whenInvalid() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.here"));
    }

    @Test
    void validateJwtToken_returnsFalse_whenEmpty() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void getUserNameFromJwtToken_returnsUsername() {
        Users user = new Users();
        user.setRole(Role.USER);
        when(userDetailsService.getUserDetails("user1")).thenReturn(user);

        String token = jwtUtils.generateTokenFromUsername("user1");
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("user1", username);
    }
}