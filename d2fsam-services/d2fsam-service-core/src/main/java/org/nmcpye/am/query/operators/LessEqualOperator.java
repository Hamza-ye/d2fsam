/*
 * Copyright (c) 2004-2022, University of Oslo
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
package org.nmcpye.am.query.operators;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.nmcpye.am.query.QueryException;
import org.nmcpye.am.query.QueryUtils;
import org.nmcpye.am.query.Type;
import org.nmcpye.am.query.Typed;
import org.nmcpye.am.query.planner.QueryPath;
import org.nmcpye.am.schema.Property;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class LessEqualOperator<T extends Comparable<? super T>> extends Operator<T> {
    public LessEqualOperator(T arg) {
        super("le", Typed.from(String.class, Boolean.class, Number.class, Date.class, Instant.class, LocalDateTime.class), arg);
    }

    @Override
    public Criterion getHibernateCriterion(QueryPath queryPath) {
        Property property = queryPath.getProperty();

        if (property.isCollection()) {
            Integer value = QueryUtils.parseValue(Integer.class, args.get(0));

            if (value == null) {
                throw new QueryException(
                    "Left-side is collection, and right-side is not a valid integer, so can't compare by size.");
            }

            return Restrictions.sizeLe(queryPath.getPath(), value);
        }

        return Restrictions.le(queryPath.getPath(), args.get(0));
    }

    @Override
    public <Y> Predicate getPredicate(CriteriaBuilder builder, Root<Y> root, QueryPath queryPath) {
        Property property = queryPath.getProperty();

        if (property.isCollection()) {
            Integer value = QueryUtils.parseValue(Integer.class, args.get(0));

            if (value == null) {
                throw new QueryException(
                    "Left-side is collection, and right-side is not a valid integer, so can't compare by size.");
            }

            return builder.lessThanOrEqualTo(builder.size(root.get(queryPath.getPath())), value);
        }

        return builder.lessThanOrEqualTo(root.get(queryPath.getPath()), args.get(0));
    }

    @Override
    public boolean test(Object value) {
        if (args.isEmpty() || value == null) {
            return false;
        }

        Type type = new Type(value);

        if (type.isString()) {
            String s1 = getValue(String.class);
            String s2 = (String) value;

            return s1 != null && (s2.equals(s1) || s2.compareTo(s1) < 0);
        }
        if (type.isInteger()) {
            Integer s1 = getValue(Integer.class);
            Integer s2 = (Integer) value;

            return s1 != null && s2 <= s1;
        }
        if (type.isFloat()) {
            Float s1 = getValue(Float.class);
            Float s2 = (Float) value;

            return s1 != null && s2 <= s1;
        }
        if (type.isDate()) {
            Date s1 = getValue(Date.class);
            Date s2 = (Date) value;

            return s1 != null && (s2.before(s1) || s2.equals(s1));
        }
        if (type.isInstant()) {
            Instant s1 = getValue(Instant.class);
            Instant s2 = (Instant) value;

            return s1 != null && (s2.isBefore(s1) || s2.equals(s1));
        }
        if (type.isLocalDateTime()) {
            LocalDateTime s1 = getValue(LocalDateTime.class);
            LocalDateTime s2 = (LocalDateTime) value;

            return s1 != null && (s2.isBefore(s1) || s2.equals(s1));
        }
        if (type.isCollection()) {
            Collection<?> collection = (Collection<?>) value;
            Integer size = getValue(Integer.class);

            return size != null && collection.size() <= size;
        }

        return false;
    }
}
