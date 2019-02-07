package com.Lino.Lucene;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
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
    	
        
        addDocuments();
       
    }
    
    public void addDocuments() throws IOException{
    	
    	File file = new File(jsonFilePath);
    	LineIterator it = FileUtils.lineIterator(file, "UTF-8");
    	try {
    		
 	        
    	    while (it.hasNext()) {
    	    	
    	    	Analyzer analyzer = new StandardAnalyzer();
    	    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	    	Directory dir = FSDirectory.open(Paths.get(indexPath));
    	    	IndexWriter indexWriter = new IndexWriter(dir, config);
    	    	
    	        String line = it.nextLine();
    	        // do something with line
    	      //parse JSON File
    	    	//Get the JSON file, in this case is in ~/resources/test.json
    	        //InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
    	        //Reader readerJson = new InputStreamReader(jsonFile);

    	        //Parse the json file using simple-json library
    	        //Object fileObjects= JSONValue.parse(readerJson);
    	        Object fileObjects= JSONValue.parse(line);
    	        //JSONArray arrayObjects=(JSONArray)fileObjects;
    	        
    	        if(fileObjects != null) {
    	        	Document doc = new Document();
    	    	    JSONObject tweet = (JSONObject) fileObjects;
    	    	    String text = (String) tweet.get("text");
    	    	    //System.out.println(text);
    	    	    
    	    	    //TODO: add other important content to the doc 
    	    	    
    	    	    doc.add(new TextField("content", text, Field.Store.YES));
    	    	    indexWriter.addDocument(doc);
    	        }
    	        	
    	    	
    	    	
//    	    	for (Object o : arrayObjects)
//    	    	{
//    	    		Document doc = new Document();
//    	    	    JSONObject tweet = (JSONObject) o;
//    	    	    String text = (String) tweet.get("text");
//    	    	    //System.out.println(text);
//    	    	    
//    	    	    //TODO: add other important content to the doc 
//    	    	    
//    	    	    doc.add(new TextField("content", text, Field.Store.YES));
//    	    	    indexWriter.addDocument(doc);
//    	    	   
//    	    	}
    	    	//indexWriter.optimize();
    	    	indexWriter.commit();
    	    	indexWriter.close();
    	    }
    	} finally {
    	    LineIterator.closeQuietly(it);
    	}
    	
    }
}