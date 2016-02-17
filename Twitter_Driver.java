//Miss Galanos
//version 12.8.2015

import twitter4j.*;       //set the classpath to lib\twitter4j-core-4.0.2.jar
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

public class Twitter_Driver
{
   private static PrintStream consolePrint;
   
   public static void main (String []args) throws TwitterException, IOException, InterruptedException
   {
      consolePrint = System.out;
      
      // PART 1
      // set up classpath and properties file
             
      TJTwitter bigBird = new TJTwitter(consolePrint);
         
      //create message to tweet, then call the tweetOut method
      // bigBird.tweetOut("test"); //I just tweeted from my Java program! #APCSRocks @TJColonials Thanks @cscheerleader!");
     
      // PART 2
      // Choose a public Twitter user's handle 
         
       Scanner scan = new Scanner(System.in);
       consolePrint.print("Please enter a Twitter handle, do not include the @symbol (type done to quit)--> "); //console
       String twitter_handle = scan.next();
      // if(twitter_handle.equals("done"))
      //   System.exit(0);
         
      while (!twitter_handle.equals("done"))
      {
         // Print the most popular word they tweet
         bigBird.makeSortedListOfWordsFromTweets(twitter_handle);
         consolePrint.println("The most common word from @" + twitter_handle + " is: " + bigBird.mostPopularWord());
         consolePrint.println();
         consolePrint.print("Please enter a Twitter handle, do not include the @ symbol (type done to quite)--> ");
         twitter_handle = scan.next();
      }
         
      // PART 3
      bigBird.investigate();
         
         
   }//end main         
         
}//end driver        
         
class TJTwitter 
{
   private Twitter twitter;
   private PrintStream consolePrint;
   private List<Status> statuses;
   private List<String> sortedTerms;
   
   public TJTwitter(PrintStream console)
   {
      // Makes an instance of Twitter - this is re-useable and thread safe.
      twitter = TwitterFactory.getSingleton(); //connects to Twitter and performs authorizations
      consolePrint = console;
      statuses = new ArrayList<Status>();
      sortedTerms = new ArrayList<String>();   
   }
   
   /******************  Part 1 *******************/
   public void tweetOut(String message) throws TwitterException, IOException
   {
      twitter.updateStatus(message);
   }
   @SuppressWarnings("unchecked")
   /******************  Part 2 *******************/
   public void makeSortedListOfWordsFromTweets(String handle) throws TwitterException, IOException
   {
      statuses.clear();
      sortedTerms.clear();
      PrintStream fileout = new PrintStream(new FileOutputStream("tweets.txt")); // Creates file for dedebugging purposes
      Paging page = new Paging (1,200);
      int p = 1;
      while (p <= 10)
      {
         page.setPage(p);
         statuses.addAll(twitter.getUserTimeline(handle,page)); 
         p++;        
      }
      int numberTweets = statuses.size();
      fileout.println("Number of tweets = " + numberTweets);
      
      fileout = new PrintStream(new FileOutputStream("garbageOutput.txt"));
   
      int count=1;
      for (Status j: statuses)
      {
         fileout.println(count+".  "+j.getText());
         count++;
      }		
         	
     	//Makes a list of words from user timeline
      for (Status status : statuses)
      {			
         String[]array = status.getText().split(" ");
         for (String word : array)
            sortedTerms.add(removePunctuation(word).toLowerCase());
      }	
   					
      // Remove common English words, which are stored in commonWords.txt
      sortedTerms = removeCommonEnglishWords(sortedTerms);
      sortAndRemoveEmpties();
      
   }
   
   // Sort words in alpha order. You should use your old code from the Sort/Search unit.
   // Remove all empty strings ""
   @SuppressWarnings("unchecked")
   private void sortAndRemoveEmpties()
   {
     int i = 0;
     while(i < sortedTerms.size())
     {
         if(sortedTerms.get(i).equals(""))
           sortedTerms.remove(i);
         else
           i++;
     }
     Selection.sort(sortedTerms);
   }
   
