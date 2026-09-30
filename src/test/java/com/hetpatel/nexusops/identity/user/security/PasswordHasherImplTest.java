package com.hetpatel.nexusops.identity.user.security;

import com.hetpatel.nexusops.identity.user.security.impl.PasswordHasherImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherImplTest {

    private PasswordHasherImpl passwordHasher;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        passwordHasher = new PasswordHasherImpl();

        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void hash_shouldReturnNonNullHash() {

        String rawPassword = "Password@123";

        String hashedPassword =
                passwordHasher.hash(rawPassword);

        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isBlank());
    }

    @Test
    void hash_shouldNotReturnRawPassword() {

        String rawPassword = "Password@123";

        String hashedPassword =
                passwordHasher.hash(rawPassword);

        assertNotEquals(
                rawPassword,
                hashedPassword
        );
    }

    @Test
    void hash_shouldCreateValidBCryptHash() {

        String rawPassword = "Password@123";

        String hashedPassword =
                passwordHasher.hash(rawPassword);

        assertTrue(
                passwordEncoder.matches(
                        rawPassword,
                        hashedPassword
                )
        );
    }

    @Test
    void hash_shouldGenerateDifferentHashesForSamePassword() {

        String rawPassword = "Password@123";

        String firstHash =
                passwordHasher.hash(rawPassword);

        String secondHash =
                passwordHasher.hash(rawPassword);

        assertNotEquals(
                firstHash,
                secondHash
        );
    }

    @Test
    void hash_shouldAllowOnlyOriginalPasswordToMatch() {

        String originalPassword = "Password@123";
        String wrongPassword = "WrongPassword@123";

        String hashedPassword =
                passwordHasher.hash(originalPassword);

        assertTrue(
                passwordEncoder.matches(
                        originalPassword,
                        hashedPassword
                )
        );

        assertFalse(
                passwordEncoder.matches(
                        wrongPassword,
                        hashedPassword
                )
        );
    }
}
