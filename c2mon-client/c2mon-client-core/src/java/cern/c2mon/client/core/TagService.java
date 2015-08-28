/*******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 *
 * Copyright (C) 2004 - 2011 CERN. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Author: TIM team, tim.support@cern.ch
 ******************************************************************************/
package cern.c2mon.client.core;

import java.util.Collection;
import java.util.Set;

import cern.c2mon.client.common.listener.DataTagListener;
import cern.c2mon.client.common.listener.DataTagUpdateListener;
import cern.c2mon.client.common.tag.ClientDataTagValue;
import cern.c2mon.client.core.cache.CacheSynchronizationException;

/**
 * This interface describes the methods which are provided by
 * the C2MON TagService singleton. 
 * <p>
 * The tag service allows e.g. subscribing listeners to tags
 * to get informed when a new update is received.
 *
 * @author Matthias Braeger
 */
public interface TagService {

  /**
   * Use this method for registering a listener and to receive the current (initial) values and updates
   * for the list of specified data tags.<p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before. <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   *
   * @param dataTagIds A collection of data tag IDs
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tags. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTags(Set, DataTagListener)
   * @see C2monSupervisionManager#isServerConnectionWorking()
   */
  void subscribeDataTags(final Set<Long> dataTagIds, final DataTagUpdateListener listener) throws CacheSynchronizationException;

  /**
   * Registers a listener to receive the current (initial) value and updates for one specific data tag.<p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before. <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   *
   * @param tagId The unique identifier of the data tag you want to subscribe to
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tag. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTag(Long, DataTagListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  void subscribeDataTag(final Long tagId, final DataTagUpdateListener listener) throws CacheSynchronizationException;
  
  /**
   * Registers a listener to receive the current (initial) values and updates for all tags where the
   * name matches the regular expression. If the string contains no special characters the server will
   * only return the tag whose name is equals the string.
   * <p>
   * <b>Please note</b>, that the call is NOT constantly checking in the background whether new tags have been
   * configured on the server that would match the given list!
   * <p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before.
   * <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   * <p>
   * <p />
   * Expressions are always case insensitive
   * <p />
   * The following special characters are supported:
   * <ul>
   * <li> '?' - match any one single character </li>
   * <li> '*' - match any multiple character(s) (including zero) </li>
   * </ul>
   * The supported wildcard characters can be escaped with a backslash '\', and a literal backslash can be included with '\\'
   * <p />
   * WARN: Expressions starting with a leading wildcard character are potentially very expensive (ie. full scan) for indexed caches 
   *
   * @param regex A concrete tag name or wildcard expression, which shall be used to subscribe to all matching data tags.
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tag. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTag(String, DataTagListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  void subscribeDataTagsByName(final String regex, final DataTagUpdateListener listener) throws CacheSynchronizationException;

  /**
   * Registers a listener to receive the current (initial) values and updates for all tags where the
   * name matches the regular expression. If the string contains no special characters the server will
   * only return the tag whose name is equals the string.
   * <p>
   * <b>Please note</b>, that the call is NOT constantly checking in the background whether new tags have been
   * configured on the server that would match the given list!
   * <p>
   * The method will return the initial value(s) of the subscribed tag(s) to {@link DataTagListener#onInitialUpdate(Collection)}. <b>Please note</b>
   * that the {@link DataTagListener#onUpdate(ClientDataTagValue)} method will then not receive the initial value.
   * <p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before.
   * <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   * <p>
   * <p />
   * Expressions are always case insensitive
   * <p />
   * The following special characters are supported:
   * <ul>
   * <li> '?' - match any one single character </li>
   * <li> '*' - match any multiple character(s) (including zero) </li>
   * </ul>
   * The supported wildcard characters can be escaped with a backslash '\', and a literal backslash can be included with '\\'
   * <p />
   * WARN: Expressions starting with a leading wildcard character are potentially very expensive (ie. full scan) for indexed caches 
   *
   * @param regex A concrete tag name or wildcard expression, which shall be used to subscribe to all matching data tags.
   * @param listener the listener which shall be registered and which will receive the initial values in 
   *                 a separate method
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tag. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTag(String, DataTagListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  void subscribeDataTagsByName(final String regex, final DataTagListener listener) throws CacheSynchronizationException;

  
  /**
   * Registers a listener to receive the current (initial) values and updates for all tags, where the
   * name matches the regular expression.
   * <p>
   * <b>Please note</b>, that the call is NOT constantly checking in the background whether new tags have been
   * configured on the server that would match the given list!
   * <p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before.
   * <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   * <p>
   * <p />
   * Expressions are always case insensitive
   * <p />
   * The following special characters are supported:
   * <ul>
   * <li> '?' - match any one single character </li>
   * <li> '*' - match any multiple character(s) (including zero) </li>
   * </ul>
   * The supported wildcard characters can be escaped with a backslash '\', and a literal backslash can be included with '\\'
   * <p />
   * WARN: Expressions starting with a leading wildcard character are potentially very expensive (ie. full scan) for indexed caches 
   *
   * @param regexList List of concrete tag names and/or wildcard expressions, which shall be used to subscribe to all matching data tags.
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tag. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTag(String, DataTagListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  void subscribeDataTagsByName(final Set<String> regexList, final DataTagUpdateListener listener) throws CacheSynchronizationException;

  /**
   * Registers a listener to receive the current (initial) values and updates for all tags, where the
   * name matches the provided string and/or regular expression list.
   * <p>
   * <b>Please note</b>, that the call is NOT constantly checking in the background whether new tags have been
   * configured on the server that would match the given list!
   * <p>
   * The method will return the initial values of the subscribed tags to {@link DataTagListener#onInitialUpdate(Collection)}. <b>Please note</b>
   * that the {@link DataTagListener#onUpdate(ClientDataTagValue)} method will then not receive the initial value.
   * <p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before.
   * <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   * <p>
   * <p />
   * Expressions are always case insensitive
   * <p />
   * The following special characters are supported:
   * <ul>
   * <li> '?' - match any one single character </li>
   * <li> '*' - match any multiple character(s) (including zero) </li>
   * </ul>
   * The supported wildcard characters can be escaped with a backslash '\', and a literal backslash can be included with '\\'
   * <p />
   * WARN: Expressions starting with a leading wildcard character are potentially very expensive (ie. full scan) for indexed caches 
   *
   * @param regexList List of concrete tag names and/or wildcard expressions, which shall be used to subscribe to all matching data tags.
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tag. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTag(String, DataTagListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  void subscribeDataTagsByName(final Set<String> regexList, final DataTagListener listener) throws CacheSynchronizationException;

  
  /**
   * Registers a listener to receive updates for specific data tags.
   * The method will return the initial values of the subscribed tags to {@link DataTagListener#onInitialUpdate(Collection)}.
   * <b>Please note</b> that the {@link DataTagListener#onUpdate(ClientDataTagValue)} method will then not
   * receive the initial values.<p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before. <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   *
   * @param dataTagIds A collection of data tag IDs
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tags. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTags(Set, DataTagUpdateListener)
   * @see C2monSupervisionManager#isServerConnectionWorking()
   */
  void subscribeDataTags(final Set<Long> dataTagIds, final DataTagListener listener) throws CacheSynchronizationException;

