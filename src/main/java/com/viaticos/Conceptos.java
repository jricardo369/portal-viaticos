package com.viaticos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:conceptos-viaticos.properties")
public class Conceptos {
	
	@Value("${dispersar-concepto-tempus}")
	private String D003_700;
	
	@Value("${percepcion-concepto-tempus}")
	private String D050_701;
	
	@Value("${ajustes-anticipados-concepto-tempus}")
	private String D081_702;
	
	@Value("${descuento-comp-concepto-tempus}")
	private String D080_703;
	
	@Value("${reintegro-concepto-tempus}")
	private String D004_704;
	
	@Value("${gravado-concepto-tempus}")
	private String D050_705;
	
	@Value("${descuento-ord-concepto-tempus}")
	private String D004_706;
	

	public String D003_700() {
		return D003_700;
	}
	
	public String D050_701() {
		return D050_701;
	}

	public String D081_702() {
		return D081_702;
	}
	
	public String D080_703() {
		return D080_703;
	}
	
	public String D004_704() {
		return D004_704;
	}
	
	public String D050_705() {
		return D050_705;
	}
	
	public String D004_706() {
		return D004_706;
	}

}
