package com.bugtracker.bug;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Ticket {

    @NotNull
    private String title;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private T_User t_user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private T_Group t_group;

    private Date t_date;
    private String body;
    protected Boolean resolved;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setUser(T_User t_user) {
        this.t_user = t_user;
    }

    public T_User getUser() {
        return this.t_user;
    }

    public void setGroup(T_Group t_group) {
        this.t_group = t_group;
    }

    public T_Group getGroup() {
        return this.t_group;
    }

    public void setDate() {
        this.t_date = new Date();
    }

    public Date getDate() {
        return this.t_date;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return this.body;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public boolean getResolved() {
        return this.resolved;
    }
}
