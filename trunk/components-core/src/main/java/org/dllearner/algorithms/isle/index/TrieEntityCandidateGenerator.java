package org.dllearner.algorithms.isle.index;

import com.google.common.collect.Lists;
import org.dllearner.algorithms.isle.EntityCandidateGenerator;
import org.dllearner.algorithms.isle.StopWordFilter;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Generates candidates using a entity candidates prefix trie
 * @author Andre Melo
 *
 */
public class TrieEntityCandidateGenerator extends EntityCandidateGenerator{

	final EntityCandidatesTrie candidatesTrie;
	final StopWordFilter stopWordFilter = new StopWordFilter();
	int window = 10;
	
	public TrieEntityCandidateGenerator(OWLOntology ontology, EntityCandidatesTrie candidatesTrie) {
		super(ontology);
		this.candidatesTrie = candidatesTrie;
	}
	
	public Set<EntityScorePair> getCandidates(Annotation annotation) {
        Set<EntityScorePair> candidateEntities = candidatesTrie.getCandidateEntities(annotation.getTokens());
        System.out.println(annotation + " --> " + candidateEntities);
        return candidateEntities;
	}

    /**
     * Postprocess the annotations generated by annotate
     * The objective is to merge annotations which are likely to belong to the same entity
     * @param window : maximum distance between the annotations
     * @return
     */
    public void postProcess(HashMap<Annotation,Set<EntityScorePair>> candidatesMap, int window, StopWordFilter stopWordFilter) {
    	Set<Annotation> annotations = candidatesMap.keySet();
    	List<Annotation> sortedAnnotations = new ArrayList<Annotation>(annotations);
    	//TODO refactoring
    	/**
    	  
    	
    	// Sort annotations by offset in ascending order
    	Collections.sort(sortedAnnotations, new Comparator<Annotation>(){
            public int compare(Annotation a1,Annotation a2){
                return Integer.compare(a1.getOffset(), a2.getOffset());
            }
    	});
    	
    	int windowStart = 0;
    	int windowEnd = 0;
    	for (int i=0; i<sortedAnnotations.size(); i++) {
    		
    		Annotation annotation_i = sortedAnnotations.get(i);
    		int begin_i = annotation_i.getOffset();
    		int end_i = begin_i + annotation_i.getLength()-1;
    		String token_i = annotation_i.getString();
    		Set<Entity> candidates_i = getCandidates(annotation_i);
    		Set<Entity> newCandidates_i = new HashSet<Entity>();
    		
    		// Determine the annotations contained in the window
    		while ((sortedAnnotations.get(windowStart).getOffset()+sortedAnnotations.get(windowStart).getLength()-1)<(begin_i-window))
    			windowStart++;
    		while (windowEnd<sortedAnnotations.size() && sortedAnnotations.get(windowEnd).getOffset()<(end_i+window))
    			windowEnd++;
    		
    		// For every annotation in the window (defined by the number of characters between offsets)
    		for (int j=windowStart; j<sortedAnnotations.size() && j<windowEnd; j++) {
    			if (j!=i) {
	    			Annotation annotation_j = sortedAnnotations.get(j);
	    			String token_j = annotation_j.getString();
	    			Set<Entity> candidates_j = getCandidates(annotation_j);
	    			Set<Entity> intersection = Sets.intersection(candidates_i, candidates_j);
	    			Set<Entity> newCandidates_ij = new HashSet<Entity>();
	    			for (Entity commonEntity: intersection) {
	    				if (!(stopWordFilter.isStopWord(token_i) && stopWordFilter.isStopWord(token_j))) {
		    				if (!token_i.contains(token_j) && !token_j.contains(token_i)) {
		    					newCandidates_ij.add(commonEntity);
		    					//System.out.println("common("+token_i+","+token_j+")="+commonEntity);
		    				}
	    				}
	    			}
	    			if (!newCandidates_ij.isEmpty()) {
	    				Annotation mergedAnnotation = mergeAnnotations(annotation_i,annotation_j);
	    				// If there's no punctuation in the merged annotation
	    				if (!Pattern.matches("\\p{Punct}", mergedAnnotation.getString())) {
		    				candidatesMap.put(mergedAnnotation, newCandidates_ij);
		    				candidatesMap.remove(annotation_i);
		    				candidatesMap.remove(annotation_j);
	    				}
	    				
	    				newCandidates_i.addAll(newCandidates_ij);
	    			}
    			}
    		}
    		
    		// Deletes annotation if it's a stop word and doesn't have any matching annotation in the window
    		if (stopWordFilter.isStopWord(token_i)) {
    			if (newCandidates_i.isEmpty())
    				candidatesMap.remove(annotation_i);	
    		}
    	}
    	
    	
    	 */
    }

	private Annotation mergeAnnotations(Annotation annotation_i, Annotation annotation_j) {
		List<Token> tokens = Lists.newArrayList();
		tokens.addAll(annotation_i.getTokens());
		tokens.addAll(annotation_j.getTokens());
		return new Annotation(annotation_i.getReferencedDocument(), tokens);
	}

	@Override
	public HashMap<Annotation, Set<EntityScorePair>> getCandidatesMap(Set<Annotation> annotations) {
		HashMap<Annotation, Set<EntityScorePair>> candidatesMap = new HashMap<>();
		for (Annotation annotation: annotations) 
			candidatesMap.put(annotation, getCandidates(annotation));
		
		postProcess(candidatesMap, window, stopWordFilter);
		
		return candidatesMap;
	}
}
