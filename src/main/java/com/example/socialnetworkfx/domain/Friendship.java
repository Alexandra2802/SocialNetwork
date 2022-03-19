package com.example.socialnetworkfx.domain;

public class Friendship extends Entity<Long>{
    private Long user1ID;
    private Long user2ID;
    private  String date;

    public Friendship(Long u1,Long u2, String date)
    {
        this.user1ID=u1;
        this.user2ID=u2;
        this.date = date;
    }

    public long getuser1ID() {
        return user1ID;
    }

    public long getuser2ID() {
        return user2ID;
    }

    public String getDate() {
        return date;
    }

    public void setuser1ID(Long user1ID) {
        this.user1ID = user1ID;
    }

    public void setuser2ID(Long user2ID) {
        this.user2ID = user2ID;
    }

    @Override
    public String toString() {
        return "friendship id: "+this.id+" user1: "+user1ID+" user2: "+user2ID + " date: " + date;
    }
}
