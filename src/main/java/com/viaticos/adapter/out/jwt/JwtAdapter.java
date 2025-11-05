package com.viaticos.adapter.out.jwt;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.GenerarTokenDeAutenticacionPort;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
public class JwtAdapter implements GenerarTokenDeAutenticacionPort{
	
	@Value("${jwt-secret-key}")
	private String jwtSecretKey;

	@Value("${jwt-id}")
	private String JwtId;
	
	private final String KEY = "p0RVi4";

	@Override
	public String generarTokenDeAutenticacion(String usuario) {
//		System.out.println("jwtSecretKey:"+KEY);
//		System.out.println("JwtId:"+JwtId);
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setId(JwtId)
				.setSubject(usuario)
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 3600000)) //38000000
				.signWith(SignatureAlgorithm.HS512,
						KEY.getBytes()).compact();

		return "Bearer " + token;
	}
	
//	private void parseJWT(String jwt) {
//	    //This line will throw an exception if it is not a signed JWS (as expected)
//	    Claims claims = Jwts.parser()         
//	       .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecretKey))
//	       .parseClaimsJws(jwt).getBody();
//	    System.out.println("ID: " + claims.getId());
//	    System.out.println("Subject: " + claims.getSubject());
//	    System.out.println("Issuer: " + claims.getIssuer());
//	    System.out.println("Expiration: " + claims.getExpiration());
//	}

}
