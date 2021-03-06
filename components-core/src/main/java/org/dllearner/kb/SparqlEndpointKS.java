/**
 * Copyright (C) 2007-2011, Jens Lehmann
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
 */

package org.dllearner.kb;

import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aksw.jena_sparql_api.cache.extra.CacheCoreEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreH2;
import org.aksw.jena_sparql_api.cache.extra.CacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheExImpl;
import org.dllearner.core.ComponentAnn;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.config.ConfigOption;
import org.dllearner.core.config.ListStringEditor;
import org.dllearner.kb.sparql.SPARQLTasks;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.springframework.beans.propertyeditors.URLEditor;

/**
 * SPARQL endpoint knowledge source (without fragment extraction),
 * in particular for those algorithms which work directly on an endpoint
 * without requiring an OWL reasoner.
 * 
 * @author Jens Lehmann
 *
 */
@ComponentAnn(name = "SPARQL endpoint", shortName = "sparql", version = 0.2)
public class SparqlEndpointKS implements KnowledgeSource {

	private SparqlEndpoint endpoint;
	private CacheEx cache;
	private boolean supportsSPARQL_1_1 = false;
	private boolean isRemote = true;
	private boolean initialized = false;

	// TODO: turn those into config options
	
	@ConfigOption(name = "url", required=true, propertyEditorClass = URLEditor.class)
	private URL url;
	
	@ConfigOption(name = "defaultGraphs", defaultValue="[]", required=false, propertyEditorClass = ListStringEditor.class)
	private List<String> defaultGraphURIs = new LinkedList<String>();
	
	@ConfigOption(name = "namedGraphs", defaultValue="[]", required=false, propertyEditorClass = ListStringEditor.class)
	private List<String> namedGraphURIs = new LinkedList<String>();
	
	public SparqlEndpointKS() {
		
	}
	
	public SparqlEndpointKS(SparqlEndpoint endpoint) {
		this(endpoint, (String)null);
	}
	
	public SparqlEndpointKS(SparqlEndpoint endpoint, CacheEx cache) {
		this.endpoint = endpoint;
		this.cache = cache;
	}
	
	public SparqlEndpointKS(SparqlEndpoint endpoint, String cacheDirectory) {
		this.endpoint = endpoint;
		if(cacheDirectory != null){
			try {
				long timeToLive = TimeUnit.DAYS.toMillis(30);
				CacheCoreEx cacheBackend = CacheCoreH2.create(cacheDirectory, timeToLive, true);
				cache = new CacheExImpl(cacheBackend);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public CacheEx getCache() {
		return cache;
	}
	
	/**
	 * @param cache the cache to set
	 */
	public void setCache(CacheEx cache) {
		this.cache = cache;
	}
	
	@Override
	public void init() throws ComponentInitException {
		if(!initialized){
			if(endpoint == null) {
				endpoint = new SparqlEndpoint(url, defaultGraphURIs, namedGraphURIs);
			}
			supportsSPARQL_1_1 = new SPARQLTasks(endpoint).supportsSPARQL_1_1();
			initialized = true;
		}
	}
	
	public SparqlEndpoint getEndpoint() {
		return endpoint;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public boolean isRemote() {
		return isRemote;
	}

	public List<String> getDefaultGraphURIs() {
		return defaultGraphURIs;
	}

	public void setDefaultGraphURIs(List<String> defaultGraphURIs) {
		this.defaultGraphURIs = defaultGraphURIs;
	}

	public List<String> getNamedGraphURIs() {
		return namedGraphURIs;
	}

	public void setNamedGraphURIs(List<String> namedGraphURIs) {
		this.namedGraphURIs = namedGraphURIs;
	}

	public boolean supportsSPARQL_1_1() {
		return supportsSPARQL_1_1;
	}

	public void setSupportsSPARQL_1_1(boolean supportsSPARQL_1_1) {
		this.supportsSPARQL_1_1 = supportsSPARQL_1_1;
	}

	@Override public String toString()
	{
		return endpoint.toString();
	}	
	
}
