package com.viaticos.adapter.out.file;

import java.math.BigDecimal;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.viaticos.UtilidadesAdapter;

public class PdfUtilidad {

	public static PdfPCell cell(Object valor, Font font, int border, String align, String colorFondo) {

		String texto = "";
		texto = valor.toString();
		if (valor.getClass() == Integer.class) {
			texto = valor.toString();
		} else if (valor.getClass() == Float.class) {
			texto = valor.toString();
		} else if (valor.getClass() == BigDecimal.class) {
			texto = UtilidadesAdapter.formatNumber(valor);
		}

		PdfPCell cell = new PdfPCell(new Phrase(texto, font));

		if (align.equals("centro")) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		} else if (align.equals("derecha")) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		} else if (align.equals("izquierda")) {
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		}
		if (border == 0) {
			cell.setBorder(Rectangle.NO_BORDER);
		} else {
			cell.setBorder(Rectangle.BOX);
		}

		if (colorFondo.equals("gris"))
			cell.setBackgroundColor(BaseColor.GRAY);
		if (colorFondo.equals("blanco"))
			cell.setBackgroundColor(BaseColor.WHITE);

		return cell;
	}

}
