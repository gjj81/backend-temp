package mes.service;

public interface RedisTokenService {
    void saveRefreshToken(String userId, String deviceType, String refreshToken);// 保存刷新令牌
    String getRefreshToken(String userId, String deviceType);
    void deleteRefreshToken(String userId,String deviceType);// 删除指定设备类型的刷新令牌
    void deleteAllRefreshToken(String userId);// 删除所有刷新令牌


    Long getTokenVersion(String userId, String deviceType);
    Long updateTokenVersion(String userId, String deviceType);
    boolean validateTokenVersion(String userId, String deviceType, Long tokenVersion);
    void kickSingleDevice(String userId, String deviceType);

    void kickAllDevices(String userId);
}
