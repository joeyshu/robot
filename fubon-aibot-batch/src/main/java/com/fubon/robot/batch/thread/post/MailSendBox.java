package com.fubon.robot.batch.thread.post;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSendBox {
	private String to;
	private String from;
	private String host;
	private String port;
	private String protocol;
	private String subject;
	private String contenText;
	private String user;
	private String pwd;
	
	
	public MailSendBox(String to,String from,String host,String port,String protocol,String subject ,String contenText,String user ,String pwd) {
		this.to = to;
		this.from = from;
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.subject = subject;
		this.contenText = contenText;
		this.user = user;
		this.pwd = pwd;
		
	}
	
//	
//	public static void main(String[] args) {
//		String to = "i55963852@gmail.com";
//		String from = "i55963852@gmail.com";
//		String host = "smtp.mailtrap.io";
//		Properties properties = System.getProperties();
//		properties.setProperty("mail.smtp.host", host);
//		properties.setProperty("mail.smtp.auth", "true");
//		properties.setProperty("mail.smtp.port", "2525");
////		properties.setProperty("mail.smtp.starttls.enable", "true");
//		properties.setProperty("mail.transport.protocol", "smtp");
//		properties.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
//		Session session = Session.getInstance(properties, new Authenticator() {
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("51d1acc0051e2f", "b752e564c727ec");
//			}
//		});
//		try {
//			MimeMessage message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(from));
//
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			message.setSubject("This is the Subject Line!");
//			message.setText("This is actual message");
//			Transport.send(message, message.getAllRecipients());
//			System.out.println("Sent message successfully....");
//		} catch (MessagingException mex) {
//			mex.printStackTrace();
//		}
//	}

	public void sendMail() {
		
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", port);
//		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.transport.protocol", protocol);
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pwd);
			}
		});
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(contenText);
			Transport.send(message, message.getAllRecipients());
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		
		
		
	}

}
