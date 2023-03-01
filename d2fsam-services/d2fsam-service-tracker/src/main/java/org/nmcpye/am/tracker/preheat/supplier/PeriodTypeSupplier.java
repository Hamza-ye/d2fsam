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
package org.nmcpye.am.tracker.preheat.supplier;

import lombok.RequiredArgsConstructor;
import org.nmcpye.am.common.IdentifiableObject;
import org.nmcpye.am.period.Period;
import org.nmcpye.am.period.PeriodRepositoryExt;
import org.nmcpye.am.tracker.TrackerIdSchemeParam;
import org.nmcpye.am.tracker.TrackerImportParams;
import org.nmcpye.am.tracker.preheat.TrackerPreheat;
import org.nmcpye.am.tracker.preheat.cache.PreheatCacheService;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Luciano Fiandesio
 */
@RequiredArgsConstructor
@Component
public class PeriodTypeSupplier extends AbstractPreheatSupplier {
    @Nonnull
    private final PeriodRepositoryExt periodStore;

    @Nonnull
    private final PreheatCacheService cache;

    @Override
    public void preheatAdd(TrackerImportParams params, TrackerPreheat preheat) {
        if (cache.hasKey(Period.class.getName())) {
            preheat.put(TrackerIdSchemeParam.UID, cache.getAll(Period.class.getName()));
        } else {
            final List<Period> periods = periodStore.getAll();
            addToCache(cache, periods);
            _addToPreheat(preheat,
                periods.stream().map(p -> (IdentifiableObject) p).collect(Collectors.toList()));
        }
        // Period store can't be cached because it's not extending
        // `IdentifiableObject`
        periodStore.getAllPeriodTypes()
            .forEach(periodType -> preheat.getPeriodTypeMap().put(periodType.getName(), periodType));
    }

    private void _addToPreheat(TrackerPreheat preheat, List<IdentifiableObject> periods) {
        periods.forEach(p -> preheat.getPeriodMap().put(p.getName(), (Period) p));
    }
}
