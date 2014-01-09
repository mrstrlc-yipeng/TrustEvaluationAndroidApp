package fr.utt.isi.tx.trustevaluationandroidapp.computingalgo;


public class MatchingAlgo {
	private static String compOne;
    private static String compTwo;
    private static int mRange = -1;
    private static int minSize; 
    private static int sizeStrOne;
    private static int sizeStrTwo;
    private static String theMatchA = null;
    private static String theMatchB = null;
    
	public static boolean isOnePerson (String s1, String s2) {
		boolean isOnePerson = false;	
		int match = 0;
		double score;
		
		String[] name1 = s1.split("\\s"); 
		String[] name2 = s2.split("\\s"); 
		
		// Number of sub-strings
		int strNb1 = name1.length;
		int strNb2 = name2.length;
		
		int minNb = Math.min(strNb1, strNb2);
						
		for (int i = 0; i < strNb1; i++) {
			double maxScore = 0;
			
			for (int j = 0; j < strNb2; j++) {
				score = getSimilarity (name1[i], name2[j]);
				//Log.d("MatchingAlgo",name1[i] + " " + name2[j] + " score " + score);
				if ( score > maxScore) {
					maxScore = score;
				}				
			}
			
			if (maxScore >= 0.9) {
				match++;
			}			
		}
		
		if (match == minNb) isOnePerson = true;
		
		return isOnePerson;
	}
	
	
    public static double getSimilarity (String s1, String s2)
    {
    	theMatchA = "";
    	theMatchB = "";
	    
        compOne = s1;
        compTwo = s2;
        minSize = Math.min(compOne.length(), compTwo.length());
 
        // Calculate the limit distance of matching 2 characters
        mRange = Math.max(compOne.length(), compTwo.length()) / 2 - 1;
         
        double dw;
        double f;
        double mt;
        double dj;
 
        int m = getMatch(theMatchA, theMatchB);
        int t = 0;
        
        t = getMissMatch(theMatchA, theMatchB) / 2;
 
        sizeStrOne = compOne.length();
        sizeStrTwo = compTwo.length();
 
        f = (double)1/3;
        
        if (m==0) mt = 0;
        else mt = (double)(m-t)/m;
        
        dj = f * ((double)m/sizeStrOne+(double)m/sizeStrTwo+(double)mt);
        dw = dj + getCommonPrefix(compOne,compTwo) * (0.1*(1.0 - dj));
        
        /*Log.d("MatchingAlgo"," sizeStrOne " + sizeStrOne);
        Log.d("MatchingAlgo"," sizeStrTwo " + sizeStrTwo);
        Log.d("MatchingAlgo"," m " + m);
        Log.d("MatchingAlgo"," t " + t);
        Log.d("MatchingAlgo"," dj " + dj);
        Log.d("MatchingAlgo"," dw " + dw);*/
        
        return dw;
    }
 
    // Get m : the number of matching characters
	private static int getMatch(String theMatchA, String theMatchB) 
    {
		char[] strOne = compOne.toCharArray();
		char[] strTwo = compTwo.toCharArray();       
        int matches = 0;
 
        for (int i = 0; i < compOne.length(); i++)
        {
        	boolean find = false;
        	
            //Look backward
            int counter = 0;
            while(counter <= mRange && counter <= i)
            {
            	if (i - counter < compTwo.length() 
                		&& (compOne.charAt(i) == Character.toUpperCase(compTwo.charAt(i - counter)) 
                		|| compOne.charAt(i) == Character.toLowerCase(compTwo.charAt(i - counter)) 
                		|| compOne.charAt(i) == compTwo.charAt(i - counter) )
                		&& strOne[i] != '0' && strTwo[i - counter] != '0')
                {
                    matches++;
                    
                    theMatchA += compOne.charAt(i);
                    
                    strOne[i] = '0';
                    strTwo[i - counter] = '0';
                    find = true;
                }
                counter++;                
            }
 
            //Look forward
            counter = 1;
            while(counter <= mRange && i < compTwo.length() && find == false && counter + i < compTwo.length())
            {
            	if (strOne[i] != '0' 
                		&& strTwo[i + counter] != '0'
                		&& (compOne.charAt(i) == compTwo.charAt(i + counter) 
                		|| compOne.charAt(i) == Character.toUpperCase(compTwo.charAt(i + counter)) 
                		|| compOne.charAt(i) == Character.toLowerCase(compTwo.charAt(i + counter))))
                {
                    matches++;
                    
                    theMatchA += compOne.charAt(i);
                    
                    strOne[i] = '0';
                    strTwo[i + counter] = '0';
                    find = true;
                }
                counter++;
            }
            
        }
        
        for (int j = 0; j < strTwo.length; j++) {
        	if (strTwo[j] == '0') theMatchB += compTwo.charAt(j);
        }

        return matches;
    }
 
    private static int getMissMatch(String theMatchA, String theMatchB)
    {
        int transPositions = 0;
        
        for (int i = 0; i < theMatchA.length(); i++)
        {
        	if (theMatchA.charAt(i) != theMatchB.charAt(i) 
        			&& theMatchA.charAt(i) != Character.toUpperCase(theMatchB.charAt(i))
        			&& theMatchA.charAt(i) != Character.toLowerCase(theMatchB.charAt(i))) {
        		transPositions++;
        	}       	
        }
          
        return transPositions;
    }
 
    private static int getCommonPrefix(String compOne, String compTwo)
    {
        int cp = 0;    
        
        for (int i = 0; i < 4 && i < minSize; i++)
        {
            if (compOne.charAt(i) == compTwo.charAt(i)) cp++;
        }
        
        return cp;
    }
    
}
