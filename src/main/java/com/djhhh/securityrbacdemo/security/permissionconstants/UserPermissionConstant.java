package com.djhhh.securityrbacdemo.security.permissionconstants;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 用户模块
public final class UserPermissionConstant {
    /**
     * 用户读权限
     */
    @Target(ElementType.METHOD) // 仅用于方法
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAuthority('user:read')")
    public @interface UserRead {
        String description() default "用户读权限";
    }

    /**
     * 用户写权限
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAuthority('user:write')")
    public @interface UserWrite {
        String description() default "用户写权限";
    }
}