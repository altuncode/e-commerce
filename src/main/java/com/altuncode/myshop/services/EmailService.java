package com.altuncode.myshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service("EmailService")
public class EmailService {

    @Value("${adminMail}")
    private String adminMail;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendHtmlEmail(String to, String subject, String mytext) {
        try {
            // Create a MimeMessage
            MimeMessage message = mailSender.createMimeMessage();

            // Use MimeMessageHelper to set up the email details
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Prepare the context and set the dynamic variables
            Context context = new Context();
            context.setVariable("mytext", mytext);

            // Generate the HTML content using Thymeleaf
            String htmlContent = templateEngine.process("admin/forbox.html", context);

            helper.setTo(to); // Set the recipient
            helper.setSubject(subject); // Set the subject
            helper.setText(htmlContent, true); // Set the HTML content

            // Add the inline image
            helper.addInline("logo", new ClassPathResource("static/admin/mail.png"));

            // Send the email
            mailSender.send(message);
        } catch (Exception e) {
            System.out.printf("Error sending email to user: %s\n", e.getMessage());
            System.out.println("Subject of message: " + subject);
            System.out.println("Body of message: " + mytext);
        }
    }

    public void sendMailtoAdmin(String subject, String body){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminMail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.printf("Error sending email to admin: %s\n", e.getMessage());
            System.out.println("Subject of message: " + subject);
            System.out.println("Body of message: " + body);
        }
    }

}