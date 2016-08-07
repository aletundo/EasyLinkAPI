package org.wikitolearn.EasyLinkAPI.controllers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
import it.uniroma1.lcl.jlt.util.Language;
import org.bson.Document;
import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;
import org.wikitolearn.EasyLinkAPI.models.Gloss;
import org.wikitolearn.EasyLinkAPI.utils.MongoConnection;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

@Path("/annotation")
public class AnnotationsController {

    @GET
    @Path("{babelnetId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Gloss> getMoreGlosses(@PathParam("babelnetId") String babelnetId){
        BabelNet bn = BabelNet.getInstance();
        BabelSynset syns = bn.getSynset(new BabelSynsetID(babelnetId));
        // Get glosses
        List<BabelGloss> glosses = syns.getGlosses(Language.IT);
        List<Gloss> glossesToAdd = new ArrayList<>();
        if (!glosses.isEmpty()) {
            for(BabelGloss gloss : glosses) {
                Gloss g = new Gloss();
                g.setGloss(gloss.getGloss());
                g.setGlossSource(gloss.getSource().getSourceName());
                glossesToAdd.add(g);
            }
        }
        return glossesToAdd;
    }

    @GET
    @Path("{babelnetId}/{glossSource}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String getAnnotations(@PathParam("babelnetId") String babelnetId, @PathParam("glossSource") String glossSource) {
        MongoCollection collection = MongoConnection.getInstance().connect();
        //List<Document> foundDocuments2 = (List<Document>) collection.aggregate(Arrays.<Document>asList(new Document("$match", new Document("babelnetId", babelnetId)), new Document("$unwind", "$glosses"), new Document("$sort", new Document("glosses.addedOn", -1)))).into(new ArrayList<Document>());
        List<Document> foundDocuments = (List<Document>) collection.find(new Document("babelnetId", babelnetId).append("glosses", new Document("$elemMatch", new Document("glossSource", glossSource)))).into(new ArrayList<Document>());
        //MongoConnection.getInstance().disconnect();
        return foundDocuments.get(0).toJson();
    }

    @POST
    public String storeAnnotation(@FormParam("annotation") String annotation, @FormParam("username") String username, @FormParam("pageName") String pageName) {
        if(username == null || "null".equals(username) || "".equals(username)) {
            username = "notLogged";
        }
        //Gson gson = new Gson();
        MongoCollection collection = MongoConnection.getInstance().connect();
        /*List<WriteModel<Document>> updates = null;
        EasyLinkBean e = gson.fromJson(annotation, EasyLinkBean.class);
        String selectedGlossSource = e.getGlossSource();
        if("WikiToLearn".equals(selectedGlossSource)){
            updates = Arrays.<WriteModel<Document>>asList(
                    new UpdateOneModel<Document>(
                            buildDocument(e),
                            new Document("$push", new Document("glosses", new Document("$each", Arrays.<Document>asList(new Document("gloss", e.getGloss()).append("glossSource", e.getGlossSource()).append("username", username).append("addedOn", new Date()))).append("$sort", new Document("addedOn", -1)))),
                            new UpdateOptions().upsert(true)
                    )
            );
        }*/
        Document documentToStore = Document.parse(annotation);
        String gloss = (String) documentToStore.get("gloss");
        String glossSource = (String) documentToStore.get("glossSource");
        documentToStore.remove("gloss");
        documentToStore.remove("glossSource");
        documentToStore.remove("glosses");
        System.out.println(documentToStore);
        List<WriteModel<Document>> updates = null;
        if("WikiToLearn".equals(glossSource)){
            updates = Arrays.<WriteModel<Document>>asList(
                    new UpdateOneModel<Document>(
                            documentToStore,
                            new Document("$push", new Document("glosses", new Document("$each", Arrays.<Document>asList(new Document("gloss", gloss).append("glossSource", glossSource).append("username", username).append("addedOn", new Date()))).append("$sort", new Document("addedOn", -1)))),
                            new UpdateOptions().upsert(true)
                    )
            );
        }else{
            updates = Arrays.<WriteModel<Document>>asList(
                    new UpdateOneModel<Document>(
                            documentToStore,
                            new Document("$addToSet", new Document("glosses", new Document("gloss", gloss).append("glossSource", glossSource))),
                            new UpdateOptions().upsert(true)
                    )
            );
        }
        BulkWriteResult bulkWriteResult = collection.bulkWrite(updates);
        System.out.println(bulkWriteResult.toString());
        return "ok";
    }

    private Document buildDocument(EasyLinkBean bean){
        Document document = new Document();
        List<Document> glossesList = new ArrayList<>();
        document.append("babelnetId", bean.getBabelnetId()).append("title", bean.getTitle()).append("babelLink", bean.getBabelLink());
        if(!"".equals(bean.getWikiLink())) {
            document.append("wikiLink", bean.getWikiLink());
        }
        for(int index = 0; index < bean.getGlosses().size(); index++){
            if(bean.getGloss().equals(bean.getGlosses().get(index).getGloss()) || bean.getGlossSource().equals(bean.getGlosses().get(index).getGlossSource())){
                bean.getGlosses().remove(index);
            }else{
                glossesList.add(new Document("gloss", bean.getGlosses().get(index).getGloss()).append("glossSource", bean.getGlosses().get(index).getGlossSource()));
            }
        }
        document.append("glosses", glossesList);
        System.out.println(document);
        return  document;
    }
}
