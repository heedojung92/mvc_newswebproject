package com.dao;

import com.vo.MemberVO;

public interface MemberDAO {
	//로그인 체크 
	int loginCheck(String email,String pw);	
	// 회원 정보 수정
	int memUpdate(String email, String pw, String name);
	// 이메일로 멤버 찾기
	MemberVO getMemberByEmail(String email);
	// 유저조회뉴스 테이블 업데이트
	int updateview(int userno,int newsno);
	// 유저 인원수 구하기 
	int countMember();
	
	int viewCheck(int userno, int newsno);
	
}
