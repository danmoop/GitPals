package com.moople.gitpals.MainApplication.Model;

import lombok.Getter;

@Getter
public class Response {

    private Status status;

    public Response(Status status) {
        this.status = status;
    }

    public final static Response OK = new Response(Status.OK);
    public final static Response FAILED = new Response(Status.FAILED);
    public final static Response PROJECT_EXISTS = new Response(Status.PROJECT_EXISTS);

    private enum Status {
        OK, FAILED, PROJECT_EXISTS
    }
}