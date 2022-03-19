package com.example.socialnetworkfx.domain;


public class FriendRequest extends Entity<Long>{
    private Long id;
    private User from;
    private User to;
    private FriendRequestStatus status;

    public FriendRequest(User from, User to, FriendRequestStatus status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }
}
