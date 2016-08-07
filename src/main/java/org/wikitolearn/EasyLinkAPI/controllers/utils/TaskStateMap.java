package org.wikitolearn.EasyLinkAPI.controllers.utils;

import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Created by alessandro on 07/08/16.
 */
public class TaskStateMap extends TaskStateAbstract<Map<String,List<EasyLinkBean>>> {

    private Map<String, List<EasyLinkBean>> results;
    public TaskStateMap(UUID id, ExecutorService executorService){
        super(id, executorService);
        this.results = new HashMap<>();
    };

    public void setResults(Map<String, List<EasyLinkBean>> results){
        this.results = results;
    }

    public Map<String, List<EasyLinkBean>> getResults(){
        return this.results;
    }
}
