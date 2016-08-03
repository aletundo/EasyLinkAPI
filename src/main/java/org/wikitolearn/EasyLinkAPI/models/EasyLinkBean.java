package org.wikitolearn.EasyLinkAPI.models;

import java.util.List;

public class EasyLinkBean {
	private String babelnetId;
	private String title;
	private String gloss;
	private String language;
	private String babelLink;
	private String wikiLink;
	private String dbPediaLink;
	private String babelDomain;
	private String glossSource;
    private List<Gloss> glosses;
	
	public EasyLinkBean(){
		
	}

    public List<Gloss> getGlosses() {
        return glosses;
    }

    public void setGlosses(List<Gloss> glosses) {
        this.glosses = glosses;
    }
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the gloss
	 */
	public String getGloss() {
		return gloss;
	}
	/**
	 * @param gloss the gloss to set
	 */
	public void setGloss(String gloss) {
		this.gloss = gloss;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the babelLink
	 */
	public String getBabelLink() {
		return babelLink;
	}
	/**
	 * @param babelLink the babelLink to set
	 */
	public void setBabelLink(String babelLink) {
		this.babelLink = babelLink;
	}
	/**
	 * @return the wikiLink
	 */
	public String getWikiLink() {
		return wikiLink;
	}
	/**
	 * @param wikiLink the wikiLink to set
	 */
	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}
	/**
	 * @return the dbPediaLink
	 */
	public String getDbPediaLink() {
		return dbPediaLink;
	}
	/**
	 * @param dbPediaLink the dbPediaLink to set
	 */
	public void setDbPediaLink(String dbPediaLink) {
		this.dbPediaLink = dbPediaLink;
	}
	/**
	 * @return the babelDomain
	 */
	public String getBabelDomain() {
		return babelDomain;
	}
	/**
	 * @param babelDomain the babelDomain to set
	 */
	public void setBabelDomain(String babelDomain) {
		this.babelDomain = babelDomain;
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

	/**
	 * @return the balenetId
	 */
	public String getBabelnetId() {
		return babelnetId;
	}

	/**
	 * @param babelnetId the babelnetId to set
	 */
	public void setBabelnetId(String babelnetId) {
		this.babelnetId = babelnetId;
	}
}
