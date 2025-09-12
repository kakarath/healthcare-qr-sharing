package com.healthcare.service;

import com.healthcare.clinical.DrugInteractionChecker;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecureDrugInteractionService {
    
    private final Map<String, List<String>> drugInteractions = new ConcurrentHashMap<>();
    private final DrugInteractionChecker checker = new DrugInteractionChecker();
    
    public SecureDrugInteractionService() {
        initializeDrugDatabase();
    }
    
    private void initializeDrugDatabase() {
        // RxNorm codes for common drug interactions
        drugInteractions.put("11289", Arrays.asList("1191", "5640", "3616")); // Warfarin interactions
        drugInteractions.put("3616", Arrays.asList("11289", "1191", "6809")); // Digoxin interactions  
        drugInteractions.put("6809", Arrays.asList("887", "3616")); // Metformin interactions
        drugInteractions.put("36567", Arrays.asList("3616", "11289")); // Simvastatin interactions
        drugInteractions.put("197361", Arrays.asList("6809")); // Lisinopril interactions
    }
    
    public List<DrugInteractionChecker.InteractionResult> checkInteractions(List<String> medications) {
        if (medications == null || medications.isEmpty()) {
            return Collections.emptyList();
        }
        
        return checker.checkInteractions(medications);
    }
    
    public List<String> getInteractingDrugs(String rxnormCode) {
        if (rxnormCode == null || rxnormCode.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return drugInteractions.getOrDefault(rxnormCode, Collections.emptyList());
    }
    
    public boolean hasInteraction(String drug1, String drug2) {
        if (drug1 == null || drug2 == null) {
            return false;
        }
        
        List<String> interactions = drugInteractions.get(drug1);
        return interactions != null && interactions.contains(drug2);
    }
}