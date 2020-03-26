package com.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.vo.MemberVO;

public class SignUpDAO {
	private static SqlSessionFactory sqlSessionFactory = null;
	static {
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
	public int register(MemberVO entity) {
		int r = 0;
		try (SqlSession res = SignUpDAO.getSqlSessionFactory().openSession();) {
			r = res.insert("mybatis.membermapper.insertMember", entity);
			if (r > 0) {
				res.commit();
			} else {
				res.rollback();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return r;
	}

}