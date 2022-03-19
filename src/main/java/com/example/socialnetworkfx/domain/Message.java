package com.example.socialnetworkfx.domain;


import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private Long id;
    private User from;
    private List<User> to;
    private String text;
    private LocalDateTime date;
    private Message replyingTo;

    public Message( User from, List<User> to, String text, LocalDateTime date,Message replying_to) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = date;
        this.replyingTo=replying_to;
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

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReplyingTo() {
        return replyingTo;
    }

    public void setReplyingTo(Message replyingTo) {
        this.replyingTo = replyingTo;
    }

    @Override
    public String toString() {
        return "from: " + from + ", to: " + to + " text: '" + text + '\'' +
                ", date: " + date ;
    }
}

