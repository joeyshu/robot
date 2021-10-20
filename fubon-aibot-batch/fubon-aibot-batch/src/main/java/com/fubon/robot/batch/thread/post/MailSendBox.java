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
	
	
//	public static void main(String[] args) {
//		MailSendBox box = new MailSendBox(to, from, host, port, protocol, subject, contenText, user, pwd);
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
