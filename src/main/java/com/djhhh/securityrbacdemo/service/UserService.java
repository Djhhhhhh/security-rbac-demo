package com.djhhh.securityrbacdemo.service;

import com.djhhh.securityrbacdemo.model.vo.UserPermissionsVO;
import com.djhhh.securityrbacdemo.model.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: _Djhhh
 * @Date: 2025/3/4 13:16
 */
public interface UserService extends UserDetailsService {

    List<UserPermissionsVO> getPermissionsList();

    List<UserVO> getList();
}
