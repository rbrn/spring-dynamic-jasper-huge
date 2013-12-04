package org.convey.user.registration.controller;

import org.convey.user.registration.model.User;

import java.util.ArrayList;
import java.util.Date;

public class UserFactory {
    public static ArrayList<User> createBeanCollection() {
        ArrayList<User> userList = new ArrayList<User>();
        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastname");
        user.setEmail("#@email");
        user.setUserName("username");
        user.setPassWord("passwd");
        user.setRegisteredDate(new Date());
        user.setId(1);
        userList.add(user);
        return userList;
    }
}
