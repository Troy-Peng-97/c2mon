package cern.c2mon.cache.actions.commfault;

import cern.c2mon.cache.actions.AbstractCacheTest;
import cern.c2mon.cache.api.C2monCache;
import cern.c2mon.server.cache.test.SupervisionCacheResetRule;
import cern.c2mon.server.cache.test.factory.AbstractCacheObjectFactory;
import cern.c2mon.server.cache.test.factory.CommFaultTagCacheObjectFactory;
import cern.c2mon.server.common.commfault.CommFaultTag;
import cern.c2mon.server.common.supervision.SupervisionStateTag;
import cern.c2mon.shared.common.supervision.SupervisionStatus;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static cern.c2mon.server.common.util.KotlinAPIs.apply;
import static cern.c2mon.shared.common.CacheEvent.UPDATE_ACCEPTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = SupervisionCacheResetRule.class)
public class CommFaultCascadeSpringTest extends AbstractCacheTest<CommFaultTag, CommFaultTag> {

  @Inject
  private C2monCache<CommFaultTag> commFaultCache;

  @Inject
  private C2monCache<SupervisionStateTag> stateTagCache;

  @Rule
  @Inject
  public SupervisionCacheResetRule supervisionCacheResetRule;

  @Override
  protected C2monCache<CommFaultTag> initCache() {
    return commFaultCache;
  }

  @Override
  protected AbstractCacheObjectFactory<CommFaultTag> initFactory() {
    return new CommFaultTagCacheObjectFactory();
  }

  @Test
  @Ignore("This test is failing in Maven runs")
  public void cascadeToState() throws InterruptedException {
    CountDownLatch stateTagUpdate = new CountDownLatch(1);

    stateTagCache.getCacheListenerManager().registerListener(__ -> stateTagUpdate.countDown(), UPDATE_ACCEPTED);

    apply(factory.sampleBase(),
      commFaultTag -> {
        commFaultTag.setSourceTimestamp(Timestamp.from(Instant.now()));
        commFaultTag.setValue(true);
        commFaultCache.put(commFaultTag.getId(), commFaultTag);
      });

    assertTrue(stateTagUpdate.await(200, TimeUnit.MILLISECONDS));
    assertEquals(SupervisionStatus.RUNNING, stateTagCache.get(getSample().getStateTagId()).getSupervisionStatus());
  }
}
