package com.wdd.studentmanager.mapper;

import com.wdd.studentmanager.domain.Teacher;

import java.util.List;
import java.util.Map;

/**
 * @Classname TeacherMapper
 * @Description None
 * @Date 2019/6/28 19:06
 * @Created by WDD
 */
public interface TeacherMapper {
    // 查询所有老师信息
    List<Teacher> queryList(Map<String, Object> paramMap);

    // 查询所有老师数量
    Integer queryCount(Map<String, Object> paramMap);

    // 删除老师信息
    int deleteTeacher(List<Integer> ids);

    // 添加老师信息
    int addTeacher(Teacher teacher);

    // 查找老师信息
    Teacher findById(Integer tid);

    // 修改老师信息
    int editTeacher(Teacher teacher);

    // 查询老师信息
    Teacher findByTeacher(Teacher teacher);

    // 教师修改密码
    int editPswdByTeacher(Teacher teacher);
}
