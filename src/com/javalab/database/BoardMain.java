package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BoardMain {

	// [멤버 변수]
	/** database 관련 문자열 상수 선언 **/
	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	// 2. oracle 데이터베이스 접속 경로 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

	/** database 관련 객체 선언 **/
	// 1. 데이터베이스 접속 객체
	public static Connection con = null;
	// 2. query 실행 객체
	public static Statement stmt = null;
	public static PreparedStatement pstmt = null;
	// 3. selcet 결과 저장 객체
	public static ResultSet rs = null;

	/** 그 외 필요한 객체, 변수 선언 **/
	// 4. oracle 계정(id/pwd)
	public static String oracleId = "board";
	// 5. oracle Password
	public static String oraclePwd = "1234";
	
	public static void main(String[] args) {
		
		// 1. 디비 접속 메소드 호출
		connectDB();
		
		// 2. 게시물 목록 조회
		getBoardList();
		
		// 3. 새글 등록
		// 새글 등록이 완료 되었으면 주석처리 한후에 답글등록으로 이동
//		insertNewBoard();
//		
//		// 4. 답글등록
//		// 어떤 게시물에 답글을 달지 부모 게시글의 정보를 전달해야 함.
//		int replyGroup = 7; // 부모글의 그룹번호
//		int replyOrder = 0; // 부모글의 그룹내순서
//		int replyIndent = 0; // 부모글의 들여쓰기
//		
//		insertReply(replyGroup, replyOrder, replyIndent);
//		
		// 5. 게시물 목록 조회( 반드시 1번~5번 까지)
		int starNo = 1;
		int length = 5;
		getBoardListTopN(starNo, length);
//		
		// 6. 중간에 특정 부분 조회 (5번~9번까지)
		starNo = 5;
		length = 10;
		getBoardListPart(starNo, length);
		
//		// 7. 게시물 조회수 증가
//		int bno = 2; // 조회수를 증가시킬 게시물 번호
//		updateCount(bno);
		
//		// 8. 수정
//		// 5번 게시물의 "다섯번째 글"로 수정하세요.
//		int bno = 5;
//		String newTitle = "다섯번째 글";
//		updateTitle(bno, newTitle);
		
		// 9. user01님이 작성한 게시물을 모두  삭제하세요.
		int bno = 6;
		deleteBoard(bno);
		
		// 자원 반납
		closeResource();
	}

private static void deleteBoard(int bno) {
		String sql = " ";
		try {
			
			// PrepareStatement 객체에서 사용할 SQL문 생성
			sql = "delete from tbl_board";
			sql += " where bno = ?";
			
			// PrepareStatement 객체 얻음
			pstmt = con.prepareStatement(sql);
			
			// 쿼리문에 인자 전달
			pstmt.setInt(1, bno);
			
			// 쿼리 실행
			// 처리된 결과 반환됨(수정된 행수)
			int resultRows = pstmt.executeUpdate();
			if(resultRows > 0) {
				System.out.println("게시물 삭제 성공");
			}else {
				System.out.println("게시물 삭제 실패");
			}
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}

//private static void updateTitle(int bno, String newTitle) {
//		String sql = " ";
//		try {
//			
//			// PreparedStatement 객체에서 사용할  SQL문 생성
//			sql = "update tbl_board";
//			sql += " set title = ?";
//			sql += " where bno = ?";
//			
//			// PrepareStatement 객체 얻음
//			pstmt = con.prepareStatement(sql);
//			
//			// 쿼리문에 인자 전달
//			pstmt.setString(1, newTitle);
//			pstmt.setInt(2, bno);
//			
//			// 쿼리 실행
//			// 처리된 결과 반환됨(수정된 행수)
//			int resultRows = pstmt.executeUpdate();
//			if( resultRows > 0 ) {
//				System.out.println("타이틀 수정 성공");
//			}else {
//				System.out.println("타이틀 수정 실패");
//			}
//		} catch (SQLException e) {
//			System.out.println("SQL ERR! : " + e.getMessage());
//		}finally {
//			closeResource(pstmt, rs);
//		}
//	}

//private static void updateCount(int bno) {
//		String sql = " ";
//		try {
//			
//			// PreparedStatement 객체에서 사용할 SQL문 생성
//			sql = "update tbl_board";
//			sql += " set count = count + 1";
//			sql += " where bno = ?";
//			
//			// PreparedStatement 객체얻음
//			pstmt = con.prepareStatement(sql);
//			
//			// 쿼리문에 인자 전달
//			pstmt.setInt(1, bno);
//			
//			// 쿼리실행
//			// 처리된 결과 반환됨(수정된 행수)
//			int resultRows = pstmt.executeUpdate();
//			
//			if(resultRows > 0) {
//				System.out.println("조회수 증가 성공");
//			}else {
//				System.out.println("조회수 증가 실패");
//			}
//			
//		} catch (SQLException e) {
//			System.out.println("SQL ERR! : " + e.getMessage());
//		}finally {
//			closeResource(pstmt, rs);
//		}
//		
//	}

private static void getBoardListPart(int starNo, int length) {
		String sql = " ";
		System.out.println();
		try {
			
			// SQL 쿼리문 만들기
			sql += "select c.bno, c.title, c.content, c.member_id, c.count,  ";
			sql += "    c.created_date, c.reply_group, c.reply_order, c.reply_indent ";
			sql += "from( ";
			sql += "    select rownum rnum, a.* ";
			sql += "    from( ";
			sql += "            select b.* ";
			sql += "            from tbl_board b ";
			sql += "            ORDER BY reply_group DESC, reply_order ";
			sql += "            )a ";
			sql += "        )c ";
			sql += "where rnum between ? and ? ";
			
			// PreparedStatement 객체 얻기
			pstmt = con.prepareStatement(sql);
			
			// 쿼리문에 인자 전달
			pstmt.setInt(1, starNo);
			pstmt.setInt(2, length);
			
			// pstmt 객체의 executeQuery() 메소드를 통해서 쿼리 실행
			// 데이터 베이스에서 조회된 결과가 ResultSet 객체에 담겨옴
			rs = pstmt.executeQuery();
			
			System.out.println(starNo + " 번 부터" + length + " 개까지 조회결과 ");
			
		    while(rs.next()) {
		    	
		    	String strInd = " ";
				int indent = rs.getInt("reply_indent");
				if(indent > 0) {
					for(int i = 0; i < indent; i++) {
						strInd += " ";
					}
				}
	
				System.out.println(
						rs.getInt("bno") + "\t" + 
						rs.getInt("reply_group") + "\t" + 
						rs.getInt("reply_order") + "\t" +
						rs.getInt("reply_indent") + "\t" +
						rs.getString("title") + "\t" + 
						rs.getString("member_id") + "\t" + 
						rs.getInt("count") + "\t" + 
						rs.getDate("created_date") + "\t" 
						);
		    }
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}

private static void getBoardListTopN(int starNo, int length) {
		String sql = " ";
		System.out.println();
		try {
			
			// 쿼리문
			sql = "select a.bno, a.title, a.content, a.member_id, a.count, a.created_date, ";
			sql += " a.reply_group, a.reply_order, a.reply_indent";
		    sql += " from(";
		    sql += " select b.*";
		    sql += " from tbl_board b";
		    sql += " order by reply_group desc, reply_order"; 
		    sql += " )a";
		    sql += " where rownum between ? and ?";
		    
		    // PreparedStatement 객체 얻기
		    pstmt = con.prepareStatement(sql);
		    
		    // 쿼리문에 인자 전달
		    pstmt.setInt(1,  starNo);
		    pstmt.setInt(2,  length);
		    
		    // pstmt 객체의 executeQuery() 메소드를 통해서 쿼리 실행
		    // 데이터 베이스에서 조회된 결과가 ResultSet 객체에 담겨옴
		    rs = pstmt.executeQuery();
		    
		    System.out.println(starNo + " 번 부터" + length + " 개까지 조회결과 ");
		    
		    while(rs.next()) {
		    	
		    	String strInd = " ";
				int indent = rs.getInt("reply_indent");
				if(indent > 0) {
					for(int i = 0; i < indent; i++) {
						strInd += " ";
					}
				}
	
				System.out.println(
						rs.getInt("bno") + "\t" + 
						rs.getInt("reply_group") + "\t" + 
						rs.getInt("reply_order") + "\t" +
						rs.getInt("reply_indent") + "\t" +
						rs.getString("title") + "\t" + 
						rs.getString("member_id") + "\t" + 
						rs.getInt("count") + "\t" + 
						rs.getDate("created_date") + "\t" 
						);
		    }
		    
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	} // end getBoardListTopN

/**
 * 답글 등록 메소드
 * - 원글(부모글)에 대한 정보를 전달받음
 * @param replyGroup
 * @param replyOrder
 * @param replyIndent
 */
	
//private static void insertReply(int replyGroup, int replyOrder, int replyIndent) {
//		String sql = " ";
//		try {
//			
//			// 저장할 게시물 세팅
//			String title = "[답글] 7/7은 좋은날";
//			String content = "[답글] 이유는 없어";
//			String memberId = "user04";
//			
//			/*
//			 * [ 1. 현재 부모글에 달린 답글들의 그룹내 순서 증가(+1)]
//			 * - 현재 부모글 아래에 답글이 있는 경우 그 답글들의 그룹내순서를 +1 한다.
//			 *   왜냐하면 현재 추가될 답글이 부모글 바로 다음에 올 수 있도록 기존 답글들의
//			 *   그룹내 순서를 +1씩 해서 뒤로 밀어내는 효과를 낸다.
//			 * - 뒤로 밀어낸 그 자리를 현재 추가될 답글이 들어간다.
//			 *   데이터베이스에 들어가는 순서는 개발자가 정할수 없다. 그래서 나중에
//			 *   정렬할 때 사용하기 위해서 그렇게 하는 것임.
//			 */
//			
//			// 그룹으로 묶인 글들 중에서 현재 답글이 달릴 부모의 그룹내순서 보다 큰 답글들의 그룹내순서 + 1
//			// 기존 답글들이 뒤로 한칸씩 밀리는 효과. 밀린 자리를 이후에 달릴 답글이 차지함.
//			
			// 쿼리문
//			sql = "update tbl_board";
//			sql += " set reply_order = reply_order + 1 ";
//			sql += " where reply_group = ? and reply_order > ?";
//			
//			// PreparedStatement 객체 얻음(쿼리 실행해주는 객체)
//			pstmt = con.prepareStatement(sql);
//			
//			// 부모글의 그룹
//			pstmt.setInt(1, replyGroup);
//			// 부모글의 그룹내순서
//			pstmt.setInt(2, replyOrder);
//			
//			// 쿼리 실행
//			int resultRows = pstmt.executeUpdate();
//			if( resultRows > 0 ) {
//				System.out.println("기존 답글의 order 컬럼 +1 변경 성공");
//			}else {
//				System.out.println("기존 답글의 order 컬럼 +1 변경 실패");
//			}
//			
//			// 2. 답글의 저장 작업 시작
//			
//			// PreparedStatement 객체에 사용할 SQL문 생성
//			sql = "insert into tbl_board(bno, title, content, member_id, count,";
//			sql += " created_date, reply_group, reply_order, reply_indent)"; 
//			sql += " values ( seq_bno.nextval, ?, ?, ?, 0, sysdate, ?, ?, ?)";
//			
//			// PreparedStatement 객체 얻음(쿼리 실행해주는 객체)
//			pstmt = con.prepareStatement(sql);
//			
//			// 쿼리문에 인자전달
//			pstmt.setString(1, title);
//			pstmt.setString(2, content);
//			pstmt.setString(3, memberId);
//			// 부모글의 그룹이 그대로 들어감(같이 묶임)
//			pstmt.setInt(4, replyGroup);
//			// 그룹내순서는 부모글+1 (최신글이 부모글 바로 다음에 옴)
//			pstmt.setInt(5, replyOrder + 1);
//			// 들여쓰기는 부모 들여쓰기+1
//			pstmt.setInt(6, replyIndent + 1);
//			
//			// executeUpdate() 쿼리 실행
//			// resultRows으로 실행 결과(영향을 받은 행수)
//			resultRows = pstmt.executeUpdate();
//			if( resultRows > 0 ) {
//				System.out.println("답글 저장 완료");
//			}else {
//				System.out.println("답글 저장 실패");
//			}
//			
//		} catch (SQLException e) {
//			System.out.println("SQL ERR! : " + e.getMessage());
//		}finally {
//			closeResource(pstmt, rs);
//		}
//	} // end insertReply

//	private static void insertNewBoard() {
//		String sql = " ";
//		
//		try {
//			// 저장할 게시물 세팅
//			String title = "Eleven to eleven Post";
//			String content = "This is a another reply to the third post.";
//			String memberId = "user03";
//			
//			int replyOrder = 0; // order는 부모 order + 1(부모 다음으로 위치하도록)
//			int replyIndent = 0; // indent는 부모 indent + 1(부모 보다 한큰 들여쓰기)

//			// PreparedStatement 객체에 사용할 SQL문 생성
//			sql = "insert into tbl_board(bno, title, content, member_id, count,";
//			sql += " created_date, reply_group, reply_order, reply_indent)"; 
//			sql += " values ( seq_bno.nextval, ?, ?,";
//			sql += " ?, 0, sysdate, seq_bno.currval, ?, ?)";
//			
//			// PreparedStatement 객체 얻기(쿼리를 실행해주는 객체)
//			pstmt = con.prepareStatement(sql);
//			
//			// 쿼리문에 인자 전달
//			pstmt.setString(1, title);
//			pstmt.setString(2, content);
//			pstmt.setString(3, memberId);
//			pstmt.setInt(4, replyOrder);
//			pstmt.setInt(5, replyIndent);
//			
            // executeUpdate() 쿼리실행
			// resultRows으로 실행 결과(영향을 받은 행수)
//			int resultRows = pstmt.executeUpdate();
//			if( resultRows > 0 ) {
//				System.out.println("새글 입력 성공");
//			}else {
//				System.out.println("새글 입력 실패");
//			}
//			
//		} catch (SQLException e) {
//			System.out.println("SQL ERR! : " + e.getMessage());
//		}finally {
			//자원 해제 메소드 호출
//			closeResource(pstmt, rs);
//		}
//	} // end insertNewBoard

	private static void getBoardList() {
		String sql = " ";
		try {
		
		// 쿼리문 
		sql = "select b.bno, b.title, b.content, b.member_id, b.count,"; 
		sql += " to_char(b.created_date,'YYYY-MM-DD') \"created_date\", b.reply_group,";
		sql += " b.reply_order, b.reply_indent";
		sql += " from tbl_board b";
		sql += " order by reply_group desc, b.reply_order ";
		
		// PreparedStatement 객체 얻기
		pstmt = con.prepareStatement(sql);
		
		
		// pstmt 객체의 executeQuery() 메소드를 통해서 쿼리 실행
		// 데이터 베이스에서 조회된 결과가 ResultSet 객체에 담겨옴
		rs = pstmt.executeQuery();
		
		// 게시물 목록 제목
//		System.out.println("============================================================================================================");
//		System.out.println("1. 전체 게시물 목록");
//		System.out.println("bno" + "\t" + "title" + "\t" + "content" + "\t" + "member_id"
//							+ "\t" + "count" + "\t" + "created_date" + "\t" + "reply_group"
//							+ "\t" + "reply_order" + "\t" + "reply_indent");
		while(rs.next()) {
			
			String strInd = " ";
			int indent = rs.getInt("reply_indent");
			if(indent > 0) {
				for(int i = 0; i < indent; i++) {
					strInd += " ";
				}
			}
			
			System.out.println(rs.getInt("bno") + "\t" + rs.getString("title") + "\t" 
					+ rs.getString("content") + "\t" + rs.getString("member_id") + "\t"
					+ rs.getInt("count") + "\t" + rs.getDate("created_date") + "\t"
					+ rs.getInt("reply_group") + "\t" + rs.getInt("reply_order") + "\t"
					+ rs.getInt("reply_indent"));
		}
		
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}finally {
			// 자원 해제 메소드 호출
			closeResource(pstmt, rs);
		}
		
	} // end getBoardList

	private static void closeResource() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("6. 자원해제 ERR! : " + e.getMessage());
		}
	} // end closeResource

	// 커넥션 객체 자원 반환
	private static void closeResource(PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}
	} // end closeResource

	private static void connectDB() {
		try {
			Class.forName(DRIVER_NAME);
			System.out.println("드라이버 로드 성공!");

			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("커넥션 객체 생성 성공!");

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR! : " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}
		
	} // end connectDB
}