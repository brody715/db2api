package com.brody715.db2api.filters;

import com.alibaba.fastjson.JSON;
import com.brody715.db2api.common.BaseContext;
import com.brody715.db2api.common.R;
import com.brody715.db2api.utils.AuthChecker;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    @Autowired
    AuthChecker authChecker;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        String[] whitelistUrls = {
                "/login"
        };

        if (checkInWhitelist(whitelistUrls, request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // get header
        String token = request.getHeader("authorization");

        // check
        if (authChecker.authCheck(token)) {
            // pass
            String username = authChecker.getUserFromToken(token);
            BaseContext.instance().setUsername(username);
            chain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error(400, "invalid token")));
    }

    boolean checkInWhitelist(String[] whitelist, String url) {
        for (String whitelistUrl : whitelist) {
            if (PATH_MATCHER.match(whitelistUrl, url)) {
                return true;
            }
        }
        return false;
    }
}
