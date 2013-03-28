package Sketchup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.kaon2.api.DefaultOntologyResolver;
import org.semanticweb.kaon2.api.KAON2Manager;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.OntologyChangeEvent;
import org.semanticweb.kaon2.api.OntologyManager;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.reasoner.Query;
import org.semanticweb.kaon2.api.reasoner.Reasoner;

import TrafficDataPkg.CarData;
import TrafficDataPkg.EventKey;
import TrafficDataPkg.TrafficEvent;

public class KAON2Sketchup {
        public static void main(String[] args) throws Exception {
                
                /**
                 * Ontology Infrastructure
                 */
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://www.solesschong.com/KAON2Examples","file:Examples");
        ontologyManager.setOntologyResolver(resolver);
        Ontology ontology=ontologyManager.createOntology("http://www.solesschong.com/KAON2Examples",new HashMap<String,Object>());

        List<OntologyChangeEvent> changes=new ArrayList<OntologyChangeEvent>();

        /**
         * A graph contains Nodes and Edges. Node is represented as the OWLClass "node" whilst Edge is an ObjectProperty "pointTo".  
         */
        OWLClass node = KAON2Manager.factory().owlClass("node");
        Individual A = KAON2Manager.factory().individual("A");
        Individual B = KAON2Manager.factory().individual("B");
        Individual C = KAON2Manager.factory().individual("C");
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(node, A), OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(node, B), OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(node, C), OntologyChangeEvent.ChangeType.ADD));
        
        ObjectProperty pointTo = KAON2Manager.factory().objectProperty("pointTo");
        ObjectProperty adj = KAON2Manager.factory().objectProperty("adj");
        
        /**
         * Add some facts...
         */
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyMember(pointTo, A, B), OntologyChangeEvent.ChangeType.ADD));
        
        Variable P1 = KAON2Manager.factory().variable("P1");
        Variable P2 = KAON2Manager.factory().variable("P2");
        Disjunction disjLiterals = KAON2Manager.factory().disjunction(
                                KAON2Manager.factory().literal(true, pointTo, new Term[]{ P1, P2 }),
                                KAON2Manager.factory().literal(true, pointTo, new Term[]{ P2, P1 })
                        );

        /**
         * disjunction literal
         */
                Rule adjRule = KAON2Manager.factory().rule(
                        KAON2Manager.factory().literal(true, adj, new Term[]{ P1, P2 }),
                        new Formula[]{
                                disjLiterals
                        }
                        );
        changes.add(new OntologyChangeEvent(adjRule, OntologyChangeEvent.ChangeType.ADD));
                
        
        ontology.applyChanges(changes);
        
        /**
         * The query
         */
        Reasoner reasoner = ontology.createReasoner();
        
        Query query = reasoner.createQuery(adj);
                query.open();

                System.out.println("reasoner output:");
                while(!query.afterLast()){
                        Term[] tupleBufferTerms = query.tupleBuffer();

                        System.out.println("'"+tupleBufferTerms[0].toString()+"' is adj to '"+tupleBufferTerms[1].toString()+"'");
                        // Next
                        query.next();
                }
        
        }
}
