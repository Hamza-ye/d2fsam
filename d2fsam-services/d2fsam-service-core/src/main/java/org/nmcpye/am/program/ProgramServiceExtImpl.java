package org.nmcpye.am.program;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetValuedMap;
import org.nmcpye.am.association.jdbc.JdbcOrgUnitAssociationsStore;
import org.nmcpye.am.common.IdentifiableObjectManager;
import org.nmcpye.am.organisationunit.OrganisationUnit;
import org.nmcpye.am.trackedentity.TrackedEntityType;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Service Implementation for managing {@link Program}.
 */
@Service("org.nmcpye.am.program.ProgramServiceExt")
@RequiredArgsConstructor
@Slf4j
public class ProgramServiceExtImpl implements ProgramServiceExt {

    private final ProgramRepositoryExt programRepositoryExt;

    private final IdentifiableObjectManager idObjectManager;

    private final CurrentUserService currentUserService;

    @Qualifier("jdbcProgramOrgUnitAssociationsStore")
    private final JdbcOrgUnitAssociationsStore jdbcOrgUnitAssociationsStore;

    @Override
    @Transactional
    public Long addProgram(Program program) {
        programRepositoryExt.saveObject(program);
        return program.getId();
    }

    @Override
    @Transactional
    public void updateProgram(Program program) {
        programRepositoryExt.update(program);
    }

    @Override
    @Transactional
    public void deleteProgram(Program program) {
        programRepositoryExt.deleteObject(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Program> getAllPrograms() {
        return programRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Program> getPrograms(Collection<String> uids) {
        return programRepositoryExt.getByUid(uids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Program> getPrograms(OrganisationUnit organisationUnit) {
        return programRepositoryExt.get(organisationUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public Program getProgram(Long id) {
        return programRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Program getProgram(String uid) {
        return programRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Program> getProgramsByTrackedEntityType(TrackedEntityType trackedEntityType) {
        return programRepositoryExt.getByTrackedEntityType(trackedEntityType);
    }

    //    @Override
    //    @Transactional(readOnly = true)
    //    public List<Program> getProgramsByDataEntryForm(DataEntryForm dataEntryForm) {
    //        return programRepositoryExt.getByDataEntryForm(dataEntryForm);
    //    }

    @Override
    @Transactional(readOnly = true)
    public List<Program> getCurrentUserPrograms() {
        User user = currentUserService.getCurrentUser();
        if (user == null || user.isSuper()) {
            return getAllPrograms();
        }
        return programRepositoryExt.getDataReadAll(user);
    }

    // -------------------------------------------------------------------------
    // ProgramDataElement
    // -------------------------------------------------------------------------

    //    @Override
    //    @Transactional(readOnly = true)
    //    public List<ProgramDataElementDimensionItem> getGeneratedProgramDataElements(String programUid) {
    //        Program program = getProgram(programUid);
    //
    //        List<ProgramDataElementDimensionItem> programDataElements = Lists.newArrayList();
    //
    //        if (program == null) {
    //            return programDataElements;
    //        }
    //
    //        for (DataElement element : program.getDataElements()) {
    //            programDataElements.add(new ProgramDataElementDimensionItem(program, element));
    //        }
    //
    //        Collections.sort(programDataElements);
    //
    //        return programDataElements;
    //    }

    @Override
    public Boolean hasOrgUnit(Program program, OrganisationUnit organisationUnit) {
        return this.programRepositoryExt.hasOrgUnit(program, organisationUnit);
    }

    @Override
    public SetValuedMap<String, String> getProgramOrganisationUnitsAssociationsForCurrentUser(Set<String> programUids) {
        idObjectManager.loadByUid(Program.class, programUids);

        return jdbcOrgUnitAssociationsStore.getOrganisationUnitsAssociationsForCurrentUser(programUids);
    }

    @Override
    public SetValuedMap<String, String> getProgramOrganisationUnitsAssociations(Set<String> programUids) {
        return jdbcOrgUnitAssociationsStore.getOrganisationUnitsAssociations(programUids);
    }
}
