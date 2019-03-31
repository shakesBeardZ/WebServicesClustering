package wsproject;

//Authors:
//Nikita  Tribhuvan (nst2785)
//Gagan Govindapla Ravi (gxg3042)
//Trisha Malhotra (tpm6421)

//package wsproject;

import com.predic8.schema.*;
import com.predic8.wsdl.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import org.json.simple.parser.*;


public class WSProject {

public static void main(String[] args) throws Exception {

   WSDLParser parser = new WSDLParser();
   

  HashMap<String, Set> serviceNameDict = new HashMap<String, Set>();  
   HashMap<String, Set> messageDict = new HashMap<String, Set>();
   HashMap<String, Set> portTypeDict = new HashMap<String, Set>();
   HashMap<String, Set> complexTypeDict = new HashMap<String, Set>();
   
   File folder = new File("WS-dataset/Mix");
   File[] listOfFiles = folder.listFiles();
   
   ArrayList<String> wsdl_link = new ArrayList<String>();
   
   
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
          if(!listOfFiles[i].getName().equals(".DS_Store"))
              wsdl_link.add("WS-dataset/Mix/"+listOfFiles[i].getName());
//        System.out.println("WS-dataset/Activities/" + listOfFiles[i].getName());
        
      } else if (listOfFiles[i].isDirectory()) {
        System.out.println("Directory " + listOfFiles[i].getName());
      }
    }
  
    System.out.println(wsdl_link);
   String[] wsdl_links = wsdl_link.toArray(new String[wsdl_link.size()]);
   
   int wsdllinks_size = wsdl_links.length;

   double[][] similarity = new double[wsdllinks_size][wsdllinks_size];
   
   
   
