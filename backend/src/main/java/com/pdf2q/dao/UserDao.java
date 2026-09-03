package com.pdf2q.dao;

import com.pdf2q.domain.User;
import com.pdf2q.domain.UserToken;

public interface UserDao {

  int insert(User user);

  User selectByPhone(String phone);

  User selectById(Long id);

  int updateNickname(Long id, String nickname);

  int insertToken(UserToken token);

  UserToken selectToken(String token);

  int deleteToken(String token);
}
