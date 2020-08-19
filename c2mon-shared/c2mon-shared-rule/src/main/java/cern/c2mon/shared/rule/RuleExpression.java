/******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 * 
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 * 
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package cern.c2mon.shared.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cern.c2mon.shared.common.rule.RuleInputValue;
import cern.c2mon.shared.common.type.TypeConverter;

public abstract class RuleExpression 
    implements IRuleExpression {
  
  /**
   * Enum for defining the rule type of this instance
   * @author jlopezco
   * @see RuleTransferObject#getRuleType()
   */
  public enum RuleType {
    /** Simple rule type */
    Simple,
    /** Conditioned rule type */
    ConditionedRule
  }
  
  private static final long serialVersionUID = -8053889874595191829L;

  /**
   * Text of the rule expression (as defined by the user)
   */
  protected final String expression;
  
  /** This value must be set by the underlying rule expression Implementation; */
  private final RuleType ruleType;

  /**
   * Rules extracted from the database use the following tag in the xml file 
   * to separate each rule from each other.
   */
  private static final String RULE_DATABASE_XML_TAG = "TAGRULE";

  /**
   * Default constructor
   * 
   * @param pExpression The rule expression string
   */
  protected RuleExpression(final String pExpression, RuleType ruleType) {
    this.expression = pExpression.trim();
    this.ruleType = ruleType;
  }

  /**
   * Clone implementation.
   */
  public Object clone() {
    try {
      RuleExpression ruleExpression = (RuleExpression) super.clone();
      return ruleExpression;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception caught when cloning a RuleExpression object - this should not happen!!");
    }
  }

  /**
   * The method evaluates the rule for the given input values of the tags. 
   * The result is then casted into the given result type class
   * 
   * @param pInputParams Map of value objects related to the input tag ids
   * @param resultType The result type class to which the rule result shall be casted
   * @return The casted rule result for the given input values
   * @throws RuleEvaluationException In case of errors during the rule evaluation
   */
  public final <T> T evaluate(final Map<Long, RuleInputValue> pInputParams, Class<T> resultType)
      throws RuleEvaluationException {
    
    try {
      return TypeConverter.castToType(evaluate(pInputParams), resultType);
    } catch (ClassCastException ce) {
      throw new RuleEvaluationException("Rule result cannot be converted to " + resultType.getName());
    }
  }
  
  /**
   * Calculates a value for a rule even if it is marked as Invalid
   * (this can be possible if a value is received for that Invalid tag).
   * @return The casted rule result for the given input values.
   * 
   * @param pInputParams Map of value objects related to the input tag ids
   * @param resultType The result type class to which the rule result shall be casted
   */
  public final <T> T forceEvaluate(final Map<Long, RuleInputValue> pInputParams, Class<T> resultType) {
    return TypeConverter.castToType(forceEvaluate(pInputParams), resultType);
  }

  /**
   * @return List of input tag id's
   */
  public abstract Set<Long> getInputTagIds();

  /**
   * Static method that creates a {@link RuleExpression} object due to the given rule string. The following two class can be returned: <li>
   * {@link SimpleRuleExpression}: In case of a rule without conditions <li>
   * {@link ConditionedRuleExpression}: In case of a rule with conditions
   * 
   * @param pExpression the rule as string representation
   * @return An instance of a {@link RuleExpression}
   * @throws RuleFormatException In case of errors in parsing the rule expression string
   */
  public static RuleExpression createExpression(final String pExpression) throws RuleFormatException {
    if (pExpression != null) {
      if (pExpression.indexOf(",") == -1) {
        // simple rule --> the whole expression is the rule
        if (MultipleReturnValueRuleExpression
            .isMultipleReturnValueExpression(pExpression)) {
          return new MultipleReturnValueRuleExpression(pExpression);
        }
        else {
          return new SimpleRuleExpression(pExpression);
        }
      } else {
        return new ConditionedRuleExpression(pExpression);
      }
    } else {
      throw new RuleFormatException("Rule expression cannot be null.");
    }

  }

  /**
   * @return A Collection of Rules, created from the given XML.
   * 
   * @param XMLpath the path where the XML is stored
   * The XML should follow the format below:
   * (default XML format for Benthic pl / sql editor)
   * 
   * <?xml version="1.0" encoding="ISO-8859-1" ?> 
   * <ROWSET name="Query2">
   * <ROW> <TAGRULE><![CDATA[(#141324 < 10)[2],true[3]]]></TAGRULE></ROW>
   * <ROW> <TAGRULE><![CDATA[(#51083 = false) & (#51090 = false)[0],true[3]]]></TAGRULE></ROW> 
   * </ROWSET>
   */
  public static Collection<RuleExpression> createExpressionFromDatabaseXML(final String XMLpath)
      throws RuleFormatException, SAXException, IOException,
      ParserConfigurationException {

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    DocumentBuilder db = dbf.newDocumentBuilder();
    Document dom = db.parse(XMLpath);

    Element doc = dom.getDocumentElement();

    Collection<RuleExpression> ruleStrings = new ArrayList<RuleExpression>();

    NodeList nl;
    nl = doc.getElementsByTagName(RULE_DATABASE_XML_TAG);
    final int size = nl.getLength();
    int currentCount = 0;

    while (currentCount < size) {
      String ruleString = null;

      if (nl.getLength() > 0 && nl.item(currentCount).hasChildNodes()) {
        ruleString = nl.item(currentCount).getFirstChild().getNodeValue();
      }
      RuleExpression ruleExpression = RuleExpression.createExpression(ruleString);
      ruleStrings.add(ruleExpression);
      currentCount++;
    }
    return ruleStrings;
  }

  @Override
  public String toString() {
    
    StringBuffer str = new StringBuffer();
    if (this.expression != null) {
      str.append(this.expression.replace("\n", ""));
    }
    return str.toString();
  }
  
  public String toXml() {

    StringBuffer str = new StringBuffer();
    str.append("<RuleExpression>");
    str.append(this.expression);
    str.append("</RuleExpression>\n");
    return str.toString();
  }
  
  /**
   * The rule type is either SIMPLE or a conditioned rule. In case of a conditioned
   * Rule type the user might want to cast the {@link RuleExpression} into an
   * {@link IConditionedRule} to access internal information of the conditions.
   * @see RuleType
   */
  public RuleType getRuleType() {
    return ruleType;
  }

  /**
   * @return Get the text of the expression (as defined by the user)
   */
  public String getExpression() {
    return this.expression;
  }

}
