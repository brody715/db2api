package com.brody715.db2api.utils;

import java.util.List;

import com.brody715.db2api.config.RootApplicationConfig;
import com.brody715.db2api.model.config.UserConfig;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthChecker {

    @Autowired
    RootApplicationConfig config;

    public boolean authCheck(String token) {
        if (token != null && token.startsWith("Basic ")) {
            String data = getDecodedData(token.substring(6));
            if (data.indexOf(':') < 0) {
                return false;
            }
            return checkPsw(getUserFromToken(token), getPswFromToken(token));
        }
        return false;
    }

    private boolean checkPsw(String userFromToken, String pswFromToken) {
        List<UserConfig> u = config.getUsers().stream().filter((user) -> {
            return user.getName().equals(userFromToken);
        }).toList();
        if (u.size() != 1) {
            return false;
        }
        return u.get(0).getToken().equals(pswFromToken);
    }

    public String getUserFromToken(String token) {
        String data = getDecodedData(token.substring(6));
        int k = data.indexOf(':');
        return data.substring(0, k);
    }

    public String getPswFromToken(String token) {
        String data = getDecodedData(token.substring(6));
        int k = data.indexOf(':');
        return data.substring(k + 1);
    }

    // 加密
    public static String getEncodedData(String plainText) {
        return Base64.encode(plainText, CharsetUtil.CHARSET_UTF_8);
    }


    /**
     * 获取解密后信息
     *
     * @param cipherText 密文
     * @return 解密后信息
     */
    public static String getDecodedData(String cipherText) {
        return StrUtil.str(Base64.decode(cipherText), CharsetUtil.CHARSET_UTF_8);

    }
}
