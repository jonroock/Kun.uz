package dasturlash.uz.util;

import java.util.Random;

public class PhoneUtil {

    public static String toLocalPhone(String phone) {
        if (phone == null) {
            return null;
        }
        phone = phone.trim();
        if (phone.startsWith("+")) {
            return phone;
        } else {
            return "+" + phone;
        }
    }
}
