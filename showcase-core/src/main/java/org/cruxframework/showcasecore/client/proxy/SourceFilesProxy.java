package org.cruxframework.showcasecore.client.proxy;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.rest.RestProxy;
import org.cruxframework.crux.core.client.rest.RestProxy.TargetEndPoint;
import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.Path;
import org.cruxframework.crux.core.shared.rest.annotation.PathParam;
import org.cruxframework.showcasecore.client.dto.GitResponse;
import org.cruxframework.showcasecore.client.util.LoadingCallback;

@TargetEndPoint("https://api.github.com/repos/CruxFramework/crux-samples-showcase/contents/")
@Path("cross-device-showcase/")
public interface SourceFilesProxy extends RestProxy
{
	@GET
	@Path("src/main/java/org/cruxframework/crossdeviceshowcase/client/controller/samples/{viewName}")
	void listControllerFiles(@PathParam("viewName") String viewName, LoadingCallback<ArrayList<GitResponse>> listControllerFiles);
	
	@GET
	@Path("src/main/resources/org/cruxframework/crossdeviceshowcase/client/view/samples/{viewName}")
	void listViewFiles(@PathParam("viewName") String viewName, LoadingCallback<ArrayList<GitResponse>> listViewFiles);
	
	@GET
	@Path("src/main/java/org/cruxframework/showcasecore/server/samples/{viewName}")
	void listServerFiles(@PathParam("viewName") String viewName, LoadingCallback<ArrayList<GitResponse>> listServerFiles);
}