package com.checc.common.exception.user;

import com.checc.common.exception.base.BaseException;

/**
 * 用户信息异常类
 * 
 * @author checc
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
