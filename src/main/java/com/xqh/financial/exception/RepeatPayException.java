package com.xqh.financial.exception;

/**
 * Created by hssh on 2017/5/13.
 */
public class RepeatPayException extends RuntimeException
{

    public RepeatPayException()
    {
        super();
    }

    public RepeatPayException(String message)
    {
        super(message);
    }
}
