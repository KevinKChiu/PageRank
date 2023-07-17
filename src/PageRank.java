import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PageRank {
	
	// PageRank Implementation 
	public static Map<String, Double> pageRank(Map<String, Set<String>> graph, double lambda, double tau) {
		Map<String, Double> I = new HashMap<String, Double>(); 
		Map<String, Double> R = new HashMap<String, Double>(); 
		double norm = 0.0;
		
		for (Map.Entry<String, Set<String>> g : graph.entrySet()) {
			I.put(g.getKey(), 1.0 / graph.size());
		}
		while (!isConverged(I, R, tau, norm)) {
			double add = 0.0;
			for (Map.Entry<String, Set<String>> g : graph.entrySet()) {
				R.put(g.getKey(), lambda / graph.size());
			}
			for (Map.Entry<String, Set<String>> p : graph.entrySet()) {
				Set<String> Q = graph.get(p.getKey()); // g.getKey() is P
				for (String q : Q) {
					R.put(q, R.get(q) + ((1 - lambda) * I.get(p.getKey()) / Q.size()));
				}
				if (Q.size() == 0) {
					add += ((1 - lambda) * I.get(p.getKey()) / graph.size());
				}
			}	
			for (Map.Entry<String, Double> r : R.entrySet()) {
				R.put(r.getKey(), R.get(r.getKey()) + add);
			}
			norm = calc(I, R);
			for (Map.Entry<String, Double> r : R.entrySet()) {
				I.put(r.getKey(), R.get(r.getKey()));
			}
		}
		return R;
	}
	
	// Helper method to check for convergence
	public static boolean isConverged(Map<String, Double> I, Map<String, Double> R, double tau, double norm) {
		if (!R.isEmpty()) {
			return (norm < tau);
		} else {
			return false;
		}
	}
	
	// Helper method to calculate norm
	public static double calc(Map<String, Double> I, Map<String, Double> R) {
		double norm = 0.0;
		for (Map.Entry<String, Double> i : I.entrySet()) { 
			String key = i.getKey();
			norm +=  Math.abs(I.get(key) - R.get(key));
		}
		return norm;
	}
	
	// Helper method to get the top 75 pages in terms of PageRank (also from my Project 1)
	public static ArrayList<String> orderPages(Map<String, Double> ranks) {
		ArrayList<String> topPages = new ArrayList<String>();
		Map<String, Double> sortedMap = sortByValue1(ranks);
		int i = 1;
		for (Map.Entry<String, Double> page : sortedMap.entrySet()) {
			if (i <= 75) {
				topPages.add(page.getKey() + " " + i + " " + page.getValue());
				++i;
			}
		}
		return topPages;
	}
	
	// Helper method to get the top 75 pages in terms of inlinks (also from my Project 1)
	public static ArrayList<String> orderLinks(Map<String, Integer> inLinks) {
		ArrayList<String> topLinks = new ArrayList<String>();
		Map<String, Integer> sortedMap = sortByValue2(inLinks);
		int i = 1;
		for (Map.Entry<String, Integer> link : sortedMap.entrySet()) {
			if (i <= 75) {
				topLinks.add(link.getKey() + " " + i + " " + link.getValue());
				++i;
			}
		}
		return topLinks;
	}
	
	// Method to sort a Map by value for Map<String, Double>
	// From geeksforgeeks.org "Sorting a Hashmap according to values" (https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/)
    public static Map<String, Double> sortByValue1(Map<String, Double> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
               new LinkedList<Map.Entry<String, Double> >(hm.entrySet());
 
        // Sort the list
        Collections.sort(list, Collections.reverseOrder(new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        }));
         
        // put data from sorted list to hashmap
        Map<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
    
	// Method to sort a Map by value for Map<String, Integer>
	// From geeksforgeeks.org "Sorting a Hashmap according to values" (https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/)
    public static Map<String, Integer> sortByValue2(Map<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
               new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());
 
        // Sort the list
        Collections.sort(list, Collections.reverseOrder(new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        }));
         
        // put data from sorted list to hashmap
        Map<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
	
	public static void main(String[] args) throws IOException, FileNotFoundException {
		
		Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
		Map<String, Integer> inLinks = new HashMap<String, Integer>();
		double lambda = 0.15;
		double tau = 0.0001;
		
		// Reading in the file and adding entries into the map
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream("D:\\UMass Amherst Fall 2021\\Compsci 446\\Project 2\\links.srt.gz")), 
							"UTF-8"));
			String line;		
			while((line = br.readLine()) != null) {
				String[] toks = line.split("\t");
		        String source = toks[0];
		        String target = toks[1];
		        if (!graph.containsKey(source)) {
		        	graph.put(source, new HashSet<String>());
		        } 
		        if (!graph.containsKey(target)) {
		        	graph.put(target, new HashSet<String>());
		        }
		        graph.get(source).add(target);		        
		        inLinks.put(target, inLinks.getOrDefault(target, 0) + 1);
		      }
		   } catch (IOException ex) {
			   System.out.println("File error");
		   }
		
		ArrayList<String> ranks = orderPages(pageRank(graph, lambda, tau));
		ArrayList<String> links = orderLinks(inLinks);
	    
		// output the top 75 pages by PageRank in a .txt file
	    Path outputFile = Path.of("D:\\UMass Amherst Fall 2021\\Compsci 446\\Project 2\\pagerank.txt");
	    String str = "";
	    for (int i = 0; i < ranks.size(); i++) {
	    	if (str.equals("")) {
	    		str += ranks.get(i);
	    	} else {
	    		str += "\n" + ranks.get(i);
	    	}
	    	Files.writeString(outputFile, str);
	    }
	    
		// output the top 75 pages by inlinks in a .txt file
	    Path outputFile2 = Path.of("D:\\UMass Amherst Fall 2021\\Compsci 446\\Project 2\\inlinks.txt");
	    String str2 = "";
	    for (int i = 0; i < links.size(); i++) {
	    	if (str2.equals("")) {
	    		str2 += links.get(i);
	    	} else {
	    		str2 += "\n" + links.get(i);
	    	}
	    	Files.writeString(outputFile2, str2);
	    }
	}
}
