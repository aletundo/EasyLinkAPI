package org.wikitolearn.EasyLinkAPI.controllers.utils;

import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelDomain;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.data.BabelSenseSource;
import it.uniroma1.lcl.jlt.util.Language;
import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;
import org.wikitolearn.EasyLinkAPI.models.Gloss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alessandro on 07/08/16.
 */
public class AnalyzeTopCallable extends AnalyzeCallable<List<EasyLinkBean>> {
    public AnalyzeTopCallable(Language language, String inputText, String scoredCandidates, int threshold, String domain, TaskStateAbstract t) {
        super(language, inputText, scoredCandidates, threshold, domain, t);
    }

    @Override
    public List<EasyLinkBean> buildResults(String inputText) {
        List<EasyLinkBean> resultsList = new ArrayList<>();
        BabelNet bn = BabelNet.getInstance();
        String cleanText = CleanInputText.clean(inputText);
        List<SemanticAnnotation> bfyAnnotations = getAnnotations(cleanText);
        for (SemanticAnnotation annotation : bfyAnnotations) {
            BabelSynset syns = bn.getSynset(new BabelSynsetID(annotation.getBabelSynsetID()));
            if (BabelPOS.NOUN.equals(syns.getPOS()) || BabelPOS.ADJECTIVE.equals(syns.getPOS())) {
                EasyLinkBean e = new EasyLinkBean();
                String frag = cleanText.substring(annotation.getCharOffsetFragment().getStart(),
                        annotation.getCharOffsetFragment().getEnd() + 1);
                e.setBabelnetId(syns.getId().getID());
                e.setTitle(frag);
                e.setBabelLink("http://babelnet.org/synset?word=" + annotation.getBabelSynsetID() + "&lang=" + getLanguage().getName());
                List<BabelSense> wikiSenses = syns.getSenses(getLanguage(), BabelSenseSource.WIKI);
                if(wikiSenses != null && !wikiSenses.isEmpty()){
                    e.setWikiLink(
                            "https://" + getLanguage().toString().toLowerCase() + ".wikipedia.org/wiki/" + wikiSenses.get(0).getLemma());
                }
                getTaskState().setProgress(50);
                // Get WikipediaCategories
                //List<BabelCategory> categories = syns.getCategories(getLanguage()s.get(lang));

                // Get BabelDomains and their scores
                HashMap<BabelDomain, Double> domains = syns.getDomains();

                if (!domains.isEmpty()) {
                    Map.Entry<BabelDomain, Double> entry = domains.entrySet().iterator().next();
                    e.setBabelDomain(entry.getKey().getDomainString());
                }
                // Get glosses
                List<BabelGloss> glosses = syns.getGlosses(getLanguage());

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

                printResult(getLanguage(), frag, annotation, syns, domains, glosses);

                if (e.getTitle() != null && e.getGloss() != null) {
                    if (getDomain() == null || getDomain().trim().equals("") || getDomain().equals("ALL") || getDomain().equalsIgnoreCase(e.getBabelDomain()))
                        resultsList.add(e);
                }
            }
        }
        getTaskState().setStatus("Completed");
        getTaskState().setProgress(100);
        return resultsList;
    }

    @Override
    public List<EasyLinkBean> call() throws Exception {
        List<EasyLinkBean> results = buildResults(getInputText());
        getTaskState().setResults(results);
        return results;
    }
}
