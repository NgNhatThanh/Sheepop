package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.cart.Cart;
import com.app.bdc_backend.model.dto.response.AuthResponseDTO;
import com.app.bdc_backend.model.dto.request.LoginDTO;
import com.app.bdc_backend.model.dto.request.RegistrationDTO;
import com.app.bdc_backend.model.dto.response.OauthUserDTO;
import com.app.bdc_backend.model.enums.RoleName;
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
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class AuthFacadeService {

    private final UserService userService;

    private final JwtService jwtService;

    private final JwtRedisService jwtRedisService;

    private final Oauth2Service oauth2Service;

    private final ShopService shopSevice;

    private final CartService cartSevice;

    private final String userAvatarUrl = "https://res.cloudinary.com/daxt0vwoc/image/upload/v1740297885/User-avatar.svg_nihuye.png";

    private final String shopAvatarUrl = "https://res.cloudinary.com/daxt0vwoc/image/upload/v1740305995/online-shop-icon-vector_pj0wre.jpg";

    public AuthResponseDTO registerUser(RegistrationDTO dto)  {
        User newUser = ModelMapper.getInstance().map(dto, User.class);
        if((newUser.isFromSocial() && newUser.getEmail() == null)
        || (!newUser.isFromSocial() && newUser.getPhoneNumber() == null)) {
            throw new RequestException("Invalid user information");
        }
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
        shopSevice.save(shop);
        Cart cart = new Cart();
        cart.setUser(newUser);
        cartSevice.save(cart);
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
        OauthUserDTO userInfo = oauth2Service.getOauth2Profile(code, provider);
        User user = userService.findByUsername(userInfo.getUsername());
        if(user == null){
            return registerUser(RegistrationDTO.builder()
                    .username(userInfo.getUsername())
                    .email(userInfo.getEmail())
                    .fullName(userInfo.getFullName())
                    .avatarUrl(userInfo.getAvatarUrl())
                    .password(RandomString.make(10))
                    .fromSocial(true)
                    .build());
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

}
