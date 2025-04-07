package com.app.bdc_backend.facade;

import com.app.bdc_backend.config.Constant;
import com.app.bdc_backend.dao.ForgotPasswordDAO;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.ForgotPasswordToken;
import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.dto.request.ForgotPasswordDTO;
import com.app.bdc_backend.model.dto.request.ResetPasswordDTO;
import com.app.bdc_backend.model.dto.response.AuthResponseDTO;
import com.app.bdc_backend.model.dto.request.LoginDTO;
import com.app.bdc_backend.model.dto.request.RegistrationDTO;
import com.app.bdc_backend.model.enums.OauthProvider;
import com.app.bdc_backend.model.enums.RoleName;
import com.app.bdc_backend.model.oauth2.Oauth2UserInfo;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.service.auth.JwtService;
import com.app.bdc_backend.service.auth.Oauth2Service;
import com.app.bdc_backend.service.redis.JwtRedisService;
import com.app.bdc_backend.service.user.ShopService;
import com.app.bdc_backend.service.user.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFacadeService {

    private final UserService userService;

    private final JwtService jwtService;

    private final JwtRedisService jwtRedisService;

    private final Oauth2Service oauth2Service;

    private final ShopService shopService;

    private final CartService cartService;

    private final MailService mailService;

    private final ForgotPasswordDAO forgotPasswordDAO;

    private final Constant constant;

    private final PasswordEncoder passwordEncoder;

    private final String userAvatarUrl = "https://res.cloudinary.com/daxt0vwoc/image/upload/v1740297885/User-avatar.svg_nihuye.png";

    private final String shopAvatarUrl = "https://res.cloudinary.com/daxt0vwoc/image/upload/v1740305995/online-shop-icon-vector_pj0wre.jpg";

    public AuthResponseDTO registerUser(RegistrationDTO dto, boolean oauth)  {
        if(!oauth){
            User existedUser = userService.findByUsername(dto.getUsername());
            if(existedUser != null){
                if(existedUser.getUsername().equals(dto.getUsername())){
                    throw new RequestException("Username đã tồn tại");
                }
                else throw new RequestException("Email đã tồn tại");
            }
        }
        else{
            List<User> prefUsers = userService.getAllUsersHasUsernameStartWith(dto.getUsername());
            boolean existedUsername = true;
            int count = -1;
            while(existedUsername){
                count++;
                existedUsername = false;
                String tmp = dto.getUsername()  + (count > 0 ? "-" + count : "");
                for(User user : prefUsers){
                    if (user.getUsername().equals(tmp)) {
                        existedUsername = true;
                        break;
                    }
                }
            }
            dto.setUsername(dto.getUsername() + (count > 0 ? "-" + count : ""));
        }
        User newUser = ModelMapper.getInstance().map(dto, User.class);
        newUser.setAvatarUrl(userAvatarUrl);
        userService.register(newUser);
        String accessToken = jwtService.generateAccessToken(newUser, new HashMap<>());
        String refreshToken = jwtService.generateRefreshToken(newUser.getUsername(), new HashMap<>());
        jwtRedisService.setNewRefreshToken(newUser.getUsername(), refreshToken);
        Shop shop = new Shop();
        shop.setName(dto.getUsername());
        shop.setUser(newUser);
        shop.setDescription(dto.getFullName() + "'s shop");
        shop.setCreatedAt(new Date());
        shop.setAvatarUrl(shopAvatarUrl);
        shopService.save(shop);
        Cart cart = new Cart();
        cart.setUser(newUser);
        cartService.save(cart);
        return new AuthResponseDTO(accessToken, refreshToken, false);
    }

    public AuthResponseDTO login(LoginDTO dto)  {
        User user = userService.findByUsername(dto.getUsername());
        if(user.isDeleted())
            throw new RequestException("User was banned due to violations of our Terms of Service");
        String accessToken = jwtService.generateAccessToken(user, new HashMap<>());
        String refreshToken = jwtService.generateRefreshToken(dto.getUsername(), new HashMap<>());
        jwtRedisService.setNewRefreshToken(dto.getUsername(), refreshToken);
        return new AuthResponseDTO(accessToken,
                refreshToken,
                !user.getRole().getName().toString().equals(RoleName.USER.toString()));
    }

    public AuthResponseDTO oauthLogin(String code, String provider)  {
        OauthProvider prov = OauthProvider.fromString(provider);
        Oauth2UserInfo userInfo = oauth2Service.getUserInfo(prov, code);
        User user = userService.findByUsername(userInfo.getUsername());
        if(user == null){
            return registerUser(RegistrationDTO.builder()
                    .username(userInfo.getUsername())
                    .email(userInfo.getEmail())
                    .fullName(userInfo.getFullName())
                    .avatarUrl(userInfo.getAvatarUrl())
                    .password("")
                    .fromSocial(true)
                    .build(), true);
        }
        else{
            String accessToken = jwtService.generateAccessToken(user, new HashMap<>());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername(), new HashMap<>());
            jwtRedisService.setNewRefreshToken(user.getUsername(), refreshToken);
            return new AuthResponseDTO(accessToken, refreshToken, false);
        }
    }

    public void logout(String accessToken)  {
        String username = jwtService.extractUsername(accessToken);
        jwtRedisService.deleteRefreshToken(username);
    }

    public AuthResponseDTO refresh(String refreshToken)  {
        if (refreshToken == null || refreshToken.isEmpty()
                || !jwtService.isTokenValid(refreshToken)
                || !jwtRedisService.isRefreshTokenValid(jwtService.extractUsername(refreshToken), refreshToken)
        )
            throw new RequestException("Refresh token is invalid");
        String username = jwtService.extractUsername(refreshToken);
        User user = userService.findByUsername(username);
        String accessToken = jwtService.generateAccessToken(
                user,
                new HashMap<>());
        String newRefreshToken = jwtService.generateRefreshToken(
                username,
                new HashMap<>()
        );
        jwtRedisService.setNewRefreshToken(username, newRefreshToken);
        return new AuthResponseDTO(accessToken,
                newRefreshToken,
                !user.getRole().getName().toString().equals(RoleName.USER.toString()));
    }

    public void passwordRecovery(ForgotPasswordDTO dto) {
        User user = userService.getByEmail(dto.getEmail());
        if(user == null){
            throw new RequestException("Email không tồn tại");
        }
        String email = user.getEmail();
        LocalDateTime tenMinutesLater = LocalDateTime.now().plusMinutes(10);
        Date expire = Date.from(tenMinutesLater.atZone(ZoneId.systemDefault()).toInstant());
        ForgotPasswordToken token = ForgotPasswordToken.builder()
                .user(user)
                .expiredAt(expire)
                .build();
        forgotPasswordDAO.save(token);
        String recoveryUrl = constant.getFeBaseUrl() + "/reset-password/" + token.getToken();
        mailService.sendEmail(email, "[Sheepop] Yêu cầu đặt lại mật khẩu",
                String.format("""
                        Hãy truy cập vào link sau để đặt lại mật khẩu: \
                        
                        %s \
                        
                        Link sẽ hết hạn trong 10 phút""", recoveryUrl));
    }

    public void resetPassword(ResetPasswordDTO dto) {
        ForgotPasswordToken token = forgotPasswordDAO.findByToken(dto.getToken());
        if(token == null)
            throw new RequestException("Yêu cầu không hợp lệ");
        if(token.getExpiredAt().before(new Date()))
            throw new RequestException("Yêu cầu đã hết hạn, vui lòng tạo yêu cầu mới");
        User user = token.getUser();
        String hashPwd = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(hashPwd);
        userService.save(user);
        forgotPasswordDAO.delete(token);
    }
}
