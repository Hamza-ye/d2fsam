package org.nmcpye.am.organisationunit;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.commons.filter.FilterUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service Implementation for managing {@link OrganisationUnitGroup}.
 */
@Service("org.nmcpye.am.organisationunit.OrganisationUnitGroupServiceExt")
@Slf4j
public class OrganisationUnitGroupServiceExtImpl implements OrganisationUnitGroupServiceExt {

    private final OrganisationUnitGroupRepositoryExt organisationUnitGroupRepositoryExt;

    private final OrganisationUnitGroupSetRepositoryExt organisationUnitGroupSetRepositoryExt;

    public OrganisationUnitGroupServiceExtImpl(
        OrganisationUnitGroupRepositoryExt organisationUnitGroupRepositoryExt,
        OrganisationUnitGroupSetRepositoryExt organisationUnitGroupSetRepositoryExt) {
        this.organisationUnitGroupRepositoryExt = organisationUnitGroupRepositoryExt;
        this.organisationUnitGroupSetRepositoryExt = organisationUnitGroupSetRepositoryExt;
    }

    @Override
    @Transactional
    public Long addOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
        organisationUnitGroupRepositoryExt.saveObject(organisationUnitGroup);

        return organisationUnitGroup.getId();
    }

    @Override
    @Transactional
    public void updateOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
        organisationUnitGroupRepositoryExt.update(organisationUnitGroup);
    }

    @Override
    @Transactional
    public void deleteOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
        organisationUnitGroupRepositoryExt.deleteObject(organisationUnitGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitGroup getOrganisationUnitGroup(Long id) {
        return organisationUnitGroupRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitGroup getOrganisationUnitGroup(String uid) {
        return organisationUnitGroupRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroup> getAllOrganisationUnitGroups() {
        return organisationUnitGroupRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroup> getOrganisationUnitGroupsWithGroupSets() {
        return organisationUnitGroupRepositoryExt.getOrganisationUnitGroupsWithGroupSets();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets() {
        return organisationUnitGroupRepositoryExt.getOrganisationUnitGroupsWithoutGroupSets();
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSet
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Long addOrganisationUnitGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet) {
        organisationUnitGroupSetRepositoryExt.saveObject(organisationUnitGroupSet);

        return organisationUnitGroupSet.getId();
    }

    @Override
    @Transactional
    public void updateOrganisationUnitGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet) {
        organisationUnitGroupSetRepositoryExt.update(organisationUnitGroupSet);
    }

    @Override
    @Transactional
    public void deleteOrganisationUnitGroupSet(OrganisationUnitGroupSet organisationUnitGroupSet) {
        organisationUnitGroupSetRepositoryExt.deleteObject(organisationUnitGroupSet);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitGroupSet getOrganisationUnitGroupSet(Long id) {
        return organisationUnitGroupSetRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitGroupSet getOrganisationUnitGroupSet(String uid) {
        return organisationUnitGroupSetRepositoryExt.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroupSet> getAllOrganisationUnitGroupSets() {
        return organisationUnitGroupSetRepositoryExt.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSets() {
        List<OrganisationUnitGroupSet> groupSets = new ArrayList<>();

        for (OrganisationUnitGroupSet groupSet : getAllOrganisationUnitGroupSets()) {
            if (groupSet.getCompulsory()) {
                groupSets.add(groupSet);
            }
        }

        return groupSets;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSetsWithMembers() {
        return FilterUtils.filter(
            getAllOrganisationUnitGroupSets(),
            object -> object.getCompulsory() && object.hasOrganisationUnitGroups()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSetsNotAssignedTo(OrganisationUnit organisationUnit) {
        List<OrganisationUnitGroupSet> groupSets = new ArrayList<>();

        for (OrganisationUnitGroupSet groupSet : getCompulsoryOrganisationUnitGroupSets()) {
            if (!groupSet.isMemberOfOrganisationUnitGroups(organisationUnit) && groupSet.hasOrganisationUnitGroups()) {
                groupSets.add(groupSet);
            }
        }

        return groupSets;
    }

    //    @Override
    //    @Transactional
    //    public void mergeWithCurrentUserOrganisationUnits(OrganisationUnitGroup organisationUnitGroup,
    //                                                      Collection<OrganisationUnit> mergeOrganisationUnits) {
    //        Set<OrganisationUnit> organisationUnits = new HashSet<>(organisationUnitGroup.getMembers());
    //
    //        Set<OrganisationUnit> userOrganisationUnits = new HashSet<>();
    //
    //        for (OrganisationUnit organisationUnit : currentUserService.getCurrentUser().getOrganisationUnits()) {
    //            userOrganisationUnits
    //                .addAll(organisationUnitServiceExt.getOrganisationUnitWithChildren(organisationUnit.getUid()));
    //        }
    //
    //        organisationUnits.removeAll(userOrganisationUnits);
    //        organisationUnits.addAll(mergeOrganisationUnits);
    //
    //        organisationUnitGroup.updateOrganisationUnits(organisationUnits);
    //
    //        updateOrganisationUnitGroup(organisationUnitGroup);
    //    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationUnitGroup getOrgUnitGroupInGroupSet(Set<OrganisationUnitGroup> groups, OrganisationUnitGroupSet groupSet) {
        return organisationUnitGroupRepositoryExt.getOrgUnitGroupInGroupSet(groups, groupSet);
    }
}
