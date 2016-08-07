package org.wikitolearn.EasyLinkAPI.controllers.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.data.*;
import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;

import it.uniroma1.lcl.babelfy.commons.BabelfyParameters;
import it.uniroma1.lcl.babelfy.commons.BabelfyParameters.MCS;
import it.uniroma1.lcl.babelfy.commons.BabelfyParameters.MatchingType;
import it.uniroma1.lcl.babelfy.commons.BabelfyParameters.ScoredCandidates;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.jlt.util.Language;
import org.wikitolearn.EasyLinkAPI.models.Gloss;

public class AnalyzeCallable implements Callable<List<EasyLinkBean>> {

	private Language language;
	private String inputText;
	private String scoredCandidates;
	private int threshold;
    private String domain;
	private TaskStateAbstract taskStateAbstract;

	public AnalyzeCallable(Language language, String inputText, String scoredCandidates, int threshold, String domain, TaskStateAbstract t) {
        this.domain = domain;
        this.threshold = threshold;
		this.scoredCandidates = scoredCandidates;
		this.language = language;
		this.inputText = inputText;
		this.taskStateAbstract = t;
	}

	@Override
	public List<EasyLinkBean> call() throws Exception {
		List<EasyLinkBean> results = getAndProcessAnnotations(inputText);
		taskStateAbstract.setResults(results);
		return results;
	}

	private List<EasyLinkBean> getAndProcessAnnotations(String inputText) {
		taskStateAbstract.setStatus("Progress");
		taskStateAbstract.setProgress(10);
		List<EasyLinkBean> resultsList = new ArrayList<>();

		String cleanText = CleanInputText.clean(inputText);
		taskStateAbstract.setStatus("Progress");
		taskStateAbstract.setProgress(20);

		BabelNet bn = BabelNet.getInstance();
		Babelfy bfy = new Babelfy(setBabelfyParameters());
		taskStateAbstract.setStatus("Progress");
		taskStateAbstract.setProgress(30);
		List<SemanticAnnotation> bfyAnnotations = bfy.babelfy(cleanText, language);
		taskStateAbstract.setStatus("Progress");
		taskStateAbstract.setProgress(40);
		for (SemanticAnnotation annotation : bfyAnnotations) {
			BabelSynset syns = bn.getSynset(new BabelSynsetID(annotation.getBabelSynsetID()));
			if (BabelPOS.NOUN.equals(syns.getPOS()) || BabelPOS.ADJECTIVE.equals(syns.getPOS())) {
				EasyLinkBean e = new EasyLinkBean();
				String frag = cleanText.substring(annotation.getCharOffsetFragment().getStart(),
						annotation.getCharOffsetFragment().getEnd() + 1);
				e.setBabelnetId(syns.getId().getID());
				e.setTitle(frag);
				e.setBabelLink("http://babelnet.org/synset?word=" + annotation.getBabelSynsetID() + "&lang=" + language.getName());
				List<BabelSense> wikiSenses = syns.getSenses(language, BabelSenseSource.WIKI);
				if(wikiSenses != null && !wikiSenses.isEmpty()){
					e.setWikiLink(
							"https://" + language.toString().toLowerCase() + ".wikipedia.org/wiki/" + wikiSenses.get(0).getLemma());
				}
				taskStateAbstract.setStatus("Progress");
				taskStateAbstract.setProgress(50);
				// Get WikipediaCategories
				//List<BabelCategory> categories = syns.getCategories(languages.get(lang));

				// Get BabelDomains and their scores
				HashMap<BabelDomain, Double> domains = syns.getDomains();

				if (!domains.isEmpty()) {
					Map.Entry<BabelDomain, Double> entry = domains.entrySet().iterator().next();
					e.setBabelDomain(entry.getKey().getDomainString());
				}
				// Get glosses
				List<BabelGloss> glosses = syns.getGlosses(language);

				if (!glosses.isEmpty()) {
                    List<Gloss> glossesToAdd = new ArrayList<>();
					for(BabelGloss gloss : glosses){
                        Gloss g = new Gloss();
                        g.setGloss(gloss.getGloss());
                        g.setGlossSource(gloss.getSource().getSourceName());
                        glossesToAdd.add(g);
                    }
                    e.setGlosses(glossesToAdd);
					e.setGloss(glosses.get(0).getGloss());
					e.setGlossSource(glosses.get(0).getSource().getSourceName());
				}

				printResult(language, frag, annotation, syns, domains, glosses);

				if (e.getTitle() != null && e.getGloss() != null) {
                    if (domain == null || domain.trim().equals("") || domain.equals("ALL") || domain.equalsIgnoreCase(e.getBabelDomain()))
					    resultsList.add(e);
				}
			}
		}
		taskStateAbstract.setStatus("Completed");
		taskStateAbstract.setProgress(100);
		return resultsList;

	}

	private void printResult(Language language, String frag, SemanticAnnotation annotation, BabelSynset syns, HashMap<BabelDomain, Double> domains, List<BabelGloss> glosses) {

		System.out.println(frag + "\t BabelSynset ID: " + syns.getId().getID());
		System.out.println("\t Part Of Speech: " + syns.getPOS());
		System.out.println("\t Synset type: " + syns.getSynsetType());
		System.out.println("\t Main sense (IT): " + syns.getMainSense(language).getLemma());
		List<BabelSense> senses = syns.getSenses(language, BabelSenseSource.WIKI);
		if(!senses.isEmpty()) {
			for (BabelSense sense : senses) {
				System.out.println(sense);
			}
		}
		System.out.println("\t BabelNet URL: " + annotation.getBabelNetURL());
		System.out.println("\t DBPedia URL: " + annotation.getDBpediaURL());
		System.out.println("\t Source: " + annotation.getSource());
		// Score to measure the level of connectedness of the
		// disambiguated BabelSynset in context
		System.out.println("\t Coherence score: " + annotation.getCoherenceScore());
		// A score that represent the level of relevance of this
		// annotation within the whole document
		System.out.println("\t Global score: " + annotation.getGlobalScore());
		// The general disambiguation score associated with the
		// annotation
		System.out.println("\t Score: " + annotation.getScore());

		/*for (BabelCategory cat : categories) {
			System.out.println("\t Category: " + cat.getCategory());
		}*/

		for (Entry<BabelDomain, Double> domain : domains.entrySet()) {
			System.out.println("\t BabelNet Domain: " + domain.getKey().getDomainString());
			System.out.println("\t BabelNet Domain Score: " + domain.getValue());
		}
		System.out.println("\t Main Gloss: "  + syns.getMainGloss(language));
		for (BabelGloss g : glosses) {
			System.out.println("\t Gloss: " + g.getGloss());
			System.out.println("\t Gloss source:" + g.getSource().getSourceName());
		}
	}

	private BabelfyParameters setBabelfyParameters() {
		BabelfyParameters bp = new BabelfyParameters();
		// extends the candidates sets with the aida_means relations from YAGO
		bp.setExtendCandidatesWithAIDAmeans(true);
		bp.setThreshold(threshold);
		bp.setScoredCandidates(ScoredCandidates.valueOf(scoredCandidates));
		// Secondo me l'exact matching non funziona, bisogna controllare bene
		bp.setMatchingType(MatchingType.EXACT_MATCHING);
		/*
		 * Most Common Sense (MCS) backoff strategy that returns the most common
		 * sense for the text fragment when the system does not have enough
		 * information to select a meaning
		 */
		bp.setMCS(MCS.ON);
		return bp;
	}
}
