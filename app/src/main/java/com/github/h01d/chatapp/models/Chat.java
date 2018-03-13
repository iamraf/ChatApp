package com.github.h01d.chatapp.models;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class Chat
{
    private String message;
    private int typing;
    private long timestamp, seen;

    public Chat()
    {

    }

    public Chat(String message, int typing, long timestamp, long seen)
    {
        this.message = message;
        this.typing = typing;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public int getTyping()
    {
        return typing;
    }

    public void setTyping(int typing)
    {
        this.typing = typing;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getSeen()
    {
        return seen;
    }

    public void setSeen(long seen)
    {
        this.seen = seen;
    }
}
