package cz.bloodbear.discordLink.velocity.utils;

import java.security.SecureRandom;
import java.util.Base64;

public abstract class CodeGenerator {
    public static String generateCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
