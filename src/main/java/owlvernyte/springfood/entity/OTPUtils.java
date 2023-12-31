package owlvernyte.springfood.entity;

import java.util.Random;

public class OTPUtils {
    private static final String OTP_CHARS = "0123456789";
    private static final int OTP_LENGTH = 6;

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        Random random = new Random();

        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(OTP_CHARS.length());
            otp.append(OTP_CHARS.charAt(index));
        }

        return otp.toString();
    }
}