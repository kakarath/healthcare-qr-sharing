package com.healthcare.clinical;

public class CardiacRiskCalculator {
    
    public static class RiskFactors {
        private int age;
        private boolean isMale;
        private int totalCholesterol;
        private int hdlCholesterol;
        private int systolicBP;
        private boolean onBPMeds;
        private boolean smoker;
        private boolean diabetic;
        
        public RiskFactors(int age, boolean isMale, int totalCholesterol, int hdlCholesterol, 
                          int systolicBP, boolean onBPMeds, boolean smoker, boolean diabetic) {
            this.age = age; this.isMale = isMale; this.totalCholesterol = totalCholesterol;
            this.hdlCholesterol = hdlCholesterol; this.systolicBP = systolicBP;
            this.onBPMeds = onBPMeds; this.smoker = smoker; this.diabetic = diabetic;
        }
        
        public int getAge() { return age; }
        public boolean isMale() { return isMale; }
        public int getTotalCholesterol() { return totalCholesterol; }
        public int getHdlCholesterol() { return hdlCholesterol; }
        public int getSystolicBP() { return systolicBP; }
        public boolean isOnBPMeds() { return onBPMeds; }
        public boolean isSmoker() { return smoker; }
        public boolean isDiabetic() { return diabetic; }
    }
    
    public static class RiskResult {
        private double tenYearRisk;
        private String riskCategory;
        private String recommendations;
        
        public RiskResult(double tenYearRisk, String riskCategory, String recommendations) {
            this.tenYearRisk = tenYearRisk;
            this.riskCategory = riskCategory;
            this.recommendations = recommendations;
        }
        
        public double getTenYearRisk() { return tenYearRisk; }
        public String getRiskCategory() { return riskCategory; }
        public String getRecommendations() { return recommendations; }
    }
    
    public RiskResult calculateASCVDRisk(RiskFactors factors) {
        // Simplified ASCVD Risk Calculator (actual implementation would use full Pooled Cohort Equations)
        double risk = 0.0;
        
        // Age factor
        if (factors.isMale()) {
            risk += (factors.getAge() - 40) * 0.8;
        } else {
            risk += (factors.getAge() - 40) * 0.6;
        }
        
        // Cholesterol ratio
        double cholesterolRatio = (double) factors.getTotalCholesterol() / factors.getHdlCholesterol();
        risk += (cholesterolRatio - 3.5) * 2.0;
        
        // Blood pressure
        if (factors.getSystolicBP() > 140) {
            risk += factors.isOnBPMeds() ? 3.0 : 5.0;
        } else if (factors.getSystolicBP() > 120) {
            risk += 1.0;
        }
        
        // Risk factors
        if (factors.isSmoker()) risk += 4.0;
        if (factors.isDiabetic()) risk += 3.0;
        
        // Ensure risk is between 0-100%
        risk = Math.max(0, Math.min(100, risk));
        
        String category = getRiskCategory(risk);
        String recommendations = getRecommendations(risk, factors);
        
        return new RiskResult(risk, category, recommendations);
    }
    
    private String getRiskCategory(double risk) {
        if (risk < 5.0) return "LOW";
        if (risk < 7.5) return "BORDERLINE";
        if (risk < 20.0) return "INTERMEDIATE";
        return "HIGH";
    }
    
    private String getRecommendations(double risk, RiskFactors factors) {
        if (risk >= 20.0) {
            return "High-intensity statin therapy recommended. Consider additional risk reduction strategies.";
        } else if (risk >= 7.5) {
            return "Moderate to high-intensity statin therapy recommended. Lifestyle modifications essential.";
        } else if (risk >= 5.0) {
            return "Consider statin therapy. Focus on lifestyle modifications and risk factor control.";
        } else {
            return "Continue lifestyle modifications. Monitor risk factors regularly.";
        }
    }
}