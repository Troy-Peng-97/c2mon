package cern.c2mon.server.cache.commfault;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cern.c2mon.cache.api.Cache;
import cern.c2mon.server.common.commfault.CommFaultTag;
import cern.c2mon.server.common.commfault.CommFaultTagCacheObject;
import cern.c2mon.server.common.equipment.AbstractEquipment;

/**
 * @author Szymon Halastra
 */
@Slf4j
@Service
public class CommFaultService {

  private final Cache<Long, CommFaultTag> commFaultTagCacheRef;

  @Autowired
  public CommFaultService(final Cache<Long, CommFaultTag> commFaultTagCacheRef) {
    this.commFaultTagCacheRef = commFaultTagCacheRef;
  }

  public Cache getCache() {
    return commFaultTagCacheRef;
  }

  public void generateFromEquipment(AbstractEquipment abstractEquipment) {
    CommFaultTag commFaultTag = new CommFaultTagCacheObject(abstractEquipment.getCommFaultTagId(), abstractEquipment.getId(),
            abstractEquipment.getName(), abstractEquipment.getAliveTagId(), abstractEquipment.getStateTagId());
    commFaultTagCacheRef.put(commFaultTag.getId(), commFaultTag);
  }
}