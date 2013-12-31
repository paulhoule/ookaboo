package com.ontology2.elasticSearchLoader;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.TransportAddress;

public class SimpleTransportClient extends TransportClient {
    public SimpleTransportClient(TransportAddress address) {
        super();
        addTransportAddress(address);
    }
}
