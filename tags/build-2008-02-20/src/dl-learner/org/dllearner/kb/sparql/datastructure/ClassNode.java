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
package org.dllearner.kb.sparql.datastructure;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.dllearner.kb.sparql.Manipulator;
import org.dllearner.kb.sparql.TypedSparqlQueryInterface;
import org.dllearner.utilities.StringTuple;

/**
 * Is a node in the graph, that is a class.
 * 
 * @author Sebastian Hellmann
 */
public class ClassNode extends Node {
	Set<PropertyNode> properties = new HashSet<PropertyNode>();

	public ClassNode(URI u) {
		super(u);
	}

	// expands all directly connected nodes
	@Override
	public Vector<Node> expand(TypedSparqlQueryInterface tsq, Manipulator m) {

		Set<StringTuple> s = tsq.getTupelForResource(this.uri);
		// see manipulator
		s = m.check(s, this);
		Vector<Node> Nodes = new Vector<Node>();
		Iterator<StringTuple> it = s.iterator();
		while (it.hasNext()) {
			StringTuple t = (StringTuple) it.next();
			try {
				// substitute rdf:type with owl:subclassof
				if (t.a.equals(m.type) || t.a.equals(m.subclass)) {
					ClassNode tmp = new ClassNode(new URI(t.b));
					properties.add(new PropertyNode(new URI(m.subclass), this,
							tmp));
					Nodes.add(tmp);
				} else {
					// further expansion stops here
					// Nodes.add(tmp); is missing on purpose
					ClassNode tmp = new ClassNode(new URI(t.b));
					properties.add(new PropertyNode(new URI(t.a), this, tmp));
					// System.out.println(m.blankNodeIdentifier);
					// System.out.println("XXXXX"+t.b);

					// if o is a blank node expand further
					// TODO this needs a lot more work
					if (t.b.startsWith(m.blankNodeIdentifier)) {
						tmp.expand(tsq, m);
						System.out.println(m.blankNodeIdentifier);
						System.out.println("XXXXX" + t.b);
					}
					// Nodes.add(tmp);
				}
			} catch (Exception e) {
				System.out.println(t);
				e.printStackTrace();
			}

		}
		return Nodes;
	}

	// gets the types for properties recursively
	@Override
	public void expandProperties(TypedSparqlQueryInterface tsq, Manipulator m) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dllearner.kb.sparql.datastructure.Node#toNTriple()
	 */
	@Override
	public Set<String> toNTriple() {
		Set<String> s = new HashSet<String>();
		s.add("<" + this.uri + "><" + rdftype + "><" + classns + ">.");

		for (PropertyNode one : properties) {
			s.add("<" + this.uri + "><" + one.getURI() + "><"
					+ one.getB().getURI() + ">.");
			s.addAll(one.getB().toNTriple());
		}

		return s;
	}

	@Override
	public int compareTo(Node n) {
		return super.compareTo(n);
	}

}