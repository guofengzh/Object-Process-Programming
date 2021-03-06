/*******************************************************************************
 * Copyright (c) 2015 Arieh "Vainolo" Bibliowicz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.vainolo.phd.opp.interpreter.builtin;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import com.eclipsesource.json.JsonObject;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPWriteOPMObjectInstanceToJSON;
import com.vainolo.phd.opp.interpreter.types.OPPComplexObjectInstance;
import com.vainolo.phd.opp.interpreter.types.OPPObjectInstance;

/**
 * 
 * @author Arieh 'Vainolo' Bibliowicz
 * 
 */
public class OPPWriteOPMObjectInstanceToJSONProcessInstanceTest {

  private OPPObjectInstance writeJson(OPPObjectInstance opmObject) throws Exception {
    OPPWriteOPMObjectInstanceToJSON instance = new OPPWriteOPMObjectInstanceToJSON();
    instance.setArgument("object", opmObject);
    instance.call();
    return instance.getArgument("json");
  }

  @Test
  public void test_execute_intElement() throws Exception {
    OPPComplexObjectInstance opmObject = OPPObjectInstance.createCompositeInstance();
    opmObject.setPart("int", OPPObjectInstance.createFromValue(new BigDecimal(3)));
    OPPObjectInstance result = writeJson(opmObject);
    JsonObject jsonObject = JsonObject.readFrom(result.getStringValue());
    assertEquals(3, jsonObject.get("int").asInt());
  }

  @Test
  public void test_execute_doubleElement() throws Exception {
    OPPComplexObjectInstance opmObject = OPPObjectInstance.createCompositeInstance();
    opmObject.setPart("double", OPPObjectInstance.createFromValue(new BigDecimal(5.47)));
    OPPObjectInstance result = writeJson(opmObject);
    JsonObject jsonObject = JsonObject.readFrom(result.getStringValue());
    assertEquals(5.47, jsonObject.get("double").asDouble(), 0.00001);
  }

  @Test
  public void test_execute_stringElement() throws Exception {
    OPPComplexObjectInstance opmObject = OPPObjectInstance.createCompositeInstance();
    opmObject.setPart("name", OPPObjectInstance.createFromValue("hello"));
    OPPObjectInstance result = writeJson(opmObject);
    JsonObject jsonObject = JsonObject.readFrom(result.getStringValue());
    assertEquals("hello", jsonObject.get("name").asString());
  }

  @Test
  public void test_execute_stateElement() throws Exception {
    OPPComplexObjectInstance opmObject = OPPObjectInstance.createCompositeInstance();
    opmObject.setPart("bool", OPPObjectInstance.createFromValue("true"));
    OPPObjectInstance result = writeJson(opmObject);
    JsonObject jsonObject = JsonObject.readFrom(result.getStringValue());
    assertEquals("true", jsonObject.get("bool").asString());
  }

  @Test
  public void text_execute_multipleElements() throws Exception {
    OPPComplexObjectInstance opmObject = OPPObjectInstance.createCompositeInstance();
    opmObject.setPart("a", OPPObjectInstance.createFromValue(new BigDecimal(5)));
    OPPComplexObjectInstance part = OPPObjectInstance.createCompositeInstance();
    part.setPart("b1", OPPObjectInstance.createFromValue("hello"));
    part.setPart("b2", OPPObjectInstance.createFromValue(new BigDecimal(5.43)));
    opmObject.setPart("b", part);
    opmObject.setPart("c", OPPObjectInstance.createFromValue("one"));
    OPPObjectInstance result = writeJson(opmObject);
    JsonObject json = JsonObject.readFrom(result.getStringValue());
    assertEquals(5, json.get("a").asInt());
    assertEquals("hello", json.get("b").asObject().get("b1").asString());
    assertEquals(5.43, json.get("b").asObject().get("b2").asDouble(), 0.0001);
    assertEquals("one", json.get("c").asString());
  }
}