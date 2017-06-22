package com.xqh.financial.exception;

/**
 * Created by hssh on 2017/5/7.
 */
public class ValidationException extends RuntimeException
{
    public ValidationException()
    {
        super();
    }

    public ValidationException(String message)
    {
        super(message);
    }
}
