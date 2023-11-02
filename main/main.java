package main;

import java.lang.String;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import bn.core.*;
import bn.base.Assignment;
import bn.base.BooleanValue;
import bn.base.StringValue;
import bn.inference.EnumerationInferencer;
import bn.parser.XMLBIFParser;
import bn.base.Value;

public class Main {

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        XMLBIFParser parser = new XMLBIFParser();

        try {
            String filename = "/Users/elias/repos/Java Repos/CS 242/Bayesian Network/bn/examples/aima-alarm.xml";
            if (args.length > 0) {
                filename = args[0];
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

            // Perform inference
            EnumerationInferencer inferencer = new EnumerationInferencer();
            Distribution distribution = inferencer.query(queryVariable, evidence, network);

            // Output the result
            System.out.println("Computed distribution for " + queryVariableName + ":");
            for (Object value : distribution.keySet()) {
                System.out.println(value + ": " + distribution.get(value));
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
