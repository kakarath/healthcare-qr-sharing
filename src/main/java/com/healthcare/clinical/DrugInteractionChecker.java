package com.healthcare.clinical;

import java.util.*;

public class DrugInteractionChecker {
    
    private static final Map<String, List<String>> DRUG_INTERACTIONS = Map.of(
        "warfarin", List.of("aspirin", "ibuprofen", "amiodarone"),
        "digoxin", List.of("amiodarone", "verapamil", "quinidine"),
        "metformin", List.of("contrast-dye", "alcohol"),
        "simvastatin", List.of("amiodarone", "verapamil", "clarithromycin")
    );
    
    public static class InteractionResult {
        private String drug1, drug2, severity, description;
        
        public InteractionResult(String drug1, String drug2, String severity, String description) {
            this.drug1 = drug1; this.drug2 = drug2; 
            this.severity = severity; this.description = description;
        }
        
        public String getDrug1() { return drug1; }
        public String getDrug2() { return drug2; }
        public String getSeverity() { return severity; }
        public String getDescription() { return description; }
    }
    
    public List<InteractionResult> checkInteractions(List<String> medications) {
        List<InteractionResult> interactions = new ArrayList<>();
        
        for (int i = 0; i < medications.size(); i++) {
            for (int j = i + 1; j < medications.size(); j++) {
                String med1 = medications.get(i).toLowerCase();
                String med2 = medications.get(j).toLowerCase();
                
                if (hasInteraction(med1, med2)) {
                    interactions.add(new InteractionResult(
                        medications.get(i), medications.get(j), 
                        "MAJOR", "Potential drug interaction detected"
                    ));
                }
            }
        }
        
        return interactions;
    }
    
    private boolean hasInteraction(String drug1, String drug2) {
        return (DRUG_INTERACTIONS.containsKey(drug1) && DRUG_INTERACTIONS.get(drug1).contains(drug2)) ||
               (DRUG_INTERACTIONS.containsKey(drug2) && DRUG_INTERACTIONS.get(drug2).contains(drug1));
    }
}