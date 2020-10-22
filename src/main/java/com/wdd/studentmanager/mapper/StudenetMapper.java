package com.wdd.studentmanager.mapper;

import com.wdd.studentmanager.domain.Student;

import java.util.List;
import java.util.Map;

public interface StudenetMapper {
    // 查询所有学员信息
    List<Student> queryList(Map<String, Object> paramMap);

    // 查询学员总数据
    Integer queryCount(Map<String, Object> paramMap);

    // 批量删除学员
    int deleteStudent(List<Integer> ids);

    // 添加学员
    int addStudent(Student student);

    // 通过学员id查找学员
    Student findById(String sid);

    // 重新编辑学员
    int editStudent(Student student);

    // 查找学生信息
    Student findByStudentNo(Integer no);

    // 通过名字查找学员
    int findByName(String name);
}
