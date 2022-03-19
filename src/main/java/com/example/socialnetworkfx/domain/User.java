package com.example.socialnetworkfx.domain;


import java.util.ArrayList;
import java.util.List;

public class User extends Entity<Long>{
    private String first_name;
    private String last_name;
    private String username;
    private String password;
    private List<User> friends_list;

    public User(String first_name, String last_name, String username, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.password = password;
        friends_list=new ArrayList<>();
    }

    public List<User> getFriends_list() {
        return friends_list;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addFriend(User friend){
        this.friends_list.add(friend);
        if(!friend.getFriends_list().contains(this))
            friend.addFriend(this);
    }

    public void removeFriend(User friend)
    {
        this.friends_list.remove(friend);
        friend.getFriends_list().remove(this);
    }

    @Override
    public String toString() {
        return username;
    }


}

