package com.example.market.mapper;
// mapper/UserMapper.java


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.market.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}