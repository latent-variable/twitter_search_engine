package com.Lino.Lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
    static final String JSON_FILE_PATH = "/home/onil/eclipse-workspace/Lucene-Test/src/test/resources/my_tweets2.json";
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
    
    public static void testQuery(String testquery) throws IOException, ParseException {
    	// Now search the index:
    	Analyzer analyzer = new StandardAnalyzer();
    	
    	Directory directory = FSDirectory.open(Paths.get(INDEX_PATH));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser("content", analyzer);

        Query query = parser.parse(testquery);
       
        System.out.println(query.toString());
        int topHitCount = 100;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;

        // Iterate through the results:
        for (int rank = 0; rank < hits.length; ++rank) {
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            System.out.println((rank + 1) + " (score:" + hits[rank].score + ") --> " + hitDoc.get("content"));
//             System.out.println(indexSearcher.explain(query, hits[rank].doc));
        }
        indexReader.close();
        directory.close();
    }
	public static void main(String[] args) throws IOException, ParseException {

		System.out.println("Test Index on file.json");
		testIndex();	
		testQuery("elon musk");		
		
    }

}