//   Locale, Http, Soap, Update, Servers, Smallest, Name, Str, Copyright, Physical, Last, Version, To, Sub, Load, Creation, Expiration, Local, Out, Now, Get, Country, Save, Domain, Current, Warning, Message, In, Symbol, Legal, Time, Information, Source, T, U, Since, Regime, Convert, Post, Num, Custom, Change, Admin, Exists, boolean, No, In, Post, Http, Soap, Correct, Names_v2, Full, Out, Names, Full_v2, Validate, Get, Fast_v2, Fast, No, Result_v2, Correct, Names_v2, Full, Response, Full_v2, Fast_v2, Fast
   
   Set<String> ignoreWords = new HashSet<String>(Arrays.asList("In","No", "Soap","Http","Post","Get","Update","Servers","Of", "Response","To","String"));
   Set<String> URIs;
   String WS1;
   String WS2;
   Set<String> servicename;
   Set<String> messages;
   Set<String> portTypes;
   Set<String> complexType;
   String[] splitStringArray;
   String splitString = null;
   
   for (int i = 0 ; i <wsdllinks_size ; i++){ 
	   WS1 = wsdl_links[i];
       
    for (int j = i+1 ; j < wsdllinks_size; j++){

         WS2 = wsdl_links[j];
         

   URIs = new HashSet<String>();
   URIs.add(WS1);
   URIs.add(WS2);

   for( String URI: URIs){
   servicename = new HashSet<String>();
   messages = new HashSet<String>();
   portTypes = new HashSet<String>();
   complexType = new HashSet<String>();

       Definitions defs = parser.parse(URI);
       System.out.println("--------------URI --------------");
       System.out.println(URI);

       System.out.println("--------------Service Name --------------");
       for (Service service : defs.getServices()) {
           splitString = service.getName().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
           System.out.println("  Service Name: \t" + splitString);
           String[] split = splitString.split("\\s+");
        
           for (String split1 : split) {
           if (!ignoreWords.contains(split1) && (split1.length() != 1))
                servicename.add(split1);
        }
         
           serviceNameDict.put(URI, servicename);
           System.out.println("Service name Set: \t" + servicename);
       }

       System.out.println("-------------Messages------------");
       
       for (Message msg : defs.getMessages()) {
           splitStringArray = msg.getName().split("(?<!^)(?=[A-Z])");
           for (String word : splitStringArray){
               if (!ignoreWords.contains(word) && (word.length() != 1))
                   messages.add(word);
           }

       }
       System.out.println("Messages Set: \t" + messages);
       messageDict.put(URI, messages);



       System.out.println("-----------PortTypes-----------");
       for (PortType pt : defs.getPortTypes()) {
           String newS = pt.getName();
           if (newS.contains("US")) portTypes.add("US");
           String[] Porttypewords = newS.split("((?<!^)(?=[A-Z]))|US");
           for (String word : Porttypewords){
               if (!word.equals(""))  
                if (!ignoreWords.contains(word)  && (word.length() != 1))
                    portTypes.add(word);
           }
       }
       System.out.println("Port Type Set: " + portTypes);
       portTypeDict.put(URI, portTypes);

       System.out.println("-------------- Complextypes Details --------------");
       Set<String> set = new HashSet<String>();
       Set<String> Elementtypeset = new HashSet<String>();
       for (Schema schema : defs.getSchemas()) {
    	   if (schema == null) continue;
           for (Element element :  schema.getAllElements()) {

           String[] Complextypewords = element.getName().split("(?<!^)(?=[A-Z])");
           for (String word : Complextypewords){
                 if (!ignoreWords.contains(word)  && (word.length() != 1))
                     complexType.add(word);
           }
           
           if (element == null) continue;
           if (element.getType() == null) continue;
//           System.out.println("blah  "+element.getType());
           JSONParser p = new JSONParser();
           if (element.getAsJson() == null) continue;
           String eleStr = element.getAsJson().toString();
           Reader eleReader = new StringReader(eleStr);
           JSONObject json = new JSONObject(p.parse(eleReader));

           if (!json.has("string") && !json.has("double") &&
        		   !json.has("boolean") && !json.has("long") &&
        		   !json.has("int") && !json.has("decimal") &&
        		   !json.has("dateTime")
        		   )
           {
        	
            JSONObject elementName = null;

//           JSONObject elementName = (JSONObject) json.get(element.getName().toString());
               if(json.has(element.getName().toString())){
        	 elementName = (JSONObject)json.get(element.getName().toString());
               }
              

           if (elementName == null) continue;
           
           set=  (Set<String>) elementName.keys();

           for (String s : set) {

             String line = s;
             String[] words = line.split("(?<!^)(?=[A-Z])");
             for (String word : words){
                 if (!ignoreWords.contains(word)  && (word.length() != 1))
                     Elementtypeset.add(word);
             }

           }
           }   
           if (!ignoreWords.contains(Elementtypeset)  && (Elementtypeset.size()!= 1))
                complexType.addAll(Elementtypeset);
           }
           System.out.println("Complex Type Set:     "+ complexType);      
       }
       complexTypeDict.put(URI, complexType);
       System.out.println("\n");
   }    
   System.out.println("Service name Dictionary:     "+ serviceNameDict);
   System.out.println("Message Dictionary:     "+ messageDict);   
   System.out.println("Port Type Dictionary:     "+ portTypeDict);   
   System.out.println("ComplexType Dictionary:     "+ complexTypeDict);


   System.out.println("**************************************************************************** \n");
   System.out.println("Similarity of "+WS1 + " and "+ WS2);
   int countMessage = 0;
   for (Object word: messageDict.get(WS1) ){
       if(messageDict.get(WS2).contains(word))
           countMessage++;
   }
   double averageMessage = (messageDict.get(WS1).size() + messageDict.get(WS2).size()) / 2;

   double matchMessage = (countMessage) / averageMessage ;


   System.out.println("Message Match:" + matchMessage);

   int countPort = 0;
   for (Object word: portTypeDict.get(WS1) ){
       if(portTypeDict.get(WS2).contains(word))
       {
           countPort++;
//            System.out.println(countPort);
       }
   }
   double averagePort = (portTypeDict.get(WS1).size() + portTypeDict.get(WS2).size()) / 2;
   double matchPort = (countPort) / averagePort ;

   System.out.println("Port Match:" + matchPort);



   int countType = 0;
   for (Object word: complexTypeDict.get(WS1) ){
       if(complexTypeDict.get(WS2).contains((String)word) == true)
       {
           countType++;
       }
   }
   double averageType = (complexTypeDict.get(WS1).size() + complexTypeDict.get(WS2).size()) / 2;

   double matchType = (countType) / averageType ;

   System.out.println("Complex Type Match:" + matchType);
   
   double totalNGD = 0;
   int count = 0;
   for (Object word1: serviceNameDict.get(WS1) ){
       for (Object word2: serviceNameDict.get(WS2) )
       {
//           totalNGD += NGD(word1.toString(), word2.toString());
           count++;
           System.out.println("total"+totalNGD+"count"+count);
       }
   }
   double serviceSim = 1 - (totalNGD / count); 
   System.out.println("Service name similarity:" + serviceSim);
   
   
   int countName = 0;
   for (Object word: serviceNameDict.get(WS1) ){
       if(serviceNameDict.get(WS2).contains((String)word) == true)
       {
           countName++;
       }
   }
   double averageName = (serviceNameDict.get(WS1).size() + serviceNameDict.get(WS2).size()) / 2;

   double matchName = (countType) / averageName ;

   System.out.println("Service Name Match:" + matchName);


   double total = 0.2*(matchName + matchMessage + matchPort + matchType);


   System.out.println("****************************************************************************");

   
   if( (i == j) || (j == i)){
        similarity[i][j] = 1.0;
        similarity[j][i] = 1.0;
   }
   else{
       similarity[i][j] = Math.round(total * 100.0) / 100.0;
       similarity[j][i] = Math.round(total * 100.0) / 100.0;
   }
   

        }
   }
           
   System.out.println("Sum of match index for message, portTtype and complexType between each pair of Web services");   
       //print matrix function
       
   for (double[] similarity1 : similarity) {
           for (int j = 0; j < similarity1.length; j++) {
               System.out.print(similarity1[j] + "\t");
           }
           System.out.println();
       }
   
