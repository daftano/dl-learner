package org.dllearner.modules.sparql;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class OntologyCollector {

	boolean print_flag=false;
	SimpleHTTPRequest s;
	QueryMaker q;
	Cache c;
	InetAddress ia;
	SparqlFilter sf;
	String[] subjectList;
	int numberOfRecursions;
	HashSet<String> properties;
	HashSet<String> classes;
	HashSet<String> instances;
	HashSet<String> triples;
	
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
	
	
	public OntologyCollector(String[] subjectList,int numberOfRecursions,
			int filterMode, String[] FilterPredList,String[] FilterObjList,String[] defClasses){
		this.subjectList=subjectList;
		this.numberOfRecursions=numberOfRecursions;
	
		this.s=new SimpleHTTPRequest();
		this.q=new QueryMaker();
		this.c=new Cache("cache");
		if(defClasses!=null && defClasses.length>0 ){
			this.defaultClasses=defClasses;
		}
		
		try{
		this.sf=new SparqlFilter(filterMode,FilterPredList,FilterObjList);
		this.ia=InetAddress.getByName("dbpedia.openlinksw.com");
		//this.fw=new FileWriter(new File(System.currentTimeMillis()+".nt"),true);
		this.properties=new HashSet<String>();
		this.classes=new HashSet<String>();
		this.instances=new HashSet<String>();
		this.triples=new HashSet<String>();
		//this.all=new HashSet<String>();
		}catch (Exception e) {e.printStackTrace();}
		
	}
	public String collectOntology(){
		getRecursiveList(subjectList, numberOfRecursions);
		finalize();
		String ret="";
		for (Iterator<String> iter = triples.iterator(); iter.hasNext();) {
			ret += iter.next();
			
		}	
		return ret;
	}
	
	public void getRecursiveList(String[] subjects,int NumberofRecursions){
		for (int i = 0; i < subjects.length; i++) {
			getRecursive(subjects[i], NumberofRecursions);
			
		}
		
	}
	
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
		String FromCache=c.get(StartingSubject, sparql);
		String xml;
		if(FromCache==null){
			xml=s.sendAndReceive(ia, 8890, sparql);
			c.put(StartingSubject, xml, sparql);
			System.out.print("\n");
			}
		else{
			xml=FromCache;
			System.out.println("FROM CACHE");
		}
		p(xml);
		p("***********************");
		String[] newSubjects=processResult(StartingSubject,xml);
		
		for (int i = 0; (i < newSubjects.length)&& NumberofRecursions!=0; i++) {
			getRecursive(newSubjects[i], NumberofRecursions);
		}
		
		//System.out.println(xml);
		}catch (Exception e) {e.printStackTrace();}
		
	}
	
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
			
			
			processTriples(subject, predtmp, objtmp,al);
			//System.out.println(al.size());
			
		}
		
		Object[] o=al.toArray();
		String[] ret=new String[o.length];
		for (int i = 0; i < o.length; i++) {
			ret[i]=(String)o[i];
		}
		return ret;
		//return (String[])al.toArray();
		//System.out.println(xml);
	}
		public void processTriples(String s,String p, String o,ArrayList<String> al){
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
//		also makes subclass property between classes
		public String makeTriples(String s,String p, String o){
			//s=replaceNamespace(s);
			//p=replaceNamespace(p);
			//o=replaceNamespace(o);
			String ret="";
			ret="<"+s+"> <"+p+"> <"+o+">.\n";
			return ret;
		}
		
		public boolean isClass(String obj){
			
			boolean retval=false;
			for (String defclass : defaultClasses) {
				if(obj.contains(defclass))retval=true;
			}
			return retval;
		}
		
		
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
		
		public void p(String s){
			if(print_flag)
			System.out.println(s);
		}
		
	
}