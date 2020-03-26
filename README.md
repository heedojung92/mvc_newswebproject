뉴스 분석 웹사이트
주요 기능 설명

 (Model) 
- DB 담당 팀원이 설계한 Database에서 필요한 데이터를 검색하여 가져올 수 있도록 적절한 SQL 쿼리 및 Procedure 작성 & mybatis.mapper / Spring JDBC를 통한 Oracle RDBMS와의 컨넥션 생성 (sqlSessionFactory/jdbcTemplate 객체 사용) 



(View) 
- 디자인 부분을 제외한 모든 페이지 구현 : JavaScript DOM 에 대한 이해를 바탕으로 javascript/ jQuery 함수 구현 및 Ajax를 통한 비동기 처리 함수 구현 
- CanvasJS/ jQCloud 라이브러리를 통하여, 불러온 통계 데이터들을 차트 및 워드 클라우드로 시각화하여 보여준다.
- Handler 메소드를 정의하여, 데이터가 선택 되었을 시, AJAX를 통해 동적으로 화면을 변경시켜줄 수 있는 javascript함수 구현



(Controller) 
- 유저의 요구에 따라 올바른 View로 연결 시켜주는 모든 매핑 메소드/ 관련 기능 구현 
- 회원가입, 회원정보수정, 뉴스 스크랩, 뉴스/주가 데이터 선택적 조회, 통계 데이터 조회, 
동시출현 네트워크 구현 등의 기능 구현



(Util) 
데이터 수집)
- Jsoup 라이브러리를 통한 뉴스데이터/주가데이터 크롤링 코드 작성.
- 크롤링된 데이터는 Spring JDBC를 사용하여 Oracle RDBMS에 저장.
- 현재시간과 DB의 최종 데이터 업데이트 시간을 비교하여, 일정 기간(시간) 이상 차이가 날 때, Controller Class의 메소드에서 자동으로 크롤링 하는 코드 작성 


형태소 분석)
- Komoran 패키지를 이용하여, 뉴스 본문의 명사를 추출한 후, 단어 출현 빈도 통계데이터를 집계내어, 워드 클라우드를 통하여 보여줌


유사도 계산)
뉴스의 유사도를 코사인/ 자카드 유사도, 유클리드/ 맨하탄 거리 기반으로 계산하여, 유사 뉴스를 추천해주는 메소드 작성


데이터 집계 및 통계 계산)
- JavaSparkContext를 통해 Java의 Collection 객체를 병렬화 시켜, 분산 데이터셋(RDD)를 생성하고, 정의된 Map/Reduce 함수를 통하여 키워드 집계 및 동시출현빈도/연관관계 계산을 한다.
