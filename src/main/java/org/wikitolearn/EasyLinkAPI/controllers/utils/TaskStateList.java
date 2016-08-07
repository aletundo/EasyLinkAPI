package org.wikitolearn.EasyLinkAPI.controllers.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;

/**
 * Created by alessandro on 07/08/16.
 */
public class TaskStateList extends TaskStateAbstract<List<EasyLinkBean>>{
    private List<EasyLinkBean> results;
    public TaskStateList(UUID id, ExecutorService executorService){
        super(id, executorService);
        this.results = new ArrayList<>();
    };

    public void setResults(List<EasyLinkBean> results){
        this.results = results;
    }

    public List<EasyLinkBean> getResults(){
        return this.results;
    }
}
