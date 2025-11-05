package com.viaticos;

import org.springframework.http.MediaType;


public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int status;
	private Object entity;
	private String mediaType;

	public ApiException(int status, Object entity) {
		super("(" + status + ") : " + entity);
		this.status = status;
		this.entity = entity;
		this.mediaType = MediaType.TEXT_PLAIN_VALUE;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

}