  /**
   * Registers a listener to receive updates for a specific data tag.
   * <p>
   * The method will return the initial value of the subscribed tag to {@link DataTagListener#onInitialUpdate(Collection)}. <b>Please note</b>
   * that the {@link DataTagListener#onUpdate(ClientDataTagValue)} method will then not receive the initial value.
   * <p>
   * The C2MON client API will handle for you in the background the initialization of the data
   * tags with the C2MON server, if this was not already done before. <p>
   * Your listener will be informed about new updates via the <code>onUpdate(ClientDataTagValue)</code>
   * method.
   *
   * @param tagId The unique identifier of the data tag you want to subscribe to
   * @param listener the listener which shall be registered
   * @throws CacheSynchronizationException In case a communication problem with the C2MON server
   *         occurs while subscribing to the tag. In that case the {@link TagService} will
   *         rollback the subscription.
   * @see #subscribeDataTag(Long, DataTagUpdateListener)
   * @see C2monSupervisionManager#isServerConnectionWorking()
   */
  void subscribeDataTag(final Long tagId, final DataTagListener listener) throws CacheSynchronizationException;

  
  
  /**
   * Use this method for unregistering a listener from receiving updates for specific data tags.
   *
   * @param dataTagIds A collection of data tag id's
   * @param listener the listener which shall be registered
   */
  void unsubscribeDataTags(final Set<Long> dataTagIds, final DataTagUpdateListener listener);

  /**
   * Unregisters a listener from receiving updates for specific data tag.
   *
   * @param dataTagId The unique identifier of the data tag from which we want to unsubscribe
   * @param listener the listener which shall be registered
   */
  void unsubscribeDataTag(final Long dataTagId, final DataTagUpdateListener listener);


  /**
   * Use this method to unsubscribe from all previously registered data tags.
   * @param listener the listener which shall be registered
   */
  void unsubscribeAllDataTags(final DataTagUpdateListener listener);


  /**
   * Returns for a given listener a copy of all subscribed data tags with
   * their current state as <code>ClientDataTagValue</code> instances.
   *
   * @param listener The listener for which we want to get the data tags
   *        subscriptions
   * @return A collection of <code>ClientDataTag</code> objects
   */
  Collection<ClientDataTagValue> getAllSubscribedDataTags(final DataTagUpdateListener listener);

  /**
   * Returns for a given listener a list of all subscribed data tags ids.
   *
   * @param listener The listener for which we want to get the data tags
   *        subscriptions
   * @return A collection of tag ids
   */
  Set<Long> getAllSubscribedDataTagIds(final DataTagUpdateListener listener);

