package com.hetpatel.nexusops.identity.user.security;

public interface PasswordHasher {

    String hash(String rawPassword);
}
