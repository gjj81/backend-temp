package mes.common.enums;

import lombok.Getter;

import static org.springframework.data.redis.core.script.RedisScript.of;

@Getter
public enum DeviceTypeEnum {
    PC("PC"),// PC 端
    TABLET("TABLET"),// 平板端
    MOBILE("MOBILE");//移动端

    private final String code;


    DeviceTypeEnum(String code) {
        this.code = code;
    }

    /**
     * 根据前端传的 code 找枚举
     * @param code 前端传的"PC"/"PAD"/"MOBILE"
     * @return 找到就返回枚举对象，没找到就返回 null（= 非法值）
     */
    public static DeviceTypeEnum of(String code) {
        if (code == null) return null;
        for (DeviceTypeEnum item : values()) {
            if (item.getCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 校验 code 是否合法
     * @return true=合法，false=非法
     */
    public static boolean isValid(String code) {
        return of(code) != null;
    }

}
