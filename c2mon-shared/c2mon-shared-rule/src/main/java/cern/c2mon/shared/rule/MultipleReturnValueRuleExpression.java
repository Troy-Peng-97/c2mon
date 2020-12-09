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

import java.util.*;

import org.apache.commons.lang.StringUtils;

import cern.c2mon.shared.common.rule.RuleInputValue;

/**
 * Rule expression consisting of OR statements 
 * with different Return Values.
 * 
 * @see http://issues/browse/TIMS-839
 * 
 * @author ekoufaki
 */
public class MultipleReturnValueRuleExpression extends RuleExpression 
    implements IConditionedRule, IRuleCondition {

  public MultipleReturnValueRuleExpression(final String pExpression) throws RuleFormatException {
    super(pExpression, RuleType.ConditionedRule);
    this.conditions = splitToConditions(pExpression);
  }
  
  /**
   * @return True if the rule is a {@link MultipleReturnValueRuleExpression}
   * @see http://issues/browse/TIMS-839
   * 
   * @param rule the rule to be checked
   */
  public static boolean isMultipleReturnValueExpression(final String rule) {
    final int bracketsCount = StringUtils.countMatches(rule.toString(), "[");
    return bracketsCount > 1;
  }

  /**
   * Ordered conditions making up this rule expression.
   */
  private List<IRuleCondition> conditions;

  /**
   * @return Splits the given expression to multiple {@link IRuleCondition}.
   * 
   * @param expression String representing a {@link MultipleReturnValueRuleExpression}
   * @throws RuleFormatException in case the rule expression has incorrect syntax
   */
  public static List<IRuleCondition> splitToConditions(final String expression) throws RuleFormatException {

    final String[] subConditions = expression.split("]");
    List<IRuleCondition> conditions = new ArrayList<IRuleCondition>();

    for (int i = 0; i != subConditions.length; i++) {
      if (i > 0) {
        subConditions[i] = subConditions[i].substring(subConditions[i].indexOf("|") + 1);
      }
      conditions.add(new DefaultRuleCondition(subConditions[i].trim() + "]"));
    }
    return conditions;
  }

  @Override
  public Object evaluate(final Map<Long, RuleInputValue> pInputParams) throws RuleEvaluationException {

    Object result = null;
    Iterator<IRuleCondition> i = conditions.iterator();
    
    boolean isInvalid = false;
    
    while (result == null && i.hasNext()) {

      try {
        result = i.next().evaluate(pInputParams);
      } catch (final RuleEvaluationException e) {
        isInvalid = true;
      }
    }
    if (result == null && isInvalid) {
      throw new RuleEvaluationException();
    }
    return result;
  }

  @Override
  public Object forceEvaluate(final Map<Long, RuleInputValue> pInputParams) {

    try {
      return evaluate(pInputParams);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public RuleValidationReport validate(final Map<Long, RuleInputValue> pInputParams) {

    try {

      Object result = null;
      Iterator<IRuleCondition> i = conditions.iterator();
      while (result == null && i.hasNext()) {
        result = i.next().evaluate(pInputParams);
      }
      return new RuleValidationReport(true);
    } catch (final Exception e) {
      return new RuleValidationReport(false, e.getMessage());
    }
  }

  /**
   * @return identifiers of all input tags used in this rule expression (and its dependent conditions).
   */
  @Override
  public Set<Long> getInputTagIds() {
    Set<Long> ids = new LinkedHashSet<Long>();
    Iterator<IRuleCondition> i = conditions.iterator();
    while (i.hasNext()) {
      ids.addAll(i.next().getInputTagIds());
    }
    return ids;
  }

  /**
   * @return a Deque of IRuleCondition objects making up this conditioned rule
   */
  public List<IRuleCondition> getConditions() {
    return conditions;
  }

  @Override
  public Object getResultValue() {
    
    Collection<Object> resultValues = new ArrayList<Object>();
    Iterator<IRuleCondition> i = conditions.iterator();
    while (i.hasNext()) {
      resultValues.add(i.next().getResultValue());
    }
    return resultValues;
  }
  
  /**
   * Clone method implementation
   */
  public Object clone() {

    MultipleReturnValueRuleExpression clone = (MultipleReturnValueRuleExpression) super.clone();

    clone.conditions = new ArrayList<IRuleCondition>();
    Iterator<IRuleCondition> i = conditions.iterator();
    while (i.hasNext()) {
      clone.conditions.add((IRuleCondition) i.next().clone());
    }
    return clone;
  }
}
