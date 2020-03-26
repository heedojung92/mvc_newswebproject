package com.dao;

import com.vo.MemberVO;

public interface MemberDAO {
	//�α��� üũ 
	int loginCheck(String email,String pw);	
	// ȸ�� ���� ����
	int memUpdate(String email, String pw, String name);
	// �̸��Ϸ� ��� ã��
	MemberVO getMemberByEmail(String email);
	// ������ȸ���� ���̺� ������Ʈ
	int updateview(int userno,int newsno);
	// ���� �ο��� ���ϱ� 
	int countMember();
	
	int viewCheck(int userno, int newsno);
	
}
