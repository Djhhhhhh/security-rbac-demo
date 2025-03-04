package com.djhhh.securityrbacdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.djhhh.securityrbacdemo.model.entity.Permission;
import com.djhhh.securityrbacdemo.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT p.* FROM sys_permission p " +
            "JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<Permission> selectPermissionsByRoleId(Long roleId);
}