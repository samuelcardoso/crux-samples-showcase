package org.cruxframework.showcasecore.client.dto;

import java.io.Serializable;

public class GitResponse implements Serializable
{
	private static final long serialVersionUID = -3533860001019825051L;
	
	private String name;
	private String path;

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
