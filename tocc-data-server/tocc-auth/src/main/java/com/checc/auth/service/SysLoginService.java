package com.checc.auth.service;

import com.checc.common.constant.Constants;
import com.checc.common.exception.CustomException;
import com.checc.common.exception.user.UserNotExistsException;
import com.checc.common.exception.user.UserPasswordNotMatchException;
import com.checc.common.log.publish.PublishFactory;
import com.checc.common.security.LoginUser;
import com.checc.common.security.service.TokenService;
import com.checc.common.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Service
public class SysLoginService
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;



    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    public String login(String username, String password)
    {
        if (StringUtils.isAnyBlank(username, password))
        {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null"));
            throw new UserNotExistsException();
        }
        // 用户验证
        Authentication authentication = null;
        try
        {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage());
                throw new CustomException(e.getMessage());
            }
        }
        PublishFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }
}
