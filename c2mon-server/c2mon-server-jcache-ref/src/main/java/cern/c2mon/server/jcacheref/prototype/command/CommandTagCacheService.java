package cern.c2mon.server.jcacheref.prototype.command;

import java.util.Iterator;

import javax.cache.Cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cern.c2mon.shared.common.command.CommandTag;

/**
 * @author Szymon Halastra
 */

@Service
public class CommandTagCacheService {

//  @Autowired
//  public CommandTagCacheService() {
//  }

  public Long getCommandTagId(final String name) {
    Long commandTagKey = null;

    Iterator<Cache.Entry<Long, CommandTag>> entries = null /*commandTagCache.iterator()*/;

    while(entries.hasNext()) {
      Cache.Entry<Long, CommandTag> entry = entries.next();

      if(entry.getValue().getName().equals(name)) {
        commandTagKey = entry.getKey();

        return commandTagKey;
      }
    }

    return commandTagKey;
  }
}
