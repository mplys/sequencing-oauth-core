package com.sequencing.oauth2demo.core;

import com.sequencing.oauth2demo.exception.NonAuthorizedException;

/**
 * Interface that defines two methods to download files from sequencing.com 
 */
public interface SequencingFileMetadataApi {
	/**
	 * Returns sample files from sequencing.com
	 * @return String json of file content
	 */
	public String getSampleFiles() throws NonAuthorizedException;

	/**
	 * Returns own files from sequencing.com
	 * @return String json of file content
	 */
	public String getOwnFiles() throws NonAuthorizedException;
}
