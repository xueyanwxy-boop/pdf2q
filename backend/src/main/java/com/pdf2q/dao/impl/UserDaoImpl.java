package com.pdf2q.dao.impl;

import com.pdf2q.dao.UserDao;
import com.pdf2q.domain.User;
import com.pdf2q.domain.UserToken;
import com.pdf2q.mapper.UserMapper;
import java.time.Instant;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

  private final UserMapper userMapper;

  public UserDaoImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public int insert(User user) {
    return userMapper.insert(user);
  }

  @Override
  public User selectByPhone(String phone) {
    return userMapper.selectByPhone(phone);
  }

  @Override
  public User selectById(Long id) {
    return userMapper.selectById(id);
  }

  @Override
  public int updateNickname(Long id, String nickname) {
    return userMapper.updateNickname(id, nickname);
  }

  @Override
  public int insertToken(UserToken token) {
    return userMapper.insertToken(token);
  }

  @Override
  public UserToken selectToken(String token) {
    UserToken found = userMapper.selectToken(token);
    if (found == null) {
      return null;
    }
    if (found.getExpiresAt() != null && found.getExpiresAt().isBefore(Instant.now())) {
      userMapper.deleteToken(token);
      return null;
    }
    return found;
  }

  @Override
  public int deleteToken(String token) {
    return userMapper.deleteToken(token);
  }
}
