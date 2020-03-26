package com.dao;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.vo.MemberVO;

public class SignUpValidDAO {
	private static SqlSessionFactory sqlSessionFactory = null;	
	static {
		//mybatis ��������;
		String resource = "com/mapper/memberConfig.xml";
		try {
			InputStream inputStream = Resources.getResourceAsStream(resource);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	//�̸��� �ߺ��˻�
	public int idCheck(String email) {
		int r = 0;
		try (SqlSession res = SignUpDAO.getSqlSessionFactory().openSession();) {
			r = res.selectOne("mybatis.validMapper.emailCheck", email);
		} catch (Exception e) {
			System.out.println(e);
		}
		return r;
	}
	//�����̸� �ߺ��˻� 
	public int nameCheck(String name) {
		int r = 0;
		try (SqlSession res = SignUpDAO.getSqlSessionFactory().openSession();) {
			r = res.selectOne("mybatis.validMapper.nameCheck", name);
		} catch (Exception e) {
			System.out.println(e);
		}
		return r;
	}

}
