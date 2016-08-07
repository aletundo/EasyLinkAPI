package org.wikitolearn.EasyLinkAPI.controllers;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import it.uniroma1.lcl.babelfy.commons.BabelfyParameters;
import it.uniroma1.lcl.jlt.util.Language;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.wikitolearn.EasyLinkAPI.controllers.utils.*;

@Singleton
@Path("/analyze")
public class AnalyzeController {

	@Context
	private ServletContext application;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Test EasyLinkAPI, @GET works!";
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String analyze(@FormParam("wikitext") String inputText, @FormParam("scoredCandidates") String scoredCandidates, @FormParam("threshold") int threshold, @FormParam("babelDomain") String babelDomain, @FormParam("language") String language, @Context UriInfo ui) throws URISyntaxException {
        UUID requestId = UUID.randomUUID();

        System.out.println("Scored:" + scoredCandidates);
        System.out.println("Threshold:" + threshold);
        System.out.println("Language: " + language);
        @SuppressWarnings("unchecked")
        Map<UUID, TaskStateAbstract> activeThreads = (HashMap<UUID, TaskStateAbstract>) application
                .getAttribute("activeThreads");
        Map<String, Language> languages = (HashMap<String, Language>) application.getAttribute("languages");

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(requestId.toString()).build();
        TaskStateAbstract taskState;
        AnalyzeCallable analyzeCallable;

        ExecutorService e = Executors.newSingleThreadExecutor(threadFactory);
        if(BabelfyParameters.ScoredCandidates.TOP.name().equals(scoredCandidates)){
            taskState = new TaskStateList(requestId, e);
            analyzeCallable = new AnalyzeTopCallable(languages.get(language), inputText, scoredCandidates, threshold, babelDomain, taskState);
        }else{
            taskState = new TaskStateMap(requestId, e);
            analyzeCallable = new AnalyzeAllCallable(languages.get(language), inputText, scoredCandidates, threshold, babelDomain, taskState);
        }
        activeThreads.put(requestId, taskState);
        e.submit(analyzeCallable);
		/*
		 * Future<List<EasyLinkBean>> f = e.submit(ac); List<EasyLinkBean>
		 * results = new ArrayList<>(); try { results = f.get(); } catch
		 * (InterruptedException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } catch (ExecutionException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * e.shutdown();
		 */
        return requestId.toString();
    }
}
