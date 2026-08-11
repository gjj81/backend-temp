package mes.service;

public interface RedisTokenService {
    public void saveRefreshToken(String userId, String refreshToken, long expiration);
    public boolean validateRefreshToken(String userId, String refreshToken);
    public void deleteRefreshToken(String userId);
    public void blacklistAccessToken(String accessToken, long expiration);
    public boolean validateAccessToken(String accessToken);
    public boolean hasRefreshToken(String userId);
    public boolean isBlacklisted(String accessToken);
}
