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
package cern.c2mon.server.rule.evaluation;

import java.sql.Timestamp;

import org.junit.Before;

import cern.c2mon.shared.common.datatag.util.TagQualityStatus;

import static org.junit.Assert.*;


/**
 * It is difficult to test the full <code>RuleUpdateBuffer</code> logic,
 * since it uses JNDI and needs a J2EE container. However, you can comment
 * out the call to the <code>DataTagFacadeBean</code> and test the basic
 * behavior.
 * 
 * TODO need to adapt this to C2MON code ... only started so far
 *
 * @author Matthias Braeger
 */
public class RuleUpdateBufferTest {
  
  /** Singleton instance */
  private RuleUpdateBuffer rub = null;
  
  
  /**
   * It is called before running any of the tests defined on this class
   */
  @Before
  protected final void setUp() {
    //rub = RuleUpdateBuffer.getInstance();
  }
  
  
  public final void testUpdate() {
    for (int i = 0; i < 100; i++) {
      rub.update(new Long(1234L), (Object) ("test " + i), "testUpdate " + i, new Timestamp(System.currentTimeMillis()));
//      try {Thread.sleep(40);} catch (InterruptedException e) {}
      rub.update(new Long(1235L), (Object) ("test " + i), "testUpdate " + i, new Timestamp(System.currentTimeMillis()));
      try {Thread.sleep(65);} catch (InterruptedException e) {}
      System.out.println(i);
    }
    
    try {Thread.sleep(1000);} catch (InterruptedException e) {}
    assertTrue(true);
  }
  
  public final void testInvalidate() {
    for (int i = 0; i < 100; i++) {
      rub.invalidate(new Long(12345L), TagQualityStatus.INACCESSIBLE, "testUpdate " + i, new Timestamp(System.currentTimeMillis()));
      try {Thread.sleep(60);} catch (InterruptedException e) {}
      System.out.println(i);
    }
    
    try {Thread.sleep(1000);} catch (InterruptedException e) {}
    assertTrue(true);
  }
}
