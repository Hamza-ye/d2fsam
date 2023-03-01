/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.common;

import org.nmcpye.am.attribute.Attribute;
import org.nmcpye.am.attribute.AttributeValue;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;

/**
 * @author Lars Helge Overland
 */
public interface IdentifiableObjectManager {
    String ID = IdentifiableObjectManager.class.getName();

    void save(IdentifiableObject object);

    void save(IdentifiableObject object, boolean clearSharing);

    void save(List<IdentifiableObject> objects);

    void update(IdentifiableObject object);

    void update(IdentifiableObject object, User user);

    void update(List<IdentifiableObject> objects);

    void update(List<IdentifiableObject> objects, User user);

    void delete(IdentifiableObject object);

    void delete(IdentifiableObject object, User user);

    /**
     * Lookup objects of unknown type.
     * <p>
     * If the type is known at compile time this method should not be used.
     * Instead, use
     * {@link IdentifiableObjectManager#get(Class, String)}.
     *
     * @param uid a UID of an object of unknown type
     * @return The {@link IdentifiableObject} with the given UID
     */
    @Nonnull
    Optional<? extends IdentifiableObject> find(@Nonnull String uid);

    /**
     * Lookup objects of a specific type by database ID.
     *
     * @param type the object class type.
     * @param id   object's database ID
     * @return the found object
     */
    @CheckForNull
    <T extends IdentifiableObject> T get(@Nonnull Class<T> type, long id);

    /**
     * Retrieves the object of the given type and UID, or null if no object
     * exists.
     *
     * @param type the object class type.
     * @param uid  the UID.
     * @return the object with the given UID.
     */
    @CheckForNull
    <T extends IdentifiableObject> T get(@Nonnull Class<T> type, @Nonnull String uid);

    /**
     * Retrieves the object of the given type and UID, throws exception if no
     * object exists.
     *
     * @param type the object class type.
     * @param uid  the UID.
     * @return the object with the given UID.
     * @throws IllegalQueryException if no object exists.
     */
    @Nonnull
    <T extends IdentifiableObject> T load(@Nonnull Class<T> type, @Nonnull String uid) throws IllegalQueryException;

    /**
     * Retrieves the object of the given type and UID, throws exception using
     * the given error code if no object exists.
     *
     * @param type      the object class type.
     * @param errorCode the {@link ErrorCode} to use for the exception.
     * @param uid       the UID.
     * @return the object with the given UID.
     * @throws IllegalQueryException if no object exists.
     */
    @Nonnull
    <T extends IdentifiableObject> T load(@Nonnull Class<T> type, @Nonnull ErrorCode errorCode, @Nonnull String uid)
        throws IllegalQueryException;

    <T extends IdentifiableObject> boolean exists(@Nonnull Class<T> type, @Nonnull String uid);

    @CheckForNull
    <T extends IdentifiableObject> T get(@Nonnull Collection<Class<? extends T>> types, @Nonnull String uid);

    @CheckForNull
    <T extends IdentifiableObject> T get(@Nonnull Collection<Class<? extends T>> types, @Nonnull IdScheme idScheme, @Nonnull String value);

    /**
     * Retrieves the object of the given type and code, or null if no object
     * exists.
     *
     * @param type the object class type.
     * @param code the code.
     * @return the object with the given code.
     */
    @CheckForNull
    <T extends IdentifiableObject> T getByCode(@Nonnull Class<T> type, @Nonnull String code);

    /**
     * Retrieves the object of the given type and code, throws exception if no
     * object exists.
     *
     * @param type the object class type.
     * @param code the code.
     * @return the object with the given code.
     * @throws IllegalQueryException if no object exists.
     */
    @Nonnull
    <T extends IdentifiableObject> T loadByCode(@Nonnull Class<T> type, @Nonnull String code) throws IllegalQueryException;

    @Nonnull
    <T extends IdentifiableObject> List<T> getByCode(@Nonnull Class<T> type, @Nonnull Collection<String> codes);

    @CheckForNull
    <T extends IdentifiableObject> T getByName(@Nonnull Class<T> type, @Nonnull String name);

    <T extends IdentifiableObject> T search(Class<T> clazz, String query);

    <T extends IdentifiableObject> List<T> filter(Class<T> clazz, String query);

    <T extends IdentifiableObject> List<T> getAll(Class<T> clazz);

    <T extends IdentifiableObject> List<T> getDataWriteAll(Class<T> clazz);

    <T extends IdentifiableObject> List<T> getDataReadAll(Class<T> clazz);

    <T extends IdentifiableObject> List<T> getAllSorted(Class<T> clazz);

    <T extends IdentifiableObject> List<T> getByUid(Class<T> clazz, Collection<String> uids);

    /**
     * Retrieves the objects of the given type and collection of UIDs, throws
     * exception is any object does not exist.
     *
     * @param type the object class type.
     * @param uids the collection of UIDs.
     * @return a list of objects.
     * @throws IllegalQueryException if any object does not exist.
     */

    @Nonnull
    <T extends IdentifiableObject> List<T> getByUid(@Nonnull Collection<Class<? extends T>> types, @Nonnull Collection<String> uids);

