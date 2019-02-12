package com.Lino.Lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import static org.junit.Assert.*;



public class Search {

	///improved_tweets.json"
    static final String INDEX_PATH = "indexDir";
    //Requires this format for going line by line 
    static final String JSON_FILE_PATH = "/home/onil/eclipse-workspace/Lucene-Test/src/test/resources/improved_tweets.json";
    //requires this format for reading file all at onces
    //static final String JSON_FILE_PATH = "/data02_aaron.json";
    
    public static void testIndex(){
        try {
        	LuceneIndexWriter lw = new LuceneIndexWriter(INDEX_PATH, JSON_FILE_PATH);
            lw.createIndex();
            //Check the index has been created successfully
//            Directory indexDirectory = FSDirectory.open(Paths.get(INDEX_PATH));
//            IndexReader indexReader = DirectoryReader.open(indexDirectory);
//            int numDocs = indexReader.numDocs();
//            //assertEquals(1,numDocs);
//            for ( int i = 0; i < numDocs; i++)
//            {
//                Document document = indexReader.document( i);
//                System.out.println( "d=" +document);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void testQuery(String testquery, String querytype) throws IOException, ParseException {
    	Analyzer analyzer = new StandardAnalyzer();    	
    	Directory directory = FSDirectory.open(Paths.get(INDEX_PATH));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser("content", analyzer);
        
        if(querytype == "hashtag") {
        	analyzer = new WhitespaceAnalyzer();
        	parser = new QueryParser("hashtag", analyzer);
        }
        else if(querytype == "location") {
        	analyzer = new WhitespaceAnalyzer();
        	parser = new QueryParser("location", analyzer);
        }
        else if(querytype == "name") {
        	analyzer = new WhitespaceAnalyzer();
        	parser = new QueryParser("name", analyzer);
        }
        else if(querytype == "screen_name") {
        	analyzer = new WhitespaceAnalyzer();
        	parser = new QueryParser("screen_name", analyzer);
        }

        Query query = parser.parse(testquery);
       
        System.out.println(query.toString());
        int topHitCount = 100;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;

        // Iterate through the results:
        for (int rank = 0; rank < hits.length; ++rank) {
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            System.out.println((rank + 1) + " (score:" + hits[rank].score + ") --> " 
            		+ hitDoc.get("screen_name") + " ("
            		+ hitDoc.get("name") + "): "
            		+ hitDoc.get("content") + " "
            		+ hitDoc.get("hashtags") + " ("
            		+ hitDoc.get("location") + ")");
            
//             System.out.println(indexSearcher.explain(query, hits[rank].doc));
        }
        indexReader.close();
        directory.close();
    }
	public static void main(String[] args) throws IOException, ParseException {
		long startTime = System.nanoTime();
		System.out.println("Test Index on file.json");
		testIndex();	
		System.out.println("Index time (seconds): " + (System.nanoTime() - startTime) / 1000000000.0);
		testQuery("elon musk", "text");
		testQuery("NBA", "hashtag");
		testQuery("Mexico", "location");
		System.out.println("Total run time (seconds): " + (System.nanoTime() - startTime) / 1000000000.0);
    }

}
