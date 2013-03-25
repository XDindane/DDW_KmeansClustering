
package kkmeansproject;

import kkmeansproject.keyword.KeywordExtractionImpl;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KkmeansProject {

    public static String html, response;

    @SuppressWarnings({"ConvertToTryWithResources", "element-type-mismatch"})

   
    
    String StartClustering(String URL1, String URL2) {       
                                   
        System.setProperty("gate.home", "gate/");
response = "\n";
response += "Keyword Extraction from URL instantiated object\n";
      try {
           KeywordExtraction kex = new KeywordExtractionImpl(); 
            Vector dataPoints = new Vector();

            String[] doc = {
                URL1,
                URL2
            };

            for (int i = 0; i < doc.length; i++) {  // i indexes each element successively.
                System.out.println(doc[i]);
                WebFile file = null;
                try {
                    file = new WebFile(doc[i]);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(KkmeansProject.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Nevalidní URL. vložte validní URL");
                    response += "Nevalidní URL. vložte validní URL\n";
                } catch (IOException ex) {
                    Logger.getLogger(KkmeansProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                 Logger.getLogger(KkmeansProject.class.getName()).log(Level.SEVERE, null, ex);
                response += "URL Musí být HTTP protocol";
                }
                @SuppressWarnings({"null", "ConstantConditions"})
                String MIME = file.getMIMEType();
                Object content = file.getContent();
                if (MIME.equals("text/html") && content instanceof String) {
                    html = (String) content;

                    System.out.println("Keyword Extraction from URL instantiated object");
                    
                    
                    Map<String, Double> KeyWord = kex.getKeywordsFromText(html);
                    Set<Map.Entry<String, Double>> set = KeyWord.entrySet();
                    for (Map.Entry<String, Double> me : set) {
                       /* System.out.print(me.getKey() + ": ");
                        System.out.println(me.getValue());*/

                        dataPoints.add(new DataPoint(me.getValue(), me.getValue(), doc[i] + "  -- " + me.getKey()));
                    }
                    System.out.println("Document size:" + kex.getInputDocumentSize());
                    response += "Document size:" + kex.getInputDocumentSize()+ "\n";
                    System.out.println("Lexicon size:" + kex.getInputLexiconSize());
                    response += "Lexicon size:"  + kex.getInputLexiconSize()+ "\n";
                    System.out.println("Language:" + kex.getInputLanguage());/**/
                    response += "Language:" + kex.getInputLanguage()+ "\n";
                }
            }
            System.out.println("-------------------- K Means Clustering WebSites  -------------------------");
             response += "-------------------- K Means Clustering WebSites  -------------------------\n";
            JCA jca = new JCA(doc.length, 1000, dataPoints);
            jca.startAnalysis();

            Vector[] v = jca.getClusterOutput();
            for (int x = 0; x < v.length; x++) {
                Vector tempV = v[x];
                System.out.println("-----------Cluster" + x + "---------");
                response += "-----------Cluster" + x + "---------\n";
                Iterator iter = tempV.iterator();
                while (iter.hasNext()) {
                    DataPoint dpTemp = (DataPoint) iter.next();
                // System.out.println(dpTemp.getObjName() + "[" + dpTemp.getX() + "," + dpTemp.getY() + "]");
                    response += dpTemp.getObjName() + " [" + String.valueOf(dpTemp.getX()) + "," + String.valueOf(dpTemp.getY()) + "]\n";
                }
            }

        } catch (Exception exn) {
            exn.printStackTrace();
        }
        return response;
}
}



