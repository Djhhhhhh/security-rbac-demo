package com.djhhh.securityrbacdemo.controller;

import com.djhhh.securityrbacdemo.result.Result;
import com.djhhh.securityrbacdemo.model.dto.UserDTO;
import com.djhhh.securityrbacdemo.security.permissionconstants.UserPermissionConstant;
import com.djhhh.securityrbacdemo.model.vo.UserPermissionsVO;
import com.djhhh.securityrbacdemo.model.vo.UserVO;
import com.djhhh.securityrbacdemo.service.UserService;
import com.djhhh.securityrbacdemo.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Resource
    private UserService userService;

    @GetMapping("/getPermissionsList")
    @Operation(summary = "获取权限用户列表")
    @UserPermissionConstant.UserRead
    public Result<List<UserPermissionsVO>> getPermissionsList() {
        List<UserPermissionsVO> userPermissionsVOList = this.userService.getPermissionsList();
        return Result.ok(userPermissionsVOList);
    }

    @GetMapping("/getList")
    @Operation(summary = "获取用户列表")
    @UserPermissionConstant.UserRead
    public Result<List<UserVO>> getList() {
        List<UserVO> userVOList = this.userService.getList();
        return Result.ok(userVOList);
    }

    @PostMapping(value = "/login")
    @Operation(summary = "用户登录")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUsername(),
                        userDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(
                userDetails.getUsername(),
                userDetails.getAuthorities()
        );

        return Result.ok(token);
    }
}