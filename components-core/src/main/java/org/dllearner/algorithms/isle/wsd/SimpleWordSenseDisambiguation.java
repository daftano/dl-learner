/**
 * 
 */
package org.dllearner.algorithms.isle.wsd;

import org.apache.log4j.Logger;
import org.dllearner.algorithms.isle.index.Annotation;
import org.dllearner.algorithms.isle.index.EntityScorePair;
import org.dllearner.algorithms.isle.index.SemanticAnnotation;
import org.dllearner.core.owl.Entity;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lorenz Buehmann
 *
 */
public class SimpleWordSenseDisambiguation extends WordSenseDisambiguation{
	
	
	private static final Logger logger = Logger.getLogger(SimpleWordSenseDisambiguation.class.getName());
	
	private IRIShortFormProvider sfp = new SimpleIRIShortFormProvider();
	private OWLDataFactory df = new OWLDataFactoryImpl();
	private OWLAnnotationProperty annotationProperty = df.getRDFSLabel();

	/**
	 * @param ontology
	 */
	public SimpleWordSenseDisambiguation(OWLOntology ontology) {
		super(ontology);
	}

	/* (non-Javadoc)
	 * @see org.dllearner.algorithms.isle.WordSenseDisambiguation#disambiguate(org.dllearner.algorithms.isle.index.Annotation, java.util.Set)
	 */
	@Override
	public SemanticAnnotation disambiguate(Annotation annotation, Set<EntityScorePair> candidateEntities) {
		logger.debug("Linguistic annotations:\n" + annotation);
		logger.debug("Candidate entities:" + candidateEntities);
		String token = annotation.getString().trim();
		//check if annotation token matches label of entity or the part behind #(resp. /)
		for (EntityScorePair entityScorePair : candidateEntities) {
            Entity entity = entityScorePair.getEntity();
            Set<String> labels = getLabels(entity);
            for (String label : labels) {
                if (label.equals(token)) {
                    logger.debug("Disambiguated entity: " + entity);
                    return new SemanticAnnotation(annotation, entity);
                }
            }
            String shortForm = sfp.getShortForm(IRI.create(entity.getURI()));
            if (annotation.equals(shortForm)) {
                logger.debug("Disambiguated entity: " + entity);
                return new SemanticAnnotation(annotation, entity);
            }
        }
        return null;
	}
	
	private Set<String> getLabels(Entity entity){
		Set<String> labels = new HashSet<String>();
		OWLEntity owlEntity = OWLAPIConverter.getOWLAPIEntity(entity);
		Set<OWLAnnotationAssertionAxiom> axioms = ontology.getAnnotationAssertionAxioms(owlEntity.getIRI());
		for (OWLAnnotationAssertionAxiom annotation : axioms) {
			if(annotation.getProperty().equals(annotationProperty)){
				if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    labels.add(val.getLiteral());
                }
			}
		}
		return labels;
	}
	
	private Set<String> getRelatedWordPhrases(Entity entity){
		//add the labels if exist
		Set<String> relatedWordPhrases = new HashSet<String>();
		OWLEntity owlEntity = OWLAPIConverter.getOWLAPIEntity(entity);
		Set<OWLAnnotationAssertionAxiom> axioms = ontology.getAnnotationAssertionAxioms(owlEntity.getIRI());
		for (OWLAnnotationAssertionAxiom annotation : axioms) {
			if(annotation.getProperty().equals(annotationProperty)){
				if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    relatedWordPhrases.add(val.getLiteral());
                }
			}
		}
		//add the short form of the URI if no labels are available
		if(relatedWordPhrases.isEmpty()){
			relatedWordPhrases.add(sfp.getShortForm(IRI.create(entity.getURI())));
		}
		return relatedWordPhrases;
	}

}
