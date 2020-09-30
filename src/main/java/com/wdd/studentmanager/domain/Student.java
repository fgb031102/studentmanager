package com.wdd.studentmanager.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Student {
	private int id;
	private String studentId;
	private String username;
	private String bindPhone;
	private String payPhone;
	private String wx;
	private String qq;
	private String fromSrc;
	private String payDate;
	private String name;
	private String pay;
	private String address;
	private String mark;
	private String photo;
	private String teacherId;
	private String teacherName;
}
