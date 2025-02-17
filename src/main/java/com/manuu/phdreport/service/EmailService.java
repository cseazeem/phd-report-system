package com.manuu.phdreport.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Your OTP for Registration");
            helper.setText("Your OTP is: " + otp + "\n\nPlease use this OTP to complete your registration. It is valid for 5 minutes.", true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendConfirmation(String email, String role) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Complete Your Registration Profile");

        String messageBody = "";

        if ("SCHOLAR".equals(role)) {
            messageBody = "Dear Scholar,\n\n" +
                    "Congratulations! Your PhD Scholar registration has been approved. To complete your profile and proceed with the next steps in your academic journey, please log in and provide the necessary details.\n\n" +
                    "If you have any questions, feel free to reach out to us.\n\n" +
                    "Best regards,\n" +
                    "The PhD Scholar Team";
        }
        if ("COORDINATOR".equals(role)) {
            messageBody = "Dear Coordinator,\n\n" +
                    "Your registration has been approved! Please log in to complete your profile. Once completed, you will be able to access coordinator-specific features and manage your responsibilities effectively.\n\n" +
                    "Should you need assistance, don't hesitate to contact our support team.\n\n" +
                    "Best regards,\n" +
                    "The Coordinator Team";
        }
        if("RAC_MEMBER".equals(role)) {
            messageBody = "Dear RAC Member,\n\n" +
                    "Your registration has been successfully approved. Please log in and update your profile to get started with your responsibilities as a member of the Research Advisory Committee.\n\n" +
                    "If you have any inquiries, our support team is ready to help.\n\n" +
                    "Best regards,\n" +
                    "The RAC Team";
        }

        message.setText(messageBody);

        mailSender.send(message);
    }

    public void sendRejectionNotification(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your PhD Registration Status - Rejected");

        String messageBody = "Dear User,\n\n" +
                "We regret to inform you that your registration has been rejected. Unfortunately, we cannot proceed with your application at this time.\n\n" +
                "If you have any questions or would like further clarification, please feel free to reach out to us.\n\n" +
                "Best regards,\n" +
                "The PhD Registration Team";

        message.setText(messageBody);

        mailSender.send(message);
    }


}

