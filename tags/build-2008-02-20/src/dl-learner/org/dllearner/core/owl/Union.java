package org.dllearner.core.owl;

import java.util.List;
import java.util.Map;

public class Union extends Description {

	public Union() {
		
	}
	
	public Union(Description... children) {
		for(Description child : children) {
			addChild(child);
		}
	}
	
	// Kinder müssen in einer Liste sein, sonst ist nicht garantiert,
	// dass die Ordnung der Kinder die gleiche wie im Argument ist
	public Union(List<Description> children) {
		for(Description child : children) {
			addChild(child);
		}
	}
	
	@Override
	public int getArity() {
		return children.size();		
	}

	public int getLength() {
		int length = 0;
		for(Description child : children) {
			length += child.getLength();
		}
		return length + children.size() - 1;		
	}
	
	public String toString(String baseURI, Map<String,String> prefixes) {
		if(children.size()==0)
			return "EMPTY_OR";
		
		String ret = "(";
		for(int i=0; i<children.size()-1; i++) {
			ret += children.get(i).toString(baseURI, prefixes) + " OR "; 
		}
		ret += children.get(children.size()-1).toString(baseURI, prefixes) + ")";
		return ret;
	}	
	
	@Override
	public String toManchesterSyntaxString(String baseURI, Map<String,String> prefixes) {
		if(children.size()==0)
			return "EMPTY_OR";
		
		String ret = "(";
		for(int i=0; i<children.size()-1; i++) {
			ret += children.get(i).toManchesterSyntaxString(baseURI, prefixes) + " or "; 
		}
		ret += children.get(children.size()-1).toManchesterSyntaxString(baseURI, prefixes) + ")";
		return ret;
	}	
	
	public String toStringOld() {
		String ret = "MULTI_OR [";
		for(Description child : children) {
			ret += child.toString() + ",";
		}
		ret += "]";
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Description#accept(org.dllearner.core.owl.DescriptionVisitor)
	 */
	@Override
	public void accept(DescriptionVisitor visitor) {
		visitor.visit(this);
	}	
	
	public void accept(KBElementVisitor visitor) {
		visitor.visit(this);
	}	
}