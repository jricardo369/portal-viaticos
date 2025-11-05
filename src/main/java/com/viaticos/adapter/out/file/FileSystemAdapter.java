package com.viaticos.adapter.out.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.domain.ComprobanteViaticoEntity;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-viaticos.properties")
public class FileSystemAdapter implements ArchivosPort {

	@Value("${ambiente}")
	private String ambiente;
	
	@Value("${rutaArchivos}")
	private String rutaArchivos;
	
	@Value("${rutaArchivos.qas}")
	private String rutaArchivosQas;

	Logger log = LoggerFactory.getLogger(FileSystemAdapter.class);

	@Override
	public boolean guardarArchivo(byte[] bytes, String rutaComprobantes, String nombre) {
		
		String rutaFinalArchivos = "";
		if ("qas".equals(ambiente)) {
			rutaFinalArchivos = rutaArchivosQas + rutaComprobantes; // QAS
		} else if ("pro".equals(ambiente)) {
			rutaFinalArchivos = rutaArchivos + rutaComprobantes; // PRO
		}
		
		return almacenArchivo(rutaFinalArchivos, bytes, rutaFinalArchivos + nombre);
	}

	private boolean almacenArchivo(String ruta, byte[] bytes, String nombre) {

		log.info("Se gardará en : " + ruta);

		new File(ruta).mkdirs();

		boolean respuesta = false;
		OutputStream os = null;
		OutputStream osClone = null;

		try {

			log.info("ruta completa con nombre:" + nombre);
			File f = new File(nombre);
			f.createNewFile();

			os = new FileOutputStream(f, false);
			os.write(bytes);
			os.flush();
			os.close();

			respuesta = true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("ERROR:" + e.getLocalizedMessage());
			respuesta = false;
		} finally {

			try {

				if (os != null)
					os.close();
				if (osClone != null)
					osClone.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

		return respuesta;
	}

	@Override
	public boolean eliminarArchivo(String ruta) {
		boolean respuesta = false;
		File archivo = new File(ruta);
		respuesta = eliminarArchivoYSubArchivos(archivo);
		log.info("Archivo " + archivo.getName() + " borrado: " + respuesta);
		return respuesta;
	}

	private boolean eliminarArchivoYSubArchivos(File f) {
		if (f.isDirectory())
			for (File child : f.listFiles())
				eliminarArchivoYSubArchivos(child);
		return f.delete();
	}

	public File obtenerArchivoDesdeRuta(String rutaRelativa) {
		
		String rutaFinalArchivos = "";
		if ("qas".equals(ambiente)) {
			rutaFinalArchivos = rutaArchivosQas + rutaRelativa; // QAS
		} else if ("pro".equals(ambiente)) {
			rutaFinalArchivos = rutaArchivos + rutaRelativa; // PRO
		}
		
		return new File(rutaFinalArchivos);
	}

	@Override
	public byte[] obtenerArchivo(String rutaRelativa) {

		File file = obtenerArchivoDesdeRuta(rutaRelativa);

		if (!file.exists()) {
			System.out.println(file.getAbsolutePath() + " not found");
		}

		FileInputStream fis = null;
		try {

			fis = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fis.read(bytes);
			return bytes;

		} catch (IOException e) {

			e.printStackTrace();
			return null;

		} finally {

			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public void guardarArchivoDeLayout(String datos, String ruta, String nombre) {
		almacenArchivo(ruta, datos.getBytes(Charset.defaultCharset()), ruta+nombre);
	}

	@Override
	public String generaRutaArchivo(String anio, String usuario, String numeroSolicitud) {
		return "/" + anio + "/" + usuario + "/" + numeroSolicitud;
	}

	public ByteArrayOutputStream generarZip(List<String> srcFiles) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ZipOutputStream zipOut = new ZipOutputStream(bos);
			for (String srcFile : srcFiles) {
				File fileToZip = new File(srcFile);

				if (!fileToZip.exists()) {
					System.out.println(fileToZip.getAbsolutePath() + " no encontrado");
				}else {
				FileInputStream fis = new FileInputStream(fileToZip);
				ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
				zipOut.putNextEntry(zipEntry);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zipOut.write(bytes, 0, length);
				}
				fis.close();
				}
			}
			zipOut.close();
			bos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bos;
	}

	@Override
	public byte[] obtenerArchivosSolicitudZip(List<ComprobanteViaticoEntity> comprobantes) {
		
		String rutaFinalArchivos = "";
		if ("qas".equals(ambiente)) {
			rutaFinalArchivos = rutaArchivosQas;// QAS
		} else if ("pro".equals(ambiente)) {
			rutaFinalArchivos = rutaArchivos;// PRO
		}
		
		List<String> srcFiles = new ArrayList<>();
		for (ComprobanteViaticoEntity comp : comprobantes) {
			if (!comp.getRutaXml().equals("")) {
				srcFiles.add(rutaFinalArchivos + "/" + comp.getRutaXml());
			}
			srcFiles.add(rutaFinalArchivos + "/" + comp.getRutaPdf());
		}
		return generarZip(srcFiles).toByteArray();
	}
	
	

}
