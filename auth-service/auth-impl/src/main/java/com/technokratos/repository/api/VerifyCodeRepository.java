package com.technokratos.repository.api;

public interface VerifyCodeRepository {

    void save(String email, String code);
    String findByEmail(String email);

}
