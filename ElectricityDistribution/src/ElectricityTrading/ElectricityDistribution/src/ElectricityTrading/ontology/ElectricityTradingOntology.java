package ElectricityTrading.ontology;


import jade.content.onto.*;
import jade.content.schema.*;

public class ElectricityTradingOntology extends Ontology implements ElectricityTradingVocabulary {
// The name identifying this ontology
public static final String ONTOLOGY_NAME = "Electricity-trading-ontology";


// The singleton instance of this ontology
private static Ontology theInstance = new ElectricityTradingOntology();

// Retrieve the singleton electricity-trading ontology instance
public static Ontology getInstance() {
return theInstance;
}

// Private constructor
private ElectricityTradingOntology() {
// The electricity-trading ontology extends the basic ontology
super(ONTOLOGY_NAME, BasicOntology.getInstance());
try {
add(new ConceptSchema(PRICE), Electricity.class);
add(new PredicateSchema(COSTS), Costs.class);
add(new AgentActionSchema(SELL), Sell.class);

// Structure of the schema for the tenders 
ConceptSchema cs = (ConceptSchema) getSchema(PRICE);
cs.add(TITLE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
cs.add(COMPANY_NAMES, (PrimitiveSchema) getSchema(BasicOntology.STRING), 0,
ObjectSchema.UNLIMITED);
cs.add(BOOK_EDITOR, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

// Structure of the schema for the Costs predicate
PredicateSchema ps = (PredicateSchema) getSchema(COSTS);
ps.add(COSTS_ITEM, (ConceptSchema) cs);
ps.add(COSTS_PRICE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

// Structure of the schema for the Sell agent action
AgentActionSchema as = (AgentActionSchema) getSchema(SELL);
as.add(SELL_ITEM, (ConceptSchema) getSchema(PRICE));
}
catch (OntologyException oe) {
oe.printStackTrace();
}
}
}