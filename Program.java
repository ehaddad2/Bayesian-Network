

import java.lang.String;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import bn.core.*;
import bn.base.Assignment;
import bn.base.StringValue;
import bn.inference.EnumerationInferencer;
import bn.inference.LikelihoodWeighting;
import bn.inference.RSampleInferencer;
import bn.parser.XMLBIFParser;
import bn.core.Value;


public class Program {//main method for running all inferences and taking in user input

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        XMLBIFParser parser = new XMLBIFParser();

        try {
            String filename;
            if (args.length > 0) {
                filename = args[0];
            } else {
                System.out.println("Which type of example do you want to run? Enter the path to the XML file:");
                filename = reader.readLine();
            }
            
            BayesianNetwork network = parser.readNetworkFromFile(filename);
            System.out.println("Bayesian Network loaded:");
            System.out.println(network);

            // Ask for the query variable
            System.out.println("Enter the query variable (e.g., 'A'):");
            String queryVariableName = reader.readLine();
            RandomVariable queryVariable = network.getVariableByName(queryVariableName);

            // Ask for evidence
            Assignment evidence = new Assignment();
            System.out.println("Enter evidence (case sensitive) (e.g., 'B,true'). Enter 'done' when finished:");
            String line;
            while (!(line = reader.readLine()).equals("done")) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    RandomVariable eVar = network.getVariableByName(parts[0]);
                    StringValue s = new StringValue(parts[1]);
                    evidence.put(eVar, (Value)s);
                } else {
                    System.out.println("Invalid evidence format. Please try again.");
                }
            }

            // ask user which inferencer to use
            System.out.println("\n");
            System.out.println("Choose inferencer type (enumeration, rejection, likelihood):):");
            System.out.println("1 - Enumeration Inferencer");
            System.out.println("2 - Rejection Sampling");
            System.out.println("3 - Likelihood Weighting");
            System.out.println("0 - Exit");

            Distribution distribution = null;

            int inferencerType = Integer.parseInt(reader.readLine());

            switch (inferencerType) {
                case 1:
                    EnumerationInferencer eInferencer = new EnumerationInferencer();
                    distribution = eInferencer.query(queryVariable, evidence, network);
                    break;
                case 2:
                    System.out.println("Enter sample size for Rejection Sampling:");
                    int sampleSize = Integer.parseInt(reader.readLine());
                    RSampleInferencer rsInferencer = new RSampleInferencer(sampleSize);
                    distribution = rsInferencer.query(queryVariable, evidence, network);
                    break;
                case 3:
                    System.out.println("Enter sample size for Likelihood Weighting:");
                    int n = Integer.parseInt(reader.readLine());
                    LikelihoodWeighting lwInferencer = new LikelihoodWeighting(n);
                    distribution = lwInferencer.query(queryVariable, evidence, network);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    System.exit(0);
                    return; //exit the program
                default:
                    throw new IllegalArgumentException("Invalid choice of inferencer.");
            }

            // Output the result
            if (distribution != null) {
                System.out.println("Computed distribution for " + queryVariableName + ":");
                for (Object value : distribution.keySet()) {
                    System.out.println(value + ": " + distribution.get(value));
                }
            } else {
                System.out.println("No inferencing performed.");
            }
        
        } catch (IOException e) {
            System.out.println("IO EXCEPTION ENCOUNTERED, Message: " + e.getMessage());
        } catch (ParserConfigurationException p) {
            System.out.println("PARSER EXCEPTION ENCOUNTERED, Message: " + p.getMessage());
        } catch (SAXException s) {
            System.out.println("SAX EXCEPTION ENCOUNTERED, Message: " + s.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}