package com.bugtracker.bug;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Ticket {

    @NotNull
    private String title;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int ticket_id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private T_User t_user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private T_Group t_group;

    private Date t_date;
    private String body;
    protected Boolean resolved;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.ticket_id = id;
    }

    public int getId() {
        return ticket_id;
    }

    public void setUser(T_User t_user) {
        this.t_user = t_user;
    }

    @JsonIgnore
    public T_User getUser() {
        return t_user;
    }

    public String getUserId() {
        return t_user.getUser_id();
    }

    public void setGroup(T_Group t_group) {
        this.t_group = t_group;
    }

    @JsonIgnore
    public T_Group getGroup() {
        return t_group;
    }

    public String getGroupId() {
        return t_group.getGroup();
    }

    public void setDate(Date date) {
        this.t_date = date;
    }

    public Date getDate() {
        return t_date;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public boolean getResolved() {
        return resolved;
    }
}