    @Nonnull
    <T extends IdentifiableObject> List<T> loadByUid(@Nonnull Class<T> type, @CheckForNull Collection<String> uids)
        throws IllegalQueryException;

    <T extends IdentifiableObject> List<T> getById(Class<T> clazz, Collection<Long> ids);

    <T extends IdentifiableObject> List<T> getOrdered(Class<T> clazz, IdScheme idScheme, Collection<String> values);

    <T extends IdentifiableObject> List<T> getByUidOrdered(Class<T> clazz, List<String> uids);

    <T extends IdentifiableObject> List<T> getLikeName(Class<T> clazz, String name);

    <T extends IdentifiableObject> List<T> getLikeName(Class<T> clazz, String name, boolean caseSensitive);

    <T extends IdentifiableObject> List<T> getBetweenSorted(Class<T> clazz, int first, int max);

    <T extends IdentifiableObject> List<T> getBetweenLikeName(Class<T> clazz, Set<String> words, int first, int max);

    <T extends IdentifiableObject> Instant getLastUpdated(Class<T> clazz);

    <T extends IdentifiableObject> Map<String, T> getIdMap(Class<T> clazz, IdentifiableProperty property);

    <T extends IdentifiableObject> Map<String, T> getIdMap(Class<T> clazz, IdScheme idScheme);

    <T extends IdentifiableObject> Map<String, T> getIdMapNoAcl(Class<T> clazz, IdentifiableProperty property);

    <T extends IdentifiableObject> Map<String, T> getIdMapNoAcl(Class<T> clazz, IdScheme idScheme);

    <T extends IdentifiableObject> List<T> getObjects(Class<T> clazz, IdentifiableProperty property, Collection<String> identifiers);

    <T extends IdentifiableObject> List<T> getObjects(Class<T> clazz, Collection<Long> identifiers);

    <T extends IdentifiableObject> T getObject(Class<T> clazz, IdentifiableProperty property, String value);

    <T extends IdentifiableObject> T getObject(Class<T> clazz, IdScheme idScheme, String value);

    IdentifiableObject getObject(String uid, String simpleClassName);

    IdentifiableObject getObject(long id, String simpleClassName);

    <T extends IdentifiableObject> int getCount(Class<T> clazz);

    <T extends IdentifiableObject> int getCountByCreated(Class<T> clazz, Instant created);

    <T extends IdentifiableObject> int getCountByLastUpdated(Class<T> clazz, Instant lastUpdated);

    <T extends DimensionalObject> List<T> getDataDimensions(Class<T> clazz);

    <T extends DimensionalObject> List<T> getDataDimensionsNoAcl(Class<T> clazz);

    void refresh(Object object);

    /**
     * Resets all properties that are not owned by the object type.
     *
     * @param object object to reset
     */
    void resetNonOwnerProperties(Object object);

    void flush();

    void clear();

    void evict(Object object);

    void updateTranslations(IdentifiableObject persistedObject, Set<Translation> translations);

    <T extends IdentifiableObject> List<T> getNoAcl(Class<T> clazz, Collection<String> uids);

    //    boolean isDefault(IdentifiableObject object);

    List<String> getUidsCreatedBefore(Class<? extends IdentifiableObject> klass, Date date);

    /**
     * Remove given UserGroup UID from all sharing records in database
     */
    void removeUserGroupFromSharing(@Nonnull String userGroupUid);

    // -------------------------------------------------------------------------
    // NO ACL
    // -------------------------------------------------------------------------

    <T extends IdentifiableObject> T getNoAcl(Class<T> clazz, String uid);

    <T extends IdentifiableObject> void updateNoAcl(T object);

    <T extends IdentifiableObject> List<T> getAllNoAcl(Class<T> clazz);

    @Nonnull
    <T extends IdentifiableObject> List<T> getAllByAttributes(@Nonnull Class<T> type,
                                                              @Nonnull List<Attribute> attributes);

    @Nonnull
    <T extends IdentifiableObject> List<AttributeValue> getAllValuesByAttributes(@Nonnull Class<T> type,
                                                                                 @Nonnull List<Attribute> attributes);

    <T extends IdentifiableObject> long countAllValuesByAttributes(@Nonnull Class<T> type,
                                                                   @Nonnull List<Attribute> attributes);

    @Nonnull
    <T extends IdentifiableObject> List<T> getByAttributeAndValue(@Nonnull Class<T> type, @Nonnull Attribute attribute,
                                                                  @Nonnull String value);

    <T extends IdentifiableObject> boolean isAttributeValueUnique(@Nonnull Class<T> type,
                                                                  @Nonnull T object, @Nonnull AttributeValue attributeValue);

    <T extends IdentifiableObject> boolean isAttributeValueUnique(@Nonnull Class<T> type,
                                                                  @Nonnull T object, @Nonnull Attribute attribute, @Nonnull String value);

    @Nonnull
    <T extends IdentifiableObject> List<T> getAllByAttributeAndValues(@Nonnull Class<T> type,
                                                                      @Nonnull Attribute attribute, @Nonnull List<String> values);
}
