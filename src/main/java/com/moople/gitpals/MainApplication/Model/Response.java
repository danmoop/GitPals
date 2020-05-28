package com.moople.gitpals.MainApplication.Model;

import lombok.Getter;

@Getter
public class Response {

    private Status status;

    private Response(Status status) {
        this.status = status;
    }

    public final static Response OK = new Response(Status.OK);
    public final static Response FAILED = new Response(Status.FAILED);
    public final static Response PROJECT_EXISTS = new Response(Status.PROJECT_EXISTS);
    public final static Response USER_NOT_FOUND = new Response(Status.USER_NOT_FOUND);

    private enum Status {
        OK, FAILED, PROJECT_EXISTS, USER_NOT_FOUND
    }
}