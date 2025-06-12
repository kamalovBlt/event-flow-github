package com.technokratos.repository.api;

public interface RefreshTokenRepository {

    void save(String email, String refreshToken);
    String findByEmail(String email);
    void delete(String email);

}
