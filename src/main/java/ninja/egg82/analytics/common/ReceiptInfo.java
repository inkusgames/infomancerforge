package ninja.egg82.analytics.common;

import org.json.simple.JSONObject;

public class ReceiptInfo {
    private String store;
    private String receipt;
    private String signature;

    public ReceiptInfo(String store, String receipt, String signature) {
        this.store = store;
        this.receipt = receipt;
        this.signature = signature;
    }

    @SuppressWarnings("unchecked")
	public JSONObject getObject() {
        JSONObject retVal = new JSONObject();

        retVal.put("store", store);
        retVal.put("receipt", receipt);
        retVal.put("signature", signature);

        return retVal;
    }
}
