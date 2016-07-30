package org.wikitolearn.EasyLinkAPI.models;

/**
 * Created by alessandro on 30/07/16.
 */
public class Gloss{
    private String gloss;
    private String glossSource;

    public Gloss(){

    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    /**
     * @return the glossSource
     */
    public String getGlossSource() {
        return glossSource;
    }

    /**
     * @param glossSource the glossSource to set
     */
    public void setGlossSource(String glossSource) {
        this.glossSource = glossSource;
    }
}
