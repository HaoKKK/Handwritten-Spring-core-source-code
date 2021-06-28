package test.service;

import test.pojo.User;

import java.util.List;
public interface UserService {
    List<User> findUsers(String name);
}
