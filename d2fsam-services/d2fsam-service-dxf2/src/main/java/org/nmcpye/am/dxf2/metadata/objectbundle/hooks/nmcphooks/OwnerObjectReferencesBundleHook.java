///*
// * Copyright (c) 2004-2022, University of Oslo
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright notice, this
// * list of conditions and the following disclaimer.
// *
// * Redistributions in binary form must reproduce the above copyright notice,
// * this list of conditions and the following disclaimer in the documentation
// * and/or other materials provided with the distribution.
// * Neither the name of the HISP project nor the names of its contributors may
// * be used to endorse or promote products derived from this software without
// * specific prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//package org.nmcpye.am.dxf2.metadata.objectbundle.hooks.nmcphooks;
//
//import lombok.AllArgsConstructor;
//import org.nmcpye.am.common.IdentifiableObject;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
//import org.nmcpye.am.dxf2.metadata.objectbundle.hooks.AbstractObjectBundleHook;
//import org.nmcpye.am.hibernate.HibernateProxyUtils;
//import org.nmcpye.am.program.Program;
//import org.nmcpye.am.program.ProgramStage;
//import org.nmcpye.am.schema.Property;
//import org.nmcpye.am.schema.Schema;
//import org.nmcpye.am.system.util.ReflectionUtils;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Hamza
// */
//@Component
//@Profile({"testdev", "testprod"})
//@AllArgsConstructor
//public class OwnerObjectReferencesBundleHook
//    extends AbstractObjectBundleHook<IdentifiableObject> {
//    // NMCP
//
//
//    @Override
//    public <E extends IdentifiableObject> void postTypeImport(Class<E> klass, List<E> objects, ObjectBundle bundle) {
//        super.postTypeImport(klass, objects, bundle);
//    }
//
//    @Override
//    public void postCreate(IdentifiableObject object, ObjectBundle bundle) {
////        builder.append(" ").append(klass.getName());
//        Class<?> klass = HibernateProxyUtils.getRealClass(object);
//        Schema schema = schemaService.getDynamicSchema(klass);
//
//        Iterable<? extends IdentifiableObject> owningObjects = bundle.getObjects(HibernateProxyUtils.getRealClass(object));
//        Map<String, Map<String, Object>> owningObjectReferences = bundle.getObjectReferences(HibernateProxyUtils.getRealClass(object));
//
//        if (owningObjectReferences == null || owningObjectReferences.isEmpty()) {
//            return;
//        }
//
//        for (IdentifiableObject identifiableObject : owningObjects) {
//            IdentifiableObject owningObject = identifiableObject;
//
//            owningObject = bundle.getPreheat().get(bundle.getPreheatIdentifier(), owningObject);
//
//            Map<String, Object> owningObjectReferenceMap = owningObjectReferences.get(identifiableObject.getUid());
//
//            if (owningObject == null || owningObjectReferenceMap == null || owningObjectReferenceMap.isEmpty()) {
//                continue;
//            }
////
////            Program program = (Program) owningObjectReferenceMap.get("program");
////            owningObject.setProgram(program);
////
////            preheatService.connectReferences(owningObject, bundle.getPreheat(), bundle.getPreheatIdentifier());
////
////            getSession().update(owningObject);
//        }
////
////        Schema schema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(object));
////
////        if (schema == null || schema.getEmbeddedObjectProperties().isEmpty()) {
////            return;
////        }
////
////        Collection<Property> properties = schema.getEmbeddedObjectProperties().values();
////
////        handleEmbeddedObjectsBackReference(object, bundle, properties);
//    }
//
//    private void handleEmbeddedObjectsBackReference(IdentifiableObject parent, ObjectBundle bundle,
//                                       Collection<Property> embeddedProperties) {
//        for (Property property : embeddedProperties) {
//            Object embeddedPropertyObject = ReflectionUtils.invokeMethod(parent, property.getGetterMethod());
//
//            if (property.isCollection()) {
//                Collection<?> objects = (Collection<?>) embeddedPropertyObject;
//                objects.forEach(itemPropertyObject -> {
//                    handleBackReference(parent, itemPropertyObject, bundle, property);
//                });
//            } else {
//                handleBackReference(parent, embeddedPropertyObject, bundle, property);
//            }
//        }
//    }
//
//    // handleEmbeddedAnalyticalProperty(IdentifiableObject parentObject, Object embeddedObject, ObjectBundle bundle, property)
//    // handleProperty(Object object, ObjectBundle bundle, Property property)
//    // handleProperty(embeddedPropertyObject, bundle, property);
//    private void handleBackReference(IdentifiableObject parentObject, Object embeddedObject,
//                                     ObjectBundle bundle, Property embeddedProperty) {
//        Schema parentSchema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(parentObject));
//
//        if (parentSchema == null || embeddedObject == null || bundle == null
//            || embeddedProperty == null) {
//            return;
//        }
//
//        Schema embeddedSchema = schemaService.getDynamicSchema(HibernateProxyUtils.getRealClass(embeddedObject));
//
//        if (!embeddedSchema.havePersistedProperty(parentSchema.getName()))
//            return;
//
//        Property parentProperty = embeddedSchema.getProperty(parentSchema.getName());
//        ReflectionUtils.invokeMethod(embeddedObject, parentProperty.getSetterMethod(), parentObject);
//
//        preheatService.connectReferences(embeddedObject, bundle.getPreheat(), bundle.getPreheatIdentifier());
//
//        getSession().update(parentObject);
//    }
//    /////////////////////////////////////////////
//}
