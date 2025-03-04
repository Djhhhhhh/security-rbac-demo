package com.djhhh.securityrbacdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.djhhh.securityrbacdemo.model.entity.Permission;
import com.djhhh.securityrbacdemo.model.entity.Role;
import com.djhhh.securityrbacdemo.model.entity.User;
import com.djhhh.securityrbacdemo.mapper.RoleMapper;
import com.djhhh.securityrbacdemo.mapper.UserMapper;
import com.djhhh.securityrbacdemo.model.vo.UserPermissionsVO;
import com.djhhh.securityrbacdemo.model.vo.UserVO;
import com.djhhh.securityrbacdemo.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Resource
    private final UserMapper userMapper;

    @Resource
    private final RoleMapper roleMapper;

    /**
     * UserDetailsService 类的 loadUserByUsername 方法是 Spring Security 用户认证的核心实现。
     * 负责根据用户名加载用户信息及其关联的角色和权限。
     * @param username 用户名
     * @return 用户
     */
    @Override
    public User loadUserByUsername(String username) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
        );
        if (user == null) throw new UsernameNotFoundException("用户不存在");
        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        user.setRoles(roles != null ? roles : Collections.emptyList());
        user.getRoles().forEach(role -> {
            List<Permission> permissions = roleMapper.selectPermissionsByRoleId(role.getId());
            role.setPermissions(permissions != null ? permissions : Collections.emptyList());
        });
        return user;
    }

    @Override
    public List<UserPermissionsVO> getPermissionsList() {
        List<User> users = this.userMapper.selectList(null);
        return users.stream().map(user->{
            UserPermissionsVO userVO = new UserPermissionsVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setEnabled(user.getEnabled());
            List<Role> roles = userMapper.selectRolesByUserId(user.getId());
            List<String> permissions = roles.stream()
                    .flatMap(role ->
                            roleMapper.selectPermissionsByRoleId(role.getId())
                                    .stream()
                                    .map(Permission::getCode)
                    )
                    .distinct()
                    .collect(Collectors.toList());
            userVO.setPermissions(permissions);
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserVO> getList() {
        List<User> users = userMapper.selectList(null);
        return users.stream().map(user -> {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setEnabled(user.getEnabled());
            // 查询用户角色并构建 RoleVO 列表
            List<Role> roles = userMapper.selectRolesByUserId(user.getId());
            List<Role> roleVOs = roles.stream().peek(role -> {
                // 查询角色权限并提取权限码
                List<Permission> permissions = roleMapper.selectPermissionsByRoleId(role.getId());
                role.setPermissions(permissions);
            }).collect(Collectors.toList());
            userVO.setRoles(roleVOs);
            return userVO;
        }).collect(Collectors.toList());
    }


}