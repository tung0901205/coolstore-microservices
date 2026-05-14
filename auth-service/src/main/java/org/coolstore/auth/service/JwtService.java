package org.coolstore.auth.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.coolstore.auth.entity.AppUser;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

/**
 * Dịch vụ tạo JWT Token theo chuẩn SmallRye JWT.
 * Token chứa thông tin: username, userId, role.
 * Các service khác (catalog, order) sẽ xác thực token này.
 */
@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "coolstore.jwt.duration", defaultValue = "86400")
    long thoiHanGiay;

    @ConfigProperty(name = "coolstore.jwt.issuer", defaultValue = "https://coolstore.vn")
    String issuer;

    /**
     * Tạo JWT token cho người dùng sau khi đăng nhập thành công.
     *
     * Cấu trúc claims:
     * - upn (User Principal Name): username
     * - sub (Subject): userId dạng string
     * - groups: role của user (USER hoặc ADMIN)
     * - userId: Long ID để các service khác dùng
     * - email: email người dùng
     */
    public String taoToken(AppUser user) {
        return Jwt.issuer(issuer)
                .upn(user.username)
                .subject(user.id.toString())
                .groups(Set.of(user.role.name()))
                .claim("userId", user.id)
                .claim("email", user.email)
                .claim("fullName", user.fullName)
                .claim("role", user.role.name())
                .expiresIn(Duration.ofSeconds(thoiHanGiay))
                .sign();
    }
}