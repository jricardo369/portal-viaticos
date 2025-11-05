package com.viaticos.adapter.out.mail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
public class TemplateMail {
	
	Logger log = LoggerFactory.getLogger(TemplateMail.class);
	
	@Value("${ambiente}")
	private String ambiente;
	
	@Value("${mail.assets.folder}")
	private String assets_folder;
	
	@Value("${ruta.portal}")
	private String rutaPortal;
	
	@Value("${ruta.portal.qas}")
	private String rutaPortalQas;

	public String solveTemplate(String templateName, Map<String, Object> customParams) throws Exception {

		String template = getTemplate(templateName);
		
		String ruta = "";
		if ("qas".equals(ambiente)) {
			ruta = rutaPortalQas; // QAS
		} else if ("pro".equals(ambiente)) {
			ruta = rutaPortal; // PRO
		}

		Map<String, Object> params = new HashMap<>();
			params.put("${url-portal-viaticos}", ruta);

		for (Entry<String, Object> entry : customParams.entrySet())
			params.put(entry.getKey(), entry.getValue());

		for (Entry<String, Object> entry : params.entrySet()) {
			String entryKey = entry.getKey();
			String value = "" + entry.getValue();
			template = template.replace(entryKey, value);
		}

		return template;
	}
	
	private String getTemplate(String templateName) throws Exception {

		String style = "<style>" + getStyles() + "</style>\r\n";
		
		log.info("assets_folder:"+assets_folder);

		File templateFile = new File(assets_folder+"/"+templateName);
		
		if(!templateFile.canRead())
			templateFile.setReadable(true);

		if (!templateFile.exists()) throw new Exception("No se encontró el archivo plantilla en la dirección <" + templateFile.getAbsolutePath() + ">");

		try (InputStream stream = new FileInputStream(templateFile)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = stream.read()) >= 0)
				baos.write(b);
			
			String font = "<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\r\n"
					+ "<link href=\"https://fonts.googleapis.com/css2?family=Roboto:wght@100&display=swap\" rel=\"stylesheet\">";
			
			//System.out.println("<html>"+font+"<head>"+style+ new String(baos.toByteArray(), "UTF-8")+"</head></html>");
			String body = new String(baos.toByteArray(), "UTF-8");
			return "<html>"+font+"<head>"+style + body +"</head></html>";
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception( "Ocurrió un error al leer la plantilla; " + e.getMessage());
		}
	}
	
	private String getStyles() throws Exception {
		
		log.info("assets_folder:"+assets_folder);

		File templateFile = new File(assets_folder+"/template-styles.css");

		if (!templateFile.exists()) throw new Exception("No se encontró el archivo de estilos de plantilla en la dirección <" + templateFile.getAbsolutePath()
						+ ">");

		try (InputStream stream = new FileInputStream(templateFile)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = stream.read()) >= 0)
				baos.write(b);
			return new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Ocurrió un error al leer la plantilla; " + e.getMessage());
		}
	}

}