  /**
   * Returns for the given id a copy of the cached data tag.
   * If the tag is not in the local cache it will try to fetch it from the server.
   * In case of an unknown tag id the result will be an empty {@link ClientDataTagValue}
   * object.
   * <p>
   * <b>Please notice</b>, that this method call does not write anything to the local
   * cache. This means that you might increase the server load when asking constantly
   * for tags on which no {@link DataTagUpdateListener} is subscribed to.
   *
   * @param tagId A data tag id
   * @return A <code>ClientDataTag</code> object
   * @throws RuntimeException In case a communication problems with JMS or the C2MON server
   *         occurs while trying to retrieve tag information.
   * @see #getDataTags(Collection)
   * @see #subscribeDataTags(Set, DataTagUpdateListener)
   * @see C2monSupervisionManager#isServerConnectionWorking()
   */
  ClientDataTagValue getDataTag(final Long tagId);

  /**
   * Returns for every valid id of the list a copy of the cached data tag.
   * If the value is not in the local cache it will try to fetch it from the server.
   * However, in case of an unknown tag id the corresponding tag might be missing.
   * <p>
   * <b>Please notice</b>, that this method call does not write anything to the local
   * cache. This means that you might increase the server load when asking constantly
   * for tags on which no {@link DataTagUpdateListener} is subscribed to.
   *
   * @param tagIds A collection of data tag id's
   * @return A collection of all <code>ClientDataTag</code> objects
   * @throws RuntimeException In case a communication problems with JMS or the C2MON server
   *         occurs while trying to retrieve tag information.
   * @see #subscribeDataTags(Set, DataTagUpdateListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  Collection<ClientDataTagValue> getDataTags(final Collection<Long> tagIds);
  
  /**
   * Returns a list of tags which match the given wilcard expression. Different to
   * {@link #getDataTags(Collection)} this call will always result in a server request.
   * <p />
   * Expressions are always case insensitive
   * <p />
   * The following special characters are supported:
   * <ul>
   * <li> '?' - match any one single character </li>
   * <li> '*' - match any multiple character(s) (including zero) </li>
   * </ul>
   * The supported wildcard characters can be escaped with a backslash '\', and a literal backslash can be included with '\\'
   * <p />
   * WARN: Expressions starting with a leading wildcard character are potentially very expensive (ie. full scan) for indexed caches 
   *
   * @param tagIds A collection of data tag id's
   * @return A collection of all <code>ClientDataTag</code> objects
   * @throws RuntimeException In case a communication problems with JMS or the C2MON server
   *         occurs while trying to retrieve tag information.
   * @see #subscribeDataTags(Set, DataTagUpdateListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  Collection<ClientDataTagValue> findDataTagsByName(final String regex);
  
  /**
   * Returns a list of all tags which match the given list of wilcard expressions. Different to
   * {@link #getDataTags(Collection)} this call will always result in a server request.
   * <p />
   * Expressions are always case insensitive
   * <p />
   * The following special characters are supported:
   * <ul>
   * <li> '?' - match any one single character </li>
   * <li> '*' - match any multiple character(s) (including zero) </li>
   * </ul>
   * The supported wildcard characters can be escaped with a backslash '\', and a literal backslash can be included with '\\'
   * <p />
   * WARN: Expressions starting with a leading wildcard character are potentially very expensive (ie. full scan) for indexed caches 
   *
   * @param tagIds A collection of data tag id's
   * @return A collection of all <code>ClientDataTag</code> objects
   * @throws RuntimeException In case a communication problems with JMS or the C2MON server
   *         occurs while trying to retrieve tag information.
   * @see #subscribeDataTags(Set, DataTagUpdateListener)
   * @see C2monSupervisionManager#isServerConnectionWorking();
   */
  Collection<ClientDataTagValue> findDataTagsByName(final Set<String> regexList);

  /**
   * Returns the total number of subscribed tags in the local cache (cache size).
   * @return the cache size
   */
  int getCacheSize();

  /**
   * This method is used to synchronize subscribed data tags with the
   * server. It will ask the server to send the actual tag information for
   * all subscribed data tags. The C2MON client API will then send an update
   * to all subscribed listeners.
   * @throws CacheSynchronizationException In case a communicatin problem with the C2MON server
   *         occurs while refreshing to the tags.
   */
  void refreshDataTags() throws CacheSynchronizationException;

  /**
   * This method is used to synchronize a list subscribed data tags with the
   * server. It will ask the server to send the actual tag information for
   * all subscribed tags of the given list. The C2MON client API will then send
   * an update to all subscribed listeners.
   *
   * @param tagIds A collection of data tag id's
   * @throws NullPointerException if the Collection is <code>null</code>.
   * @throws CacheSynchronizationException In case a communicatin problem with the C2MON server
   *         occurs while refreshing to the tags.
   */
  void refreshDataTags(Collection<Long> tagIds) throws CacheSynchronizationException;

  /**
   * Checks whether the given listener is subscribed to any data tags.
   *
   * @param listener the listener to check subscriptions for
   * @return true if the listener is subscribed, false otherwise
   */
  boolean isSubscribed(DataTagUpdateListener listener);
}
