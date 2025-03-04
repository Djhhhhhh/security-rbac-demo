package com.djhhh.securityrbacdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.djhhh.securityrbacdemo.model.entity.Role;
import com.djhhh.securityrbacdemo.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT r.* FROM sys_role r " +
            "JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(Long userId);
}
