package cn.zhangz.getaway2.login.controller;

import cn.zhangz.getaway2.config.security.AccessTokenProperties;
import cn.zhangz.getaway2.config.util.TokenCookieUtil;
import cn.zhangz.getaway2.login.config.LoginHandleAdepter;
import cn.zhangz.getaway2.login.config.LoginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping
public class LogoutController {
    @Autowired
    private AccessTokenProperties accessTokenProperties;

    public AccessTokenProperties getAccessTokenProperties(){
        return this.accessTokenProperties;
    }

    @Autowired
    private LoginHandleAdepter loginHandleAdepter;

    @Autowired
    private RestTemplate restTemplate;

    public RestTemplate getRestTemplate(){
        return restTemplate;
    }

    @Autowired
    private LoginProperties loginProperties;

    private String getLoginModel(){
        if(null == loginProperties || org.apache.commons.lang3.StringUtils.isEmpty(loginProperties.getModel())){
            return "default";
        }
        return loginProperties.getModel();
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = TokenCookieUtil.getTokenFromCookie(request.getCookies());
        if (!StringUtils.isEmpty(token)) {
            invalidateToken(token);
        }
        response.addCookie(TokenCookieUtil.deleteTokenCookie());
        loginHandleAdepter.logout(getLoginModel(),request,response);
    }

    private void invalidateToken(String token){
        try{
            getRestTemplate().getForEntity("http://" + getAccessTokenProperties().getAppName() + "/authentication/removeToken?access_token="+token, boolean.class);
        }catch (HttpClientErrorException e){
            log.error("Invalidate Token error!" + e);
        }
    }

}
