package com.spring.springRestDemo.security;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.RSAKey;

public class Jwks {
    private Jwks(){}
    public static RSAKey generateRsa(){
        KeyPair keyPair=KeyGeneratorUtils.generateRsaKey();// Generates RSA key pair
        RSAPublicKey publicKey=(RSAPublicKey) keyPair.getPublic();// Extract public key
        RSAPrivateKey privateKey=(RSAPrivateKey) keyPair.getPrivate(); // Extract private key
        return new RSAKey.Builder(publicKey)// Build RSAKey with public key
        .privateKey(privateKey) // Attach private key
        .keyID(UUID.randomUUID().toString()) // Assign a random key ID
        .build();// Return the built RSAKey
    }
    
}
