package org.nmcpye.am.trackedentityfilter;

import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.common.AssignedUserSelectionMode;
import org.nmcpye.am.common.OrganisationUnitSelectionMode;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.programstagefilter.DateFilterPeriod;
import org.nmcpye.am.programstagefilter.DatePeriodType;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.trackedentity.TrackedEntityAttributeServiceExt;
import org.nmcpye.am.webapi.controller.event.mapper.OrderParamsHelper;
import org.nmcpye.am.webapi.controller.event.webrequest.OrderCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service("org.nmcpye.am.trackedentityfilter.TrackedEntityInstanceFilterServiceExt")
public class TrackedEntityInstanceFilterServiceExtImpl implements TrackedEntityInstanceFilterServiceExt {
    private final TrackedEntityInstanceFilterRepositoryExt trackedEntityInstanceFilterStore;

    private final ProgramServiceExt programService;

    private final TrackedEntityAttributeServiceExt teaService;

    public TrackedEntityInstanceFilterServiceExtImpl(TrackedEntityInstanceFilterRepositoryExt trackedEntityInstanceFilterStore,
                                                     ProgramServiceExt programService,
                                                     TrackedEntityAttributeServiceExt teaService) {
        checkNotNull(trackedEntityInstanceFilterStore);
        checkNotNull(programService);
        checkNotNull(teaService);

        this.trackedEntityInstanceFilterStore = trackedEntityInstanceFilterStore;
        this.programService = programService;
        this.teaService = teaService;
    }

    @Override
    @Transactional
    public Long add(TrackedEntityInstanceFilter trackedEntityInstanceFilter) {
        trackedEntityInstanceFilterStore.saveObject(trackedEntityInstanceFilter);
        return trackedEntityInstanceFilter.getId();
    }

    @Override
    @Transactional
    public void delete(TrackedEntityInstanceFilter trackedEntityInstanceFilter) {
        trackedEntityInstanceFilterStore.deleteObject(trackedEntityInstanceFilter);
    }

    @Override
    @Transactional
    public void update(TrackedEntityInstanceFilter trackedEntityInstanceFilter) {
        trackedEntityInstanceFilterStore.update(trackedEntityInstanceFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackedEntityInstanceFilter get(Long id) {
        return trackedEntityInstanceFilterStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityInstanceFilter> getAll() {
        return trackedEntityInstanceFilterStore.getAll();
    }

    @Override
    public List<String> validate(TrackedEntityInstanceFilter teiFilter) {
        List<String> errors = new ArrayList<>();

        if (teiFilter.getProgram() != null && !StringUtils.isEmpty(teiFilter.getProgram().getUid())) {
            Program pr = programService.getProgram(teiFilter.getProgram().getUid());

            if (pr == null) {
                errors.add("Program is specified but does not exist: " + teiFilter.getProgram().getUid());
            }
        }

        EntityQueryCriteria eqc = teiFilter.getEntityQueryCriteria();

        if (eqc == null) {
            return errors;
        }

        validateAttributeValueFilters(errors, eqc);

        validateDateFilterPeriods(errors, eqc);

        validateAssignedUsers(errors, eqc);

        validateOrganisationUnits(errors, eqc);

        validateOrderParams(errors, eqc);

        return errors;
    }

    private void validateDateFilterPeriods(List<String> errors, EntityQueryCriteria eqc) {
        validateDateFilterPeriod(errors, "EnrollmentCreatedDate", eqc.getEnrollmentCreatedDate());
        validateDateFilterPeriod(errors, "EnrollmentIncidentDate", eqc.getEnrollmentIncidentDate());
        validateDateFilterPeriod(errors, "EventDate", eqc.getEventDate());
        validateDateFilterPeriod(errors, "LastUpdatedDate", eqc.getLastUpdatedDate());
    }

    private void validateOrganisationUnits(List<String> errors, EntityQueryCriteria eqc) {
        if (StringUtils.isEmpty(eqc.getOrganisationUnit())
            && (eqc.getOuMode() == OrganisationUnitSelectionMode.SELECTED
            || eqc.getOuMode() == OrganisationUnitSelectionMode.DESCENDANTS
            || eqc.getOuMode() == OrganisationUnitSelectionMode.CHILDREN)) {
            errors.add(String.format("Organisation Unit cannot be empty with %s org unit mode",
                eqc.getOuMode().toString()));
        }
    }

    private void validateOrderParams(List<String> errors, EntityQueryCriteria eqc) {
        if (!StringUtils.isEmpty(eqc.getOrder())) {
            List<OrderCriteria> orderCriteria = OrderCriteria.fromOrderString(eqc.getOrder());
            Map<String, TrackedEntityAttribute> attributes = teaService.getAllTrackedEntityAttributes()
                .stream().collect(Collectors.toMap(TrackedEntityAttribute::getUid, att -> att));
            errors.addAll(
                OrderParamsHelper.validateOrderParams(OrderParamsHelper.toOrderParams(orderCriteria), attributes));
        }
    }

    private void validateAssignedUsers(List<String> errors, EntityQueryCriteria eqc) {
        if (CollectionUtils.isEmpty(eqc.getAssignedUsers())
            && eqc.getAssignedUserMode() == AssignedUserSelectionMode.PROVIDED) {
            errors.add("Assigned Users cannot be empty with PROVIDED assigned user mode");
        }
    }

    private void validateAttributeValueFilters(List<String> errors, EntityQueryCriteria eqc) {
        List<AttributeValueFilter> attributeValueFilters = eqc.getAttributeValueFilters();
        if (!CollectionUtils.isEmpty(attributeValueFilters)) {
            attributeValueFilters.forEach(avf -> {
                if (StringUtils.isEmpty(avf.getAttribute())) {
                    errors.add("Attribute Uid is missing in filter");
                } else {
                    TrackedEntityAttribute tea = teaService.getTrackedEntityAttribute(avf.getAttribute());
                    if (tea == null) {
                        errors.add("No tracked entity attribute found for attribute:" + avf.getAttribute());
                    }
                }

                validateDateFilterPeriod(errors, avf.getAttribute(), avf.getDateFilter());
            });
        }
    }

    private void validateDateFilterPeriod(List<String> errors, String item, DateFilterPeriod dateFilterPeriod) {
        if (dateFilterPeriod == null || dateFilterPeriod.getType() == null) {
            return;
        }

        if (dateFilterPeriod.getType() == DatePeriodType.ABSOLUTE
            && dateFilterPeriod.getStartDate() == null && dateFilterPeriod.getEndDate() == null) {
            errors.add("Start date or end date not specified with ABSOLUTE date period type for " + item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedEntityInstanceFilter> get(Program program) {
        return trackedEntityInstanceFilterStore.get(program);
    }
}