ArrayList<Double> list = new ArrayList<Double>();
    for (int i = 0; i < similarity.length; i++) {
           for (int j = i+1; j < similarity.length; j++) { 
                  list.add(similarity[i][j]);
           }
       }
    System.out.println("-------------------"+list);

   }
   

     public static double NGD(String term1, String term2) { 
        Long M = 10000000000L;
        double freqx = logResults(term1); 
        double freqy = logResults(term2); 
        String xy = term1.concat("+").concat(term2); 
        double freqxy = logResults(xy); 
        if (freqx == Double.NEGATIVE_INFINITY || freqy == Double.NEGATIVE_INFINITY) { //deal with zero results = infinite logarithms 
            return 1;//return 1 by definition 
        } else { 
            double num = Math.max(freqx, freqy) - freqxy; 
            double den = Math.log10(M) - Math.min(freqx, freqy); 
 
            double formula = num / den; 
            return formula; 
        } 
    } 
    
     public static double logResults(String term) { 
        return Math.log10(makeQuery(term)); 
 
    } 
     
     private static long makeQuery(String query) { 
 
        try { 
            long total = 0;
                    
            query = URLEncoder.encode(query, "UTF-8"); 
 
            URL url = new URL("https://www.googleapis.com/customsearch/v1?key=AIzaSyDmmofEyGEd3QGKdbjSRmTfeEdanKiCheE&cx=017576662512468239146:omuauf_lfve&q=" + query); 
            URLConnection connection = url.openConnection(); 
             
            String line; 
            StringBuilder builder = new StringBuilder(); 
            BufferedReader reader = new BufferedReader( 
                    new InputStreamReader(connection.getInputStream())); 
            while ((line = reader.readLine()) != null) { 
                builder.append(line); 
            } 
 
            String response = builder.toString();
            JSONObject rootObject = new JSONObject(response); 
            JSONObject queries = rootObject.getJSONObject("queries");
            JSONArray request = queries.getJSONArray("request"); 
                
            for(int j=0; j < request.length(); j++) { 
                JSONObject row = request.getJSONObject(j);
                
                if (row.has("totalResults")) { 
                    total = row.getLong("totalResults");
                }
                else{
                    total = 0; 
                }
        }
            
            return total;
            
        } catch (Exception e) { 
            System.err.println("Something went wrong..."); 
            e.printStackTrace(); 
            long results = -1; 
            return results; 
        } 
    } 

}