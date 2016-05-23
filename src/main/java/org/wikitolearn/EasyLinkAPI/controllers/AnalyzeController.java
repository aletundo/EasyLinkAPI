package org.wikitolearn.EasyLinkAPI.controllers;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
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

import it.uniroma1.lcl.babelfy.commons.BabelfyConfiguration;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.jlt.Configuration;
import it.uniroma1.lcl.jlt.util.Language;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.wikitolearn.EasyLinkAPI.controllers.utils.AnalyzeCallable;
import org.wikitolearn.EasyLinkAPI.controllers.utils.ThreadProgress;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

@Singleton
@Path("/analyze")
public class AnalyzeController {

	@Context
	private ServletContext application;

	private Detector detector;
	private Map<String, Language> languages = new HashMap<>();

	@PostConstruct
	public void init() {
		Configuration jltConfiguration = Configuration.getInstance();
		jltConfiguration
				.setConfigurationFile(new File(application.getRealPath("/") + "/WEB-INF/config/jlt.properties"));
		BabelNetConfiguration bnconf = BabelNetConfiguration.getInstance();
		bnconf.setConfigurationFile(new File(application.getRealPath("/") + "/WEB-INF/config/babelnet.properties"));
		bnconf.setBasePath(application.getRealPath("/") + "/WEB-INF/");
		BabelfyConfiguration.getInstance()
				.setConfigurationFile(new File(application.getRealPath("/") + "/WEB-INF/config/babelfy.properties"));

		try {
			DetectorFactory.clear();
			DetectorFactory.loadProfile(application.getRealPath("/") + "/WEB-INF/resources/profiles");
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		languages.put("it", Language.IT);
		languages.put("en", Language.EN);
		languages.put("fr", Language.FR);
		languages.put("es", Language.ES);
		languages.put("de", Language.DE);

		application.setAttribute("ActiveThreads", new HashMap<UUID, ThreadProgress>());
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Test EasyLinkAPI, @GET works!";
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String analyze(@FormParam("wikitext") String inputText, @FormParam("scoredCandidates") String scoredCandidates, @FormParam("threshold") int threshold, @Context UriInfo ui) throws URISyntaxException {
		UUID requestId = UUID.randomUUID();
		
		System.out.println("Scored:" + scoredCandidates);
		System.out.println("Threshold:" + threshold);

		@SuppressWarnings("unchecked")
		Map<UUID, ThreadProgress> activeThreads = (HashMap<UUID, ThreadProgress>) application
				.getAttribute("ActiveThreads");
		ThreadProgress t = new ThreadProgress(requestId);
		activeThreads.put(requestId, t);

		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(requestId.toString()).build();

		ExecutorService e = Executors.newSingleThreadExecutor(threadFactory);
		AnalyzeCallable ac = new AnalyzeCallable(detector, languages, inputText, scoredCandidates, threshold, t);
		e.submit(ac);
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

		// return results;

	}

	/*
	 * @POST
	 * 
	 * @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * @ManagedAsync public void analyze(@FormParam("wikitext") String
	 * inputText, @Suspended final AsyncResponse asyncResponse) {
	 * List<EasyLinkBean> results = getAndProcessAnnotations(inputText);
	 * asyncResponse.resume(results); }
	 */

	/*
	 * @POST
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public List<EasyLinkBean>
	 * analyze(@FormParam("wikitext") String inputText) { List<EasyLinkBean>
	 * results = getAndProcessAnnotations(inputText); return results; }
	 */

	/*
	 * private List<EasyLinkBean> getAndProcessAnnotations(String inputText){
	 * 
	 * List<EasyLinkBean> resultsList = new ArrayList<>();
	 * 
	 * String cleanText = CleanInputText.removeMath(inputText); String lang =
	 * "";
	 * 
	 * try { detector = DetectorFactory.create(); detector.append(cleanText);
	 * lang = detector.detect(); //System.out.println(lang); } catch
	 * (LangDetectException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); }
	 * 
	 * BabelNet bn = BabelNet.getInstance(); Babelfy bfy = new
	 * Babelfy(setBabelfyParameters());
	 * 
	 * List<SemanticAnnotation> bfyAnnotations = bfy.babelfy(cleanText,
	 * languages.get(lang));
	 * 
	 * for (SemanticAnnotation annotation : bfyAnnotations) { BabelSynset syns =
	 * bn.getSynset(new BabelSynsetID(annotation.getBabelSynsetID())); if
	 * (BabelPOS.NOUN.equals(syns.getPOS()) ||
	 * BabelPOS.ADJECTIVE.equals(syns.getPOS())) { EasyLinkBean e = new
	 * EasyLinkBean(); String frag =
	 * cleanText.substring(annotation.getCharOffsetFragment().getStart(),
	 * annotation.getCharOffsetFragment().getEnd() + 1); e.setName(frag);
	 * e.setBabelLink("http://babelnet.org/synset?word=" +
	 * annotation.getBabelSynsetID() + "&lang=" + languages.get(lang));
	 * e.setWikiLink( "https://" + languages.get(lang).toString().toLowerCase()
	 * + ".wikipedia.org/wiki/" + frag);
	 * 
	 * // Get WikipediaCategories List<BabelCategory> categories =
	 * syns.getCategories(languages.get(lang));
	 * 
	 * // Get BabelDomains and their scores HashMap<BabelDomain, Double> domains
	 * = syns.getDomains();
	 * 
	 * if (!domains.isEmpty()) { Map.Entry<BabelDomain, Double> entry =
	 * domains.entrySet().iterator().next();
	 * e.setBabelDomain(entry.getKey().getDomainString()); }
	 * 
	 * // Get glosses List<BabelGloss> glosses =
	 * syns.getGlosses(languages.get(lang));
	 * 
	 * if (!glosses.isEmpty()) { e.setGloss(glosses.get(0).getGloss());
	 * e.setGlossSource(glosses.get(0).getSource().getSourceName()); }
	 * 
	 * printResult(lang, frag, annotation, syns, categories, domains, glosses);
	 * 
	 * if (e.getName() != null && e.getGloss() != null) { resultsList.add(e); }
	 * } } return resultsList;
	 * 
	 * }
	 * 
	 * private void printResult(String lang, String frag, SemanticAnnotation
	 * annotation, BabelSynset syns, List<BabelCategory> categories,
	 * HashMap<BabelDomain, Double> domains, List<BabelGloss> glosses) {
	 * 
	 * System.out.println(frag + "\t BabelSynset ID: " +
	 * annotation.getBabelSynsetID()); System.out.println("\t Part Of Speech: "
	 * + syns.getPOS()); System.out.println("\t Synset type: " +
	 * syns.getSynsetType()); System.out.println("\t Main sense (IT): " +
	 * syns.getMainSense(languages.get(lang))); System.out.println(
	 * "\t BabelNet URL: " + annotation.getBabelNetURL()); System.out.println(
	 * "\t DBPedia URL: " + annotation.getDBpediaURL()); System.out.println(
	 * "\t Source: " + annotation.getSource()); // Score to measure the level of
	 * connectedness of the // disambiguated BabelSynset in context
	 * System.out.println("\t Coherence score: " +
	 * annotation.getCoherenceScore()); // A score that represent the level of
	 * relevance of this // annotation within the whole document
	 * System.out.println("\t Global score: " + annotation.getGlobalScore()); //
	 * The general disambiguation score associated with the // annotation
	 * System.out.println("\t Score: " + annotation.getScore());
	 * 
	 * for (BabelCategory cat : categories) { System.out.println("\t Category: "
	 * + cat.getCategory()); }
	 * 
	 * for (Entry<BabelDomain, Double> domain : domains.entrySet()) {
	 * System.out.println("\t BabelNet Domain: " +
	 * domain.getKey().getDomainString()); System.out.println(
	 * "\t BabelNet Domain Score: " + domain.getValue()); }
	 * 
	 * for (BabelGloss g : glosses) { System.out.println("\t Gloss: " +
	 * g.getGloss()); System.out.println("\t Gloss source:" +
	 * g.getSource().getSourceName()); } }
	 * 
	 * private BabelfyParameters setBabelfyParameters(){ BabelfyParameters bp =
	 * new BabelfyParameters(); // extends the candidates sets with the
	 * aida_means relations from YAGO bp.setExtendCandidatesWithAIDAmeans(true);
	 * bp.setScoredCandidates(ScoredCandidates.TOP); // Secondo me l'exact
	 * matching non funziona, bisogna controllare bene
	 * bp.setMatchingType(MatchingType.EXACT_MATCHING);
	 * 
	 * Most Common Sense (MCS) backoff strategy that returns the most common
	 * sense for the text fragment when the system does not have enough
	 * information to select a meaning
	 * 
	 * bp.setMCS(MCS.ON); // Se imposto il Threshold non ottengo gli score!
	 * Perchè?!?! // bp.setThreshold(90.0);
	 * 
	 * return bp; }
	 */
}
