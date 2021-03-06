package org.wikitolearn.EasyLinkAPI.controllers.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;

public abstract class TaskStateAbstract<T> {
	private UUID id;
	private String status;
	private float progress;
	private ExecutorService executorService;

	public TaskStateAbstract(UUID id, ExecutorService executorService) {
		this.executorService = executorService;
		this.status = "Pending";
		this.progress = 0;
		this.id = id;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the progress
	 */
	public float getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(float progress) {
		this.progress = progress;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the results
	 */
	public abstract T getResults();

	/**
	 * the results to set
	 */
	public abstract void setResults(T results);

}
