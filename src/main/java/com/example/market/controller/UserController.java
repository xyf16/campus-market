package com.example.market.controller;

import com.example.market.dto.LoginDTO;
import com.example.market.dto.RegisterDTO;
import com.example.market.dto.Result;
import com.example.market.entity.User;
import com.example.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.DigestUtils;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        try {
            // 检查用户名是否已存在
            User existingUser = userService.findByUsername(registerDTO.getUsername());
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }

            // 创建新用户
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
            user.setNickname(registerDTO.getNickname());
            user.setPhone(registerDTO.getPhone());
            user.setSchool(registerDTO.getSchool());
            user.setStatus(1);
            user.setRole(0);

            boolean success = userService.save(user);
            if (success) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.findByUsername(loginDTO.getUsername());
            if (user == null) {
                return Result.error("用户不存在");
            }

            String encryptPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
            if (!user.getPassword().equals(encryptPassword)) {
                return Result.error("密码错误");
            }

            if (user.getStatus() == 0) {
                return Result.error("账号已被禁用");
            }

            // 生成简单token（实际项目中应该用JWT）
            String token = user.getId() + "_" + System.currentTimeMillis();
            return Result.success(token);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
    }
}