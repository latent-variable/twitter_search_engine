package com.Lino.Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.nio.file.Paths;



/**
 * Created by suay on 5/13/14.
 */
public class LuceneIndexWriter {

    String indexPath = "";
    String jsonFilePath = "";
    public LuceneIndexWriter(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }

    public void createIndex() throws IOException{
    	
        JSONArray jsonObjects = parseJSONFile();
        addDocuments(jsonObjects);
       
    }
    public JSONArray parseJSONFile(){

        //Get the JSON file, in this case is in ~/resources/test.json
        InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        //Parse the json file using simple-json library
        Object fileObjects= JSONValue.parse(readerJson);
        JSONArray arrayObjects=(JSONArray)fileObjects;

        return arrayObjects;

    }
    public void addDocuments(JSONArray jsonObjects) throws IOException{
    	Analyzer analyzer = new StandardAnalyzer();
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	Directory dir = FSDirectory.open(Paths.get(indexPath));
        IndexWriter indexWriter = new IndexWriter(dir, config);
    	
    	for (Object o : jsonObjects)
    	{
    		Document doc = new Document();
    	    JSONObject tweet = (JSONObject) o;
    	    String text = (String) tweet.get("text");
    	    //System.out.println(text);
    	    
    	    //TODO: add other important content to the doc 
    	    
    	    doc.add(new TextField("content", text, Field.Store.YES));
    	    indexWriter.addDocument(doc);
    	}

    	indexWriter.commit();
    	indexWriter.close();
    }
}