   // Removes common English words from list
   // Remove all words found in commonWords.txt  from the argument list.
   // The count will not be given in commonWords.txt. You must count the number of words in this method.
   // This method should NOT throw an exception. Use try/catch.
    @SuppressWarnings("unchecked")
   private List removeCommonEnglishWords (List<String> list)
   {	
      int i = 0;
      int count = 0; //91
      
      try{
         Scanner infile = new Scanner(new File("cw.txt"));
         while(infile.hasNext())
         {
            infile.next();
            count++;
         }
      }
      catch (FileNotFoundException e)
      {
         System.out.println("Not Found");
         System.exit(0);  
      }
      String[] commons = new String[count];
      try{
      
      Scanner infile2 = new Scanner(new File("cw.txt"));
      for(int b = 0; b < commons.length; b++)
         {
            commons[b] = infile2.next();
         }
      }
      catch (FileNotFoundException e) { System.exit(0);}
      while(i < list.size())
      {
      String s = list.get(i);
      s.replace(" ", "");
      s.toLowerCase();
      
      String[] punct = commons;
         int testerthis = -1;
            for(int k = 0; k < punct.length; k++)
            {
               if(s.equals(punct[k]))
               {
                  testerthis = i;
                  break;
               }
                 if(testerthis != -1)
                  break;
               
            }
            if(testerthis != -1)
            {
              list.remove(i);
            }
            else
              i++;
            
            testerthis = -1;
      }
      return list;
   }
   //Remove punctuation - borrowed from prevoius lab
   //Consider if you want to remove the # or @ from your words. They could be interesting to keep (or remove).
   private String removePunctuation( String s )
   {
      s.replace(" ", "");
      s.toLowerCase();
      if(s.length() != 0 && (s.substring(0,1).contains("#") || s.substring(0,1).contains("@")))
        return "";
      String[] punct = {"~","!","@","#","$","%","^","&", "*","(",")","-","_","+","=","\"",">","<","?", ";", ",", ".", ":","--", "'"};
         String returnthis = "";
         int testerthis = -1;
         for(int i = 0; i<s.length(); i++)
         {
            for(int k = 0; k < punct.length; k++)
            {
               if(s.substring(i, i+1).equals(punct[k]))
               {
                  testerthis = i;
                  break;
               }
                 if(testerthis != -1)
                  break;  
            }
            if(testerthis == -1)
            returnthis += s.substring(i, i+1);
            
            testerthis = -1;
         }
         return returnthis;
      //return "";
   }
   //Should return the most common word from sortedTerms. 
   //Consider case. Should it be case sensitive? The choice is yours.
    @SuppressWarnings("unchecked")
   public String mostPopularWord()
   {
      int count = 0;
      int max = 0;
      String common = "";
      String before = sortedTerms.get(0);
      for(int i = 0; i < sortedTerms.size(); i++)
      {
         if(sortedTerms.get(i).equals(before))
         {
            count++;
            if(count > max)
            {
            max = count;
            common = sortedTerms.get(i);
            }
         }
         else 
         {
            if(count > max)
            {
            max = count;
            common = before;
            }

            count = 0; 
            before = sortedTerms.get(i);
         }   
      }
      return common;
   }
   /******************  Part 3 *******************/
   //Will retweet all tweets that say giveawaay, set on a timer to do so every hour or until you reach a tweet you have already retweeted
   // or if you have reached your limit on tweets //
   public void investigate () throws TwitterException, InterruptedException
   {
    while(true)
    {
      Query query = new Query("giveaways");
      query.setCount(100);
      Twitter twitter = new TwitterFactory().getInstance();
      QueryResult found = twitter.search(query);
      query = found.nextQuery();
      List<Status> tweets = found.getTweets();
      int size = tweets.size();
      if(5 < size)
       size = 5;
      for(int i = 0; i < size; i++)
      {
         twitter.retweetStatus(tweets.get(i).getId());
      }
     
       Thread.sleep(60*60*1000);
      }
   }
   // A sample query to determine how many people in Arlington, VA tweet about the Miami Dolphins
   public void sampleInvestigate ()
   {
      Query query = new Query("Miami Dolphins");
      query.setCount(100);
      query.setGeoCode(new GeoLocation(38.8372839,-77.1082443), 5, Query.MILES);
      query.setSince("2015-12-1");
      try {
         QueryResult result = twitter.search(query);
         System.out.println("Count : " + result.getTweets().size()) ;
         for (Status tweet : result.getTweets()) {
            System.out.println("@"+tweet.getUser().getName()+ ": " + tweet.getText());  
         }
      } 
      catch (TwitterException e) {
         e.printStackTrace();
      } 
      System.out.println(); 
   }  
   
}  

// Consider adding a sorter class here.

class Selection
{
   public static void sort(List<String> array)
   { 
      int count = array.size()-1;
      for(int i = 0; i < array.size(); i++)
      {
         
         swap(array, findMax(array,count), count);
         count--;
      }
      
   }
   private static int findMax(List<String> array, int n)
   {
      int pos = 0;
      String max = array.get(0);
      for(int i = 0; i <= n; i++)
      {
         if(array.get(i).compareTo(max) > 0)
         {
            max = array.get(i);
            pos = i;
         }
      }
      return pos;
      //return (int) max;
   }
   private static void swap(List<String> array, int a, int b)
   {
      String temp =  array.get(a);
      array.set(a, array.get(b));
      array.set(b, temp);

   }
   	/***************************************************
   	  for Strings
   	  ***********************************************/
   public static void sort(String[] array)
   {
      int count = array.length-1;
      for(int i = 0; i < array.length; i++)
      {
         
         swap(array, findMax(array,count), count);
         count--;
      }
       
   }
   public static int findMax(String[] array, int upper)
   {
      String temp = array[0];
      int pos = 0;
      for(int i = 0; i <= upper; i++)
      {
         if(array[i].compareTo(temp) > 0)
         {
          temp = array[i];
          pos = i;
         }
      }
      return pos;
   }
   public static void swap(String[] array, int a, int b)
   {
      String temp = array[a];
      array[a] = array[b];
      array[b] = temp;
   }
   	/***************************************************
   	 for Comparables,
   	      Swap() is for Objects.
   	      make sure that print() is for Objects, too.
   	  ***********************************************/
   @SuppressWarnings("unchecked")//this removes the warning for Comparable
       public static void sort(Comparable[] array)
   {
      int pos = findMax(array, array.length);
      swap(array, pos, array.length-1);
      
      Comparable[] newarray = new Comparable[array.length-1];
      for(int i = 0; i < newarray.length; i++)
      {
         newarray[i] = array[i];
      }
      sort(newarray);
   }
   @SuppressWarnings("unchecked")
       public static int findMax(Comparable[] array, int upper)
   {
      Comparable temp = array[0];
      int pos = 0;
      for(int i = 0; i < upper; i++)
      {
         if(array[i].compareTo(temp) > 0)
         {
          temp = array[i];
          pos = i;
         }
      }
      return pos;

   }
   public static void swap(Object[] array, int a, int b)
   {
      Object temp = array[a];
      array[a] = array[b];
      array[b] = temp;

   }
}   
