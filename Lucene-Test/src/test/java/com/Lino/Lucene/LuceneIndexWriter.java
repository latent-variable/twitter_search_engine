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

import com.sun.tools.javac.util.List;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;



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
    	
    	
    	//***************************
    	//Read File aLL at onces 
    	// Requires Json array format 
    	//****************************
    	
  
//       //parse JSON File
//    	//Get the JSON file, in this case is in ~/resources/test.json
//        InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
//        Reader readerJson = new InputStreamReader(jsonFile);
//        
//        //Parse the json file using simple-json library
//        Object fileObjects= JSONValue.parse(readerJson);
//        JSONArray arrayObjects=(JSONArray)fileObjects;
//        
//        
//        //indexing tools 
//        Analyzer analyzer = new StandardAnalyzer();
//    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
//    	Directory dir = FSDirectory.open(Paths.get(indexPath));
//    	IndexWriter indexWriter = new IndexWriter(dir, config);
//    	
//    	
//    	for (Object o : arrayObjects)
//    	{
//    		Document doc = new Document();
//    	    JSONObject tweet = (JSONObject) o;
//    	    String text = (String) tweet.get("text");
//    	    //System.out.println(text);
//    	    
//    	    //TODO: add other important content to the doc 
//    	    
//    	    doc.add(new TextField("content", text, Field.Store.YES));
//    	    indexWriter.addDocument(doc);
//    	   
//    	}
//    	indexWriter.commit();
//    	indexWriter.close();
//    	
    	//**************************************************************************
    	//Read File line by line 
    	// Requires normal Json format i.e. every json tweet is separated by "\n"
    	//**************************************************************************
    	ArrayList<Object> list = new ArrayList<Object>();
    	File file = new File(jsonFilePath);
    	LineIterator it = FileUtils.lineIterator(file, "UTF-8");   	
    	try {		 
    	    while (it.hasNext()) {
    	    	//add json to the list
    	    	String line = it.nextLine();
    	        Object fileObject= JSONValue.parse(line);
    	        list.add(fileObject);	
    	        
    	        //when the list is to big index it 
    	        if(list.size() > 100000 ) {
    	        	//indexing tools 
    	    	    Analyzer analyzer = new StandardAnalyzer();
    		    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    		    	Directory dir = FSDirectory.open(Paths.get(indexPath));
    		    	IndexWriter indexWriter = new IndexWriter(dir, config);
    		    	
    		    	for (Object o : list)
    	        	{
    			        if(o != null) {
    			        	Document doc = new Document();
    			    	    JSONObject tweet = (JSONObject) o;
    			    	    String text = (String) tweet.get("text");
    			    	    //System.out.println(text);
    			    	    
    			    	    //TODO: add other important content to the doc 
    			    	    
    			    	    doc.add(new TextField("content", text, Field.Store.YES));
    			    	    
    			    	    indexWriter.addDocument(doc);
    			        }
    	        	   
    	        	}
    	        	indexWriter.commit();
    	        	indexWriter.close();
    	        	list = new ArrayList<Object>();	//clear the contents of the list re-use memory 
    	        }
    	    }
    	    
    	    //At the end index all the remaining json tweets
    	    //indexing tools 
    	    Analyzer analyzer = new StandardAnalyzer();
	    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    	Directory dir = FSDirectory.open(Paths.get(indexPath));
	    	IndexWriter indexWriter = new IndexWriter(dir, config);
	    	for (Object o : list)
        	{
		        if(o != null) {
		        	Document doc = new Document();
		    	    JSONObject tweet = (JSONObject) o;
		    	    String text = (String) tweet.get("text");
		    	    //System.out.println(text);
		    	    
		    	    //TODO: add other important content to the doc 
		    	    
		    	    doc.add(new TextField("content", text, Field.Store.YES));
		    	    indexWriter.addDocument(doc);
		        }
        	   
        	}
        	indexWriter.commit();
        	indexWriter.close();
    	    
    	} finally {
    	    LineIterator.closeQuietly(it);
    	}
    	
    }
}