package org.dllearner.algorithm.tbsl.templator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dllearner.algorithm.tbsl.sem.util.Pair;

public class SlotBuilder {
	
	private WordNet wordnet;
	private String[] noun = {"NN","NNS","NNP","NNPS","NPREP"};
	private String[] adjective = {"JJ","JJR","JJS","JJH"};
	private String[] verb = {"VB","VBD","VBG","VBN","VBP","VBZ","PASSIVE","PASSPART","VPASS","VPASSIN","GERUNDIN","VPREP","WHEN","WHERE"};
	private String[] preps = {"IN","TO"};
	
	public SlotBuilder() {
		
		wordnet = new WordNet();
		wordnet.init();
	}
	
	/**
	 *  gets synonyms, attribute etc. from WordNet and construct grammar entries 
	 *  INPUT:  array of tokens and array of POStags, from which preprocessor constructs a list of pairs (token,pos)
	 *  OUTPUT: list of (treestring,dude) 
	 **/
	public List<String[]> build(String taggedstring,List<Pair<String,String>> tokenPOSpairs) {
		
		List<String[]> result = new ArrayList<String[]>();
		
		for (Pair<String,String> pair : tokenPOSpairs) {
		
			String token = pair.fst;
			String pos = pair.snd;
			
			String type = "UNSPEC";
			
			/* NOUNS */
			if (equalsOneOf(pos,noun)) {
				
				if (pos.equals("NN") || pos.equals("NNS")) {
					type = "CLASS";
				}
				else if (pos.equals("NNP") || pos.equals("NNPS")) {
					type = "RESOURCE";
				}
				else if (pos.equals("NPREP")) {
					type = "PROPERTY";
				}
				
				List<String> words = new ArrayList<String>();
				words.add(token); 
				if (!pos.equals("NNP") && !pos.equals("NNPS")) {
					words.addAll(wordnet.getBestSynonyms(token));
				}
				
				String tokenfluent = token.replaceAll(" ","");
				String slotX = "x/" + type + "/";
				String slotP = "SLOT_" + tokenfluent + "/" + type + "/";
				String slotC = "SLOT_" + tokenfluent + "/CLASS/"; 
				for (Iterator<String> i = words.iterator(); i.hasNext();) {
					String next = i.next().replaceAll(" ","_");
					slotX += next; slotP += next; slotC += next;
					if (i.hasNext()) { slotX += "^"; slotP += "^"; slotC += "^"; }
				}
				// treetoken
				String treetoken = "N:'" + token.toLowerCase() + "'";
				if (token.trim().contains(" ")) {
					String[] tokenParts = token.split(" ");
					treetoken = "";
					for (String t : tokenParts) {
						treetoken += " N:'" + t.toLowerCase() + "'";
					}
					treetoken = treetoken.trim();
				}
				//
				if (pos.equals("NN") || pos.equals("NNS")) {
					/* DP */
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + "))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x) ] ],[],[],[" + slotP + "]>"};
					String[] dpEntry2 = {token,
							"(DP (NP " + treetoken + " DP[name]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x), equal(x,y) ] ],[ (l2,y,name,<<e,t>,t>) ],[l2=l1],[" + slotP + "]>"};
					result.add(dpEntry1);
					result.add(dpEntry2);
					/* NP */
					String[] npEntry1 = {token,
							"(NP " + treetoken + ")",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x) ] ],[],[],[" + slotP + "]>"};
					String[] npEntry2 = {token,
							"(NP " + treetoken + " DP[name])",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x), equal(x,y) ] ],[ (l2,y,name,<<e,t>,t>) ],[l2=l1],[" + slotP + "]>"};
					result.add(npEntry1);
					result.add(npEntry2);	
				} 
				else if (pos.equals("NNP") || pos.equals("NNPS")) {
					/* DP */
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + "))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | ] ],[],[],[" + slotX + "]>"};
					String[] dpEntry2 = {token,
							"(DP DET[det] (NP " + treetoken + "))",
							"<x,l1,<<e,t>,t>,[ l1:[ | ] ],[(l2,x,det,e)],[l2=l1],[" + slotX + "]>"};					
					result.add(dpEntry1);
					result.add(dpEntry2);
				}
				else if (pos.equals("NPREP")) {
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + " DP[pobj]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(y,x) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slotP + "]> ;; " + 
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x), SLOT_of(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slotC + "," + "SLOT_of/PROPERTY/" + "]>"}; 
					String[] dpEntry2 = {token,
							"(DP DET[det] (NP " + treetoken + " DP[pobj]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(y,x) ] ],[(l2,y,pobj,<<e,t>,t>),(l3,x,det,e)],[l2=l1,l3=l1],[" + slotP + "]> ;; " +
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x), SLOT_of(x,y) ] ],[(l2,y,pobj,<<e,t>,t>),(l3,x,det,e)],[l2=l1,l3=l1],[" + slotC + "," + "SLOT_of/PROPERTY/" + "]>"};
					String[] npEntry = {token,
							"(NP " + treetoken + " DP[pobj])",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(y,x) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slotP + "]> ;; " +
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x), SLOT_of(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slotC + "," + "SLOT_of/PROPERTY/" + "]>"};
					result.add(dpEntry1);
					result.add(dpEntry2);
					result.add(npEntry);
				}
						
			}
			/* VERBS */
			else if (equalsOneOf(pos,verb)) {
				
				String slot = "SLOT_" + token + "/PROPERTY/" + token; 				
				List<String> preds = wordnet.getAttributes(token);
				for (Iterator<String> i = preds.iterator(); i.hasNext();) {
					slot += i.next();
					if (i.hasNext()) {
						slot += "^";
					}
				}
				if (pos.equals("PASSIVE")) {
					String[] passEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					String[] passEntry2 = {token,
							"(S DP[wh] (VP DP[dp] V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(x,y) ] ],[(l2,x,wh,<<e,t>,t>),(l3,y,dp,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(passEntry1);
					result.add(passEntry2);
				}
				else if (pos.equals("PASSPART")) {
					String[] passpartEntry = {token,
							"(NP NP* (VP V:'" + token + "' DP[dp]))",
							"<x,l1,t,[ l1:[ | SLOT_" + token + "(y,x) ] ],[(l2,y,dp,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>"};
					result.add(passpartEntry);
				}
				else if (pos.equals("VPASS")) {
					String[] passEntry = {token,
							"(S DP[subj] (VP V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(passEntry);
				}
				else if (pos.equals("VPASSIN")) {
					String[] passEntry = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(passEntry);
				}
				else if (pos.equals("GERUNDIN")) {
					String[] gerundinEntry1 = {token,
							"(NP NP* V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[ | SLOT_" + token + "(x,y) ] ],[(l2,y,obj,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>"};
					String[] gerundinEntry2 = {token,
							"(ADJ V:'" + token + "' DP[obj]))",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + token + "(x,y) ] ],[(l2,y,obj,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>"};
					result.add(gerundinEntry1);
					result.add(gerundinEntry2);
				}
				else if (pos.equals("VPREP")) {
					String[] passEntry = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					String[] whEntry = {token,
							"(S DP[obj] (VP DP[subj] V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(passEntry);
					result.add(whEntry);
				}
				else if (pos.equals("VBD") || pos.equals("VBZ") || pos.equals("VBP")) {
					String[] vEntry = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(vEntry);
				} 
				else if (pos.equals("VB")) {
					String[] whEntry = {token,
							"(S DP[obj] (VP DP[subj] V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(whEntry);
				} 
				else if (pos.equals("VBG") || pos.equals("VBN")) {
					String[] gerEntry = {token,
							"(NP NP* (VP V:'" + token + "' DP[dp]))",
							"<x,l1,t,[ l1:[ | SLOT_" + token + "(x,y) ] ],[(l2,y,dp,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>"};
					result.add(gerEntry);
				}
				else if (pos.equals("WHEN")) {
					String dateSlot = "SLOT_" + token + "/PROPERTY/" + token + "Date";
					String tokenSlot = "SLOT_" + token + "/PROPERTY/" + token;
					String[] whenEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "'))",
							"<x,l1,t,[ l1:[ ?y | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>)],[ l2=l1 ],[ " + dateSlot + " ]>"};
					String[] whenEntry2 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ ?z | SLOT_" + token + "(x,y), SLOT_date(x,z) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ]," +
									"[" + tokenSlot + ", SLOT_date/PROPERTY/date ]>"};
					result.add(whenEntry1);
					result.add(whenEntry2);
				}
				else if (pos.equals("WHERE")) {
					String placeSlot = "SLOT_" + token + "/PROPERTY/" + token + "Place";
					String tokenSlot = "SLOT_" + token + "/PROPERTY/" + token;
					String[] whereEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "'))",
							"<x,l1,t,[ l1:[ ?y | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>)],[ l2=l1 ],[ " + placeSlot + " ]>"};
					String[] whereEntry2 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ ?z | SLOT_" + token + "(x,y), SLOT_place(x,z) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ]," +
									"[" + tokenSlot + ", SLOT_place/PROPERTY/place ]>"};
					result.add(whereEntry1);
					result.add(whereEntry2);
				}
				
			}
			/* ADJECTIVES */
			else if (equalsOneOf(pos,adjective)) {
				
				String slot = "SLOT_" + token + "/PROPERTY/" + token;
				List<String> preds = wordnet.getAttributes(token);
				for (Iterator<String> i = preds.iterator(); i.hasNext();) {
					slot += i.next();
					if (i.hasNext()) {
						slot += "^";
					}
				}
				/* ADJECTIVE */
				if (pos.equals("JJ")) {
					String[] adjEntry = {token,
							"(NP ADJ:'" + token.toLowerCase() + "' NP*)",
							"<x,l1,<e,t>,[ l1:[ j | SLOT_" + token + "(x,j) ] ],[],[],["+slot+"]>"};			
					result.add(adjEntry);
				}
				if (pos.equals("JJH")) {
					String[] howEntry = {"how "+token,
							"(DP WH:'how' ADJ:'" + token.toLowerCase() + "')",
							"<x,l1,<<e,t>,t>,[ l1:[ ?j,x | SLOT_" + token + "(x,j) ] ],[],[],["+slot+"]>"};
					result.add(howEntry);
				}
				/* COMPARATIVE */
				else if (pos.equals("JJR")) {
					String pol = polarity(token);
					String comp; 
					if (pol.equals("POS")) {
						comp = "greater";
					} else { comp = "less"; }
					
					String[] compEntry1 = {token,
							"(ADJ ADJ:'" + token.toLowerCase() + "' P:'than' DP[compobj])",
							"<x,l1,<e,t>,[ l1:[ j,i | SLOT_" + token + "(x,j), SLOT_" + token + "(y,i), " + comp + "(j,i) ] ],[ (l2,y,compobj,<<e,t>,t>) ],[l1=l2],["+slot+"]>"};			
					result.add(compEntry1);	
					String[] compEntry2 = {token,
							"(NP NP* (ADJ ADJ:'" + token.toLowerCase() + "' P:'than' DP[compobj]))",
							"<x,l1,<e,t>,[ l1:[ j,i | SLOT_" + token + "(x,j), SLOT_" + token + "(y,i), " + comp + "(j,i) ] ],[ (l2,y,compobj,<<e,t>,t>) ],[l1=l2],["+slot+"]>"};			
					result.add(compEntry2);
				}
				/* SUPERLATIVE */
				else if (pos.equals("JJS")) {
					String pol = polarity(token);
					String comp; 
					if (pol.equals("POS")) {
						comp = "maximum";
					} else { comp = "minimum"; }
					
					String[] superEntry1 = {token,
							"(DET DET:'the' ADJ:'" + token.toLowerCase() + "')",
							"<x,l1,e,[ l1:[ x,j | SLOT_" + token + "(x,j), " + comp + "(j) ] ],[],[],["+slot+"]>"};			
					result.add(superEntry1);	
					String[] superEntry2 = {token,
							"(DP (NP DET:'the' ADJ:'" + token.toLowerCase() + "'))",
							"<x,l1,<<e,t>,t>,[ l1:[ x,j | SLOT_" + token + "(x,j), " + comp + "(j) ] ],[],[],["+slot+"]>"};			
					result.add(superEntry2);
					String[] superEntry3 = {token,
							"(DP (NP DET:'the' ADJ:'" + token.toLowerCase() + "' NP[noun]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x,j | SLOT_" + token + "(x,j), " + comp + "(j) ] ],[ (l2,x,noun,<e,t>) ],[l2=l1],["+slot+"]>"};			
					result.add(superEntry3);
				}
			}
			/* PREPOSITIONS */
			else if (equalsOneOf(pos,preps)) {
				String slot = "SLOT_" + token + "/PROPERTY/";
				String[] npAdjunct = {token,
						"(NP NP* (PP P:'" + token.toLowerCase() + "' DP[pobj]))",
						"<x,l1,<e,t>,[ l1:[ | SLOT_" + token + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],["+slot+"]>"};
				result.add(npAdjunct);
			}
		}
		
		return result;
	}
	
	private boolean equalsOneOf(String string,String[] strings) {
		for (String s : strings) {
			if (string.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	private String polarity(String adj) {
		
		String polarity = "POS";
		
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("src/main/resources/tbsl/lexicon/adj_list.txt"));
			String line;
			while ((line = in.readLine()) != null ) {
				if (line.contains(adj)) {
					polarity = line.split(" ")[0];
					break;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return polarity;
	}


}