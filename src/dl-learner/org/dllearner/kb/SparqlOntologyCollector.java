/**
 * Copyright (C) 2007, Sebastian Hellmann
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
package org.dllearner.kb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This class collects the ontology from dbpedia,
 * everything is saved in hashsets, so the doublettes are taken care of
 * 
 * 
 * @author Sebastian Hellmann
 *
 */
public class SparqlOntologyCollector {

	boolean print_flag=false;
	SparqlQueryMaker q;
	SparqlCache c;
	URL url;
	SparqlFilter sf;
	String[] subjectList;
	int numberOfRecursions;
	HashSet<String> properties;
	HashSet<String> classes;
	HashSet<String> instances;
	HashSet<String> triples;
	String format;
	static final char value[]={13,10};
	static final String cut=new String(value);
	
	// some namespaces
	String subclass="http://www.w3.org/2000/01/rdf-schema#subClassOf";
	String type="http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	String objectProperty="http://www.w3.org/2002/07/owl#ObjectProperty";
	String classns="http://www.w3.org/2002/07/owl#Class";
	String thing="http://www.w3.org/2002/07/owl#Thing";
	
	
	String[] defaultClasses={
			"http://dbpedia.org/class/yago",
			"http://dbpedia.org/resource/Category:",
			"http://dbpedia.org/resource/Template:",
			"http://www.w3.org/2004/02/skos/core",
			"http://dbpedia.org/class/"}; //TODO FEHLER hier fehlt yago
	
	
	/**
	 * 
	 * 
	 * @param subjectList
	 * @param numberOfRecursions
	 * @param filterMode
	 * @param FilterPredList
	 * @param FilterObjList
	 * @param defClasses
	 */
	public SparqlOntologyCollector(String[] subjectList,int numberOfRecursions,
			int filterMode, String[] FilterPredList,String[] FilterObjList,String[] defClasses, String format, URL url){
		this.subjectList=subjectList;
		this.numberOfRecursions=numberOfRecursions;
		this.format=format;
		this.q=new SparqlQueryMaker();
		this.c=new SparqlCache("cache");
		if(defClasses!=null && defClasses.length>0 ){
			this.defaultClasses=defClasses;
		}
		
		try{
			this.sf=new SparqlFilter(filterMode,FilterPredList,FilterObjList);
			this.url=url;
			this.properties=new HashSet<String>();
			this.classes=new HashSet<String>();
			this.instances=new HashSet<String>();
			this.triples=new HashSet<String>();
		}catch (Exception e) {e.printStackTrace();}
		
	}
	/**
	 * first collects the ontology 
	 * then types everything so it becomes owl-dl
	 * 
	 * @return all triples in n-triple format
	 */
	public String collectOntology(){
		getRecursiveList(subjectList, numberOfRecursions);
		finalize();
		String ret="";
		for (Iterator<String> iter = triples.iterator(); iter.hasNext();) {
			ret += iter.next();
			
		}	
		return ret;
	}
	
	/**
	 * calls getRecursive for each subject in list
	 * @param subjects
	 * @param NumberofRecursions
	 */
	public void getRecursiveList(String[] subjects,int NumberofRecursions){
		for (int i = 0; i < subjects.length; i++) {
			getRecursive(subjects[i], NumberofRecursions);
			
		}
		
	}
	
	/**
	 * gets all triples until numberofrecursion-- gets 0
	 * 
	 * @param StartingSubject
	 * @param NumberofRecursions
	 */
	public void getRecursive(String StartingSubject,int NumberofRecursions){
		System.out.print("SparqlModul: Depth: "+NumberofRecursions+" @ "+StartingSubject+" ");
		if(NumberofRecursions<=0)
			{	return;
			}
		else {NumberofRecursions--;}
		//System.out.println(NumberofRecursions);
		try{
	
		String sparql=q.makeQueryFilter(StartingSubject,this.sf);
		p(sparql);
		p("*******************");
		// checks cache
		String FromCache=c.get(StartingSubject, sparql);
		String xml;
		// if not in cache get it from dbpedia
		if(FromCache==null){
			xml=sendAndReceive(sparql);
			c.put(StartingSubject, xml, sparql);
			System.out.print("\n");
			}
		else{
			xml=FromCache;
			System.out.println("FROM CACHE");
		}
		p(xml);
		p("***********************");
		// get new Subjects
		String[] newSubjects=processResult(StartingSubject,xml);
		
		for (int i = 0; (i < newSubjects.length)&& NumberofRecursions!=0; i++) {
			getRecursive(newSubjects[i], NumberofRecursions);
		}
		
		//System.out.println(xml);
		}catch (Exception e) {e.printStackTrace();}
		
	}
	
	/**
	 * process the sparql result xml in a simple manner
	 * 
	 * 
	 * @param subject
	 * @param xml
	 * @return list of new individuals
	 */
	public  String[] processResult(String subject,String xml){
		//TODO if result is empty, catch exceptions
		String one="<binding name=\"predicate\"><uri>";
		String two="<binding name=\"object\"><uri>";
		String end="</uri></binding>";
		String predtmp="";
		String objtmp="";
		ArrayList<String> al=new ArrayList<String>();
		
		while(xml.indexOf(one)!=-1){
			//get pred
			xml=xml.substring(xml.indexOf(one)+one.length());
			predtmp=xml.substring(0,xml.indexOf(end));
			//getobj
			xml=xml.substring(xml.indexOf(two)+two.length());
			objtmp=xml.substring(0,xml.indexOf(end));
			
			// writes the triples and resources in the hashsets
			// also fills the arraylist al
			processTriples(subject, predtmp, objtmp,al);
			//System.out.println(al.size());
			
		}
		
		// convert al to list
		Object[] o=al.toArray();
		String[] ret=new String[o.length];
		for (int i = 0; i < o.length; i++) {
			ret[i]=(String)o[i];
		}
		return ret;
		//return (String[])al.toArray();
		//System.out.println(xml);
	}
		
	
	
