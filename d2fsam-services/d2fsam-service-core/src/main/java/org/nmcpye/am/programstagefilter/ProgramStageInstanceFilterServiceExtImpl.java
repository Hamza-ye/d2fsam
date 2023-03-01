package org.nmcpye.am.programstagefilter;

import org.nmcpye.am.common.AssignedUserSelectionMode;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.organisationunit.OrganisationUnitServiceExt;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramServiceExt;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.program.ProgramStageServiceExt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service( "org.nmcpye.am.programstagefilter.ProgramStageInstanceFilterServiceExt" )
@Transactional( readOnly = true )
public class ProgramStageInstanceFilterServiceExtImpl implements ProgramStageInstanceFilterServiceExt {
    private final ProgramServiceExt programService;

    private final ProgramStageServiceExt programStageService;

    private final OrganisationUnitServiceExt organisationUnitServiceExt;

    public ProgramStageInstanceFilterServiceExtImpl(ProgramServiceExt programService,
                                                    ProgramStageServiceExt programStageService,
                                                    OrganisationUnitServiceExt organisationUnitServiceExt) {
        this.programService = programService;
        this.programStageService = programStageService;
        this.organisationUnitServiceExt = organisationUnitServiceExt;
    }

    @Override
    public List<String> validate(ProgramStageInstanceFilter programStageInstanceFilter) {
        List<String> errors = new ArrayList<>();

        if (programStageInstanceFilter.getProgram() == null) {
            errors.add("Program should be specified for event filters.");
        } else {
            Program pr = programService.getProgram(programStageInstanceFilter.getProgram());

            if (pr == null) {
                errors.add("Program is specified but does not exist: " + programStageInstanceFilter.getProgram());
            }
        }

        if (programStageInstanceFilter.getProgramStage() != null) {
            ProgramStage ps = programStageService.getProgramStage(programStageInstanceFilter.getProgramStage());
            if (ps == null) {
                errors.add(
                    "Program stage is specified but does not exist: " + programStageInstanceFilter.getProgramStage());
            }
        }

        EventQueryCriteria eventQC = programStageInstanceFilter.getEventQueryCriteria();
        if (eventQC != null) {
            if (eventQC.getOrganisationUnit() != null) {
                OrganisationUnit ou = organisationUnitServiceExt.getOrganisationUnit(eventQC.getOrganisationUnit());
                if (ou == null) {
                    errors.add("Org unit is specified but does not exist: " + eventQC.getOrganisationUnit());
                }
            }
            if (eventQC.getAssignedUserMode() != null && eventQC.getAssignedUsers() != null
                && !eventQC.getAssignedUsers().isEmpty()
                && !eventQC.getAssignedUserMode().equals(AssignedUserSelectionMode.PROVIDED)) {
                errors.add("Assigned User uid(s) cannot be specified if selectionMode is not PROVIDED");
            }

            if (eventQC.getEvents() != null && !eventQC.getEvents().isEmpty() && eventQC.getDataFilters() != null
                && !eventQC.getDataFilters().isEmpty()) {
                errors.add("Event UIDs and filters can not be specified at the same time");
            }

            if (eventQC.getDisplayColumnOrder() != null && eventQC.getDisplayColumnOrder().size() > 0
                && (new HashSet<String>(eventQC.getDisplayColumnOrder())).size() < eventQC.getDisplayColumnOrder()
                .size()) {
                errors.add("Event query criteria can not have duplicate column ordering fields");
            }
        }

        return errors;
    }
}
