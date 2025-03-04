package com.djhhh.securityrbacdemo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@TableName("sys_user")
public class User implements UserDetails {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private Boolean enabled;
    
    @TableField(exist = false)
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(Role::getPermissions) // 获取每个角色的权限列表
                .filter(Objects::nonNull) // 过滤掉 permissions 为 null 的角色
                .flatMap(Collection::stream) // 展开所有权限对象
                .filter(Objects::nonNull) // 过滤掉 null 的权限对象
                .map(Permission::getCode) // 提取权限码
                .filter(code -> code != null && !code.isBlank()) // 过滤空权限码
                .map(SimpleGrantedAuthority::new) // 转换为 Spring Security 权限对象
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 用户是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户是否锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户凭证是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否启用
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}