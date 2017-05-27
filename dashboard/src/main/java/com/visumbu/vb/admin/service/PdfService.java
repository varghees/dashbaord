/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.admin.service;

import com.visumbu.vb.admin.dao.DealerDao;
import com.visumbu.vb.admin.dao.UserDao;
import com.visumbu.vb.bean.LoginUserBean;
import com.visumbu.vb.bean.map.auth.SecurityAuthBean;
import com.visumbu.vb.model.Account;
import com.visumbu.vb.model.AccountUser;
import com.visumbu.vb.model.Agency;
import com.visumbu.vb.model.AgencyLicence;
import com.visumbu.vb.model.AgencyProduct;
import com.visumbu.vb.model.AgencySettings;
import com.visumbu.vb.model.Currency;
import com.visumbu.vb.model.Dealer;
import com.visumbu.vb.model.Property;
import com.visumbu.vb.model.UserAccount;
import com.visumbu.vb.model.VbUser;
import com.visumbu.vb.utils.VbUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jp
 */
@Service("pdfService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PdfService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DealerDao dealerDao;
    private String pdfGeneratorCommand = "wkhtmltopdf";
    private String pdfFilesPath = "/tmp/";

    public String generatePdf(String url, String windowStatus) {

        try {
            // command ===> wkhtmltopdf --window-status done cover http://localhost:8080/dashboard/index.html#/viewPdf/27/Jose/30/Product%201/271?startDate=4~2F28~2F2017\&endDate=5~2F27~2F2017 test.pdf
            String windowStatusCommand = "";
            if (windowStatus != null) {
                windowStatusCommand = " --window-status " + windowStatus;
            }
            String filename = pdfFilesPath + RandomStringUtils.randomAlphanumeric(32).toUpperCase() + ".pdf";
            String command = pdfGeneratorCommand + " " + windowStatusCommand + " " + URLEncoder.encode(url, "UTF-8") + " " + filename;
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            java.lang.Process p = rt.exec(command);
            p.waitFor();
            return filename;
        } catch (InterruptedException ex) {
            Logger.getLogger(PdfService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PdfService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
