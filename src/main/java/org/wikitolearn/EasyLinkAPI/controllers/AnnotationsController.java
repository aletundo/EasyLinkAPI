package org.wikitolearn.EasyLinkAPI.controllers;

import javax.print.Doc;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteRequestBuilder;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.operation.AggregateOperation;
import org.bson.BSONObject;
import org.bson.Document;
import org.wikitolearn.EasyLinkAPI.models.EasyLinkBean;
import org.wikitolearn.EasyLinkAPI.utils.MongoConnection;

import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

@Path("/annotation")
public class AnnotationsController {

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
        System.out.println("the user name " + username);
        if(username == null || "null".equals(username)){
            username = "notLogged";
        }
        Document documentToStore = Document.parse(annotation);
        String gloss = (String) documentToStore.get("gloss");
        String glossSource = (String) documentToStore.get("glossSource");
        documentToStore.remove("gloss");
        documentToStore.remove("glossSource");
        System.out.println(documentToStore);
        MongoCollection collection = MongoConnection.getInstance().connect();
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
}
