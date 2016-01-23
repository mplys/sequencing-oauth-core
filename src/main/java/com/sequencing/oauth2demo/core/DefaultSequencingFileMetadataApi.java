package com.sequencing.oauth2demo.core;

import java.util.HashMap;
import java.util.Map;

import com.sequencing.oauth2demo.exception.NonAuthorizedException;
import com.sequencing.oauth2demo.helper.HttpHelper;

public class DefaultSequencingFileMetadataApi implements SequencingFileMetadataApi
{
	private SequencingOAuth2Client client;

	public DefaultSequencingFileMetadataApi(SequencingOAuth2Client client) {
		this.client = client;
	}
	
	@Override
	public String getSampleFiles() throws NonAuthorizedException
	{
        return getFilesByType("sample");
    }
	
	@Override
	public String getOwnFiles() throws NonAuthorizedException
	{
        return getFilesByType("uploaded");
    }
	
	/**
	 * Returns files depending on file type
	 */
	private String getFilesByType(String fileType) throws NonAuthorizedException
	{
		if (client.isAuthorized() == false) {
            throw new NonAuthorizedException();
        }
		
        String uri = String.format("%s/DataSourceList?%s=true&shared=true", client.getAuthenticationParameters().getApiUri(), fileType);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + client.getToken().getAccessToken());

        String result = HttpHelper.doGet(uri, headers);
        return result;
	}
}
