/**
 * Copyright (C) 2007, Jens Lehmann
 *
 * This file is part of DL-Learner.
 * 
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.dllearner.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.dllearner.algorithms.refinement.ROLearner;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.LearningAlgorithm;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.kb.OWLFile;
import org.dllearner.kb.SparqlEndpoint;
import org.dllearner.learningproblems.PosNegDefinitionLP;
import org.dllearner.learningproblems.PosNegInclusionLP;
import org.dllearner.reasoning.DIGReasoner;

/**
 * DL-Learner web service interface.
 * 
 * @author Jens Lehmann
 * @author Sebastian Hellmann
 *
 */
@WebService(name = "DLLearnerWebService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class DLLearnerWSNew {

	private Map<Long, State> clients = new HashMap<Long,State>();
	private Random rand=new Random();
	private static ComponentManager cm = ComponentManager.getInstance();
	
	/**
	 * Generates a unique ID for the client and initialises a session. 
	 * Using the ID the client can call the other web service methods. 
	 * Two calls to this method are guaranteed to return different results. 
	 * 
	 * @return A session ID.
	 */
	@WebMethod
	public long generateID() {
		long id;
		do {
			id = rand.nextLong();
		} while(clients.containsKey(id));
		clients.put(id, new State());
		return id;
	}
	
	// returns session state or throws client not known exception
	private State getState(long id) throws ClientNotKnownException {
		State state = clients.get(id);
		if(state==null)
			throw new ClientNotKnownException(id);
		return state;
	}
	
	///////////////////////////////////////
	// methods for basic component setup //
	///////////////////////////////////////
	
	@WebMethod
	public boolean addKnowledgeSource(long id, String component, String url) throws ClientNotKnownException, UnknownComponentException {
		State state = getState(id);
		Class<? extends KnowledgeSource> ksClass;
		if(component.equals("sparql"))
			ksClass = SparqlEndpoint.class;
		else if(component.equals("owlfile"))
			ksClass = OWLFile.class;
		else
			throw new UnknownComponentException(component);
		KnowledgeSource ks = cm.knowledgeSource(ksClass);
		cm.applyConfigEntry(ks, "url", url);
		return state.addKnowledgeSource(ks);
	}
	
	@WebMethod
	public boolean removeKnowledgeSource(long id, String url) throws ClientNotKnownException {
		return getState(id).removeKnowledgeSource(url);
	}
	
	@WebMethod
	public void setReasoner(long id, String component) throws ClientNotKnownException, UnknownComponentException {
		State state = getState(id);
		Class<? extends ReasonerComponent> rcClass;
		if(component.equals("dig"))
			rcClass = DIGReasoner.class;
		else
			throw new UnknownComponentException(component);
		
		ReasonerComponent rc = cm.reasoner(rcClass, state.getKnowledgeSources());
		state.setReasonerComponent(rc);
	}
	
	@WebMethod
	public void setLearningProblem(long id, String component) throws ClientNotKnownException, UnknownComponentException {
		State state = getState(id);
		Class<? extends LearningProblem> lpClass;
		if(component.equals("posNegDefinition"))
			lpClass = PosNegDefinitionLP.class;
		else if(component.equals("posNegInclusion"))
			lpClass = PosNegInclusionLP.class;
		else
			throw new UnknownComponentException(component);
		
		LearningProblem lp = cm.learningProblem(lpClass, state.getReasoningService());
		state.setLearningProblem(lp);
	}
	
	@WebMethod
	public void setLearningAlgorithm(long id, String component) throws ClientNotKnownException, UnknownComponentException {
		State state = getState(id);
		Class<? extends LearningAlgorithm> laClass;
		if(component.equals("refinement"))
			laClass = ROLearner.class;
		else
			throw new UnknownComponentException(component);
		
		LearningAlgorithm la = cm.learningAlgorithm(laClass, state.getLearningProblem(), state.getReasoningService());
		state.setLearningAlgorithm(la);
	}
	
	/**
	 * Initialise all components.
	 * @param id Session ID.
	 */
	@WebMethod
	public void init(long id) throws ClientNotKnownException {
		State state = getState(id);
		for(KnowledgeSource ks : state.getKnowledgeSources())
			ks.init();
		state.getReasonerComponent().init();
		state.getLearningProblem().init();
		state.getLearningAlgorithm().init();
	}
	
	@WebMethod
	public String learn(long id) throws ClientNotKnownException {
		State state = getState(id);
		state.getLearningAlgorithm().start();
		return state.getLearningAlgorithm().getBestSolution().toString();
	}
	
	/////////////////////////////////////////
	// methods for component configuration //
	/////////////////////////////////////////
	
	@WebMethod
	public void setPositiveExamples(long id, String[] positiveExamples) throws ClientNotKnownException {
		State state = getState(id);
		Set<String> posExamples = new TreeSet<String>(Arrays.asList(positiveExamples));
		cm.applyConfigEntry(state.getLearningProblem(), "positiveExamples", posExamples);
	}
	
	@WebMethod
	public void setNegativeExamples(long id, String[] negativeExamples) throws ClientNotKnownException {
		State state = getState(id);
		Set<String> negExamples = new TreeSet<String>(Arrays.asList(negativeExamples));
		cm.applyConfigEntry(state.getLearningProblem(), "negativeExamples", negExamples);
	}
	
	////////////////////////////////////
	// reasoning and querying methods //
	////////////////////////////////////

}
