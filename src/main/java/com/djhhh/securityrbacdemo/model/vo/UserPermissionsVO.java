package com.djhhh.securityrbacdemo.model.vo;

import com.djhhh.securityrbacdemo.model.entity.Permission;
import com.djhhh.securityrbacdemo.model.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * @Author: _Djhhh
 * @Date: 2025/3/4 13:30
 */
@Data
public class UserPermissionsVO {
    private Long id;
    private String username;
    private Boolean enabled;
    private List<String> permissions;
}
