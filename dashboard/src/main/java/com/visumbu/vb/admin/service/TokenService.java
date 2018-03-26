/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.admin.service;

import com.visumbu.vb.admin.dao.TokenDao;
import com.visumbu.vb.admin.oauth.service.TokenTemplate;
import com.visumbu.vb.controller.BaseController;
import com.visumbu.vb.model.Account;
import com.visumbu.vb.model.Agency;
import com.visumbu.vb.model.TokenDetails;
import com.visumbu.vb.model.VbUser;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Lino
 */
@Service("TokenService")
public class TokenService extends BaseController{

    @Autowired
    private TokenDao tokenDao;
    @Autowired
    UserService userService;

    public void insertTokenDetails(TokenDetails tokenDetail) {
        tokenDao.create(tokenDetail);
    }

    public TokenDetails getTokenObject(MultiValueMap<String, Object> data, TokenTemplate tokenData) {
        TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setTokenValue(tokenData.getTokenValue());
        tokenDetails.setRefreshToken(tokenData.getRefreshToken());
        tokenDetails.setExpiryDate(Long.toString(tokenData.getExpiryDate()));
        tokenDetails.setScope(tokenData.getScope());
        tokenDetails.setClientId((String) data.getFirst("clientId"));
        tokenDetails.setClientSecret((String) data.getFirst("clientSecret"));
        tokenDetails.setDataSourceType((String) data.getFirst("source"));
        tokenDetails.setAgencyId((Agency) data.getFirst("agencyId"));
        return tokenDetails;
    }

    public TokenDetails insertIntoDb(MultiValueMap<String, Object> dataMap, TokenTemplate tokenDetails, HttpServletRequest request) throws Exception {

        VbUser user = userService.findByUsername(getUser(request));
        dataMap.add("agencyId", user.getAgencyId());
        TokenDetails tokenData = getTokenObject(dataMap, tokenDetails);
        insertTokenDetails(tokenData);
        return tokenData;
    }
}