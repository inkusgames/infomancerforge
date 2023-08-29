package ninja.egg82.analytics.events;

import ninja.egg82.analytics.common.CurrencyType;
import ninja.egg82.analytics.common.ReceiptInfo;
import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GABusiness implements GAEvent {
    private final GAEventBase eventBase;
    private final String itemType;
    private final String itemID;
    private final int amount;
    private final CurrencyType currencyType;
    private final long transactionNum;

    private String cartType = null;
    private ReceiptInfo receiptInfo = null;

    private GABusiness(GAEventBase eventBase, String itemType, String itemID, int amount, CurrencyType currencyType, long transactionNum) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("itemType cannot be null.");
        }
        if (itemID == null) {
            throw new IllegalArgumentException("itemID cannot be null.");
        }
        if (currencyType == null) {
            throw new IllegalArgumentException("currencyType cannot be null.");
        }
        if (currencyType == CurrencyType.UNKNOWN) {
            throw new IllegalArgumentException("currencyType cannot be UNKNOWN.");
        }
        if (transactionNum <= 0L) {
            throw new IllegalArgumentException("transactionNum cannot be <= 0");
        }

        this.eventBase = eventBase;
        this.itemType = itemType;
        this.itemID = itemID;
        this.amount = amount;
        this.currencyType = currencyType;
        this.transactionNum = transactionNum;
    }

    public static GABusiness.Builder builder(GAEventBase eventBase, String itemType, String itemID, int amount, CurrencyType currencyType, long transactionNum) { return new GABusiness.Builder(eventBase, itemType, itemID, amount, currencyType, transactionNum); }

    public static class Builder {
        private final GABusiness event;

        private Builder(GAEventBase eventBase, String itemType, String itemID, int amount, CurrencyType currencyType, long transactionNum) { event = new GABusiness(eventBase, itemType, itemID, amount, currencyType, transactionNum); }

        public Builder cartType(String val) {
            event.cartType = val;
            return this;
        }

        public Builder receiptInfo(ReceiptInfo val) {
            event.receiptInfo = val;
            return this;
        }

        public GABusiness build() { return event; }
    }

    @SuppressWarnings("unchecked")
	public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "business");
        retVal.put("event_id", itemType + ":" + itemID);
        retVal.put("amount", amount);
        retVal.put("currency", currencyType.getName());
        retVal.put("transaction_num", transactionNum);
        if (cartType != null) {
            retVal.put("cart_type", cartType);
        }
        if (receiptInfo != null) {
            retVal.put("receipt_info", receiptInfo.getObject());
        }

        return retVal;
    }
}
