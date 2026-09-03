package com.pdf2q.mapper;

import com.pdf2q.domain.User;
import com.pdf2q.domain.UserToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

  int insert(User user);

  User selectByPhone(@Param("phone") String phone);

  User selectById(@Param("id") Long id);

  int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);

  int insertToken(UserToken token);

  UserToken selectToken(@Param("token") String token);

  int deleteToken(@Param("token") String token);

  int deleteExpiredTokens(@Param("now") java.time.Instant now);
}
