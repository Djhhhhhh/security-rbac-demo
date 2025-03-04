package com.djhhh.securityrbacdemo.model.vo;

import com.djhhh.securityrbacdemo.model.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * @Author: _Djhhh
 * @Date: 2025/3/4 14:54
 * @Description：TODO
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private Boolean enabled;
    private List<Role> roles;
}
