/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.mail;

import java.util.List;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

/**
 *
 * @author varghees
 */
public class HtmlMail {

    final static Logger log = Logger.getLogger(HtmlMail.class);
    private MailProperties props = null;

    public HtmlMail(MailProperties props) {
        this.props = props;
    }

    public String sendMail() {
        log.debug("Start function of sendMail in HtmlMail class");
        try {
            // Create the email message
            HtmlEmail email = new HtmlEmail();
            email.setHostName(props.getHostName());
            email.setSmtpPort(props.getPort());
            email.setAuthentication(props.getAuthUser(), props.getAuthPasswd());
            //email.setSSLOnConnect(props.isSetSSLOnConnect());
            email.setFrom(props.getFrom());
            email.setSubject(props.getSubject());
            email.setMsg(props.getHtmlMessage());
            for (int i = 0; i < props.getTo().split(",").length; i++) {
                String to = props.getTo().split(",")[i];
                email.addTo(to);

            }
            //email.addTo(props.getTo());
            if (props.getCc() != null) {
                for (int i = 0; i < props.getCc().split(",").length; i++) {
                    String cc = props.getCc().split(",")[i];
                    email.addCc(cc);
                }
            }
//            email.addCc(props.getCc());

            // embed the image and get the content id
            List<MailAttachment> attachFiles = props.getAttachment();
            if (attachFiles != null) {
                for (int i = 0; i < attachFiles.size(); i++) {
                    MailAttachment attachFile = attachFiles.get(i);
                    EmailAttachment emailAttachment = new EmailAttachment();
                    emailAttachment.setPath(attachFile.getAttachmentPath());
                    emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
                    emailAttachment.setDescription(attachFile.getAttachDescription());
                    emailAttachment.setName(attachFile.getAttachName());
                    email.attach(emailAttachment);
                }
            }

            // set the html message
            email.setHtmlMsg(props.getHtmlMessage());

            // set the alternative message
            if (props.getTxtMessage() != null) {
                email.setTextMsg(props.getTxtMessage());
            }

            // send the email
            return email.send();

        } catch (EmailException ex) {
            log.error("EmailException in sendMail function: "+ex);
        }
        log.debug("Start function of sendMail in HtmlMail class");
        return "Not Sent";
    }
}
