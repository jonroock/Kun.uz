package dasturlash.uz.enums;

import java.util.LinkedList;
import java.util.List;

public enum ProfileRoleEnum {
    ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR, ROLE_PUBLISH;

    public static List<ProfileRoleEnum> values(String[] roles) {
        if (roles == null || roles.length == 0) {
            return new LinkedList<>();
        }
        List<ProfileRoleEnum> roleList = new LinkedList<>();
        for (String role : roles) {
            roleList.add(ProfileRoleEnum.valueOf(role));
        }
        return roleList;
    }

    public static List<ProfileRoleEnum> valuesFromStr(String valuesStr) {
        if (valuesStr == null) {
            return new LinkedList<>();
        }
        return values(valuesStr.split(","));
    }
}
