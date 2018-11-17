package com.eu.habbo.messages;

import com.eu.habbo.messages.incoming.MessageHandler;

public interface ICallable
{
    public void call(MessageHandler handler);
}