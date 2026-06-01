package com.example.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.market.entity.User;
import com.example.market.dto.LoginDTO;
import com.example.market.dto.RegisterDTO;

public interface UserService extends IService<User> {
    String login(LoginDTO loginDTO);
    boolean register(RegisterDTO registerDTO);
    User findByUsername(String username);
}