package com.wdd.studentmanager.mapper;

import com.wdd.studentmanager.domain.Admin;
import org.springframework.stereotype.Repository;

/**
 * @Classname UserMapper
 * @Description None
 * @Date 2019/6/24 20:09
 * @Created by WDD
 */
@Repository
public interface AdminMapper {

    // 查找管理员
    Admin findByAdmin(Admin admin);

    // 修改管理员密码
    int editPswdByAdmin(Admin admin);
}
