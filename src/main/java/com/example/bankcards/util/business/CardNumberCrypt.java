package com.example.bankcards.util.business;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class CardNumberCrypt {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKeySpec;

    public CardNumberCrypt(@Value("${app.security.card-number-secret-key}") String secretKey) {
        this.secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), ALGORITHM);
    }

    public String generateMaskedNumber(String last4Digits) {
        // Вообще бек может вернуть только 4 цифры, но т.к. в требовании указана маска, то делаем такое
        return "**** **** **** " + last4Digits;
    }

    public String encryptNumber(String number) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(number.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // В текущей реализации полный номер не используется
    public String decryptNumber(String encryptedNumber) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(
                Base64.getDecoder().decode(encryptedNumber));

            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
