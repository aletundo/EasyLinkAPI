package org.wikitolearn.EasyLinkAPI.models;

public class EasyLinkBean {
	private String id;
	private String title;
	private String gloss;
	private String language;
	private String babelLink;
	private String wikiLink;
	private String dbPediaLink;
	private String babelDomain;
	private String glossSource;
	
	public EasyLinkBean(){
		
	}
	
	/**
	 * @return the name
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param name the name to set
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
