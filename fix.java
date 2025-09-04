    @DeleteMapping("/api/qr/session/{sessionId}")
    public Map<String, Object> cancelSession(@PathVariable String sessionId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Session cancelled");
        return response;
    }