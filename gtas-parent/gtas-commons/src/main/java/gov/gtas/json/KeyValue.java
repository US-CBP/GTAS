/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.json;

/**
 *
 * @author gbays
 */
public class KeyValue {
	private String key;
	private String value;

	public KeyValue(String keyP, String valueP) {
		key = keyP;
		value = valueP;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
