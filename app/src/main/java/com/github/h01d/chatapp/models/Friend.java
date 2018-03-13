package com.github.h01d.chatapp.models;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class Friend
{
    private long date;

    public Friend()
    {

    }

    public Friend(long date)
    {
        this.date = date;
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }
}
