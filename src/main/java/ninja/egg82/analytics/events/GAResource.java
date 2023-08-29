package ninja.egg82.analytics.events;

import ninja.egg82.analytics.common.FlowType;
import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GAResource implements GAEvent {
    private final GAEventBase eventBase;
    private final FlowType flowType;
    private final int amount;
    private final String resourceType;
    private final String itemType;
    private final String itemID;

    private GAResource(GAEventBase eventBase, FlowType flowType, int amount, String resourceType, String itemType, String itemID) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (flowType == null) {
            throw new IllegalArgumentException("flowType cannot be null.");
        }
        if (flowType == FlowType.UNKNOWN) {
            throw new IllegalArgumentException("flowType cannot be UNKNOWN.");
        }
        if (resourceType == null) {
            throw new IllegalArgumentException("resourceType cannot be null.");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("itemType cannot be null.");
        }
        if (itemID == null) {
            throw new IllegalArgumentException("itemID cannot be null.");
        }

        if ((flowType == FlowType.SINK && amount > 0) || (flowType == FlowType.SOURCE && amount < 0)) {
            amount *= -1;
        }

        this.eventBase = eventBase;
        this.flowType = flowType;
        this.amount = amount;
        this.resourceType = resourceType;
        this.itemType = itemType;
        this.itemID = itemID;
    }

    public static GAResource.Builder builder(GAEventBase eventBase, FlowType flowType, int amount, String resourceType, String itemType, String itemID) { return new GAResource.Builder(eventBase, flowType, amount, resourceType, itemType, itemID); }

    public static class Builder {
        private final GAResource event;

        private Builder(GAEventBase eventBase, FlowType flowType, int amount, String resourceType, String itemType, String itemID) { event = new GAResource(eventBase, flowType, amount, resourceType, itemType, itemID); }

        public GAResource build() { return event; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "resource");
        retVal.put("event_id", flowType.getName() + ":" + resourceType + ":" + itemType + ":" + itemID);
        retVal.put("amount", amount);

        return retVal;
    }
}