	/**
	 * 
	* writes the triples and resources in the hashsets
	* also fills the arraylist al with new individals for further processing
	 * @param s
	 * @param p
	 * @param o
	 * @param al
	 */
	public void processTriples(String s,String p, String o,ArrayList<String> al){
			// the next two lines bump out some inconsistencies within dbpedia
			String t="/Category";
			if(s.equals(t) || o.equals(t))return ;
			
			if(sf.mode==2)
			{
				if(  o.startsWith("http://dbpedia.org/resource/Category:")
						&& 
					!p.startsWith("http://www.w3.org/2004/02/skos/core")
				   )
					{return;}
				if(p.equals("http://www.w3.org/2004/02/skos/core#broader")){
					p=subclass;
				}
				else if(p.equals("http://www.w3.org/2004/02/skos/core#subject")){
				p=type;
				}
				else {}
			}
			
			//save for further processing
			al.add(o);
			
			// type classes
			if(isClass(o)){
				classes.add(o);
				if(isClass(s))p=subclass;
				else p=type;
			}
			else {
				instances.add(o);
				this.properties.add(p);
			}
			
			
			
			//maketriples
			try{
			this.triples.add(makeTriples(s, p, o));
			//fw.write(makeTriples(subject, predtmp, objtmp));
			}catch (Exception e) {e.printStackTrace();}
			
			
			return;
		}
//		
		/**
		 * also makes subclass property between classes
		 * 
		 * @param s
		 * @param p
		 * @param o
		 * @return triple in the n triple notation
		 */
		public String makeTriples(String s,String p, String o){
			String ret="";
			if (format.equals("N-TRIPLES")) ret="<"+s+"> <"+p+"> <"+o+">.\n";
			else if (format.equals("KB")){
				if (p.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) ret="\""+o+"\"(\""+s+"\").\n"; 
				else ret="\""+p+"\"(\""+s+"\",\""+o+"\").\n";
			}
			return ret;
		}
		
		/**
		 * decides if an object is treated as a class
		 * 
		 * @param obj
		 * @return true if obj is in the defaultClassesList
		 */
		public boolean isClass(String obj){
			
			boolean retval=false;
			for (String defclass : defaultClasses) {
				if(obj.contains(defclass))retval=true;
			}
			return retval;
		}
		
	
		/** 
		 * @see java.lang.Object#finalize()
		 */
		@Override
		public void finalize(){
			typeProperties();
			typeClasses();
			typeInstances();
		}
		
		public void typeProperties(){
			String rdfns="http://www.w3.org/1999/02/22-rdf-syntax-ns";
			String owlns="http://www.w3.org/2002/07/owl";
			Iterator<String> it=properties.iterator();
			String current="";
			while (it.hasNext()){
				try{
				current=it.next();
				if(current.equals(subclass))continue;
				if(current.contains(rdfns)||current.contains(owlns)){/*DO NOTHING*/}
				else {this.triples.add(makeTriples(current,type,objectProperty));}
				}catch (Exception e) {}

			}
		}
		public void typeClasses(){ 
			Iterator<String> it=classes.iterator();
			String current="";
			while (it.hasNext()){
				try{
				current=it.next();
				this.triples.add(makeTriples(current,type,classns));
				}catch (Exception e) {}
			}
		}
		public void typeInstances(){
			Iterator<String> it=instances.iterator();
			String current="";
			while (it.hasNext()){
				try{
				current=it.next();
				this.triples.add(makeTriples(current,type,thing));
				}catch (Exception e) {}
			}
		}
		
		/**
		 * debug print turn on print_flag
		 * @param s
		 */
		public void p(String s){
			if(print_flag)
			System.out.println(s);
		}
		
		private String sendAndReceive(String sparql) {
			StringBuilder answer = new StringBuilder();	
			
			// String an Sparql-Endpoint schicken
			HttpURLConnection connection;
				
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
							
				connection.addRequestProperty("Host", "dbpedia.openlinksw.com");
				connection.addRequestProperty("Connection","close");
				connection.addRequestProperty("Accept","text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
				connection.addRequestProperty("Accept-Language","de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
				connection.addRequestProperty("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");
				connection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8.1.4) Gecko/20070515 Firefox/2.0.0.4 Web-Sniffer/1.0.24");
				
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				osw.write("default-graph-uri=http%3A%2F%2Fdbpedia.org&query=" +
					URLEncoder.encode(sparql, "UTF-8")+
					"&format=application%2Fsparql-results%2Bxml");
				osw.close();
				
				// receive answer
				InputStream is = connection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				String line;
				do {
					line = br.readLine();
					if(line!=null)
						answer.append(line);
				} while (line != null);
				
				br.close();
				
			} catch (IOException e) {		
				System.out.println("Communication problem with Sparql Server.");
				System.exit(0);
			}	
			
			return answer.toString();
		}
}
