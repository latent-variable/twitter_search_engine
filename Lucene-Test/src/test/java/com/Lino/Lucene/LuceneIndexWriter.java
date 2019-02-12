package com.Lino.Lucene;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.tools.javac.util.List;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;



public class LuceneIndexWriter {

    String indexPath = "";
    String jsonFilePath = "";
    public LuceneIndexWriter(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }

    public void createIndex()  throws IOException, ParseException{
    	
        addDocuments();
    } 
    public void addDocuments() throws IOException, ParseException{

    	//**************************************************************************
    	//Read File line by line 
    	// Requires normal Json format i.e. every json tweet is separated by "\n"
    	//**************************************************************************
    	ArrayList<Object> list = new ArrayList<Object>();
    	File file = new File(jsonFilePath);
    	LineIterator it = FileUtils.lineIterator(file, "UTF-8"); 
    	int total_count = 0;
		long startTime = System.nanoTime();
    	
    	try {		 
    	    while (it.hasNext()) {
    	    	//add json to the list
    	    	String line = it.nextLine();
    	        Object fileObject = JSONValue.parse(line);
    	        list.add(fileObject);	
    	        total_count ++;
    	        
    	        //when the list is to big index it 
    	        if(list.size() > 10000 ) {
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
    			    	    JSONArray hasharr = (JSONArray)((JSONObject)tweet.get("entities")).get("hashtags");
    			    	    String hashtags = "";
    			    	    if(hasharr != null) {
    			    	    	for (Object a: hasharr) {
    			    	    		String hashtag = (String)((JSONObject)a).get("text");
    				    	    	//System.out.println(hashtag);
    			    	    		hashtags += hashtag + " ";
    			    	    	}
    			    	    }

    			    	    String name = (String)((JSONObject)tweet.get("user")).get("name");
    			    	    String screen_name = (String)((JSONObject)tweet.get("user")).get("screen_name");
    			    	    String location = (String)((JSONObject)tweet.get("user")).get("location");
    			    	    
    			    	    doc.add(new TextField("content", text, Field.Store.YES));
    			    	    doc.add(new TextField("hashtag", hashtags, Field.Store.YES));
    			    	    doc.add(new TextField("name", name, Field.Store.YES));
    			    	    doc.add(new TextField("screen_name", screen_name, Field.Store.YES));
    			    	    doc.add(new TextField("location", location, Field.Store.YES));
    			    	    
    			    	    indexWriter.addDocument(doc);
    			        }
    			        
    	        	}   	    
    	        	indexWriter.commit();
    	        	indexWriter.close();
    	    		System.out.println((System.nanoTime() - startTime) / 1000000000.0);
    		    	
    	        	
    	        	//-------------------------Do the same except for white space analyzer 
    	    	    analyzer = new WhitespaceAnalyzer();
    		    	config = new IndexWriterConfig(analyzer);
    		    	IndexWriter indexWriter2 = new IndexWriter(dir, config);
    		    	
    		    	for (Object o : list)
    	        	{
    			        if(o != null) {
    			        	Document doc = new Document();
    			    	    JSONObject tweet = (JSONObject) o;
    			    	    String text = (String) tweet.get("text");
    			    	    //System.out.println(text);  			
    			    	    JSONArray hasharr = (JSONArray)((JSONObject)tweet.get("entities")).get("hashtags");
    			    	    String hashtags = "";
    			    	    if(hasharr != null) {
    			    	    	for (Object a: hasharr) {
    			    	    		String hashtag = (String)((JSONObject)a).get("text");
    				    	    	//System.out.println(hashtag);
    			    	    		hashtags += hashtag + " ";
    			    	    	}
    			    	    }

    			    	    String name = (String)((JSONObject)tweet.get("user")).get("name");
    			    	    String screen_name = (String)((JSONObject)tweet.get("user")).get("screen_name");
    			    	    String location = (String)((JSONObject)tweet.get("user")).get("location");
    			    	    
    			    	    doc.add(new TextField("content", text, Field.Store.YES));
    			    	    doc.add(new TextField("hashtag", hashtags, Field.Store.YES));
    			    	    doc.add(new TextField("name", name, Field.Store.YES));
    			    	    doc.add(new TextField("screen_name", screen_name, Field.Store.YES));
    			    	    doc.add(new TextField("location", location, Field.Store.YES)); 
    			    	    
    			    	    indexWriter2.addDocument(doc);
    			        }
    			        
    	        	}
    	        	indexWriter2.commit();
    	        	indexWriter2.close();
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
		    	    JSONArray hasharr = (JSONArray)((JSONObject)tweet.get("entities")).get("hashtags");
		    	    String hashtags = "";
		    	    if(hasharr != null) {
		    	    	for (Object a: hasharr) {
		    	    		String hashtag = (String)((JSONObject)a).get("text");
			    	    	//System.out.println(hashtag);
		    	    		hashtags += hashtag + " ";
		    	    	}
		    	    }

		    	    String name = (String)((JSONObject)tweet.get("user")).get("name");
		    	    String screen_name = (String)((JSONObject)tweet.get("user")).get("screen_name");
		    	    String location = (String)((JSONObject)tweet.get("user")).get("location");
		    	    
		    	    doc.add(new TextField("content", text, Field.Store.YES));
		    	    doc.add(new TextField("hashtag", hashtags, Field.Store.YES));
		    	    doc.add(new TextField("name", name, Field.Store.YES));
		    	    doc.add(new TextField("screen_name", screen_name, Field.Store.YES));
		    	    doc.add(new TextField("location", location, Field.Store.YES));
		        }
		        
        	}   
        	indexWriter.commit();
        	indexWriter.close();	        	
        	
        	//-------------------------Do the same except for white space analyzer 
    	    analyzer = new WhitespaceAnalyzer();
	    	config = new IndexWriterConfig(analyzer);
	    	IndexWriter indexWriter2 = new IndexWriter(dir, config);
	    	
	    	for (Object o : list)
        	{
		        if(o != null) {
		        	Document doc = new Document();
		    	    JSONObject tweet = (JSONObject) o;
		    	    String text = (String) tweet.get("text");
		    	    //System.out.println(text);  			
		    	    JSONArray hasharr = (JSONArray)((JSONObject)tweet.get("entities")).get("hashtags");
		    	    String hashtags = "";
		    	    if(hasharr != null) {
		    	    	for (Object a: hasharr) {
		    	    		String hashtag = (String)((JSONObject)a).get("text");
			    	    	//System.out.println(hashtag);
		    	    		hashtags += hashtag + " ";
		    	    	}
		    	    }

		    	    String name = (String)((JSONObject)tweet.get("user")).get("name");
		    	    String screen_name = (String)((JSONObject)tweet.get("user")).get("screen_name");
		    	    String location = (String)((JSONObject)tweet.get("user")).get("location");
		    	    
		    	    doc.add(new TextField("content", text, Field.Store.YES));
		    	    doc.add(new TextField("hashtag", hashtags, Field.Store.YES));
		    	    doc.add(new TextField("name", name, Field.Store.YES));
		    	    doc.add(new TextField("screen_name", screen_name, Field.Store.YES));
		    	    doc.add(new TextField("location", location, Field.Store.YES)); 
		    	    
		    	    indexWriter2.addDocument(doc);
		        }
		        
        	}
        	indexWriter2.commit();
        	indexWriter2.close();
        	list = new ArrayList<Object>();	//clear the contents of the list re-use memory

    	} 
    	finally {
    		System.out.println("Total count: " + total_count);
    	    LineIterator.closeQuietly(it);
    	}
    	
    	
    }
}