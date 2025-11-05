package com.viaticos.adapter.out.mail;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
public class EnvioCorreoAdapter {

	@Value("${ambiente}")
	private String ambiente;

	@Autowired
	private JavaMailSender javaMailSender;

	Logger log = LoggerFactory.getLogger(EnvioCorreoAdapter.class);

	public void enviarCorreo(String email, String subject, String html) {
		log.info("Se enviará correo");
		log.info("ambiente:" + ambiente);
		if ("pro".equals(ambiente)) {
			MimeMessage mailMessage = javaMailSender.createMimeMessage();
			try {
				mailMessage.setSubject(subject, "UTF-8");
				MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "UTF-8");
				helper.setTo(email);
				helper.setText(html.trim(), true);
				javaMailSender.send(mailMessage);
				log.info("Se envió correo");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		} else {
			log.info("no se enviará mail ya que no es PRO");
		}
	}

	@SuppressWarnings("static-access")
	public void enviarCorreoLst(String emails, String subject, String html, byte[] attach) {
		log.info("Se enviará correo");
		log.info("ambiente:" + ambiente);
		if ("pro".equals(ambiente)) {
			MimeMessage mailMessage = javaMailSender.createMimeMessage();
			RecipientType rT = null;

			ByteArrayDataSource bds = null;

			if (attach != null) {
				bds = new ByteArrayDataSource(attach, "application/pdf");
				bds.setName("PDF Reporte solicitud.pdf");

			}

			try {
				InternetAddress[] iA = InternetAddress.parse(emails, true);

				mailMessage.setSubject(subject, "UTF-8");
				mailMessage.setRecipients(rT.TO, iA);

				MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "UTF-8");
				helper.setText(html.trim(), true);

				if (attach != null)
					helper.addAttachment(bds.getName(), bds);

				javaMailSender.send(mailMessage);
				log.info("Se envió correo");

			} catch (MessagingException e) {
				e.printStackTrace();
			}
		} else {
			log.info("no se enviará mail ya que no es PRO");
		}
	}

}
