package biz.pdxtech.daap.ledger.util;

import java.util.Map;

/**
 * Created by bochenlong on 16-12-21.
 */
public class ContractState {
    private Map<String, byte[]> map;
    
    public ContractState() {
    }
    
    public ContractState(Map<String, byte[]> map) {
        this.map = map;
    }
    
    public Map<String, byte[]> getMap() {
        return map;
    }
    
    public void setMap(Map<String, byte[]> map) {
        this.map = map;
    }
}
