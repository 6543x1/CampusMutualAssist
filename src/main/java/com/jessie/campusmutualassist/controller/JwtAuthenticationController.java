package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.config.UserDetailServiceImpl;
import com.jessie.campusmutualassist.entity.JwtRequest;
import com.jessie.campusmutualassist.entity.JwtResponse;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.User;
import com.jessie.campusmutualassist.exception.BannedUserException;
import com.jessie.campusmutualassist.service.UserService;
import com.jessie.campusmutualassist.utils.JwtTokenUtil;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@Api(tags = "登录登出")
@RestController
//@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    UserService userService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailServiceImpl userDetailService;
    @Value("${jwt.header}")
    private String tokenHeader;
    private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);

    @ApiOperation(value = "登录", notes = "登录是/user/login")
    @PostMapping(value = "${jwt.route.authentication.path}", produces = "application/json;charset=UTF-8")
    public Result createAuthenticationToken(JwtRequest authenticationRequest) throws Exception {
        System.out.println("username:" + authenticationRequest.getUsername() + ",password:" + authenticationRequest.getPassword());
        try {
            if (userService.getUser(authenticationRequest.getUsername()).getStatus() <= 0) {
                throw new BannedUserException();
            }
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (BannedUserException e) {
            return Result.error("账号被封禁了，有疑问联系管理员", 403);
        } catch (Exception e) {
            e.printStackTrace();
            if ("INVALID_CREDENTIALS".equals(e.getMessage())) {
                if (!redisUtil.hasKey("username:" + authenticationRequest.getUsername() + ":type:WrongPassCount")) {
                    redisUtil.set("username:" + authenticationRequest.getUsername() + ":type:WrongPassCount", "1", 60 * 60);
                } else {
                    redisUtil.incrBy("username:" + authenticationRequest.getUsername() + ":type:WrongPassCount", 1);
                    if ("5".equals(redisUtil.get("username:" + authenticationRequest.getUsername() + ":type:WrongPassCount"))) {
                        return Result.error("一小时内连续输错五次，请一小时后重新再试密码");
                    }
                }
            }
            return Result.error("账号或密码错误", 401);
        }
        final UserDetails userDetails = userDetailService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails, userService.getUser(userDetails.getUsername()).getUid());//这一步生成token并返回
        redisUtil.set("Jwt_TOKEN" + ":" + authenticationRequest.getUsername(), token, 72 * 60 * 60);
        return Result.success("loginSuccess", new JwtResponse(token, userService.getNoPasswordUser(authenticationRequest.getUsername())));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @GetMapping(value = "/token", produces = "application/json;charset=UTF-8")
    public User getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        System.out.println("username=" + username);
        System.out.println(jwtTokenUtil.getUidFromToken(token));
        return userService.getUser(username);
    }

    @ApiOperation(value = "登出")
    @PostMapping(value = "/user/logout", produces = "application/json;charset=UTF-8")
    public Result logout() {
        redisUtil.delete("Jwt_TOKEN" + ":" + getCurrentUsername());
        return Result.success("登出成功");
    }
}
